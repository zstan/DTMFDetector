package org.jcodec.codecs.h264;

import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture8Bit;
import org.jcodec.platform.Platform;
import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonList;

public class PerformanceTest {

    @Test
    @Ignore
    public void testNoContainer() throws IOException {
        String dir = "src/test/resources/video/seq_h264_4";

        String info = Platform.stringFromBytes(readFile(dir + "/info.txt").array());
        int width = parseInt(info.split(" ")[0]);
        int height = parseInt(info.split(" ")[1]);
        int frameCount = parseInt(info.split(" ")[2]);

        byte[][] picData = Picture8Bit.create(width, height, ColorSpace.YUV420J).getData();

        H264Decoder decoder = new H264Decoder();
        decoder.addSps(singletonList(readFile(dir + "/sps")));
        decoder.addPps(singletonList(readFile(dir + "/pps")));

        List<List<ByteBuffer>> frames = new ArrayList<List<ByteBuffer>>();

        for (int fn = 0; fn < frameCount; fn++) {
            ByteBuffer buf = readFile(dir + "/" + zeroPad3(fn));
            frames.add(extractNALUnits(buf));
        }

        for (int i = 1, warmUpIterations = 30; i <= warmUpIterations; i++) {
            System.out.println("warming up " + i + "/" + warmUpIterations);

            for (int fn = 0; fn < frameCount; fn++)
                decoder.decodeFrame8BitFromNals(duplicateBuffers(frames.get(fn)), picData);
        }

        int fpss = 0;
        int iterations = 15;

        for (int i = 1; i <= iterations; i++) {
            System.out.println("benchmarking " + i + "/" + iterations);
            long t = System.currentTimeMillis();

            for (int fn = 0; fn < frameCount; fn++)
                decoder.decodeFrame8BitFromNals(duplicateBuffers(frames.get(fn)), picData);

            t = System.currentTimeMillis() - t;
            long fps = frames.size() * 1000 / t;
            System.out.println(fps + " fps");
            fpss += fps;
        }

        System.out.println("\naverage: " + (fpss / iterations) + " fps");
    }

    private ByteBuffer readFile(String path) throws IOException {
        File file = new File(path);
        InputStream _in = new BufferedInputStream(new FileInputStream(file));
        byte[] buf = new byte[(int) file.length()];
        _in.read(buf);
        _in.close();
        return ByteBuffer.wrap(buf);
    }

    private String zeroPad3(int n) {
        String s = n + "";
        while (s.length() < 3)
            s = "0" + s;
        return s;
    }

    private List<ByteBuffer> extractNALUnits(ByteBuffer buf) {
        buf = buf.duplicate();
        List<ByteBuffer> nalUnits = new ArrayList<ByteBuffer>();

        while (buf.remaining() > 4) {
            int length = buf.getInt();
            ByteBuffer nalUnit = ByteBuffer.allocate(length);
            for (int i = 0; i < length; i++) {
                nalUnit.put(buf.get());
            }
            nalUnit.flip();
            nalUnits.add(nalUnit);
        }

        return nalUnits;
    }

    private List<ByteBuffer> duplicateBuffers(List<ByteBuffer> bufs) {
        List<ByteBuffer> result = new ArrayList<ByteBuffer>();

        for (ByteBuffer buf : bufs)
            result.add(buf.duplicate());

        return result;
    }
}
