package ru.amberdata.dtmf;

import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFDecoderException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhenya on 2016-12-15.
 */
public class DTMFDetector implements Runnable {

    private InputStream source;

    public DTMFDetector(InputStream in) {
        this.source = in;
    }

    @Override
    public void run() {
        System.out.println("DTMFDetector initialize");
        try {
            DTMFUtil dtmf = new DTMFUtil(source);
            dtmf.decode();
            System.out.println("DTMFDetector start decode");
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
        System.out.println("DTMFDetector close");
    }
}
