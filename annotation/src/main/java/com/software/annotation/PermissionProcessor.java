package com.software.annotation;// PermissionProcessor.java
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

@AutoService(Processor.class) // 使用Google的auto-service库自动注册处理器
@SupportedAnnotationTypes("your.package.name.NeedPermission") // 指定处理的注解全限定名
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PermissionProcessor extends AbstractProcessor {

    private Elements elementUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 1. 遍历所有被 @NeedPermission 注解的元素
        for (Element element : roundEnv.getElementsAnnotatedWith(Permission.class)) {
            if (element.getKind() == ElementKind.CLASS) { // 确保是类
                TypeElement typeElement = (TypeElement) element;

                // 2. 获取注解的值（所需的权限数组）
                Permission needPermission = typeElement.getAnnotation(Permission.class);
                String[] permissions = needPermission.value();

                // 3. 获取类的完整名称和包名
                String className = typeElement.getSimpleName().toString();
                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();

                // 4. 生成权限请求辅助类
                generatePermissionHelper(packageName, className, permissions);
            }
        }
        return true;
    }

    private void generatePermissionHelper(String packageName, String className, String[] permissions) {
        // 使用 JavaPoet 构建 requestPermissions 方法
        MethodSpec.Builder requestMethodBuilder = MethodSpec.methodBuilder("request" + className + "Permissions")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(ClassName.get("android.app", "Activity"), "activity")
                .addParameter(ClassName.get("your.package.name", "PermissionCallback"), "callback");

        // 添加权限检查逻辑
        requestMethodBuilder.addStatement("$T permissionList = new $T<>()", ArrayList.class, ArrayList.class);

        for (String permission : permissions) {
            requestMethodBuilder.addStatement("if ($T.checkSelfPermission(activity, $S) != $T.PERMISSION_GRANTED) {",
                    ClassName.get("androidx.core.content", "ContextCompat"),
                    permission,
                    ClassName.get("android.content.pm", "PackageManager"));
            requestMethodBuilder.addStatement("    permissionList.add($S)", permission);
            requestMethodBuilder.addStatement("}");
        }

        // 如果没有权限需要请求，直接回调成功
        requestMethodBuilder.addStatement("if (permissionList.isEmpty()) {");
        requestMethodBuilder.addStatement("    callback.onPermissionGranted()");
        requestMethodBuilder.addStatement("    return");
        requestMethodBuilder.addStatement("}");

        // 请求权限
        requestMethodBuilder.addStatement("$T.requestPermissions(activity, permissionList.toArray(new String[0]), $L)",
                ClassName.get("androidx.core.app", "ActivityCompat"),
                "REQUEST_CODE");

        // 构建类
        TypeSpec permissionHelper = TypeSpec.classBuilder(className + "_PermissionHelper")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(requestMethodBuilder.build())
                .build();

        // 写入文件
        JavaFile javaFile = JavaFile.builder(packageName, permissionHelper)
                .build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Failed to write permission helper class: " + e.getMessage());
        }
    }
}