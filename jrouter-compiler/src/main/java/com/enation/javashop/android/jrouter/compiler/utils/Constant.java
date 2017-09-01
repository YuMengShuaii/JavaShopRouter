package com.enation.javashop.android.jrouter.compiler.utils;

/**
 * 常量池
 */
public class Constant {
    //===================== System interface
    /**
     *  Activity包名路径
     */
    public static final String ACTIVITY = "android.app.Activity";
    /**
     * Fragment包名路径
     */
    public static final String FRAGMENT = "android.app.Fragment";
    public static final String FRAGMENT_V4 = "android.support.v4.app.Fragment";
    /**
     * Service 包名路径
     */
    public static final String SERVICE = "android.app.Service";
    /**
     * Parcelable包名路径  序列化使用
     */
    public static final String PARCELABLE = "android.os.Parcelable";

    // ===================Java type
    /**
     *  Java中的lang包
     */
    private static final String LANG = "java.lang";
    /**
     * byte包名路径
     */
    public static final String BYTE = LANG + ".Byte";
    /**
     * Short包名路径
     */
    public static final String SHORT = LANG + ".Short";
    /**
     * Integer包名路径
     */
    public static final String INTEGER = LANG + ".Integer";
    /**
     * Long包名路径
     */
    public static final String LONG = LANG + ".Long";
    /**
     * Float包名路径
     */
    public static final String FLOAT = LANG + ".Float";
    /**
     * Double包名路径
     */
    public static final String DOUBEL = LANG + ".Double";
    /**
     * Boolean包名路径
     */
    public static final String BOOLEAN = LANG + ".Boolean";
    /**
     * String包名路径
     */
    public static final String STRING = LANG + ".String";

    // =================================Generate
    /**
     * 拼接间隔符
     */
    public static final String SEPARATOR = "$$";
    /**
     * 项目名
     */
    public static final String PROJECT = "JRouter";
    /**
     * 标签
     */
    public static final String TAG = PROJECT + "::";
    /**
     * apt生成类注释
     */
    public static final String WARNING_TIPS = "不要修改该文件，该文件由apt自动生成！";
    /**
     * 初始化路由方法名  反射调用
     */
    public static final String METHOD_LOAD_INTO = "loadInto";
    /**
     * 初始化页面参数方法名  反射调用
     */
    public static final String METHOD_INJECT = "inject";
    /**
     * 底层注入类二级名称
     */
    public static final String NAME_OF_ROOT = PROJECT + SEPARATOR + "Root";
    /**
     * 服务注入类二级名称
     */
    public static final String NAME_OF_PROVIDER = PROJECT + SEPARATOR + "Providers";
    /**
     * 组注入类二级名称
     */
    public static final String NAME_OF_GROUP = PROJECT + SEPARATOR + "Group" + SEPARATOR;
    /**
     * 拦截器注入类二级名称
     */
    public static final String NAME_OF_INTERCEPTOR = PROJECT + SEPARATOR + "Interceptors";
    /**
     * 参数注入类二级名称
     */
    public static final String NAME_OF_AUTOWIRED = SEPARATOR + PROJECT + SEPARATOR + "Autowired";
    /**
     * 生成注入类所在的文件夹
     */
    public static final String PACKAGE_OF_GENERATE_FILE = "com.javashop.android.jrouter";
    /**
     * 基础注入接口 预制服务接口 所在的包
     */
    private static final String FACADE_PACKAGE = "com.enation.javashop.android.jrouter.logic";
    /**
     * 基础注入接口所在的包
     */
    private static final String TEMPLATE_PACKAGE = ".template";
    /**
     * 预制服务接口所在的包
     */
    private static final String SERVICE_PACKAGE = ".service";
    /**
     * 基础服务接口全包名
     */
    public static final String IPROVIDER = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseProvider";
    /**
     * 基础模块服务接口全包名
     */
    public static final String IPROVIDER_GROUP = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseProviderModule";
    /**
     * 基础拦截器接口全包名
     */
    public static final String IINTERCEPTOR = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseInterceptor";
    /**
     * 基础模块拦截器接口全包名
     */
    public static final String IINTERCEPTOR_GROUP = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseInterceptorModule";
    /**
     * 模块基础注入接口
     */
    public static final String ITROUTE_ROOT = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseRouteRoot";
    /**
     * 模块路由注入接口
     */
    public static final String IROUTE_GROUP = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseRouteModule";
    /**
     * 参数注入基础接口
     */
    public static final String ISYRINGE = FACADE_PACKAGE + TEMPLATE_PACKAGE + ".BaseSyringe";
    /**
     * Json转换基础服务接口
     */
    public static final String JSON_SERVICE = FACADE_PACKAGE + SERVICE_PACKAGE + ".JsonTransforService";

    /**
     * LogTag
     */
    static final String PREFIX_OF_LOGGER = PROJECT + "::Compiler ";

    /**
     * gradle模块标识字段名
     */
    public static final String KEY_MODULE_NAME = "moduleName";

    /**
     *  注解处理类所在的包
     */
    public static final String ANNOTATION_PACKAGE = "com.enation.javashop.android.jrouter.external";
    /**
     * 拦截器注解全报名
     */
    public static final String ANNOTATION_TYPE_INTECEPTOR = ANNOTATION_PACKAGE + ".annotation.Interceptor";
    /**
     * 路由注解全包名
     */
    public static final String ANNOTATION_TYPE_ROUTE = ANNOTATION_PACKAGE + ".annotation.Router";
    /**
     * 参数注解全包名
     */
    public static final String ANNOTATION_TYPE_AUTOWIRED = ANNOTATION_PACKAGE + ".annotation.Autowired";
}
