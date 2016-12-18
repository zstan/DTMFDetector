package ru.amberdata.dtmf.configuration;

/**
 * Created by zhenya on 2016-12-16.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<dtmfDetectorConfig>
    <channel>
        <adBreak>
            <cueTone>
                <pauseLength>70</pauseLength>
                <startSymbols>1234</startSymbols>
                <stopSymbols>89</stopSymbols>
                <symbolLength>70</symbolLength>
            </cueTone>
        </adBreak>
        <adBreak>
            <cueTone>
                <pauseLength>70</pauseLength>
                <startSymbols>123</startSymbols>
                <stopSymbols>78</stopSymbols>
                <symbolLength>70</symbolLength>
            </cueTone>
        </adBreak>
        <audioFormat>MPEG</audioFormat>
        <audioPID>68</audioPID>
        <cutoffNoiseRatio>0.46</cutoffNoiseRatio>
        <dtmfChannel>auto</dtmfChannel>
        <name>ch name 1</name>
        <streamAddress>127.0.0.1:1234</streamAddress>
    </channel>
    <demoMode>true</demoMode>
</dtmfDetectorConfig>
*/


@XmlRootElement
public class DTMFDetectorConfig {

    private List <Channel> channel = new ArrayList<>();
    private boolean demoMode;

    @XmlElement(required=true)
    public List<Channel> getChannel() {
        return channel;
    }

    public void setChannel(List<Channel> channel) {
        this.channel = channel;
    }

    @XmlElement
    public boolean isDemoMode() {
        return demoMode;
    }

    public void setDemoMode(boolean demoMode) {
        this.demoMode = demoMode;
    }

    public static void main(String[] args) {
        DTMFDetectorConfig conf = new DTMFDetectorConfig();
        conf.setDemoMode(true);
        Channel ch1 = new Channel();
        ch1.setName("ch name 1");
        ch1.setAudioFormat("MPEG");
        ch1.setAudioPID(68);
        ch1.setCutoffNoiseRatio(0.46);
        ch1.setDtmfChannel(DTMFChannel.auto);
        ch1.setStreamAddress("127.0.0.1:1234");

        AdBreak ad1 = new AdBreak();
        AdBreak ad2 = new AdBreak();

        CueTone ct1 = new CueTone();
        ct1.setStartSymbols("1234");
        ct1.setStopSymbols("89");
        ct1.setPauseLength(70);
        ct1.setSymbolLength(70);

        CueTone ct2 = new CueTone();
        ct2.setStartSymbols("123");
        ct2.setStopSymbols("78");
        ct2.setPauseLength(70);
        ct2.setSymbolLength(70);

        ad1.setCueTone(ct1);
        ad2.setCueTone(ct2);

        ch1.setAdBreak(Arrays.asList(ad1, ad2));

        conf.setChannel(Arrays.asList(ch1));

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(DTMFDetectorConfig.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(conf, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
