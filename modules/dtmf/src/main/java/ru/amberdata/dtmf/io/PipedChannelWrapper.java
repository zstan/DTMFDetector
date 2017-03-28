package ru.amberdata.dtmf.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by zstan on 05.12.16.
 */
public class PipedChannelWrapper implements SeekableByteChannel {

    private static final Logger logger = LogManager.getLogger(PipedChannelWrapper.class);
    private final InputStream in;
    private long pos = 0;
    private ByteBuffer buf = ByteBuffer.allocateDirect(18_800_000);
    boolean readFomBuf = false;

    public PipedChannelWrapper(InputStream in) {
        this.in = in;
        buf.clear();
    }

    @Override
    public long position() throws IOException {
        logger.warn("PipedChannelWrapper, not supported: position ");
        return 0;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        logger.warn("PipedChannelWrapper, not supported: newPosition " + newPosition + " size: " + pos);
        readFomBuf = true;
        buf.position((int)newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        logger.warn("not supported: size " + pos);
        return pos;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        logger.warn("not supported: truncate");
        return this;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        byte[] arr = new byte[dst.remaining()];
        int size;
        if (readFomBuf) {
            if (buf.hasRemaining()) {
                if (buf.remaining() < dst.remaining()) {
                    int pos = buf.remaining();
                    buf.get(arr, 0, pos);
                    in.read(arr, pos, dst.remaining() - pos);
                }
                else {
                    buf.get(arr);
                }
                size = arr.length;
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
        logger.error("PipedChannelWrapper: deprecated write operation");
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
