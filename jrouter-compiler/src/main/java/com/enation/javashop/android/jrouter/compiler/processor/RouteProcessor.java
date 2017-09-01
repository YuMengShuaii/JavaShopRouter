package com.enation.javashop.android.jrouter.compiler.processor;

import com.enation.javashop.android.jrouter.compiler.utils.Constant;
import com.enation.javashop.android.jrouter.compiler.utils.Logger;
import com.enation.javashop.android.jrouter.compiler.utils.TypeUtils;
import com.enation.javashop.android.jrouter.external.annotation.Autowired;
import com.enation.javashop.android.jrouter.external.annotation.Router;
import com.enation.javashop.android.jrouter.external.enums.RouterType;
import com.enation.javashop.android.jrouter.external.model.RouterModel;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ACTIVITY;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ANNOTATION_TYPE_AUTOWIRED;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ANNOTATION_TYPE_ROUTE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.FRAGMENT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.IPROVIDER_GROUP;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.IROUTE_GROUP;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ITROUTE_ROOT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.KEY_MODULE_NAME;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.METHOD_LOAD_INTO;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.NAME_OF_GROUP;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.NAME_OF_PROVIDER;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.NAME_OF_ROOT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.PACKAGE_OF_GENERATE_FILE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.SEPARATOR;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.SERVICE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**自动处理*/
@AutoService(Processor.class)
/**添加支持项*/
@SupportedOptions(KEY_MODULE_NAME)
/**制定Java支持版本*/
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**注解处理器支持的注解类型*/
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
/**
 * Router注解处理器
 */
public class RouteProcessor extends AbstractProcessor {
    /**
     * 分组Map
     */
    private Map<String, Set<RouterModel>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    /**
     * 根Map
     */
    private Map<String, String> rootMap = new TreeMap<>();  // Map of root metas, used for generate class file in order.
    /**
     * 文件处理辅助类
     */
    private Filer mFiler;       // File util, write class file into disk.
    /**
     * 日志打印
     */
    private Logger logger;
    /**
     * 类型辅助类
     */
    private Types types;
    /**
     * 元素辅助类
     */
    private Elements elements;

    /**
     * 类型判断工具类
     */
    private TypeUtils typeUtils;

    /**
     * 模块名称
     */
    private String moduleName = null;

    /**
     * 基础Provider处理接口类型信息封装
     */
    private TypeMirror iProvider = null;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        /**初始化文件辅助类*/
        mFiler = processingEnv.getFiler();
        /**获取类型辅助类*/
        types = processingEnv.getTypeUtils();
        /**获取元素辅助类*/
        elements = processingEnv.getElementUtils();
        /**初始化自定义类型工具类*/
        typeUtils = new TypeUtils(types, elements);
        /**初始化日志辅助类*/
        logger = new Logger(processingEnv.getMessager());
        /**获取支持选项*/
        Map<String, String> options = processingEnv.getOptions();
        /**判空*/
        if (MapUtils.isNotEmpty(options)) {
            /**从支持选项中拿到modulename*/
            moduleName = options.get(KEY_MODULE_NAME);
        }
        /**判断modulename是否没空*/
        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logger.info("Router注解开始处理 当前模块:[" + moduleName + "]");
        } else {
            logger.error("该模块未设置模块名, 请到模块目录下 'build.gradle', 加入以下代码 Kotlin语言加入Kapt依赖并把apt字段替换为kapt :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("ARouter::没有设置模块名，请到模块下的gradle文件添加配置");
        }
        /**根据基础Provider接口全包名 获取对应封装类型信息*/
        iProvider = elements.getTypeElement(Constant.IPROVIDER).asType();
        /**初始化完毕*/
        logger.info(">>> Router处理器 初始化完毕 <<<");
    }

    /**
     * 开始处理
     * @param annotations 注解集
     * @param roundEnv    元素集
     * @return            是否处理完毕
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /**判断注解集合是否为空*/
        if (CollectionUtils.isNotEmpty(annotations)) {
            /**获取被Router注解的元素*/
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Router.class);
            try {
                /**找到被Router注解的元素 开始处理*/
                logger.info(">>> 找到Router注解的元素 开始处理... <<<");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                /**未找到被Router注解的元素，抛出异常*/
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 处理元素
     * @param routeElements  被Router注解的集合
     * @throws IOException   输入输出异常
     */
    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        /**判断元素是否为空*/
        if (CollectionUtils.isNotEmpty(routeElements)) {

            logger.info(">>> 被Router注解的元素共有 " + routeElements.size() + "个 <<<");
            /**清空根Map*/
            rootMap.clear();
            /**获取Activity类型封装信息*/
            TypeMirror type_Activity = elements.getTypeElement(ACTIVITY).asType();
            /**获取Service类型封装信息*/
            TypeMirror type_Service = elements.getTypeElement(SERVICE).asType();
            /**获取Fragment类型封装信息*/
            TypeMirror fragmentTm = elements.getTypeElement(FRAGMENT).asType();
            TypeMirror fragmentTmV4 = elements.getTypeElement(Constant.FRAGMENT_V4).asType();

            /**创建Router组类型元素*/
            TypeElement type_IRouteGroup = elements.getTypeElement(IROUTE_GROUP);
            /**创建Provider组类型元素*/
            TypeElement type_IProviderGroup = elements.getTypeElement(IPROVIDER_GROUP);
            /**获取RouterModel类名*/
            ClassName routeMetaCn = ClassName.get(RouterModel.class);
            /**获取RouterType类名*/
            ClassName routeTypeCn = ClassName.get(RouterType.class);

            /**
              *创建Map类型 泛型为String 和Class<? extends IRouteGroup>
              * ```Map<String, Class<? extends IRouteGroup>>```
              */
            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                    )
            );

            /**
              * 创建Map类型 泛型为String和RouteMeta
              * ```Map<String, RouteMeta>```
              */
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouterModel.class)
            );

            /**
             * 使用上边创建的Map类型 构建参数
             */
            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build();
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "providers").build();

            /**
             * 构建loadinfo方法 作用域public 加入Override注解  参数为routes
             */
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(rootParamSpec);

            /**
             * 循环分组创建JavaFile
             */
            for (Element element : routeElements) {
                /**获取元素类型*/
                TypeMirror tm = element.asType();
                /**获取元素注解的参数*/
                Router route = element.getAnnotation(Router.class);
                /**创建路由数据容器*/
                RouterModel routeMete = null;
                /**判断元素是否是Activity*/
                if (types.isSubtype(tm, type_Activity)) {
                    /**类型是Activity*/
                    logger.info(">>> 找了Router注解的Activity: " + tm.toString() + " <<<");
                    /**检查元素内部是否有被Autowired注解的参数*/
                    Map<String, Integer> paramsType = new HashMap<>();
                    /**循环检查*/
                    for (Element field : element.getEnclosedElements()) {
                        /**必须是一个字段 并且被Autowired注解 不能是一个Provider*/
                        if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                            /**获取被Autowired注解的数据*/
                            Autowired paramConfig = field.getAnnotation(Autowired.class);
                            /**优先使用Autowired注解的name参数作为key，如果name为空，那么使用字段名*/
                            paramsType.put(StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() : paramConfig.name(), typeUtils.typeExchange(field));
                        }
                    }
                    /**构建Activity routerMete*/
                    routeMete = new RouterModel(route, element, RouterType.ACTIVITY, paramsType);
                } else if (types.isSubtype(tm, iProvider)) {
                    /**构建Provider routerMete*/
                    logger.info(">>> 找到Router注解的Provider: " + tm.toString() + " <<<");
                    routeMete = new RouterModel(route, element, RouterType.PROVIDER, null);
                } else if (types.isSubtype(tm, type_Service)) {
                    /**构建Service  routerMete*/
                    logger.info(">>> 找到Router注解的service: " + tm.toString() + " <<<");
                    routeMete = new RouterModel(route, element, RouterType.parse(SERVICE), null);
                } else if (types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                    /**构建Fragment routerMete*/
                    logger.info(">>> 找到Router注解的fragment: " + tm.toString() + " <<<");
                    routeMete = new RouterModel(route, element, RouterType.parse(FRAGMENT), null);
                }
                /**分组操作*/
                categories(routeMete);

            }

            /**创建loadinfo方法  加入override注解 作用域public 参数Map<String, RouteMeta> */
            MethodSpec.Builder loadIntoMethodOfProviderBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(providerParamSpec);
            /**根据组来创建JavaFile*/
            for (Map.Entry<String, Set<RouterModel>> entry : groupMap.entrySet()) {
                /**获取组名*/
                String groupName = entry.getKey();
                /**创建loadinfo方法 加入override注解 作用域public 参数Map<String, RouteMeta>*/
                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(groupParamSpec);

                /**创建组loadinfo方法体*/
                Set<RouterModel> groupData = entry.getValue();
                /**根据组内成员 循环创建代码段*/
                for (RouterModel routeMeta : groupData) {
                    /**判断成员类型*/
                    switch (routeMeta.getType()) {
                        case PROVIDER:
                            /**类型为Provider 获取成员实现的接口列表*/
                            List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getRawType()).getInterfaces();
                            /**循环添加代码段*/
                            for (TypeMirror tm : interfaces) {
                                /**判断该类是否是IProder*/
                                if (types.isSameType(tm, iProvider)) {
                                    /**创建代码段*/
                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            (routeMeta.getRawType()).toString(),
                                            routeMetaCn,
                                            routeTypeCn,
                                            ClassName.get((TypeElement) routeMeta.getRawType()),
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                    /**判断该类是否是IProder的子类型*/
                                } else if (types.isSubtype(tm, iProvider)) {
                                    /**创建代码段*/
                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            tm.toString(),    // So stupid, will duplicate only save class name.
                                            routeMetaCn,
                                            routeTypeCn,
                                            ClassName.get((TypeElement) routeMeta.getRawType()),
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    /**创建参数类型map*/
                    StringBuilder mapBodyBuilder = new StringBuilder();
                    /**获取参数类型map*/
                    Map<String, Integer> paramsType = routeMeta.getParamsType();
                    /**判断是否已存在 没有则不操作*/
                    if (MapUtils.isNotEmpty(paramsType)) {
                        for (Map.Entry<String, Integer> types : paramsType.entrySet()) {
                            mapBodyBuilder.append("put(\"").append(types.getKey()).append("\", ").append(types.getValue()).append("); ");
                        }
                    }
                    /**获取代码打不*/
                    String mapBody = mapBodyBuilder.toString();
                    /**拼接注入参数代码段*/
                    loadIntoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + (StringUtils.isEmpty(mapBody) ? null : ("new java.util.HashMap<String, Integer>(){{" + mapBodyBuilder.toString() + "}}")) + ", " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                            routeMeta.getPath(),
                            routeMetaCn,
                            routeTypeCn,
                            ClassName.get((TypeElement) routeMeta.getRawType()),
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase());
                }

                /**拼接组名*/
                String groupFileName = NAME_OF_GROUP + groupName;
                /**创建文件 作用域public 添加类注释 实现RouterGroup接口 添加创建好的loadinfo方法*/
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                        TypeSpec.classBuilder(groupFileName)
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup))
                                .addModifiers(PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build())
                                .build()
                ).build().writeTo(mFiler);

                logger.info(">>> 组处理完毕 组: " + groupName + "<<<");
                rootMap.put(groupName, groupFileName);
            }
            /**判断根map是否为空 为空则不操作*/
            if (MapUtils.isNotEmpty(rootMap)) {
                /**根据组创建代码段*/
                for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                    loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
                }
            }

            /**创建provider注入JavaFile  构建文件名*/
            String providerMapFileName = NAME_OF_PROVIDER + SEPARATOR + moduleName;
            /**添加类注释 实现BaseProviderGroup接口 作用域public 添加provder中的 loadinfo方法*/
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(providerMapFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IProviderGroup))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfProviderBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Provider处理完毕 :" + providerMapFileName + " <<<");

            /**构建根JavaFile  构建根JavaFile 文件名*/
            String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
            /**添加类注释 实现BaseRootRouter接口 作用域public 添加root中loadinfo方法*/
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(elements.getTypeElement(ITROUTE_ROOT)))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Root处理完毕 : " + rootFileName + " <<<");
        }
    }

    /**
     * 分组
     *
     * @param routeMete 路由数据
     */
    private void categories(RouterModel routeMete) {
        /**验证路径及组名是否合法*/
        if (routeVerify(routeMete)) {
            logger.info(">>> 开始分组, 组 :" + routeMete.getGroup() + ", 路径 : " + routeMete.getPath() + " <<<");
            /**从组map中根据组名获取一个组*/
            Set<RouterModel> routeMetas = groupMap.get(routeMete.getGroup());
            /**判断该组是否在组map中存在 存在则直接使用 不存在则创建*/
            if (CollectionUtils.isEmpty(routeMetas)) {
                /**组不存在 创建组*/
                Set<RouterModel> routeMetaSet = new TreeSet<>(new Comparator<RouterModel>() {
                    @Override
                    public int compare(RouterModel r1, RouterModel r2) {
                        try {
                            /**路径不可相同*/
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
                            logger.error(npe.getMessage());
                            return 0;
                        }
                    }
                });
                /**把该路由信息放入新创建的组*/
                routeMetaSet.add(routeMete);
                /**把新创建的组放入组map，key是组名*/
                groupMap.put(routeMete.getGroup(), routeMetaSet);
            } else {
                /**组已存在 直接把路由信息放入组*/
                routeMetas.add(routeMete);
            }
        } else {
            logger.warning(">>> 路由信息不合法, 位于 " + routeMete.getGroup() + "组 <<<");
        }
    }

    /**
     * 获取跟map
     * @return 跟map
     */
    public Map<String, String> getRootMap() {
        return rootMap;
    }

    /**
     * 验证RouterModel是否合法
     *
     * @param routerModel  路由数据
     */
    private boolean routeVerify(RouterModel routerModel) {
        /**获取路由路径*/
        String path = routerModel.getPath();
        /**判断path是否为空 path必须以/开头 且最少二级路径 不合法一直返回false*/
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }
        /**判断group是否为空*/
        if (StringUtils.isEmpty(routerModel.getGroup())) { // Use default group(the first word in path)
            try {
                /**如果组名为空 那么获取路径下的一级路径作为组名*/
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                /**如果一级路径为空 那么该路径不合法 返回false*/
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }
                /**一级路径合法后 设置为组名*/
                routerModel.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                logger.error("无法生成默认组名 请检查路径! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
