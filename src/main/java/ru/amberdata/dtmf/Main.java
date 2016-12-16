package ru.amberdata.dtmf;

import org.jcodec.common.io.SeekableByteChannel;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.WritableByteChannel;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println( "The server is ready..." ) ;

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.socket().bind(new InetSocketAddress(1234));
        ByteBuffer buf = ByteBuffer.allocateDirect(188000);

        SeekableByteChannel source = readableFileChannel(datagramChannel);

        PipedOutputStream pipedOutput = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, 18800000);
        WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

        Thread demuxerThread = new Thread(new Demuxer(readableFileChannel(pipedInputStream)));
        demuxerThread.start();

        while (source.read(buf) != -1) {
            buf.flip();
            pipedChannel.write(buf);
        }
        demuxerThread.interrupt();
        pipedOutput.close();
        pipedChannel.close();
        datagramChannel.close();
    }

}
