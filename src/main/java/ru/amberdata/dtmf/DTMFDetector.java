package ru.amberdata.dtmf;

import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.configuration.dtmf.Channel;
import ru.amberdata.dtmf.configuration.external.ExternalConfig;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhenya on 2016-12-15.
 * based on github.com/tino1b2be/DTMF-Decoder
 */
public class DTMFDetector implements Runnable {

    private static final Logger logger = LogManager.getLogger(DTMFDetector.class);
    private final InputStream source;
    private DTMFUtil dtmf;
    private static int HEADER_SIZE = 1000;
    private final ChannelManager chManager;

    public DTMFDetector(InputStream in, ChannelManager chManager) {
        this.source = in;
        this.chManager = chManager;
    }

    @Override
    public void run() {
        logger.info("DTMFDetector initialize");
        try {
            while (source.available() < HEADER_SIZE)
                Thread.sleep(100);
            dtmf = new DTMFUtil(source);
            this.chManager.initDtmf(dtmf);

            logger.info("DTMFDetector start decode");
            dtmf.decode();
            String[] sequence = dtmf.getDecoded();

            if (dtmf.getChannelCount() == 1) {
                System.out.println("The DTMF tones found in the given file are: " + sequence[0]);
            } else {
                System.out.println("The DTMF tones found in channel one are: " + sequence[0]
                        + "\nThe DTMF tones found in channel two are: " + sequence[1]);
            }

        } catch (IOException | AudioFileException | DTMFDecoderException | UnsupportedAudioFileException | InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("DTMFDetector close");
    }
}
