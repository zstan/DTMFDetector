package org.jcodec.common.io;

/**
 * Created by zstan on 20.12.16.
 */
/*
Copyright 2007 Creare Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.io.InputStream;
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class UDPInputStream extends InputStream {

    private static final int PACKET_BUFFER_SIZE = 5000;

    DatagramSocket dsock = null;
    DatagramPacket dpack = null;

    byte[] ddata = new byte[PACKET_BUFFER_SIZE];
    int packSize = 0;
    int packIdx = 0;

    int value;


    public UDPInputStream() {}

    public UDPInputStream(String address, int port)
            throws UnknownHostException, SocketException {

        open(address, port);
    }

    public UDPInputStream(InetSocketAddress iAddr) throws SocketException {
        open(iAddr);
    }

    public void open(String address, int port)
            throws UnknownHostException, SocketException {

        dsock = new DatagramSocket(port, InetAddress.getByName(address));
    }

    public void open(InetSocketAddress iAddr) throws SocketException {
        dsock = new DatagramSocket(iAddr);
    }

    public void close() throws IOException {
        dsock.close();
        dsock = null;
        ddata = null;
        packSize = 0;
        packIdx = 0;
    }

    public int available() throws IOException {
        return packSize - packIdx;
    }

    public int read() throws IOException {
        if (packIdx == packSize) {
            receive();
        }

        value = ddata[packIdx] & 0xff;
        packIdx++;
        return value;
    }

    public int read(byte[] buff) throws IOException {
        return read(buff, 0, buff.length);
    }

    public int read(ByteBuffer dst) throws IOException {
        dst.clear();
        byte[] buff = new byte[dst.remaining()];
        int size = read(buff, 0, buff.length);
        dst.put(buff);
        return size;
    }

    public int read(byte[] buff, int off, int len) throws IOException {
        if (packIdx == packSize) {
            receive();
        }

        int lenRemaining = len;

        while(available() < lenRemaining) {
            System.arraycopy(ddata,
                    packIdx,
                    buff,
                    off + (len - lenRemaining),
                    available());
            lenRemaining -= available();
            receive();
        }

        System.arraycopy(ddata,
                packIdx,
                buff,
                off + (len - lenRemaining),
                lenRemaining);
        packIdx += lenRemaining;
        return len;
    }

    public long skip(long len) throws IOException {
        if (packIdx == packSize) {
            receive();
        }

        long lenRemaining = len;

        while(available() < lenRemaining) {
            lenRemaining -= available();
            receive();
        }

        packIdx += (int) lenRemaining;
        return len;
    }

    private void receive() throws IOException {
        dpack = new DatagramPacket(ddata, PACKET_BUFFER_SIZE);
        dsock.receive(dpack);
        packIdx = 0;
        packSize = dpack.getLength();
    }

    /********* marking and reseting are unsupported ********/
    public void mark(int readlimit) {}

    public void reset() throws IOException {
        throw new IOException("Marks are not supported by UDPInputStream.");
    }

    public boolean markSupported() {
        return false;
    }
}

