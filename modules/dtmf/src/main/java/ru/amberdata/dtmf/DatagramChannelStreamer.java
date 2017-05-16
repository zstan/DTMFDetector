package ru.amberdata.dtmf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;
import ru.amberdata.dtmf.configuration.dtmf.Channel;
import ru.amberdata.dtmf.io.RTPChannelWrapperNew;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;
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
                DatagramChannelStreamer.class.getClassLoader().getResourceAsStream("log4j2.xml");

        logger.info( "The server is ready..." );

        for (Channel ch : context.getDtmfConfig().getChannel()) {
            InetSocketAddress iAddr = initializeAddress(ch.getStreamAddress());
            logger.info("start listening on: " + iAddr);

            ByteBuffer buf = ByteBuffer.allocate(188_000);

            SeekableByteChannel source = null;

            source = readableFileChannel(iAddr, ch.getProtocol());
            //source = new RTPChannelWrapperNew();

            //UDPInputStream source = new UDPInputStream(iAddr);

            PipedOutputStream pipedOutput = new PipedOutputStream();
            PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, buf.limit() * 100);

            ChannelManager chManager = new ChannelManager(ch, this.context);
            Thread demuxerThread = new Thread(new Demuxer(readableFileChannel(pipedInputStream), chManager), "Demuxer");
            demuxerThread.start();

            while (source.read(buf) != -1) {
                buf.flip();
                byte[] bb = new byte[buf.remaining()];
                System.arraycopy(buf.array(), 0, bb, 0, bb.length);
                pipedOutput.write(bb);

                while (pipedInputStream.available() > (buf.limit() * 100) - 188_000)
                    Thread.sleep(500);
            }
            demuxerThread.interrupt();
            pipedOutput.close();
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
