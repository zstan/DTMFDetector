package ru.amberdata.dtmf.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by zstan on 28.11.16.
 */
public class DatagramChannelWrapper implements SeekableByteChannel {

    private static final Logger logger = LogManager.getLogger(DatagramChannelWrapper.class);
    private DatagramChannel ch;
    private long position = 0;

    public DatagramChannelWrapper(InetSocketAddress iAddr) throws IOException {
        this.ch = DatagramChannel.open().bind(iAddr);
    }

    @Override
    public long position() throws IOException {
        logger.warn("DatagramChannelWrapper not supported: position " + position);
        return position;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        logger.warn("DatagramChannelWrapper not supported: newPosition " + newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        logger.warn("DatagramChannelWrapper not supported: size");
        return position;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        logger.warn("DatagramChannelWrapper not supported: truncate");
        return null;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        dst.clear();
        ch.receive(dst);
        int pos = dst.position();
        position += pos;

        return dst.position() > 0 ? pos : -1;
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        return ch.write(src);
    }

    @Override
    public boolean isOpen() {
        return ch.isOpen();
    }

    @Override
    public void close() throws IOException {
        ch.close();
    }
}
