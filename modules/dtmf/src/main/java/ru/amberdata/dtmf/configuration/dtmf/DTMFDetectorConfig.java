package ru.amberdata.dtmf.configuration.dtmf;

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
    <channel id="100" name="vesti 24">
        <streamAddress>udp://127.0.0.1:1234</streamAddress>
        <audioPID>68</audioPID>
        <audioFormat>MPEG</audioFormat>
        <dtmfChannel>auto</dtmfChannel>
        <cutoffNoiseRatio>0.46</cutoffNoiseRatio>
        <symbolLength>70</symbolLength>
        <pauseLength>70</pauseLength>
        <adBreak id="1">
            <cueTone>
                <startSymbols>1234</startSymbols>
                <stopSymbols>89</stopSymbols>
            </cueTone>
        </adBreak>
        <adBreak id="2">
            <cueTone>
                <startSymbols>123</startSymbols>
                <stopSymbols>78</stopSymbols>
            </cueTone>
        </adBreak>
        <adBreak id="3">
            <cueTone>
                <startSymbols>456</startSymbols>
            </cueTone>
        </adBreak>
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
        ch1.setName("vesti 24");
        ch1.setAudioFormat("MPEG");
        ch1.setAudioPID(68);
        ch1.setCutoffNoiseRatio(0.46);
        ch1.setDtmfChannel(DTMFChannel.auto);
        ch1.setStreamAddress("udp://127.0.0.1:1234");
        ch1.setPauseLength(70);
        ch1.setSymbolLength(70);
        ch1.setId(100);

        AdBreak ad1 = new AdBreak();
        AdBreak ad2 = new AdBreak();
        AdBreak ad3 = new AdBreak();

        CueTone ct1 = new CueTone();
        ct1.setStartSymbols("1234");
        ct1.setStopSymbols("89");

        CueTone ct2 = new CueTone();
        ct2.setStartSymbols("123");
        ct2.setStopSymbols("78");

        CueTone ct3 = new CueTone();
        ct3.setStartSymbols("456");

        ad1.setCueTone(ct1);
        ad2.setCueTone(ct2);
        ad3.setCueTone(ct3);
        ad1.setId(1);
        ad2.setId(2);
        ad3.setId(3);

        ch1.setAdBreak(Arrays.asList(ad1, ad2, ad3));

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
