package ru.amberdata.dtmf.action;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * Created by zhenya on 2016-12-17.
 */
public class HttpPostAction implements Action {

    private HttpClient client = new HttpClient();

    public HttpPostAction() {

    }

    public boolean postData(String data) {
        HttpMethod method = new PostMethod();
        return true;
    }
}
