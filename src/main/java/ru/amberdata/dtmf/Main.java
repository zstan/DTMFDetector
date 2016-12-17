package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;
import ru.amberdata.dtmf.configuration.Channel;

import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.WritableByteChannel;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        InputStream instream =
                Main.class.getClassLoader().getResourceAsStream("log4j2.xml");

        System.out.println(instream);

        logger.info( "The server is ready..." );

        DTMFContext context = new DTMFContext();

        for (Channel ch : context.dtmfConfig.getChannel()) {
            DatagramChannel datagramChannel = DatagramChannel.open();
            String addr = ch.getStreamAddress();
            int port = -1;
            InetSocketAddress iAddr;
            try {
                port = Integer.parseInt(addr);
                iAddr = new InetSocketAddress(port);
            } catch (NumberFormatException e) {
                String[] hostPort = addr.split(":");
                if (hostPort.length != 2)
                    throw new Exception ("unacceptable streamAddress param: " + addr);
                iAddr  = new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1]));
            }
            logger.info("start listening on: " + iAddr);

            datagramChannel.socket().bind(iAddr);
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

}
