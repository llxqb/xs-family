package com.bhxx.xs_family.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.android.pc.ioc.app.Ioc;
import com.android.pc.util.Handler_SharedPreferences;
import com.bhxx.xs_family.rongcloud.CustomerUserInfoProvider;
import com.bhxx.xs_family.utils.CrashUtils;
import com.bhxx.xs_family.utils.SPUtils;
import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.socialize.PlatformConfig;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.OkHttpClient;

public class App extends Application {
    public static App app;

    @Override
    public void onCreate() {
        app = this;
        Ioc.getIoc().init(this);
        super.onCreate();
        /**
         * OnCreate 会被多个进程重入，这段保护代码，确保只有您需要使用 RongIM 的进程和 Push 进程执行了 init。
         * io.rong.push 为融云 push 进程名称，不可修改。
         */
        if (getApplicationInfo().packageName
                .equals(getCurProcessName(getApplicationContext()))
                || "io.rong.push"
                .equals(getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第一步 初始化
             */
            RongIM.init(this);
            RongIM.setUserInfoProvider(new CustomerUserInfoProvider(), true);
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15000L, TimeUnit.MILLISECONDS)
                .readTimeout(15000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        initImageLoader(getApplicationContext());
        // 程序启动，完成初始化操作
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
        // 异常捕捉
//        new CrashUtils().init();

        //友盟各个平台的配置
        {
            //微信
            PlatformConfig.setWeixin("wx360c5f574dd75f91", "50d392fc4456ba5627d45be6dd6a2d22");
            //QQ空间
            PlatformConfig.setQQZone("1105710840", "alHRy75DvDFZb3eT");
        }

        /**
         * 家长端监控sdk
         */
        MCRSDK.init();
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        VMSNetSDK.init(this);

//        //极光推送，建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
//        Set<String> set = new HashSet<>();
//        set.add("aaa");//名字任意，可多添加几个
//        JPushInterface.setTags(this, set, null);//设置标签

    }

    private void initImageLoader(Context context) {

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheOnDisk(true).cacheInMemory(true)
                .bitmapConfig(Config.RGB_565).considerExifParams(true).build();

        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "xsfamily/cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                // 缓存图片的大小
                .memoryCacheExtraOptions(720, 1080)
                // 线程池数量
                .threadPoolSize(3)
                // 线程的优先等级
                .threadPriority(Thread.NORM_PRIORITY - 2)
                // 缓存图片在内存
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // 内存缓存大小
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                // 内存缓存最大值
                .memoryCacheSize(2 * 1024 * 1024)
                // SD卡缓存最大值
                .diskCacheSize(60 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO).diskCache(new UnlimitedDiscCache(cacheDir))
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 3000)).writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * 建立与融云服务器的连接
     * 获得当前进程的名字
     *
     * @param context
     * @return 进程号
     */
    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    /**
     * 数据存储到本地数据库
     *
     * @param key
     * @param value
     * @return void
     */
    public void setData(String key, String value) {
        Handler_SharedPreferences.WriteSharedPreferences("Cache", key, value);
    }

    /**
     * 取出本地数据
     *
     * @param key
     * @return String
     */
    public String getData(String key) {
        return Handler_SharedPreferences.getValueByName("Cache", key, Handler_SharedPreferences.STRING).toString();
    }

    /**
     * 删除一条本地数据
     *
     * @param key
     * @return String
     */
    public void deleteData(String key) {
        Handler_SharedPreferences.removeSharedPreferences("Cache", key);
    }

    /**
     * 写入对象
     *
     * @param key
     * @param o
     */
    public void saveObjData(String key, Object o) {
        Handler_SharedPreferences.saveObject("Cache", key, o);
    }

    /**
     * 读取对象
     *
     * @param key
     */
    public Object readObjData(String key) {
        return Handler_SharedPreferences.readObject("Cache", key);
    }

    /**
     * 删除本地数据文件
     *
     * @return String
     */
    public void clearDatabase(String dataBaseName) {
        Handler_SharedPreferences.ClearSharedPreferences(dataBaseName);
    }
}
