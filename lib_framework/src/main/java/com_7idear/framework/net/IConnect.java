package com_7idear.framework.net;

/**
 * 联网接口
 * @author ieclipse 19-12-13
 * @description 支持GET，POST，UPLOAD
 */
public interface IConnect {

    /** HTTP */
    String HTTP                   = "http:";
    /** HTTPS */
    String HTTPS                  = "https:";
    /** 联网GET常量 */
    String GET                    = "GET";
    /** 联网POST常量 */
    String POST                   = "POST";
    /** Header类型——Content-Type */
    String HEADER_CONTENT_TYPE    = "Content-Type";
    /** Header类型——Accept-Encoding */
    String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    /** Header类型——RANGE */
    String HEADER_RANGE           = "RANGE";
    /** Header值——text/xml */
    String CONTENT_TYPE_TEXTXML   = "text/xml";
    /** Header值——gzip,deflate */
    String ACCEPT_ENCODING_GZIP   = "gzip,deflate";
    /** 数据类型——GZIP */
    String CONTENTENCODING_GZIP   = "GZIP";
    /** 数据类型——XML */
    String CONTENTENCODING_XML    = "XML";
    /** 编码——UTF-8 */
    String ENCODE_UTF8            = "UTF-8";

    /** 方式——1：METHOD_URL_GET */
    int METHOD_URL_GET             = 1;
    /** 方式——2：METHOD_URL_POST */
    int METHOD_URL_POST            = 2;
    /** 方式——3：METHOD_CLIENT_GET */
    int METHOD_CLIENT_GET          = 3;
    /** 方式——4：METHOD_CLIENT_POST */
    int METHOD_CLIENT_POST         = 4;
    /** 方式——5：METHOD_CLIENT_UPLOAD */
    int METHOD_CLIENT_UPLOAD       = 5;
    /** 方式——11：METHOD_HTTPS_URL_GET */
    int METHOD_HTTPS_URL_GET       = 11;
    /** 方式——12：METHOD_HTTPS_URL_POST */
    int METHOD_HTTPS_URL_POST      = 12;
    /** 方式——13：METHOD_HTTPS_CLIENT_GET */
    int METHOD_HTTPS_CLIENT_GET    = 13;
    /** 方式——14：METHOD_HTTPS_CLIENT_POST */
    int METHOD_HTTPS_CLIENT_POST   = 14;
    /** 方式——15：METHOD_HTTPS_CLIENT_UPLOAD */
    int METHOD_HTTPS_CLIENT_UPLOAD = 15;

    /** 返回码——302：跳转 */
    int CODE_302 = 302;
    /** 返回码——404：未找到网址 */
    int CODE_404 = 404;
    /** 返回码——200：成功 */
    int CODE_200 = 200;
    /** 返回码——201：成功（创建） */
    int CODE_201 = 201;
    /** 返回码——206：成功（部分） */
    int CODE_206 = 206;


}
