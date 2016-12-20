package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.io.UDPInputStream;
import ru.amberdata.dtmf.configuration.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.WritableByteChannel;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * Created by zhenya on 2016-12-18.
 */
public class DatagramChannelStreamer implements Runnable {

    private static final Logger logger = LogManager.getLogger(DatagramChannelStreamer.class);

    private static InetSocketAddress initializeAddress(String addr) throws Exception {
        int port = -1;
        try {
            port = Integer.parseInt(addr);
            return new InetSocketAddress(port);
        } catch (NumberFormatException e) {
            String[] hostPort = addr.split(":");
            if (hostPort.length != 2)
                throw new Exception ("unacceptable streamAddress param: " + addr);
            return new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1]));
        }
    }

    private void init() throws Exception {
        InputStream instream =
                Main.class.getClassLoader().getResourceAsStream("log4j2.xml");

        System.out.println(instream);

        logger.info( "The server is ready..." );

        DTMFContext context = new DTMFContext();

        for (Channel ch : context.dtmfConfig.getChannel()) { // todo: new thread chain
            InetSocketAddress iAddr = initializeAddress(ch.getStreamAddress());
            logger.info("start listening on: " + iAddr);

            ByteBuffer buf = ByteBuffer.allocateDirect(188000);

            SeekableByteChannel source = readableFileChannel(iAddr);
            //UDPInputStream source = new UDPInputStream(iAddr);

            PipedOutputStream pipedOutput = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, 18_800_000);
            WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

            Thread demuxerThread = new Thread(new Demuxer(readableFileChannel(pipedInputStream), ch));
            demuxerThread.start();

            while (source.read(buf) != -1) {
                buf.flip();
                System.out.println("udp: " + buf.remaining());
                pipedChannel.write(buf);
            }
            demuxerThread.interrupt();
            pipedOutput.close();
            pipedChannel.close();
        }
    }

    @Override
    public void run() {
        try {
            init();
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
