package ru.amberdata.dtmf;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.configuration.dtmf.Channel;
import ru.amberdata.dtmf.configuration.dtmf.DTMFDetectorConfig;

/**
 * Created by zhenya on 2016-12-15.
 */
public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {

        Injector injector = Guice.createInjector(new DTMFModule());
        DTMFContext ctx = injector.getInstance(DTMFContext.class);

        DTMFDetectorConfig dtmfConf = ctx.getDtmfConfig();
        logger.info("found {} channels", dtmfConf.getChannel().size());
        for (Channel ch : dtmfConf.getChannel()) {
            Thread t = new Thread(new DatagramChannelStreamer(ctx));
            t.start();
            t.join();
        }
    }

}
