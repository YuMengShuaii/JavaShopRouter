package com.enation.javashop.android.jrouter.logic.datainfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.SparseArray;

import com.enation.javashop.android.jrouter.JRouter;
import com.enation.javashop.android.jrouter.external.model.RouterModel;
import com.enation.javashop.android.jrouter.logic.listener.NavigationListener;
import com.enation.javashop.android.jrouter.logic.service.JsonTransforService;
import com.enation.javashop.android.jrouter.logic.template.BaseProvider;
import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

/**
 * 参数Model
 */
public final class Postcard extends RouterModel {
    /**
     * Uri数据
     */
    private Uri uri;
    /**
     * tag 存储异常等
     */
    private Object tag;
    /**
     * 存储数据的Bundle
     */
    private Bundle mBundle;
    /**
     * 获取Activity启动模式
     */
    private int flags = -1;
    /**
     * 设置timeout时间
     */
    private int timeout = 300;
    /**
     * privider
     */
    private BaseProvider provider;
    /**
     * 是否开启绿色通道
     */
    private boolean greenChannel;
    /**
     * Json对象转换器
     */
    private JsonTransforService serializationService;

    /**
     * 动画效果
     */
    private Bundle optionsCompat;    // The transition animation of activity

    /**
     * 打开Activity的动画
     */
    private int enterAnim;

    /**
     * 关闭Activity的动画
     */
    private int exitAnim;

    /**
     * 获取动画bundle
     * @return
     */
    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    /**
     * 获取进入动画
     * @return  进入Activity动画
     */
    public int getEnterAnim() {
        return enterAnim;
    }

    /**
     * 获取退出Activity动画
     * @return  退出Activity的动画
     */
    public int getExitAnim() {
        return exitAnim;
    }

    /**
     * 获取Provider实例
     * @return
     */
    public BaseProvider getProvider() {
        return provider;
    }

    /**
     * 设置Provider实例
     * @param provider
     * @return
     */
    public Postcard setProvider(BaseProvider provider) {
        this.provider = provider;
        return this;
    }

    /**
     * 构造系统
     */
    public Postcard() {
        this(null, null);
    }

    /**
     * 二参构造
     * @param path   路径
     * @param group  组
     */
    public Postcard(String path, String group) {
        this(path, group, null, null);
    }

    /**
     * 四餐构造
     * @param path     路径
     * @param group    组
     * @param uri      uri
     * @param bundle   bundle
     */
    public Postcard(String path, String group, Uri uri, Bundle bundle) {
        setPath(path);
        setGroup(group);
        setUri(uri);
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }

    /***
     * 判断是否开启绿色通道
     * @return
     */
    public boolean isGreenChannel() {
        return greenChannel;
    }


    public Object getTag() {
        return tag;
    }

    public Postcard setTag(Object tag) {
        this.tag = tag;
        return this;
    }

    /**
     * 获取 Bundle
     * @return  获取单独的Bundle进行赋值
     */
    public Bundle getExtras() {
        return mBundle;
    }

    /**
     * 获取Timeout时间
     * @return
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置Timeout时间
     * @param timeout  时间
     * @return  PostCard对象
     */
    public Postcard setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public Uri getUri() {
        return uri;
    }

    public Postcard setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    /**
     * 查找
     * @return
     */
    public Object seek() {
        return seek(null);
    }


    public Object seek(Context context) {
        return seek(context, null);
    }


    public Object seek(Context context, NavigationListener callback) {
        return JRouter.prepare().seek(context, this, -1, callback);
    }

    public void seek(Activity mContext, int requestCode) {
        seek(mContext, requestCode, null);
    }


    public void seek(Activity mContext, int requestCode, NavigationListener callback) {
        JRouter.prepare().seek(mContext, this, requestCode, callback);
    }

    /**
     * 开启绿色通道 跳过所有拦截器
     * @return
     */
    public Postcard greenChannel() {
        this.greenChannel = true;
        return this;
    }

    /**
     * 使用自己的Bundle
     */
    public Postcard with(Bundle bundle) {
        if (null != bundle) {
            mBundle = bundle;
        }

        return this;
    }

    /**
     * Activity启动模式
     */
    @IntDef({
            Intent.FLAG_ACTIVITY_SINGLE_TOP,
            Intent.FLAG_ACTIVITY_NEW_TASK,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            Intent.FLAG_DEBUG_LOG_RESOLUTION,
            Intent.FLAG_FROM_BACKGROUND,
            Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT,
            Intent.FLAG_ACTIVITY_CLEAR_TASK,
            Intent.FLAG_ACTIVITY_CLEAR_TOP,
            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS,
            Intent.FLAG_ACTIVITY_FORWARD_RESULT,
            Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY,
            Intent.FLAG_ACTIVITY_MULTIPLE_TASK,
            Intent.FLAG_ACTIVITY_NO_ANIMATION,
            Intent.FLAG_ACTIVITY_NO_USER_ACTION,
            Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP,
            Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED,
            Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
            Intent.FLAG_ACTIVITY_TASK_ON_HOME,
            Intent.FLAG_RECEIVER_REGISTERED_ONLY
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface FlagInt {
    }

    /**
     * 设置Activity启动模式
     * @param flag     模式
     * @return
     */
    public Postcard withFlags(@FlagInt int flag) {
        this.flags = flag;
        return this;
    }

    /**
     * 获取Activity启动模式
     * @return
     */
    public int getFlags() {
        return flags;
    }

    /**
     * 传输对象    使用Json转换器转换为String传出 Json专辑机制可以自己实现
     * @param key
     * @param value
     * @return
     */
    public Postcard withObject(@Nullable String key, @Nullable Object value) {
        serializationService = JRouter.prepare().seek(JsonTransforService.class);
        mBundle.putString(key, serializationService.object2Json(value));
        return this;
    }


    /**
     * 传输String
     * @param key   a String
     * @param value a boolean
     * @return current
     */
    public Postcard withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }

    /**
     * 传输布尔
     * @param key   a String, or null
     * @param value a boolean
     * @return current
     */
    public Postcard withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }

    /**
     * 传输短整型
     * @param key   a String, or null
     * @param value a short
     * @return current
     */
    public Postcard withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }

    /**
     * 传输整型
     * @param key   a String, or null
     * @param value an int
     * @return current
     */
    public Postcard withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }

    /**
     * 传输长整型
     * @param key   a String, or null
     * @param value a long
     * @return current
     */
    public Postcard withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }

    /**
     * 传输Double
     * @param key   a String, or null
     * @param value a double
     * @return current
     */
    public Postcard withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }

    /**
     * 传输字节型
     * @param key   a String, or null
     * @param value a byte
     * @return current
     */
    public Postcard withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }

    /**
     * 传输字符型
     * @param key   a String, or null
     * @param value a char
     * @return current
     */
    public Postcard withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }

    /**
     * 传输单精度浮点型
     * @param key   a String, or null
     * @param value a float
     * @return current
     */
    public Postcard withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }

    /**
     * 传输字符序列
     * @param key   a String, or null
     * @param value a CharSequence, or null
     * @return current
     */
    public Postcard withCharSequence(@Nullable String key, @Nullable CharSequence value) {
        mBundle.putCharSequence(key, value);
        return this;
    }

    /**
     * 可序列化数据
     * @param key   a String, or null
     * @param value a Parcelable object, or null
     * @return current
     */
    public Postcard withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }

    /**
     * 传输序列化数据数组
     * @param key   a String, or null
     * @param value an array of Parcelable objects, or null
     * @return current
     */
    public Postcard withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    /**
     * 传输序列化数据集合
     * @param key   a String, or null
     * @param value an ArrayList of Parcelable objects, or null
     * @return current
     */
    public Postcard withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    /**
     * 传输SparseArray
     * @param key   a String, or null
     * @param value a SparseArray of Parcelable objects, or null
     * @return current
     */
    public Postcard withSparseParcelableArray(@Nullable String key, @Nullable SparseArray<? extends Parcelable> value) {
        mBundle.putSparseParcelableArray(key, value);
        return this;
    }

    /**
     * 传输整型集合
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    /**
     * 传输字符串集合
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    /**
     * 传输字符序列集合
     * @param key   a String, or null
     * @param value an ArrayList object, or null
     * @return current
     */
    public Postcard withCharSequenceArrayList(@Nullable String key, @Nullable ArrayList<CharSequence> value) {
        mBundle.putCharSequenceArrayList(key, value);
        return this;
    }

    /**
     * 传输序列化对象
     * @param key   a String, or null
     * @param value a Serializable object, or null
     * @return current
     */
    public Postcard withSerializable(@Nullable String key, @Nullable Serializable value) {
        mBundle.putSerializable(key, value);
        return this;
    }

    /**
     * 传输字节数据
     * @param key   a String, or null
     * @param value a byte array object, or null
     * @return current
     */
    public Postcard withByteArray(@Nullable String key, @Nullable byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }

    /**
     * 传输短整型数组
     * @param key   a String, or null
     * @param value a short array object, or null
     * @return current
     */
    public Postcard withShortArray(@Nullable String key, @Nullable short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }

    /**
     * 传输字符数据
     * @param key   a String, or null
     * @param value a char array object, or null
     * @return current
     */
    public Postcard withCharArray(@Nullable String key, @Nullable char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }

    /**
     * 传输单精度浮点型数组
     * @param key   a String, or null
     * @param value a float array object, or null
     * @return current
     */
    public Postcard withFloatArray(@Nullable String key, @Nullable float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }

    /**
     * 传输字符序列数组
     * @param key   a String, or null
     * @param value a CharSequence array object, or null
     * @return current
     */
    public Postcard withCharSequenceArray(@Nullable String key, @Nullable CharSequence[] value) {
        mBundle.putCharSequenceArray(key, value);
        return this;
    }

    /**
     * 传输自己的bundle
     * @param key   a String, or null
     * @param value a Bundle object, or null
     * @return current
     */
    public Postcard withBundle(@Nullable String key, @Nullable Bundle value) {
        mBundle.putBundle(key, value);
        return this;
    }

    /**
     * 添加转场动画
     * @param enterAnim enter
     * @param exitAnim  exit
     * @return current
     */
    public Postcard withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    /**
     * 添加转场动画
     *
     */
    @RequiresApi(16)
    public Postcard withOptionsCompat(ActivityOptionsCompat compat) {
        if (null != compat) {
            this.optionsCompat = compat.toBundle();
        }
        return this;
    }


    @Override
    public String toString() {
        return "Postcard{" +
                "uri=" + uri +
                ", tag=" + tag +
                ", mBundle=" + mBundle +
                ", flags=" + flags +
                ", timeout=" + timeout +
                ", provider=" + provider +
                ", greenChannel=" + greenChannel +
                ", optionsCompat=" + optionsCompat +
                ", enterAnim=" + enterAnim +
                ", exitAnim=" + exitAnim +
                "}\n" +
                super.toString();
    }
}
