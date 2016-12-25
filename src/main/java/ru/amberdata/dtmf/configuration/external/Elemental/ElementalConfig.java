package ru.amberdata.dtmf.configuration.external.Elemental;

import ru.amberdata.dtmf.configuration.external.IExternalConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhenya on 2016-12-24.
 */
@XmlRootElement
public class ElementalConfig implements IExternalConfig {
    private List<Channel> channel = new ArrayList<>();
    private String moduleType;

    @XmlElement(required=true)
    public List<Channel> getChannel() {
        return channel;
    }

    public void setChannel(List<Channel> channel) {
        this.channel = channel;
    }

    @XmlElement(required=true)
    @Override
    public String getModuleType() {
        return moduleType;
    }

    @Override
    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public static void main(String[] args) {
        ElementalConfig mgmtConfig = new ElementalConfig();
        mgmtConfig.setModuleType("1");

        Channel ch1 = new Channel();
        Channel ch2 = new Channel();

        AdBreak ab1 = new AdBreak();
        AdBreak ab2 = new AdBreak();
        AdBreak ab3 = new AdBreak();

        CueTone ct1 = new CueTone();
        CueTone ct2 = new CueTone();
        CueTone ct3 = new CueTone();

        ct1.setId(1); ct1.setCommandTimeShift(145); ct1.setCueTimeShift(8000);
        ct2.setId(1); ct2.setCommandTimeShift(143); ct2.setCueTimeShift(4000);
        ct3.setId(1); ct3.setCommandTimeShift(141); ct3.setCueTimeShift(2000);

        ab1.setId(1); ab1.setCueTone(ct1);
        ab2.setId(2); ab2.setCueTone(ct2);
        ab3.setId(3); ab3.setCueTone(ct3);

        ch1.setId(1);
        ch1.setName("channel 1");
        ch1.setServerAddress("127.0.0.1");
        ch1.setExternalID(100);
        ch1.setUserName("admin");
        ch1.setAuthKey("some_auth_key1");
        ch1.setAdBreak(Arrays.asList(ab1, ab2));

        ch2.setId(2);
        ch2.setName("channel 2");
        ch2.setServerAddress("127.0.0.2");
        ch2.setExternalID(200);
        ch2.setUserName("admin");
        ch2.setAuthKey("some_auth_key2");
        ch2.setAdBreak(Arrays.asList(ab3));

        mgmtConfig.setChannel(Arrays.asList(ch1, ch2));

        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(ElementalConfig.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(mgmtConfig, System.out);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
