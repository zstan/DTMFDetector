package ru.amberdata.dtmf;

import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.configuration.AdBreak;
import ru.amberdata.dtmf.configuration.Channel;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhenya on 2016-12-15.
 */
public class DTMFDetector implements Runnable {

    private static final Logger logger = LogManager.getLogger(DTMFDetector.class);
    private final InputStream source;
    private final Channel ch;
    private DTMFUtil dtmf;

    public DTMFDetector(InputStream in, Channel ch) {
        this.source = in;
        this.ch = ch;
    }

    @Override
    public void run() {
        logger.info("DTMFDetector initialize");
        try {
            dtmf = new DTMFUtil(source);

            logger.info("DTMFDetector start decode");
            dtmf.decode();
            String[] sequence = dtmf.getDecoded();

            if (dtmf.getChannelCount() == 1) {
                System.out.println("The DTMF tones found in the given file are: " + sequence[0]);
            } else {
                System.out.println("The DTMF tones found in channel one are: " + sequence[0]
                        + "\nThe DTMF tones found in channel two are: " + sequence[1]);
            }

        } catch (IOException | AudioFileException | DTMFDecoderException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        logger.info("DTMFDetector close");
    }
}
