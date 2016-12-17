package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;
import org.jcodec.containers.mps.MPEGDemuxer;
import org.jcodec.containers.mps.MTSDemuxer;

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

    private SeekableByteChannel source;
    private static final Logger logger = LogManager.getLogger(Demuxer.class);

    public Demuxer (SeekableByteChannel in) {
        this.source = in;
    }

    @Override
    public void run() {
        try {
            Set<Integer> programs = MTSDemuxer.getProgramsFromChannel(source);
            logger.info("programs found: ");
            programs.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("empty programs list from stream");
            return;
        }

        MTSDemuxer demuxer = null;
        try {
            int program = 69; // 69 win 68 lin !!!
            logger.info("curr program: " + program);
            demuxer = new MTSDemuxer(source, program); // +
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("can`t initialise Demuxer with chosen program");
            return;
        }

        List<? extends MPEGDemuxer.MPEGDemuxerTrack> at = demuxer.getAudioTracks();
        logger.info("found " + at.size() + " audio tracks");
        MPEGDemuxer.MPEGDemuxerTrack demuxerTrack = at.get(0);
        logger.info("audio codec detected: " + demuxerTrack.getMeta().getCodec());
        logger.info("duration: " + demuxerTrack.getMeta().getTotalDuration());
        logger.info("frames: " + demuxerTrack.getMeta().getTotalFrames());
        logger.info("aspect ratio: " + demuxerTrack.getMeta().getPixelAspectRatio());

        FileChannelWrapper fos;
        try {
            PipedOutputStream pipedOutput = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, 18_800_000);
            WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

            Thread dtmfDetectorThread = new Thread(new DTMFDetector(pipedInputStream));
            dtmfDetectorThread.start();

            long fcount = 0;
            ByteBuffer buf = ByteBuffer.allocateDirect(10_000);

            try {
                while (true) {
                    Packet inFrame = demuxerTrack.nextFrame(buf);
                    if (inFrame != null) {
                        ByteBuffer data = inFrame.getData();
                        fcount++;
                        if (fcount % 10 == 0)
                            System.out.print("*");
                        pipedChannel.write(data);
                    } else {
                        Thread.sleep(200);
                        logger.info("sleep source.size: " + source.size() + " source.position: " + source.position());
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            pipedChannel.close();
            dtmfDetectorThread.interrupt();
            logger.info("close " + Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("exit");
    }
}
