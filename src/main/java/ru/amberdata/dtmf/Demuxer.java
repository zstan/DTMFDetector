package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.containers.mps.MPEGDemuxer;
import org.jcodec.containers.mps.MTSDemuxer;
import ru.amberdata.dtmf.configuration.dtmf.Channel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.Set;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Demuxer implements Runnable {

    private final SeekableByteChannel source;
    private static final Logger logger = LogManager.getLogger(Demuxer.class);
    private final ChannelManager chManager;

    public Demuxer (SeekableByteChannel in, ChannelManager manager) {
        this.source = in;
        this.chManager = manager;
    }

    public ChannelManager getChannelManager() {
        return chManager;
    }

    private boolean identMarkerPosition(SeekableByteChannel channel) throws IOException {
        int markerSize = 188;
        int probeSize = 1;
        ByteBuffer buffer = ByteBuffer.allocateDirect(probeSize);
        if (NIOUtils.readFromChannel(channel, buffer) != probeSize)
            return false;
        buffer.flip();
        while (buffer.get() != 0x47) {
            buffer.clear();
            if (NIOUtils.readFromChannel(channel, buffer) != probeSize)
                return false;
            buffer.flip();
        }
        buffer = ByteBuffer.allocateDirect(markerSize - probeSize);
        if (NIOUtils.readFromChannel(channel, buffer) != markerSize - probeSize) // just skip 187 bytes
            return false;
        return true;
    }

    private MPEGDemuxer.MPEGDemuxerTrack initDemuxerTrack() {
        Set<Integer> programs;
        try {
            programs = MTSDemuxer.getProgramsFromChannel(source);
            logger.info("programs pid found: ");
            programs.forEach(logger::info);
        } catch (IOException e) {
            logger.error("empty programs list found");
            return null;
        }

        MTSDemuxer demuxer = null;
        try {
            Channel ch = getChannelManager().getChannel();
            if (programs.contains(ch.getAudioPID())) {
                logger.info("try to initialize audio pid: " + ch.getAudioPID());
                demuxer = new MTSDemuxer(source, ch.getAudioPID());
            } else {
                logger.error("chosen audio pid not found or initialisation fail, pid: " + ch.getAudioPID());
                return null;
            }
        } catch (IOException e) {
            logger.error("can`t initialise Demuxer with chosen audio pid");
            return null;
        }

        List<? extends MPEGDemuxer.MPEGDemuxerTrack> at = demuxer.getAudioTracks();
        logger.info("found " + at.size() + " audio tracks");
        MPEGDemuxer.MPEGDemuxerTrack demuxerTrack = at.get(0); // todo: init from config !!!
        logger.info("audio codec detected: {}", demuxerTrack.getMeta().getCodec());
        return demuxerTrack;
    }

    @Override
    public void run() {
        try {
            logger.info("Identify marker position: {}", identMarkerPosition(this.source));
        } catch (IOException e) {
            logger.error(e);
        }
        MPEGDemuxer.MPEGDemuxerTrack demuxerTrack = initDemuxerTrack();
        if (demuxerTrack == null)
            return;

        try {
            PipedOutputStream pipedOutput = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, 18_800_000);
            WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

            Thread dtmfDetectorThread = new Thread(new DTMFDetector(pipedInputStream, this.getChannelManager()));
            dtmfDetectorThread.start();

            long fcount = 0;
            ByteBuffer buf = ByteBuffer.allocateDirect(10_000);

            try {
                while (true) {
                    buf.clear();
                    Packet inFrame = demuxerTrack.nextFrame(buf);
                    if (inFrame != null) {
                        ByteBuffer data = inFrame.getData();
                        fcount++;
                        //if (fcount % 10 == 0)
                        //    System.out.print("*");
                        if (fcount % 20 == 0)
                            logger.debug("frames decoded: {} data read: {}", fcount, data.remaining());
                        pipedChannel.write(data);
                    } else {
                        Thread.sleep(100);
                        logger.info("sleep source.size: " + source.size() + " source.position: " + source.position());
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            pipedChannel.close();
            dtmfDetectorThread.interrupt();
            logger.info("close {}", Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("exit");
    }
}
