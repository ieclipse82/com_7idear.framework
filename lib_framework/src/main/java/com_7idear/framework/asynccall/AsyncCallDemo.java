package com_7idear.framework.asynccall;

import com_7idear.framework.entity.FileEntity;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * 异步任务使用DEMO
 * @author ieclipse 19-12-26
 * @description 使用方法：
 * 1：先在自己的包内找到或定义API接口 {@link DemoApi}
 * 2：API接口可以使用自定义解析器（线程中回调），参照 {@link DemoConverter}，返回类型需要实现
 * 3：在项目同层中调用 AsyncCallUtils.getApi(DemoApi.class)方法执行相应的API就可以了
 * 如有问题请自学 retrofit2 使用方法
 */
public class AsyncCallDemo {

    public interface DemoApi {
        String URL_DEMO = "demo";

        @GET(URL_DEMO)
        AsyncCallAdapterFactory.ImplUrlCall<?> getDemo();

        @GET
        AsyncCallAdapterFactory.ImplUrlCall<?> getDemoGet(@Url String url);

        @GET(URL_DEMO)
        @IBaseConverter(className = DemoConverter.class)
        AsyncCallAdapterFactory.ImplUrlCall<?> getDemoGetWithConverter(@Path("path") String path,
                @Query("id") String id);

        @POST(URL_DEMO)
        AsyncCallAdapterFactory.ImplUrlCall<?> getDemoPost(@Body FileEntity body);
    }

    public class DemoConverter
            extends AsyncBaseConverter<FileEntity> {

        @Override
        protected FileEntity convert(String responseBody) {
            return null;
        }
    }
}
