package org.jcodec.common.io;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

/**
 * Created by zstan on 28.11.16.
 */
public class DatagramChannelWrapper implements SeekableByteChannel {

    private DatagramChannel ch;
    private long position = 0;

    public DatagramChannelWrapper(DatagramChannel ch) {
        this.ch = ch;
    }

    @Override
    public long position() throws IOException {
        //System.out.println("not supported: position " + position);
        return position;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        //System.out.println("not supported: newPosition " + newPosition);
        return this;
    }

    @Override
    public long size() throws IOException {
        //System.out.println("not supported: size");
        return position;
    }

    @Override
    public SeekableByteChannel truncate(long size) throws IOException {
        //System.out.println("not supported: truncate");
        return null;
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        dst.clear();
        ch.receive(dst);
        int rem = dst.remaining();
        //dst.flip();
        position += dst.position();

        return dst.position() > 0? rem : -1;
        //return dst.position();
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
