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

    private void createTimerNotificationEvent() {
        final int TIMER_INTERVAL_SEC = 5;
        final long[] oldPosition = {0};
        new Timer(TIMER_INTERVAL_SEC * 1000, new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
                if (position > 0 && oldPosition[0] == position) {
                    System.out.println("close socket, no activity found");
                    if (ch != null)
                        try {
                            ch.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                } else
                    oldPosition[0] = position;
            }
        }).start();
    }

    public DatagramChannelWrapper(DatagramChannel ch) {
        this.ch = ch;
        createTimerNotificationEvent();
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
        //System.out.println("read! " + dst.remaining() + " " + dst.hasRemaining());
        int rem = dst.remaining();
        //dst.flip();
        position += dst.position();
        //System.out.println("read! " + dst.position() + " " + rem);

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
