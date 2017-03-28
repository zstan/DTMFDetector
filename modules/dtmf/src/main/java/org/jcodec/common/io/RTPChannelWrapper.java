package org.jcodec.common.io;

/*import com.biasedbit.efflux.packet.DataPacket;
import com.biasedbit.efflux.participant.RtpParticipant;
import com.biasedbit.efflux.participant.RtpParticipantInfo;
import com.biasedbit.efflux.session.RtpSession;
import com.biasedbit.efflux.session.RtpSessionDataListener;
import com.biasedbit.efflux.session.SingleParticipantSession;
import com.sun.media.rtp.RTPSessionMgr;
import org.jboss.netty.util.Timeout;*/

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import javax.media.ControllerListener;
import javax.media.MediaLocator;
import javax.media.control.BufferControl;
import javax.media.format.AudioFormat;
import javax.media.protocol.DataSource;
import javax.media.rtp.*;
import javax.media.rtp.event.*;
import javax.media.rtp.rtcp.SourceDescription;
//import org.jboss.netty.util.Timeout;

/**
 * Created by zhenya on 2017-01-29.
 */
public class RTPChannelWrapper  implements SeekableByteChannel {

    private DatagramChannel ch;
    private long position = 0;
    //private SessionAddress localAddr;
    private ARTPReceiver rtpRec;

    public RTPChannelWrapper(InetSocketAddress iAddr) throws IOException {
        //localAddr = new SessionAddress(iAddr.getAddress(), iAddr.getPort());
        //this.ch = DatagramChannel.open().bind(iAddr);



        //rtpRec = new ARTPReceiver(iAddr);

/*        RtpParticipant local1 = RtpParticipant.createReceiver(new RtpParticipantInfo(1), "127.0.0.1", 1234, 1236);
        RtpParticipant remote1 = RtpParticipant.createReceiver(new RtpParticipantInfo(), "127.0.0.1", 1235, 1235);

        SingleParticipantSession session1 = new SingleParticipantSession("Session1", 8, local1, remote1);
        session1.init();
        session1.addDataListener(new RtpSessionDataListener() {
            @Override
            public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
                System.err.println("Session 1 received packet: " + packet + "(session: " + session.getId() + ")");
            }
        });

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.err.println("done");*/
/*
        RtpParticipant local2 = RtpParticipant.createReceiver(new RtpParticipantInfo(2), "127.0.0.1", 7000, 7001);
        RtpParticipant remote2 = RtpParticipant.createReceiver(new RtpParticipantInfo(1), "127.0.0.1", 6000, 6001);
        SingleParticipantSession session2 = new SingleParticipantSession("Session2", 8, local2, remote2);
        session2.init();
        session2.addDataListener(new RtpSessionDataListener() {
            @Override
            public void dataPacketReceived(RtpSession session, RtpParticipantInfo participant, DataPacket packet) {
                System.err.println("Session 2 received packet: " + packet + "(session: " + session.getId() + ")");
            }
        });

        DataPacket packet = new DataPacket();
        packet.setData(new byte[]{0x45, 0x45, 0x45, 0x45});
        packet.setSequenceNumber(1);
        session1.sendDataPacket(packet);
        packet.setSequenceNumber(2);
        session2.sendDataPacket(packet);
        */
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
        position += dst.position();

        return dst.position() > 0? rem : -1;
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

    class ARTPReceiver implements ReceiveStreamListener, SessionListener {
        // vlc.exe" -vvv " DTMF.ts" --start-time=130 --sout #rtp{dst=192.168.88.235,port=1234,mux=ts}
        RTPManager mgr = null;
        boolean dataReceived = false;
        Object dataSync = new Object();
        //RTPSessionMgr mgr1 = null;

        public ARTPReceiver(InetSocketAddress iAddr) throws IOException, InvalidSessionAddressException {
/*            mgr1 = new RTPSessionMgr();

            mgr1.addFormat(new AudioFormat(AudioFormat.MPEG_RTP, 44100, 4, 1), 18);

            mgr1.addReceiveStreamListener(this);
            SessionAddress sa = new SessionAddress(InetAddress.getLocalHost(), iAddr.getPort());
            mgr1.initSession(new SessionAddress(), new SourceDescription[]{}, 0.05, 0.25);
            mgr1.startSession(sa, 1000, null);
            System.err.println("!2");*/
                    /*
            this.mgr = RTPManager.newInstance();
            System.err.println("try to open RTP session for addr: " + iAddr);
            this.mgr.initialize(new SessionAddress(iAddr.getAddress() InetAddress.getLocalHost(), iAddr.getPort()));
            System.err.println("Open RTP session for addr: " + InetAddress.getLocalHost());
            this.mgr.addReceiveStreamListener(this);
            this.mgr.

            BufferControl bc = (BufferControl) this.mgr.getControl("javax.media.control.BufferControl");
            if (bc != null)
                bc.setBufferLength(188*10);
            */
            this.initialize();
        }

        protected boolean initialize() {

            long then = System.currentTimeMillis();
            long waitingPeriod = 600000;  // wait for a maximum of 30 secs.

            try {
                synchronized (dataSync) {
                    while (!dataReceived &&
                            System.currentTimeMillis() - then < waitingPeriod) {
                        if (!dataReceived)
                            System.err.println("  - Waiting for RTP data to arrive...");
                        dataSync.wait(1000);
                    }
                }
            } catch (Exception e) {
            }

            if (!dataReceived) {
                System.err.println("No RTP data was received.");
                //close();
                return false;
            }

            return true;
        }


        /**
         * ReceiveStreamListener
         */
        @Override
        public void update(ReceiveStreamEvent evt) {

            System.err.println("!!" + evt);

            RTPManager mgr = (RTPManager) evt.getSource();
            Participant participant = evt.getParticipant();    // could be null.
            ReceiveStream stream = evt.getReceiveStream();  // could be null.

            System.err.println("11" + stream);

            if (evt instanceof RemotePayloadChangeEvent) {

                //System.err.println("  - Received an RTP PayloadChangeEvent.");
                //System.err.println("Sorry, cannot handle payload change.");
                System.exit(0);

            } else if (evt instanceof NewReceiveStreamEvent) {

                try {
                    stream = ((NewReceiveStreamEvent) evt).getReceiveStream();
                    DataSource ds = stream.getDataSource();

                    // Find out the formats.
                    RTPControl ctl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
                    if (ctl != null) {
                        ;//System.err.println("  - Recevied new RTP stream: " + ctl.getFormat());
                    } else
                        ;//System.err.println("  - Recevied new RTP stream");

                    if (participant == null)
                        ;//System.err.println("      The sender of this stream had yet to be identified.");
                    else {
                        ;//System.err.println("      The stream comes from: " + participant.getCNAME());
                    }

                    // Notify intialize() that a new stream had arrived.
                    synchronized (dataSync) {
                        dataReceived = true;
                        dataSync.notifyAll();
                    }

                } catch (Exception e) {
                    System.err.println("NewReceiveStreamEvent exception " + e.getMessage());
                    return;
                }

            } else if (evt instanceof StreamMappedEvent) {

                if (stream != null && stream.getDataSource() != null) {
                    DataSource ds = stream.getDataSource();
                    // Find out the formats.
                    RTPControl ctl = (RTPControl) ds.getControl("javax.media.rtp.RTPControl");
                    //System.err.println("  - The previously unidentified stream ");
                    if (ctl != null)
                        ;//System.err.println("      " + ctl.getFormat());
                    //System.err.println("      had now been identified as sent by: " + participant.getCNAME());
                }
            } else if (evt instanceof ByeEvent) {

                System.err.println("  - Got \"bye\" from: " + participant.getCNAME());
            }

        }

        @Override
        public void update(SessionEvent sessionEvent) {
            if (sessionEvent instanceof NewParticipantEvent) {
                Participant p = ((NewParticipantEvent)sessionEvent).getParticipant();
                System.err.println("  - A new participant had just joined: " + p.getCNAME());
            } else {
                System.err.println("sessionEvent: " + sessionEvent.getClass().getName());
            }
        }
    }
}
