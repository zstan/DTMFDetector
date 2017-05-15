package ru.amberdata.dtmf.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jcodec.common.io.SeekableByteChannel;
import ru.amberdata.dtmf.configuration.dtmf.Channel;

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

    public static final int RTP_PACKET_HEADER_LENGTH = 12;

    private static final Logger logger = LogManager.getLogger(DatagramChannelWrapper.class);
    private DatagramChannel ch;
    private long position = 0;
    private final Channel.DTMFProtocol schema;

    public DatagramChannelWrapper(InetSocketAddress iAddr, Channel.DTMFProtocol schema) throws IOException {
        this.ch = DatagramChannel.open().bind(iAddr);
        this.schema = schema;
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

    private static boolean validateRTPPacketHeader(byte[] packet)
    {

        /*PT = (byte) ((buf[1] & 0xff) & 0x7f);
        SeqNo =(short)((buf[2] << 8) | ( buf[3] & 0xff)) ;
        TimeStamp =(((buf[4] & 0xff) << 24) | ((buf[5] & 0xff) << 16) | ((buf[6] & 0xff) << 8) | (buf[7] & 0xff)) ;
        SSRC = (((buf[8] & 0xff) << 24) | ((buf[9] & 0xff) << 16) | ((buf[10] & 0xff) << 8) | (buf[11] & 0xff));*/

        boolean versionValid = false;
        //boolean PayloadTypeValid = false;

        // +-+-+-+-+-+-+-+-+
        // |V=2|P|X|   CC  |
        // +-+-+-+-+-+-+-+-+

        // Version MUST be 2
        if ( ( (packet[0] & 0xC0) >> 6 ) == 2 )
            versionValid = true;
        else
            versionValid = false;

        // +-+-+-+-+-+-+-+-+
        // |M|     PT      |
        // +-+-+-+-+-+-+-+-+
        //  0 1 0 1 1 0 0 0

        // Payload Type must be the same as the session's

        //System.err.println("payload: " + (packet [1] & 0x7F));

/*        if ( ( packet [1] & 0x7F ) == Session.getPayloadType() )
            PayloadTypeValid = true;
        else
            PayloadTypeValid = false;*/

        return versionValid;
    }

    private static void clearFromRtpHeaders(ByteBuffer dest) {

        //assert dest.position() % 200 == 0 : dest.position();

        dest.flip();

        byte[] tmp = new byte[dest.remaining()];

        byte[] buff = new byte[dest.remaining() - RTP_PACKET_HEADER_LENGTH];

        dest.get(tmp);

        if (validateRTPPacketHeader(tmp)) {
            System.arraycopy(tmp, RTP_PACKET_HEADER_LENGTH, buff, 0, buff.length);
        }
        else {
            logger.error("probably non rtp packet found or unsupported version");
        }

        dest.clear();
        dest.put(buff);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        dst.clear();
        //while (dst.hasRemaining())
            ch.receive(dst);
        int pos = dst.position();
        if (this.schema == Channel.DTMFProtocol.RTP)
            clearFromRtpHeaders(dst);
        position += pos;

        return pos > 0 ? pos : -1;
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
