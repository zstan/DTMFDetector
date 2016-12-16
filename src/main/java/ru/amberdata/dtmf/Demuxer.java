package ru.amberdata.dtmf;

import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
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

    public Demuxer (SeekableByteChannel in) {
        this.source = in;
    }

    @Override
    public void run() {
        try {
            Set<Integer> programs = MTSDemuxer.getProgramsFromChannel(source);
            System.out.println("programs found: ");
            programs.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("empty programs list from stream");
            return;
        }

        MTSDemuxer demuxer = null;
        try {
            int program = 69; // 69 win 68 lin !!!
            System.out.println("curr program: " + program);
            demuxer = new MTSDemuxer(source, program); // +
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("can`t initialise Demuxer with chosen program");
            return;
        }

        List<? extends MPEGDemuxer.MPEGDemuxerTrack> at = demuxer.getAudioTracks();
        System.out.println("found " + at.size() + " audio tracks");
        MPEGDemuxer.MPEGDemuxerTrack demuxerTrack = at.get(0);
        System.out.println("audio codec detected: " + demuxerTrack.getMeta().getCodec());
        System.out.println("duration: " + demuxerTrack.getMeta().getTotalDuration());
        System.out.println("frames: " + demuxerTrack.getMeta().getTotalFrames());
        System.out.println("aspect ratio: " + demuxerTrack.getMeta().getPixelAspectRatio());

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
                    //buf.clear();
                    Packet inFrame = demuxerTrack.nextFrame(buf);
                    if (inFrame != null) {
                        ByteBuffer data = inFrame.getData();
                        fcount++;
                        if (fcount % 10 == 0)
                            System.out.print("*");
                        pipedChannel.write(data);
                    } else {
                        Thread.sleep(200);
                        System.out.println("sleep source.size: " + source.size() + " source.position: " + source.position());
                    }
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            pipedChannel.close();
            dtmfDetectorThread.interrupt();
            System.out.println("close " + Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("exit");
    }
}
