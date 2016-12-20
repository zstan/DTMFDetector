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

    public PipedChannelWrapper(InputStream in) {
        this.in = in;
        buf.clear();
    }

    @Override
    public long position() throws IOException {
        System.out.println("PipedChannelWrapper, not supported: position ");
        return 0;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        System.out.println("PipedChannelWrapper, not supported: newPosition " + newPosition + " size: " + pos);
        readFomBuf = true;
        buf.flip();
        return this;
    }

    @Override
    public long size() throws IOException {
        System.out.println("not supported: size " + pos);
        return pos;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        System.out.println("not supported: truncate");
        return this;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        byte[] arr = new byte[dst.remaining()];
        int size;
        if (readFomBuf) {
            if (buf.remaining()>=0) {
                if (buf.remaining() <= dst.remaining()) {
                    buf.get(arr, 0, buf.remaining());
                    in.read(arr, buf.remaining(), dst.remaining() - buf.remaining());
                }
                else {
                    buf.get(arr, 0, dst.remaining());
                }
                size = dst.remaining();
            } else {
                size = in.read(arr);
            }
        }
        else {
            size = in.read(arr);
        }
        dst.put(arr);
        if (!readFomBuf) {
            buf.put(arr);
        }
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
