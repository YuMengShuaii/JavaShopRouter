package com.enation.javashop.android.jrouter.compiler.processor;

import com.enation.javashop.android.jrouter.compiler.utils.Constant;
import com.enation.javashop.android.jrouter.compiler.utils.Logger;
import com.enation.javashop.android.jrouter.compiler.utils.TypeUtils;
import com.enation.javashop.android.jrouter.external.annotation.Autowired;
import com.enation.javashop.android.jrouter.external.enums.ValueType;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ANNOTATION_TYPE_AUTOWIRED;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.ISYRINGE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.JSON_SERVICE;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.KEY_MODULE_NAME;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.METHOD_INJECT;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.NAME_OF_AUTOWIRED;
import static com.enation.javashop.android.jrouter.compiler.utils.Constant.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * 参数注入处理器
 * @LDD
 */
/**自动处理*/
@AutoService(Processor.class)
/**模块标识名称 使用注解支持该项*/
@SupportedOptions(KEY_MODULE_NAME)
/**使用Java1.7*/
@SupportedSourceVersion(SourceVersion.RELEASE_7)
/**返回该工厂生成的注解处理器所能支持的注解类型,就是注解类的全包名*/
@SupportedAnnotationTypes({ANNOTATION_TYPE_AUTOWIRED})
public class AutowiredProcessor extends AbstractProcessor {
    /**文件辅助类*/
    private Filer mFiler;
    /**日志辅助类*/
    private Logger logger;
    /**处理TypeMirror工具类*/
    private Types types;
    /**类型判断工具类*/
    private TypeUtils typeUtils;
    /**元素处理辅助类*/
    private Elements elements;
    /**元素父类与子类集合*/
    private Map<TypeElement, List<Element>> parentAndChild = new HashMap<>();   // Contain field need autowired and his super class.
    /**JRouter处理类Class*/
    private static final ClassName ARouterClass = ClassName.get("com.enation.javashop.android.jrouter", "JRouter");
    /**Android包下的Log类*/
    private static final ClassName AndroidLog = ClassName.get("android.util", "Log");

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        /**获取文件辅助工具*/
        mFiler = processingEnv.getFiler();
        /**获取类型处理辅助工具*/
        types = processingEnv.getTypeUtils();
        /**获取元素操作辅助类*/
        elements = processingEnv.getElementUtils();      // Get class meta.
        /**初始化类型判断工具*/
        typeUtils = new TypeUtils(types, elements);
        /**初始化Logger*/
        logger = new Logger(processingEnv.getMessager());   // Package the log utils.

        logger.info(">>> 参数注解处理器  初始化完毕 <<<");
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (CollectionUtils.isNotEmpty(set)) {
            try {
                logger.info(">>> 找到被参数处理器注解的元素, 开始处理... <<<");
                /**判断元素是否都合法，并筛选合法的元素*/
                categories(roundEnvironment.getElementsAnnotatedWith(Autowired.class));
                /**开始处理*/
                generateHelper();

            } catch (Exception e) {
                /**异常错误抛出*/
                logger.error(e);
            }
            return true;
        }
        return false;
    }

    /**
     * 开始生成参数注入Java文件
     * @throws IOException             创建文件时输入输出异常
     * @throws IllegalAccessException  操作文件时非法存取异常
     */
    private void generateHelper() throws IOException, IllegalAccessException {
        /**声明ISYRINGE类元素*/
        TypeElement type_ISyringe = elements.getTypeElement(ISYRINGE);
        /**声明JSON_SERVICE类元素*/
        TypeElement type_JsonService = elements.getTypeElement(JSON_SERVICE);
        /**生成类型是IPROVIDER的TypeMirror*/
        TypeMirror iProvider = elements.getTypeElement(Constant.IPROVIDER).asType();
        /**生成类型是ACTIVITY的TypeMirror*/
        TypeMirror activityTm = elements.getTypeElement(Constant.ACTIVITY).asType();
        /**生成类型是FRAGMENT的TypeMirror*/
        TypeMirror fragmentTm = elements.getTypeElement(Constant.FRAGMENT).asType();
        /**生成类型是FRAGMENT的TypeMirror*/
        TypeMirror fragmentTmV4 = elements.getTypeElement(Constant.FRAGMENT_V4).asType();
        /**生成一个类型为object且名称为target的参数*/
        ParameterSpec objectParamSpec = ParameterSpec.builder(TypeName.OBJECT, "target").build();
        /**判断合法元素是否存在*/
        if (MapUtils.isNotEmpty(parentAndChild)) {
            /**循环合法子元素*/
            for (Map.Entry<TypeElement, List<Element>> entry : parentAndChild.entrySet()) {
                /**创建一个方法 作用域public 方法名inject 且添加Override注解 参数1个 类型为object 名称为target*/
                MethodSpec.Builder injectMethodBuilder = MethodSpec.methodBuilder(METHOD_INJECT)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(objectParamSpec);
                /**获取父元素*/
                TypeElement parent = entry.getKey();
                /**获取子元素*/
                List<Element> childs = entry.getValue();
                /**获取父元素全包名*/
                String qualifiedName = parent.getQualifiedName().toString();
                /**获取父元素包名*/
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
                /**获取父元素类名*/
                String fileName = parent.getSimpleName() + NAME_OF_AUTOWIRED;
                /**提示：开始生成类*/
                logger.info(">>> 开始处理 ，子元素共" + childs.size() + "个，  类名：" + parent.getSimpleName() + " ... <<<");
                /**生成参数注入辅助类，添加类注释，添加实现接口，添加作用域*/
                TypeSpec.Builder helper = TypeSpec.classBuilder(fileName)
                        .addJavadoc(WARNING_TIPS)
                        .addSuperinterface(ClassName.get(type_ISyringe))
                        .addModifiers(PUBLIC);
                /**创建一个变量元素，变量名为serializationService，类型为JsonTransforService，添加作用域*/
                FieldSpec jsonServiceField = FieldSpec.builder(TypeName.get(type_JsonService.asType()), "serializationService", Modifier.PRIVATE).build();
                /**加入到该类中*/
                helper.addField(jsonServiceField);
                /**向inject方法中添加serializationService的初始化代码*/
                injectMethodBuilder.addStatement("serializationService = $T.prepare().seek($T.class)", ARouterClass, ClassName.get(type_JsonService));
                /**添加类型转换代码 把target转换为元素父类型*/
                injectMethodBuilder.addStatement("$T substitute = ($T)target", ClassName.get(parent), ClassName.get(parent));

                /**循环合法筛选后的子元素集*/
                for (Element element : childs) {
                    /**获取当前子元素注解中的参数*/
                    Autowired fieldConfig = element.getAnnotation(Autowired.class);
                    /**获取子元素名称 也就是变量名*/
                    String fieldName = element.getSimpleName().toString();
                    /**判断子元素父类是否是iProvider*/
                    if (types.isSubtype(element.asType(), iProvider)) {
                        /**判断注解name参数是否为空*/
                        if ("".equals(fieldConfig.name())) {
                            /**添加代码段 初始化父元素是provider的元素 使用.class的方式初始化元素*/
                            injectMethodBuilder.addStatement(
                                    "substitute." + fieldName + " = $T.prepare().seek($T.class)",
                                    ARouterClass,
                                    ClassName.get(element.asType())
                            );
                        } else {
                            /**添加代码段 name不为空 使用name方式初始化父元素是provider的元素*/
                            injectMethodBuilder.addStatement(
                                    "substitute." + fieldName + " = ($T)$T.prepare().create($S).seek()",
                                    ClassName.get(element.asType()),
                                    ARouterClass,
                                    fieldConfig.name()
                            );
                        }

                        /**验证参数是否必须初始化*/
                        if (fieldConfig.required()) {
                            /**如果必须初始化 添加以下代码段，如果该元素在初始化时为null 那么抛出Runtime异常*/
                            injectMethodBuilder.beginControlFlow("if (substitute." + fieldName + " == null)");
                            injectMethodBuilder.addStatement(
                                    "throw new RuntimeException(\"该字段'" + fieldName + "' 为空, 位于Class '\" + $T.class.getName() + \"!\")", ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        }
                    } else {
                        /**以下参数从Activity的Intent中获取*/
                        /**拼接代码段*/
                        String statment = "substitute." + fieldName + " = substitute.";
                        /**是否是Activity*/
                        boolean isActivity = false;
                        if (types.isSubtype(parent.asType(), activityTm)) {
                            /**是Activity 修改标识 代码段拼接  直接获取Intent*/
                            isActivity = true;
                            statment += "getIntent().";
                        } else if (types.isSubtype(parent.asType(), fragmentTm) || types.isSubtype(parent.asType(), fragmentTmV4)) {   // Fragment, then use getArguments()
                            /**是Fragment 从Arguments获取*/
                            statment += "getArguments().";
                        } else {
                            throw new IllegalAccessException("该字段 ：[" + fieldName + "] 初始化必须依赖intent, 所以他必须是一个Fragment或者Activity");
                        }
                        /**拼接代码端*/
                        statment = buildStatement(statment, typeUtils.typeExchange(element), isActivity);
                        /**如果该字段为serializationService，那么添加空判断*/
                        if (statment.startsWith("serializationService.")) {   // Not mortals
                            /**开始拼接代码段*/
                            injectMethodBuilder.beginControlFlow("if (null != serializationService)");
                            injectMethodBuilder.addStatement(
                                    "substitute." + fieldName + " = " + statment,
                                    (StringUtils.isEmpty(fieldConfig.name()) ? fieldName : fieldConfig.name()),
                                    ClassName.get(element.asType())
                            );
                            /**添加else*/
                            injectMethodBuilder.nextControlFlow("else");
                            /**添加错误打印*/
                            injectMethodBuilder.addStatement(
                                    "$T.e(\"" + Constant.TAG +  "\", \"你如果想要初始化 '" + fieldName + "' 在Class '$T' , 你需要实现 'SerializationService' !\")", AndroidLog, ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        } else {
                            /**不是serializationService，直接添加代码段*/
                            injectMethodBuilder.addStatement(statment, StringUtils.isEmpty(fieldConfig.name()) ? fieldName : fieldConfig.name());
                        }

                        /**判断该字段是否为必须，是必须添加空判断*/
                        if (fieldConfig.required() && !element.asType().getKind().isPrimitive()) {  // Primitive wont be check.
                            injectMethodBuilder.beginControlFlow("if (null == substitute." + fieldName + ")");
                            injectMethodBuilder.addStatement(
                                    "$T.e(\"" + Constant.TAG +  "\", \"该字段 '" + fieldName + "' 为空, 在class '\" + $T.class.getName() + \"!\")", AndroidLog, ClassName.get(parent));
                            injectMethodBuilder.endControlFlow();
                        }
                    }
                }

                /**inject方法构造并加入构造类中*/
                helper.addMethod(injectMethodBuilder.build());

                /**构造JavaFile*/
                JavaFile.builder(packageName, helper.build()).build().writeTo(mFiler);

                logger.info(">>> " + parent.getSimpleName() + " 处理完成, " + fileName + " 处理完成. <<<");
            }

            logger.info(">>> Autowired 处理结束. <<<");
        }
    }

    /**
     * 拼接代码段
     * @param statment    代码段初始状态
     * @param type        字段类型
     * @param isActivity  是否是一个Activity
     * @return            代码单
     */
    private String buildStatement(String statment, int type, boolean isActivity) {
        if (type == ValueType.BOOLEAN.ordinal()) {
            statment += (isActivity ? ("getBooleanExtra($S, false)") : ("getBoolean($S)"));
        } else if (type == ValueType.BYTE.ordinal()) {
            statment += (isActivity ? ("getByteExtra($S, (byte) 0)") : ("getByte($S)"));
        } else if (type == ValueType.SHORT.ordinal()) {
            statment += (isActivity ? ("getShortExtra($S, (short) 0)") : ("getShort($S)"));
        } else if (type == ValueType.INT.ordinal()) {
            statment += (isActivity ? ("getIntExtra($S, 0)") : ("getInt($S)"));
        } else if (type == ValueType.LONG.ordinal()) {
            statment += (isActivity ? ("getLongExtra($S, 0)") : ("getLong($S)"));
        } else if (type == ValueType.FLOAT.ordinal()) {
            statment += (isActivity ? ("getFloatExtra($S, 0)") : ("getFloat($S)"));
        } else if (type == ValueType.DOUBLE.ordinal()) {
            statment += (isActivity ? ("getDoubleExtra($S, 0)") : ("getDouble($S)"));
        } else if (type == ValueType.STRING.ordinal()) {
            statment += (isActivity ? ("getStringExtra($S)") : ("getString($S)"));
        } else if (type == ValueType.PARCELABLE.ordinal()) {
            statment += (isActivity ? ("getParcelableExtra($S)") : ("getParcelable($S)"));
        } else if (type == ValueType.OBJECT.ordinal()) {
            statment = "serializationService.json2Object(substitute." + (isActivity ? "getIntent()." : "getArguments().") + (isActivity ? "getStringExtra($S)" : "getString($S)") + ", $T.class)";
        }
        return statment;
    }

    /**
     * 过滤非法元素
     * @param elements  元素集合
     * @throws IllegalAccessException  非法存取异常
     */
    private void categories(Set<? extends Element> elements) throws IllegalAccessException {
        /**判断是否为为空*/
        if (CollectionUtils.isNotEmpty(elements)) {
            /**循环判断*/
            for (Element element : elements) {
                /**获取类信息*/
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                /**判断元素作用域*/
                if (element.getModifiers().contains(Modifier.PRIVATE)) {
                    throw new IllegalAccessException(" Autowired注解的字段作用域必须是Private ["
                            + element.getSimpleName() + "] 位于Class [" + enclosingElement.getQualifiedName() + "]");
                }
                /**
                 * 判断是否父元素是否已经存在 存在则加入子元素
                 * 不存在则创建子元素集 并放入集
                 */
                if (parentAndChild.containsKey(enclosingElement)) {
                    parentAndChild.get(enclosingElement).add(element);
                } else {
                    List<Element> childs = new ArrayList<>();
                    childs.add(element);
                    parentAndChild.put(enclosingElement, childs);
                }
            }
            /**筛选分类完毕*/
            logger.info("筛选分类完毕.");
        }
    }

}
