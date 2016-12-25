package ru.amberdata.dtmf.configuration.external;

import org.junit.Test;
import ru.amberdata.dtmf.configuration.external.Elemental.AdBreak;
import ru.amberdata.dtmf.configuration.external.Elemental.Channel;
import ru.amberdata.dtmf.configuration.external.Elemental.CueTone;
import ru.amberdata.dtmf.configuration.external.Elemental.ElementalConfig;

import static org.junit.Assert.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by zhenya on 2016-12-24.
 */
public class ExternalConfigTest {

    static final String ManagementConfigXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
            "<elementalConfig>\n"+
            "    <channel id=\"1\" name=\"channel 1\">\n"+
            "        <serverAddress>127.0.0.1</serverAddress>\n"+
            "        <userName>admin</userName>\n"+
            "        <authKey>some_auth_key1</authKey>\n"+
            "        <externalID>100</externalID>\n"+
            "        <adBreak id=\"1\">\n"+
            "            <cueTone id=\"1\" commandTimeShift=\"145\" cueTimeShift=\"8000\"/>\n"+
            "        </adBreak>\n"+
            "        <adBreak id=\"2\">\n"+
            "            <cueTone id=\"1\" commandTimeShift=\"143\" cueTimeShift=\"4000\"/>\n"+
            "        </adBreak>\n"+
            "    </channel>\n"+
            "    <channel id=\"2\" name=\"channel 2\">\n"+
            "        <serverAddress>127.0.0.2</serverAddress>\n"+
            "        <userName>admin</userName>\n"+
            "        <authKey>some_auth_key2</authKey>\n"+
            "        <externalID>200</externalID>\n"+
            "        <adBreak id=\"3\">\n"+
            "            <cueTone id=\"1\" commandTimeShift=\"141\" cueTimeShift=\"2000\"/>\n"+
            "        </adBreak>\n"+
            "    </channel>\n"+
            "    <moduleType>1</moduleType>\n"+
            "</elementalConfig>\n";

    @Test
    public void configTest() {
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

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            jaxbMarshaller.marshal(mgmtConfig, output);

            String compare = new String(output.toByteArray(), StandardCharsets.UTF_8);

            assertEquals(compare, ManagementConfigXML);

        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
