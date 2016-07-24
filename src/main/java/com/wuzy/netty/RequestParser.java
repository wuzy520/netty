package com.wuzy.netty;

import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求参数解析器, 支持GET, POST
 */
public class RequestParser {
    private HttpRequest req;

    private HttpPostRequestDecoder decoder;
    private HttpContent content;

    /**
     * 构造一个解析器
     * @param req
     */
    public RequestParser(HttpRequest req) {
        this.req = req;
    }

    /**
     * 解析请求参数
     * @return 包含所有请求参数的键值对, 如果没有参数, 则返回空Map
     *
     * @throws IOException
     */
    public Map<String, String> parse() throws IOException {
        HttpMethod method = req.method();

        Map<String, String> parmMap = new HashMap<>();

        if (HttpMethod.GET == method) {
            // 是GET请求
            QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
            decoder.parameters().entrySet().forEach( entry -> {
                // entry.getValue()是一个List, 只取第一个元素
                parmMap.put(entry.getKey(), entry.getValue().get(0));
            });
        }else if (HttpMethod.POST == method) {
            // 是POST请求
            decoder = new HttpPostRequestDecoder(req);
            return null;
        }
        return parmMap;
    }

    public Map<String,String> getValue()throws IOException{
        Map<String, String> parmMap = new HashMap<>();
        decoder.offer(content);

        List<InterfaceHttpData> parmList = decoder.getBodyHttpDatas();

        for (InterfaceHttpData parm : parmList) {
            Attribute data = (Attribute) parm;
            parmMap.put(data.getName(), data.getValue());
        }

        return parmMap;

    }

    public void setContent(HttpContent content) {
        this.content = content;
    }

    public HttpPostRequestDecoder getDecoder() {
        return decoder;
    }
}