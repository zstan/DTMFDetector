package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;
import ru.amberdata.dtmf.configuration.dtmf.Channel;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;
//import static org.jcodec.common.io.NIOUtils.readableRTPChannel;
import static ru.amberdata.dtmf.configuration.Utils.initializeAddress;

/**
 * Created by zhenya on 2016-12-18.
 */
public class DatagramChannelStreamer implements Runnable {

    private static final Logger logger = LogManager.getLogger(DatagramChannelStreamer.class);
    private final DTMFContext context;

    public DatagramChannelStreamer(DTMFContext ctx) {
        this.context = ctx;
    }

    private void init() throws Exception {
        InputStream instream =
                Main.class.getClassLoader().getResourceAsStream("log4j2.xml");

        logger.info( "The server is ready..." );

        for (Channel ch : context.getDtmfConfig().getChannel()) { // todo: new thread chain
            InetSocketAddress iAddr = initializeAddress(ch.getStreamAddress());
            logger.info("start listening on: " + iAddr);

            ByteBuffer buf = ByteBuffer.allocateDirect(188_000);

            //readableRTPChannel(iAddr);
            //Thread.sleep(20000);

            SeekableByteChannel source = readableFileChannel(iAddr);
            //UDPInputStream source = new UDPInputStream(iAddr);

            PipedOutputStream pipedOutput = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, buf.limit() * 100);
            WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

            ChannelManager chManager = new ChannelManager(ch, this.context);
            Thread demuxerThread = new Thread(new Demuxer(readableFileChannel(pipedInputStream), chManager));
            demuxerThread.start();

            while (source.read(buf) != -1) {
                buf.flip();
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
