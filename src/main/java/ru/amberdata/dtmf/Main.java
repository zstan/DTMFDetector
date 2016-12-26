package ru.amberdata.dtmf;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static org.jcodec.common.io.NIOUtils.readableFileChannel;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new DTMFModule());
        DTMFContext ctx = injector.getInstance(DTMFContext.class);

        Thread t = new Thread(new DatagramChannelStreamer(ctx));
        t.start();
        t.join();
    }

}
