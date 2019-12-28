package com_7idear.framework.asynccall;

import android.content.Context;
import android.util.SparseArray;

import com_7idear.framework.config.AsyncCallConfig;
import com_7idear.framework.intface.IFormat;
import com_7idear.framework.intface.IThreadPool;
import com_7idear.framework.log.LogEntity;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;

/**
 * 异步任务工具类
 * @author ieclipse 19-12-18
 * @description getApi方法获取相关API接口实例，API接口样式参见{@link IDemoApi}
 */
public class AsyncCallUtils<In, Out>
        extends AsyncCallConfig {

    private static final String TAG = "AsyncCallApi";

    private static AsyncCallUtils mInstance;

    private final SparseArray                                               mApiArray;
    private final HashMap<Integer, List<WeakReference<AsyncBaseCall<Out>>>> mContextMap;
    private final ThreadPoolExecutor                                        mExecutor;

    private Retrofit mRetrofit;

    public AsyncCallUtils() {
        mApiArray = new SparseArray();
        mContextMap = new HashMap<>();
        mExecutor = new ThreadPoolExecutor(IThreadPool.CORE_POOL_SIZE,
                IThreadPool.MAXIMUM_POOL_SIZE, IThreadPool.KEEP_ALIVE, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(IThreadPool.CAPACITY), new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);

            public Thread newThread(Runnable r) {
                return new Thread(r, TAG + IFormat._3 + count.getAndIncrement());
            }
        });
    }

    @Override
    protected boolean init(Retrofit.Builder builder) {
        if (builder == null) return false;
        mRetrofit = builder.build();
        return true;
    }

    public static AsyncCallUtils getApi() {
        if (mInstance == null) {
            synchronized (AsyncCallUtils.class) {
                if (mInstance == null) mInstance = new AsyncCallUtils();
            }
        }
        return mInstance;
    }

    /**
     * 获取相关的API实例
     * @param cls API接口类
     * @param <T> 类型
     * @return
     */
    public static <T> T getApi(Class<T> cls) {
        Object obj = getApi().loadApi(cls.hashCode());
        if (obj == null) {
            obj = getApi().createApi(cls);
            getApi().saveApi(cls.hashCode(), obj);
        }
        return (T) obj;
    }

    /**
     * 创建API
     * @param service 接口对象
     * @param <T>     输入类型
     * @return
     */
    private <T> T createApi(Class<T> service) {
        return mRetrofit == null ? null : mRetrofit.create(service);
    }

    /**
     * 加载API
     * @param hashCode 标识
     * @return
     */
    private Object loadApi(int hashCode) {
        return mApiArray.get(hashCode);
    }

    /**
     * 保存API
     * @param hashCode 标识
     * @param obj      API对象
     */
    private void saveApi(int hashCode, Object obj) {
        mApiArray.put(hashCode, obj);
    }

    /**
     * 添加任务
     * @param context 环境对象
     * @param call    任务
     * @return
     */
    protected synchronized boolean addCall(Context context, AsyncBaseCall<Out> call) {
        if (call == null) return false;
        final int code = context.hashCode();
        List<WeakReference<AsyncBaseCall<Out>>> list = mContextMap.get(code);
        if (list == null) {
            list = new LinkedList<>();
            mContextMap.put(code, list);
        }
        if (list.contains(call)) {
            return false;
        } else {
            list.add(new WeakReference<AsyncBaseCall<Out>>(call));
            new LogEntity().append("call", call).toLogD();
        }
        return true;
    }

    /**
     * 移除任务
     * @param context 环境对象
     * @param call    任务
     * @return
     */
    protected synchronized boolean removeCall(Context context, AsyncBaseCall<Out> call) {
        if (call == null) return false;
        final int code = context.hashCode();
        List<WeakReference<AsyncBaseCall<Out>>> list = mContextMap.get(code);
        if (list != null) {
            list.remove(call);
        }
        call.cancel();
        new LogEntity().append("call", call).toLogD();
        return true;
    }

    /**
     * 清除任务
     * @param context 环境对象
     */
    public synchronized void clearCall(Context context) {
        final int code = context.hashCode();
        int count = 0;
        synchronized (mContextMap) {
            List<WeakReference<AsyncBaseCall<Out>>> list = mContextMap.get(code);
            if (list != null) {
                count = list.size();
                for (WeakReference<AsyncBaseCall<Out>> call : list) {
                    cancelCall(call != null ? call.get() : null);
                }
                list.clear();
            }
            mContextMap.remove(code);
        }
        new LogEntity().append("context", context).append("count", count).toLogD();
    }

    /**
     * 清除全部任务
     */
    public synchronized void clearAllCall() {
        synchronized (mContextMap) {
            Collection<List<WeakReference<AsyncBaseCall<Out>>>> allList = mContextMap.values();
            if (allList != null) {
                for (List<WeakReference<AsyncBaseCall<Out>>> list : allList) {
                    if (list != null) {
                        for (WeakReference<AsyncBaseCall<Out>> call : list) {
                            cancelCall(call != null ? call.get() : null);
                        }
                    }
                }
            }
            mContextMap.clear();
        }
        new LogEntity().toLogD();
    }

    /**
     * 取消任务
     * @param call 任务
     */
    private void cancelCall(AsyncBaseCall<Out> call) {
        if (call != null) {
            call.cancel();
            new LogEntity().append("call", call).toLogD();
        }
    }

    /**
     * 执行后台任务
     * @param context  环境对象
     * @param entity   输入对象
     * @param callback 回调
     */
    public Call<Out> runBackgroundCall(Context context, final In entity,
            IBackgroundCallback<In, Out> callback) {
        return new AsyncBackgroundCall<In, Out>(mRetrofit.callbackExecutor(), mExecutor, null,
                isLog()).runBackgroundCall(context, entity, callback);
    }


    /**
     * 例子接口
     */
    public interface IDemoApi {
        String URL_DEMO = "demo";

        @GET(URL_DEMO)
        AsyncCallAdapterFactory.ImplUrlCall<?> getDemo();
    }

}
