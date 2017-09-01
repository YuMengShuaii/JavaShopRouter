package com.enation.javashop.android.jrouter;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.enation.javashop.android.jrouter.core.RouterCenter;
import com.enation.javashop.android.jrouter.exception.JRouterException;
import com.enation.javashop.android.jrouter.exception.RouterPathNorFoundException;
import com.enation.javashop.android.jrouter.exception.UnInitException;
import com.enation.javashop.android.jrouter.logic.datainfo.Postcard;
import com.enation.javashop.android.jrouter.logic.listener.InterceptorListener;
import com.enation.javashop.android.jrouter.logic.listener.NavigationListener;
import com.enation.javashop.android.jrouter.logic.service.AutowiredService;
import com.enation.javashop.android.jrouter.logic.service.DegradeService;
import com.enation.javashop.android.jrouter.logic.service.InterceptorService;
import com.enation.javashop.android.jrouter.logic.service.PathReplaceService;
import com.enation.javashop.android.jrouter.utils.AutoPoolExecutor;
import com.enation.javashop.android.jrouter.utils.Constant;
import com.enation.javashop.android.jrouter.utils.JRouterLogger;
import com.enation.javashop.android.jrouter.utils.LoggerHelper;
import com.enation.javashop.android.jrouter.utils.TextUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * JRouter核心实现
 */

public class JRouterReal {
    /***
     * 获取日志辅助类示例
     */
    static LoggerHelper logger = new JRouterLogger().openLogHelper(true);
    /**
     * 上下文
     */
    private static Context mContext;
    /**
     * 拦截器处理服务
     */
    private static InterceptorService interceptorService;
    /**
     * 初始化标识
     */
    private volatile static boolean hasInit = false;
    /**
     * 单例
     */
    private volatile static JRouterReal instance;
    /**
     * Debug状态
     */
    private volatile static boolean debuggable = false;
    /**
     * 任务处理线程池
     */
    private volatile static ThreadPoolExecutor executor = AutoPoolExecutor.getInstance();

    /**
     * 私有构造方法 禁止初始化
     */
    private JRouterReal() {
    }

    /**
     * 初始化核心逻辑
     * @param application  application
     * @return             是否初始化完成
     */
    protected static synchronized boolean init(Application application){
        mContext = application;
        RouterCenter.init(mContext, executor);
        logger.i(Constant.LOG_TAG, "JRouter开始初始化！");
        hasInit = true;
        return true;
    }

    /**
     * 关闭JRouter
     */
    static synchronized void destroy() {
        if (debuggable()) {
            hasInit = false;
            RouterCenter.suspend();
            logger.i(Constant.LOG_TAG, "JRouter关闭成功！");
        } else {
            logger.e(Constant.LOG_TAG, "关闭操作只在调试模式使用!");
        }
    }

    /**
     * 获取调试状态
     * @return
     */
    static boolean debuggable() {
        return debuggable;
    }

    /**
     * 开启调试模式
     */
    static synchronized void openDebug() {
        debuggable = true;
        logger.i(Constant.LOG_TAG, "开启调试模式！");
    }

    /**
     * 开启日志打印
     */
    static synchronized void openLog() {
        logger.openLogHelper(true);
        logger.i(Constant.LOG_TAG, "开启Log");
    }

    /**
     * 自定义任务线程池
     * @param tpe
     */
    static synchronized void setExecutor(ThreadPoolExecutor tpe) {
        executor = tpe;
    }

    /**
     * 获取单例 双重加锁
     * @return this
     */
    protected static JRouterReal getInstance() {
        if (!hasInit) {
            throw new UnInitException("JRouter未初始化！");
        } else {
            if (instance == null) {
                synchronized (JRouterReal.class) {
                    if (instance == null) {
                        instance = new JRouterReal();
                    }
                }
            }
            return instance;
        }
    }

    /**
     * 设置自定义日志打印
     * @param userLogger
     */
    static void setLogger(LoggerHelper userLogger) {
        if (null != userLogger) {
            logger = userLogger;
        }
    }

    /**
     * 参数注入
     * @param thiz  需要执行注入的对象
     */
    static void inject(Object thiz) {
        AutowiredService autowiredService = ((AutowiredService) JRouter.prepare().create(JRouter.AUTOWIRED_SERVICE).seek());
        if (null != autowiredService) {
            autowiredService.autowire(thiz);
        }
    }

    /**
     * 初始化拦截器处理服务
     */
    static void afterInit() {
        interceptorService = (InterceptorService) JRouter.prepare().create(JRouter.INTERCEPTOR_SERVCE).seek();
    }


    /**
     * 根据路径构建Postcard
     * @param path
     * @return
     */
    protected Postcard create(String path) {
        /**判断路径是否为空*/
        if (TextUtils.isEmpty(path)) {
            throw new JRouterException(Constant.LOG_TAG + "空值异常，请检查参数！");
        } else {
            /**不为空 获取路径处理服务 进行处理路径  没有路径处理服务 不进行操作*/
            PathReplaceService pService = JRouter.prepare().seek(PathReplaceService.class);
            if (null != pService) {
                path = pService.forString(path);
            }
            return create(path, extractGroup(path));
        }
    }

    /**
     * 根据uri构建Postcard
     * @param uri  uri
     * @return
     */
    protected Postcard create(Uri uri) {
        /**判空*/
        if (null == uri || TextUtils.isEmpty(uri.toString())) {
            throw new JRouterException(Constant.LOG_TAG + "Uri异常，null");
        } else {
            /**不为空获取路径处理服务 处理uri  为空则不处理*/
            PathReplaceService pService = JRouter.prepare().seek(PathReplaceService.class);
            if (null != pService) {
                uri = pService.forUri(uri);
            }
            return new Postcard(uri.getPath(), extractGroup(uri.getPath()), uri, null);
        }
    }

    /**
     * 根据路径 组名 构建postcard
     * @param path    路径
     * @param group   组名
     * @return        postcard
     */
    protected Postcard create(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new JRouterException(Constant.LOG_TAG + "路径异常");
        } else {
            /**处理路径*/
            PathReplaceService pService = JRouter.prepare().seek(PathReplaceService.class);
            if (null != pService) {
                path = pService.forString(path);
            }
            return new Postcard(path, group);
        }
    }


    /***
     * 获取组名
     * @param path  根据path获取组名
     * @return      组名
     */
    private String extractGroup(String path) {
        /**判断path是否由/开头*/
        if (TextUtils.isEmpty(path) || !path.startsWith("/")) {
            throw new JRouterException(Constant.LOG_TAG + "路径必须以'/'开头，且最少有两级!");
        }
        /**判断路径级别*/
        try {
            String defaultGroup = path.substring(1, path.indexOf("/", 1));
            if (TextUtils.isEmpty(defaultGroup)) {
                throw new JRouterException(Constant.LOG_TAG + "路径必须以'/'开头，且最少有两级!");
            } else {
                return defaultGroup;
            }
        } catch (Exception e) {
            logger.e(Constant.LOG_TAG, "获取默认组名失败！" + e.getMessage());
            return null;
        }
    }

    /***
     * 根据接口class获取实现类
     * @param service  接口class
     * @param <T>      接口泛型
     * @return         实现类
     */
    protected <T> T seek(Class<? extends T> service) {
        try {
            Postcard postcard = RouterCenter.buildProvider(service.getName());
            /**先通过类名查找，找不到通过全包名查找*/
            if (null == postcard) { // No service, or this service in old version.
                postcard = RouterCenter.buildProvider(service.getSimpleName());
            }
            /**处理postcard*/
            RouterCenter.completion(postcard);
            /**获取实现类*/
            return (T) postcard.getProvider();
        } catch (RouterPathNorFoundException ex) {
            /**无法找到该接口实现类*/
            logger.e(Constant.LOG_TAG, ex.getMessage());
            return null;
        }
    }

    /**
     * 处理拦截器  查询目标
     * @param context        上下文
     * @param postcard       数据
     * @param requestCode    返回码
     * @param listener       监听
     * @return
     */
    protected Object seek(final Context context, final Postcard postcard, final int requestCode, final NavigationListener listener) {
        try {
            /**处理PostCard*/
            RouterCenter.completion(postcard);
        } catch (RouterPathNorFoundException ex) {
            logger.e(Constant.LOG_TAG, ex.getMessage());

            if (debuggable()) { // Show friendly tips for user.
                Toast.makeText(mContext, "页面跳转失败!\n" +
                        " 路径 ： [" + postcard.getPath() + "]\n" +
                        " 组 ： [" + postcard.getGroup() + "]", Toast.LENGTH_LONG).show();
            }

            if (null != listener) {
                /**设置跳转失败监听*/
                listener.onLost(postcard);
            } else {
                /**设置全局跳转失败处理*/
                DegradeService degradeService = JRouter.prepare().seek(DegradeService.class);
                if (null != degradeService) {
                    degradeService.onLost(context, postcard);
                }
            }

            return null;
        }
        /**设置跳转成功监听*/
        if (null != listener) {
            listener.onFound(postcard);
        }
        /**判断是否开启了绿色通道*/
        if (!postcard.isGreenChannel()) {
            interceptorService.doInterceptions(postcard, new InterceptorListener() {
                /**
                 * 继续跳转
                 */
                @Override
                public void onContinue(Postcard postcard) {
                    seekReal(context, postcard, requestCode, listener);
                }

                /**
                 * 处理拦截异常
                 */
                @Override
                public void onInterrupt(Throwable exception) {
                    if (null != listener) {
                        /**传递异常*/
                        listener.onInterrupt(postcard);
                    }

                    logger.i(Constant.LOG_TAG, "跳转失败，被拦截器拦截: " + exception.getMessage());
                }
            });
        } else {
            /**开启绿色通道直接跳转*/
            return seekReal(context, postcard, requestCode, listener);
        }
        return null;
    }


    /**
     * 处理跳转
     * @param context      上下文
     * @param postcard     数据
     * @param requestCode  返回码
     * @param callback     回调
     * @return
     */
    private Object seekReal(final Context context, final Postcard postcard, final int requestCode, final NavigationListener callback) {
        /**判断是否有使用了activityContext 没有则使用ApplicationContext*/
        final Context currentContext = null == context ? mContext : context;

        /**判断跳转类型的type*/
        switch (postcard.getType()) {
            case ACTIVITY:
                /**初始化Intent*/
                final Intent intent = new Intent(currentContext, postcard.getDestination());
                /**放入Bundle*/
                intent.putExtras(postcard.getExtras());
                /**设置Activity启动模式*/
                int flags = postcard.getFlags();
                if (-1 != flags) {
                    intent.setFlags(flags);
                } else if (!(currentContext instanceof Activity)) {    // Non activity, need less one flag.
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                /**主线程跳转*/
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        /**需要返回数据*/
                        if (requestCode > 0) {
                            ActivityCompat.startActivityForResult((Activity) currentContext, intent, requestCode, postcard.getOptionsBundle());
                            /**不需要返回数据*/
                        } else {
                            ActivityCompat.startActivity(currentContext, intent, postcard.getOptionsBundle());
                        }
                        /**设置转场动画*/
                        if ((0 != postcard.getEnterAnim() || 0 != postcard.getExitAnim()) && currentContext instanceof Activity) {    // Old version.
                            ((Activity) currentContext).overridePendingTransition(postcard.getEnterAnim(), postcard.getExitAnim());
                        }
                        /**回调监听 通知舔砖完毕*/
                        if (null != callback) { // Navigation over.
                            callback.onArrival(postcard);
                        }
                    }
                });

                break;
            case PROVIDER:
                /**返回Provider实现类*/
                return postcard.getProvider();
            case BOARDCAST:
            case CONTENT_PROVIDER:
            case FRAGMENT:
                /**获取Fragment class*/
                Class fragmentMeta = postcard.getDestination();
                try {
                    /**生成对象*/
                    Object instance = fragmentMeta.getConstructor().newInstance();
                    if (instance instanceof Fragment) {
                        /**传递数据*/
                        ((Fragment) instance).setArguments(postcard.getExtras());
                    } else if (instance instanceof android.support.v4.app.Fragment) {
                        /**传递数据*/
                        ((android.support.v4.app.Fragment) instance).setArguments(postcard.getExtras());
                    }
                    /**返回实例*/
                    return instance;
                } catch (Exception ex) {
                    logger.e(Constant.LOG_TAG, "Fragment加载失败" + TextUtils.formatStackTrace(ex.getStackTrace()));
                }
            case METHOD:
            case SERVICE:
            default:
                return null;
        }

        return null;
    }
}
