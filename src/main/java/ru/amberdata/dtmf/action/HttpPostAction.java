package ru.amberdata.dtmf.action;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.ChannelManager;
import ru.amberdata.dtmf.configuration.dtmf.Channel;

import java.io.IOException;
import java.util.function.Function;

/**
 * Created by zhenya on 2016-12-17.
 */
public class HttpPostAction implements Action {

    private static final Logger logger = LogManager.getLogger(ChannelManager.class);
    private static String POST_STR = "http://%s/channels/%d/live_events/cue_point";
    private static String XML_STR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<cue_point>\n" +
            "    <event_id>%d</event_id>\n" +
            "    <splice_offset>%d</splice_offset>\n" +
            "</cue_point>";

    ThreadLocal<HttpClient> client = new ThreadLocal<HttpClient>() {
        @Override
        public HttpClient initialValue() {
            return new HttpClient();
        }
    };

    private static String formPostXML(ru.amberdata.dtmf.configuration.external.Elemental.Channel ch) {
        return String.format(XML_STR, ch.getId(), ch.)
    }

    @Override
    public boolean apply(Channel ch) {
        String uri = ch.getExternalChannel().getServerAddress();
        int id = ch.getId();
        uri = String.format(POST_STR, uri, id);
        logger.info("try to send POST into: {}", uri);
        HttpMethod method = new PostMethod(uri);
        try {
            client.get().executeMethod(method);
        } catch (IOException e) {
            logger.error(e);
        }
        return true;
    }
}
