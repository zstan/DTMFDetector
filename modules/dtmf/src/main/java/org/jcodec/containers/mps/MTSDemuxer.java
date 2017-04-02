package org.jcodec.containers.mps;
import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import org.jcodec.api.NotSupportedException;
import org.jcodec.common.Assert;
import org.jcodec.common.IntObjectMap;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.common.io.SeekableByteChannel;
import org.jcodec.common.model.Packet;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.DatagramChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * This class is part of JCodec ( www.jcodec.org ) This software is distributed
 * under FreeBSD License
 * 
 * MPEG TS demuxer
 * 
 * @author The JCodec project
 * 
 */
public class MTSDemuxer implements MPEGDemuxer {
    private MPSDemuxer psDemuxer;
    private SeekableByteChannel tsChannel;

    private static volatile boolean exit = false;

    public static Set<Integer> getProgramsFromChannel(SeekableByteChannel src) throws IOException {
        // http://www.etherguidesystems.com/help/sdos/mpeg/semantics/mpeg-2/stream_type.aspx
        long rem = src.position();
        Set<Integer> guids = new HashSet<Integer>();
        for (int i = 0; guids.size() == 0 || i < guids.size() * 500; i++) {
            MTSPacket pkt = readPacket(src);
            if (pkt == null)
                break;
            if (pkt.payload == null)
                continue;
            ByteBuffer payload = pkt.payload;
            if (!guids.contains(pkt.pid) && (payload.duplicate().getInt() & ~0xff) == 0x100) {
                guids.add(pkt.pid);
            }
        }
        src.setPosition(rem);
        return guids;
    }

    public static Set<Integer> getPrograms(File file) throws IOException {
        FileChannelWrapper fc = null;
        try {
            fc = NIOUtils.readableChannel(file);
            return getProgramsFromChannel(fc);
        } finally {
            NIOUtils.closeQuietly(fc);
        }
    }

    public MTSDemuxer(final SeekableByteChannel src, int filterGuid) throws IOException {
        this.tsChannel = (new TSChannel(src, filterGuid));
        psDemuxer = new MPSDemuxer(this.tsChannel);
    }

    public List<? extends MPEGDemuxerTrack> getTracks() {
        return psDemuxer.getTracks();
    }

    public List<? extends MPEGDemuxerTrack> getVideoTracks() {
        return psDemuxer.getVideoTracks();
    }

    public List<? extends MPEGDemuxerTrack> getAudioTracks() {
        return psDemuxer.getAudioTracks();
    }

    private static class TSChannel implements SeekableByteChannel {
        private SeekableByteChannel src;
        private ByteBuffer data;
        private int filterGuid;

        public TSChannel(SeekableByteChannel source, int filterGuid) {
            this.src = source;
            this.filterGuid = filterGuid;
        }

        public boolean isOpen() {
            return src.isOpen();
        }

        public void close() throws IOException {
            src.close();
        }

        public int read(ByteBuffer dst) throws IOException {
            while (data == null || !data.hasRemaining()) {
                MTSPacket packet = getPacket(src);
                if (packet == null)
                    return -1;
                data = packet.payload;
            }
            int toRead = Math.min(dst.remaining(), data.remaining());
            dst.put(NIOUtils.read(data, toRead));
            return toRead;
        }

        public int write(ByteBuffer src) throws IOException {
            throw new NotSupportedException();
        }

        public long position() throws IOException {
            return src.position();
        }

        public SeekableByteChannel setPosition(long newPosition) throws IOException {
            src.setPosition(newPosition);
            data = null;
            return this;
        }

        public long size() throws IOException {
            return src.size();
        }

        public SeekableByteChannel truncate(long size) throws IOException {
            return src.truncate(size);
        }

        protected MTSPacket getPacket(ReadableByteChannel channel) throws IOException {
            MTSPacket pkt;
            do {
                pkt = readPacket(channel);
                if (pkt == null)
                    return null;
            } while (pkt.pid <= 0xf || pkt.pid == 0x1fff || pkt.payload == null);

            while (pkt.pid != filterGuid) {
                pkt = readPacket(channel);
                if (pkt == null)
                    return null;
            }

            return pkt;
        }
    }

    public static class MTSPacket {
        public ByteBuffer payload;
        public boolean payloadStart;
        public int pid;

        public MTSPacket(int guid, boolean payloadStart, ByteBuffer payload) {
            this.pid = guid;
            this.payloadStart = payloadStart;
            this.payload = payload;
        }
    }

    public static MTSPacket readPacket(ReadableByteChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(188);
        if (NIOUtils.readFromChannel(channel, buffer) != 188)
            return null;
        buffer.flip();
        return parsePacket(buffer);
    }

    public static MTSPacket parsePacket(ByteBuffer buffer) {

        int marker = buffer.get() & 0xff;
        Assert.assertEquals(0x47, marker);
        int guidFlags = buffer.getShort();
        int guid = (int) guidFlags & 0x1fff;
        int payloadStart = (guidFlags >> 14) & 0x1;
        int b0 = buffer.get() & 0xff;
        int counter = b0 & 0xf;
        if ((b0 & 0x20) != 0) {
            int taken = 0;
            taken = (buffer.get() & 0xff) + 1;
            NIOUtils.skip(buffer, taken - 1);
        }
        return new MTSPacket(guid, payloadStart == 1, ((b0 & 0x10) != 0) ? buffer : null);
    }

    public static int probe(final ByteBuffer b) {
        IntObjectMap<List<ByteBuffer>> streams = new IntObjectMap<List<ByteBuffer>>();
        while (true) {
            try {
                ByteBuffer sub = NIOUtils.read(b, 188);
                if (sub.remaining() < 188)
                    break;

                MTSPacket tsPkt = parsePacket(sub);
                if (tsPkt == null)
                    break;
                List<ByteBuffer> data = streams.get(tsPkt.pid);
                if (data == null) {
                    data = new ArrayList<ByteBuffer>();
                    streams.put(tsPkt.pid, data);
                }

                if (tsPkt.payload != null)
                    data.add(tsPkt.payload);
            } catch (Throwable t) {
                break;
            }
        }
        int maxScore = 0;
        int[] keys = streams.keys();
        for (int i : keys) {
            List<ByteBuffer> packets = streams.get(i);
            int score = MPSDemuxer.probe(NIOUtils.combineBuffers(packets));
            if (score > maxScore) {
                maxScore = score + (packets.size() > 20 ? 50 : 0);
            }
        }
        return maxScore;
    }

    @Override
    public void seekByte(long offset) throws IOException {
        tsChannel.setPosition(offset - (offset % 188));
        psDemuxer.reset();
    }
}