package com.seezoon.boot.common.http;

import com.aliyun.oss.ServiceException;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class UrlBuilder {

    public static String build(String url, Map<String, String> params) {
        try {
            URIBuilder builder = new URIBuilder(url);
            builder.setParameters(getNameValuePair(params));
            return builder.toString();
        } catch (URISyntaxException e) {
            throw new ServiceException(e);
        }
    }

    private static List<NameValuePair> getNameValuePair(Map<String, String> params) {
        List<NameValuePair> list = new ArrayList<NameValuePair>();
        if (null != params && !params.isEmpty()) {
            for (Entry<String, String> entry : params.entrySet()) {
                list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return list;
    }

}
