package ru.amberdata.dtmf;

import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhenya on 2016-12-15.
 */
public class DTMFDetector implements Runnable {

    private static final Logger logger = LogManager.getLogger(DTMFDetector.class);
    private InputStream source;

    public DTMFDetector(InputStream in) {
        this.source = in;
    }

    @Override
    public void run() {
        logger.info("DTMFDetector initialize");
        try {
            DTMFUtil dtmf = new DTMFUtil(source);
            DTMFUtil.setMinToneDuration(70);
            logger.info("DTMFDetector start decode");
            dtmf.decode();
            String[] sequence = dtmf.getDecoded();

            if (dtmf.getChannelCount() == 1) {
                System.out.println("The DTMF tones found in the given file are: " + sequence[0]);
            } else {
                System.out.println("The DTMF tones found in channel one are: " + sequence[0]
                        + "\nThe DTMF tones found in channel two are: " + sequence[1]);
            }

        } catch (IOException | UnsupportedAudioFileException | AudioFileException | DTMFDecoderException e) {
            e.printStackTrace();
        }
        logger.info("DTMFDetector close");
    }
}
