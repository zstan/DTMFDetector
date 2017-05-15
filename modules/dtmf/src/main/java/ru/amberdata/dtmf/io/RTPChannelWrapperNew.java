package ru.amberdata.dtmf.io;

import amber.net.RTP.Packets.RTPPacket;
import amber.net.RTP.Packets.RTP_actionListener;
import amber.net.RTP.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhenya on 2017-05-11.
 */
public class RTPChannelWrapperNew implements SeekableByteChannel, RTP_actionListener {

    private static final Logger logger = LogManager.getLogger(RTPChannelWrapperNew.class);
    private Session rtpSession;
    private long position = 0;

    private AtomicLong summary = new AtomicLong();

    private int BUFFER_SIZE = 18_800_000;

    PipedOutputStream pipedOutput = new PipedOutputStream();
    PipedInputStream pipedInputStream = new PipedInputStream(pipedOutput, BUFFER_SIZE);
    //WritableByteChannel pipedChannel = Channels.newChannel(pipedOutput);

    public RTPChannelWrapperNew() throws IOException {
        rtpSession = new Session (  "234.5.6.7",    //  MulticastGroupIPAddress
                8000,           //  MulticastGroupPort
                8001,           //  RTCPGroupPort
                8051,           //  RTPSendFromPort
                8052,           //  RTCPSendFromPort
                10000           //  bandwidth
        );
        rtpSession.setPayloadType (33);
        rtpSession.StartRTPReceiverThread();
        rtpSession.addRTP_actionListener (this);
    }

    @Override
    public long position() throws IOException {
        logger.warn("DatagramChannelWrapper not supported: position " + position);
        return position;
    }

    @Override
    public SeekableByteChannel setPosition(long newPosition) throws IOException {
        logger.warn("DatagramChannelWrapper not supported: setPosition " + newPosition);
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
        byte[] bb = new byte[dst.array().length];
/*        while (pipedInputStream.available() < 188)
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        //byte[] bb = new byte[188];
        int size = pipedInputStream.read(bb);
        //System.err.println("pipedInputStream: " + bb[0]);
        //dst.put(bb);
        //System.err.println(bb);
        //System.err.println("read0: " + size + " summary: " + summary.getAndAdd(-size));
        dst.put(bb, 0, size);

        int pos = dst.position();
        position += pos;

        //System.err.println("read: " + pos);

        return pos > 0 ? pos : -1;
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

    @Override
    public void handleRTPEvent(RTPPacket pkt) {
/*        System.out.println ( "**** ActionListener RTP: *****\n"
                + "CSRC: " + pkt.CSRCCount + "\n"
                + "Seq No: " + pkt.SequenceNumber + "\n"
                + "Timestmp: " + pkt.TimeStamp + "\n"
                + "SSRC: " + Long.toHexString(pkt.SSRC) + "\n"
                + "Data Length: " + pkt.data.length + "\n"
                + "Data: {" + new String(pkt.data) + "}" + "\n"
                + "\n*******************************\n");*/

        try {
            //System.err.println("pipedChannel write: " + summary.getAndAdd(pkt.data.length));
/*            while (summary.get() > 60_000)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            pipedOutput.write(pkt.data);
            //System.err.println("pipedOutput.write: " + pkt.data.length + " " + pkt.data[0]);
            //Thread.sleep(50);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            RTPChannelWrapperNew w = new RTPChannelWrapperNew();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
