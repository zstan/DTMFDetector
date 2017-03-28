package ru.amberdata.dtmf.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.InetSocketAddress;

/**
 * Created by zhenya on 2016-12-16.
 */
public class Utils {
    private static final Logger logger = LogManager.getLogger(Utils.class);

    public static <T> T importConfiguration(final String resFileName, Class _class) {
        try {
            File file = new File(resFileName);
            JAXBContext jaxbContext = JAXBContext.newInstance(_class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T out = (T) jaxbUnmarshaller.unmarshal(file);
            return out;

        } catch (JAXBException e) {
            logger.error(e);
        }
        return null;
    }

    public static InetSocketAddress initializeAddress(String addr) throws Exception {
        int port = -1;
        try {
            port = Integer.parseInt(addr);
            return new InetSocketAddress(port);
        } catch (NumberFormatException e) {
            String[] hostPort = addr.split(":");
            if (hostPort.length != 2)
                throw new Exception ("unacceptable streamAddress param: " + addr);
            return new InetSocketAddress(hostPort[0], Integer.parseInt(hostPort[1]));
        }
    }

}
