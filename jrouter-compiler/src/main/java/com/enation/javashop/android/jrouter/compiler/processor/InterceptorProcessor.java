package com.enation.javashop.android.jrouter.compiler.processor;

import com.enation.javashop.android.jrouter.compiler.utils.Logger;
import com.enation.javashop.android.jrouter.external.annotation.Interceptor;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ANNOTATION_TYPE_INTECEPTOR;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.IINTERCEPTOR;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.IINTERCEPTOR_GROUP;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.KEY_MODULE_NAME;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.METHOD_LOAD_INTO;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.NAME_OF_INTERCEPTOR;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.PACKAGE_OF_GENERATE_FILE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.SEPARATOR;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 拦截器注解处理器
 */
/**自动处理*/
@AutoService(Processor.class)
/**模块标识名称 使用注解支持该项*/
@SupportedOptions(KEY_MODULE_NAME)
/**使用Java1.7*/
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**返回该工厂生成的注解处理器所能支持的注解类型,就是注解类的全包名*/
@SupportedAnnotationTypes(ANNOTATION_TYPE_INTECEPTOR)
public class InterceptorProcessor extends AbstractProcessor {

    /**注解器注解的拦截器集合*/
    private Map<Integer, Element> interceptors = new TreeMap<>();
    /**文件处理辅助类*/
    private Filer mFiler;
    /**日志类*/
    private Logger logger;
    /**获取元素操作辅助类*/
    private Elements elementUtil;
    /**模块名称*/
    private String moduleName = null;
    /**基础拦截器封装类型信息*/
    private TypeMirror iInterceptor = null;

    /**
     * 初始化
     * @param processingEnvironment 处理环境
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /**初始化文件辅助类*/
        mFiler = processingEnv.getFiler();
        /**获取元素操作辅助类*/
        elementUtil = processingEnv.getElementUtils();
        /**初始化日志打印*/
        logger = new Logger(processingEnv.getMessager());
        /**获取option字段集合，并获取该模块的名称*/
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
        }
        /**判断模块名称是否在gradle中设置，如果没设置，则提示在Gradle中设置*/
        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");
            logger.info("正在处理拦截器  模块名:[" + moduleName + "]");
        } else {
            logger.error("该模块未设置模块名, 请到模块目录下 'build.gradle', 加入以下代码 Kotlin语言加入Kapt依赖并把apt字段替换为kapt :\n" +
                    "apt {\n" +
                    "    arguments {\n" +
                    "        moduleName project.getName();\n" +
                    "    }\n" +
                    "}\n");
            throw new RuntimeException("ARouter::没有设置模块名，请到模块下的gradle文件添加配置");
        }
        /**获取基础拦截器 类型信息*/
        iInterceptor = elementUtil.getTypeElement(IINTERCEPTOR).asType();

        logger.info(">>> InterceptorProcessor 初始化.["+moduleName+"] <<<");
    }

    /**
     * 处理注解
     * @param annotations  注解集合
     * @param roundEnv     元素
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /**判断注解Set是否为空*/
        if (CollectionUtils.isNotEmpty(annotations)) {
            /**获取被Interceptor注解的元素*/
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Interceptor.class);
            try {
                /**处理元素*/
                parseInterceptors(elements);
            } catch (Exception e) {
                logger.error(e);
            }
            return true;
        }

        return false;
    }

    /**
     * 处理拦截器元素
     * @param elements      被Interceptor注解的元素集合
     * @throws IOException  输入输出异常
     */
    private void parseInterceptors(Set<? extends Element> elements) throws IOException {
        /**首先判断是否有interceotor注解的元素*/
        if (CollectionUtils.isNotEmpty(elements)) {
            logger.info(">>> 找了拦截器, 共有 " + elements.size() + "个  <<<");

            /**循环处理元素*/
            for (Element element : elements) {
                /**判断拦截器元素是否合法*/
                if (verify(element)) {
                    logger.info("拦截器验证通过: " + element.asType());
                    /**获取拦截器注解的数据*/
                    Interceptor interceptor = element.getAnnotation(Interceptor.class);
                    /**根据优先级获取拦截器*/
                    Element lastInterceptor = interceptors.get(interceptor.priority());
                    /**验证是否有多个拦截器使用相同优先级*/
                    if (null != lastInterceptor) {
                        throw new IllegalArgumentException(
                                String.format(Locale.getDefault(), "JRouter 有多个拦截器使用相同优先级 优先级:[%d], They are [%s] and [%s].",
                                        interceptor.priority(),
                                        lastInterceptor.getSimpleName(),
                                        element.getSimpleName())
                        );
                    }
                    /**放入拦截器集合  优先级为key 元素为value*/
                    interceptors.put(interceptor.priority(), element);
                } else {
                    logger.error("拦截器验证失败 : " + element.asType());
                }
            }
            /**获取基础拦截器接口类型数据*/
            TypeElement type_ITollgate = elementUtil.getTypeElement(IINTERCEPTOR);
            /**获取基础模块拦截器接口类型数据*/
            TypeElement type_ITollgateGroup = elementUtil.getTypeElement(IINTERCEPTOR_GROUP);

            /**构建Map对象类型*/
            ParameterizedTypeName inputMapTypeOfTollgate = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(Integer.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_ITollgate))
                    )
            );

            /**组合Map对象类型和参数组，组合成一个对象*/
            ParameterSpec tollgateParamSpec = ParameterSpec.builder(inputMapTypeOfTollgate, "interceptors").build();

            /**构建loadinfo方法 添加Override注解 添加Public作用域 添加Map对象参数*/
            MethodSpec.Builder loadIntoMethodOfTollgateBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(tollgateParamSpec);

            /**检测拦截器集合是否为空，不为空执行*/
            if (null != interceptors && interceptors.size() > 0) {
                /**向loadinfo方法添加代码段*/
                for (Map.Entry<Integer, Element> entry : interceptors.entrySet()) {
                    /**添加往拦截器集合添加拦截器的代码*/
                    loadIntoMethodOfTollgateBuilder.addStatement("interceptors.put(" + entry.getKey() + ", $T.class)", ClassName.get((TypeElement) entry.getValue()));
                }
            }

            /**构建成JavaFile 并写到本地 作用域Public 添加类注释 添加loadinfo方法 添加实现接口 写入到指定包下*/
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(NAME_OF_INTERCEPTOR + SEPARATOR + moduleName)
                            .addModifiers(PUBLIC)
                            .addJavadoc(WARNING_TIPS)
                            .addMethod(loadIntoMethodOfTollgateBuilder.build())
                            .addSuperinterface(ClassName.get(type_ITollgateGroup))
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> 该模块拦截器注解处理完毕 :"+moduleName+"<<<");
        }
    }

    /**
     * 判断是否是拦截器
     * @param element  需要判断的元素
     * @return         是否是拦截器
     */
    private boolean verify(Element element) {
        /**获取拦截器注解示例 获取相关信息*/
        Interceptor interceptor = element.getAnnotation(Interceptor.class);
        /**因为拦截器必须实现基础拦截器接口 所以要判断 是否实现了基础拦截器接口*/
        return null != interceptor && ((TypeElement) element).getInterfaces().contains(iInterceptor);
    }
}
