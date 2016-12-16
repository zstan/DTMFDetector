import com.tino1b2be.audio.AudioFileException;
import com.tino1b2be.dtmfdecoder.DTMFUtil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by zhenya on 2016-11-26.
 */
public class App {

    /**
     * File name for the audio file to be decoded
     */
    private static String filename;

    /**
     * A program that decodes DTMF tones within a supported audio file.
     *
     * @param args 1st argument is the filename. 2nd argument is the minimum tone duration of the tones (0 or a negative number to use the ITU-T recommended duration)
     * @throws Exception
     * @throws AudioFileException
     *
     */
    interface O {

    }

    static class A implements O {
        int i = 10;
    }

    static class B implements O {
        int i = 10;
    }

    public static void main(String[] args) throws AudioFileException, Exception {

        A a = new A();
        A b = new A();

        System.out.println(a==b);

        if (args.length == 2) {
            filename = args[0];
            DTMFUtil.setMinToneDuration(Integer.parseInt(args[1]));
        }

        FileInputStream fi = new FileInputStream(new File(filename).getAbsolutePath());
        BufferedInputStream bufferedStream = new BufferedInputStream(fi);

        //DTMFUtil dtmf = new DTMFUtil(filename);
        DTMFUtil dtmf = new DTMFUtil(bufferedStream);
        dtmf.decode();
        String[] sequence = dtmf.getDecoded();

        bufferedStream.close();
        fi.close();

        if (dtmf.getChannelCount() == 1) {
            System.out.println("The DTMF tones found in the given file are: " + sequence[0]);
        } else {
            System.out.println("The DTMF tones found in channel one are: " + sequence[0]
                    + "\nThe DTMF tones found in channel two are: " + sequence[1]);
        }
    }

}
