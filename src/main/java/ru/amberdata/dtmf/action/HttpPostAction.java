package ru.amberdata.dtmf.action;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.amberdata.dtmf.ChannelManager;
import ru.amberdata.dtmf.configuration.dtmf.Channel;
import ru.amberdata.dtmf.configuration.external.Elemental.AdBreak;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.security.MessageDigest;

/**
 * Created by zhenya on 2016-12-17.
 */
public class HttpPostAction implements Action, AutoCloseable {

    private static final Logger logger = LogManager.getLogger(ChannelManager.class);
    private static String POST_STR = "http://%s/channels/%d/live_events/cue_point";
    private static String XML_STR = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<cue_point>\n" +
            "    <event_id>%d</event_id>\n" +
            "    <splice_offset>%d</splice_offset>\n" +
            "</cue_point>";
    private int currentDay = LocalDateTime.now().getDayOfYear();
    private static final String EXPIRES = "0";

    ThreadLocal<HttpClient> client = new ThreadLocal<HttpClient>() {
        @Override
        public HttpClient initialValue() {
            return new HttpClient();
        }
    };

    ThreadLocal<Integer> counter = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };

    private int getCounter() {
        int day = LocalDateTime.now().getDayOfYear();
        if (day != currentDay) {
            counter.set(0);
            currentDay = day;
        }
        int id = counter.get();
        counter.set(id + 1);
        return id;
    }

    private String formPostXML(Channel ch, Integer adId) {
        Optional<AdBreak> adBreak = ch.getExternalChannel().getAdBreak().stream().filter(s -> s.getId() == adId).findFirst();
        if (!adBreak.isPresent()) {
            logger.error("can`t find adID: {} in wired config", adId);
            ch.getExternalChannel().getAdBreak().forEach(logger::error);
            return "";
        } else {
            Integer spliceOffset = adBreak.get().getCueTone().getCueTimeShift();
            String str2Send = String.format(XML_STR, this.getCounter(), spliceOffset);
            logger.debug("send:\n{}", str2Send);
            return str2Send;
        }
    }

    private String formMD5(Channel ch) {
        //md5(api_key + md5(url + X-Auth-User + api_key + X-Auth_expires))
        try {
            String uri = ch.getExternalChannel().getServerAddress();
            String apiKey = ch.getExternalChannel().getAuthKey();
            String user = ch.getExternalChannel().getUserName();
            String summary = uri + user + apiKey + EXPIRES;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] dig = md5.digest(summary.getBytes());
            dig = md5.digest((uri + new String(dig)).getBytes());
            return new String(dig);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e);
        }
        return "";
    }

    @Override
    public boolean apply(Channel ch, Integer adId) {
        String uri = ch.getExternalChannel().getServerAddress();
        int id = ch.getId();
        uri = String.format(POST_STR, uri, id);
        logger.info("try to send POST into: {}", uri);
        PostMethod postMethod = new PostMethod(uri);
        postMethod.addRequestHeader("X-Auth-User", ch.getExternalChannel().getUserName());
        postMethod.addRequestHeader("X-Auth-Expires", EXPIRES);
        postMethod.addRequestHeader("X-Auth-Key", this.formMD5(ch));
        postMethod.addRequestHeader("Accept", "application/xml");
        postMethod.addRequestHeader("Content-type", "application/xml");
        RequestEntity re = null;
        try {
            re = new StringRequestEntity(this.formPostXML(ch, adId), "text/xml", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e);
        }
        postMethod.setRequestEntity(re);
        try {
            client.get().executeMethod(postMethod);
        } catch (IOException e) {
            logger.error(e);
        }
        postMethod.releaseConnection();
        return true;
    }

    @Override
    public void close() throws IOException { // todo ?
    }
}
