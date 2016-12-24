package ru.amberdata.dtmf;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Main {



    public static void main(String[] args) throws Exception {
        Thread t = new Thread(new DatagramChannelStreamer());
        t.start();
        t.join();
    }

}
