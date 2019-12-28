package com_7idear.framework.asynccall;

import android.content.Context;

import com_7idear.framework.intface.IFormat;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.net.NetUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 异步任务拦截器
 * @author ieclipse 19-12-18
 * @description
 */
public abstract class AsyncCallInterceptor
        implements Interceptor, IFormat {

    private final Context mContext;

    protected AsyncCallInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain)
            throws IOException {
        Request originalRequest = chain.request();

        Request newRequest = appendCommonParams(originalRequest);

        logRequest(newRequest);

        Response response = proceed(chain, newRequest);

        logResponse(response);

        return response;
    }

    protected void logRequest(Request request) {
        if (!AsyncCallUtils.getApi().isLog()) return;
        new LogEntity().append("Request").append("url", request.url()).toLogD();
    }


    protected void logResponse(Response response) {
        if (!AsyncCallUtils.getApi().isLog()) return;
        if (response == null) {
            new LogEntity().append("Response", "null").toLogW();
        } else {
            new LogEntity().append("Response")
                           .appendLine("url", response.request().url())
                           .append("code", response.code())
                           .append("message", response.message())
                           .append("contentLength", response.body().contentLength())
                           .append("contentType", response.body().contentType())
                           .append("protocol", response.protocol())
                           .toLogD();
            //TODO
            //            try {
            //                String body = response.body().string();
            //                int line = 0, s, e;
            //                new LogEntity().appendLine("Response", "body").toLogD();
            //                while (true) {
            //                    s = 2048 * line;
            //                    e = 2048 * (line + 1) > body.length() ? body.length() : 2048 * (line + 1) + 1;
            //                    new LogEntity().appendLine(body.substring(s, e)).toLogD();
            //                    if (e == body.length()) break;
            //                    line++;
            //                }
            //            } catch (IOException e) {
            //                e.printStackTrace();
            //            }
        }
    }

    private Request appendCommonParams(Request request) {
        HttpUrl.Builder builder = HttpUrl.parse(request.url().toString()).newBuilder();
        Request newRequest = request.newBuilder()
                                    .url(appendCommonParams(builder).toString())
                                    .build();
        return newRequest;
    }

    private Response proceed(Chain chain, Request newRequest)
            throws IOException {
        try {
            return chain.proceed(newRequest);
        } catch (UnknownHostException e) {
            if (NetUtils.isNetworkConnected(mContext)) {
                if (AsyncCallUtils.getApi().isLog())
                    new LogEntity().append("Response", "ERRCODE_UNKNOWN_HOST").toLogW();
                throw new AsyncCallException(AsyncCallException.ERRCODE_UNKNOWN_HOST);
            } else {
                if (AsyncCallUtils.getApi().isLog())
                    new LogEntity().append("Response", "ERRCODE_UNKNOWN_NETWORK").toLogW();
                throw new AsyncCallException(AsyncCallException.ERRCODE_UNKNOWN_NETWORK);
            }
        } catch (MalformedURLException e) {
            if (AsyncCallUtils.getApi().isLog())
                new LogEntity().append("Response", "ERRCODE_MALFORMED_URL").toLogW();
            throw new AsyncCallException(AsyncCallException.ERRCODE_MALFORMED_URL);
        } catch (SocketTimeoutException e) {
            if (AsyncCallUtils.getApi().isLog())
                new LogEntity().append("Response", "ERRCODE_SOCKET_TIMEOUT").toLogW();
            throw new AsyncCallException(AsyncCallException.ERRCODE_SOCKET_TIMEOUT);
        } catch (ProtocolException e) {
            if (AsyncCallUtils.getApi().isLog())
                new LogEntity().append("Response", "ERRCODE_PROTOCOL").toLogW();
            throw new AsyncCallException(AsyncCallException.ERRCODE_PROTOCOL);
        } catch (IOException e) {
            if (AsyncCallUtils.getApi().isLog())
                new LogEntity().append("Response", "ERRCODE_IOEXCEPTION").toLogW();
            throw new AsyncCallException(AsyncCallException.ERRCODE_IOEXCEPTION);
        }
    }

    public abstract HttpUrl.Builder appendCommonParams(HttpUrl.Builder builder);
}
