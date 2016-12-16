package org.jcodec.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by zstan on 05.12.16.
 */
public class PipedChannelWrapper implements SeekableByteChannel {

    private final InputStream in;
    private long pos = 0;
    ByteBuffer buf = ByteBuffer.allocate(138_000_0);
    boolean readFomBuf = false;
    private long count = 0;

    public PipedChannelWrapper(InputStream iin) {
        in = iin;
        //buf.clear();
    }

    @Override
    public long position() throws IOException {
        System.out.println("not supported: position ");
        return 0;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        System.out.println("not supported: newPosition " + newPosition);
        readFomBuf = true;
        buf.flip();
        return this;
    }

    @Override
    public long size() throws IOException {
        long size = 188 + pos;
        System.out.println("not supported: size " + size);
        return size;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        System.out.println("not supported: truncate");
        return this;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        //System.out.println("dst remaining : " + dst.remaining() + " pos " + dst.position());
        byte[] arr;
        int size;
        if (readFomBuf) {
            if (buf.remaining()>=188) {
                arr = new byte[dst.remaining()];
                size = dst.remaining();
                int p1 = buf.position();
                int p2 = dst.remaining();
                buf.get(arr, 0, p2);
            } else {
                //System.out.println("dst remaining : " + dst.remaining());
                arr = new byte[dst.remaining()];
                size = in.read(arr);
            }
        }
        else {
            arr = new byte[dst.remaining()];
            size = in.read(arr);
        }
        //System.out.println("read : " + dst.remaining() + " " + size);
        //dst.clear();
        dst.put(arr);
        //dst.flip(); // -
        if (!readFomBuf) {
            buf.put(arr);
            //System.out.println(count ++);
        }
        //dst.flip(); // -
        pos += size;
        return size;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return 0;
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void close() throws IOException {

    }
}
