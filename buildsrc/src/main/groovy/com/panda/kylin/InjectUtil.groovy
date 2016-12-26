package com.panda.kylin

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import javassist.ClassPool
import javassist.CtClass
import javassist.CtField
import javassist.CtMethod
import javassist.Modifier
import org.apache.commons.io.FileUtils

import java.lang.annotation.Annotation

public class InjectUtil {

    private static ClassPool pool = ClassPool.getDefault()

    public
    static void injectDir(String projectBuildDir, String androidSdkPath, TransformInvocation transformInvocation, String path, String packageName) {
        pool.insertClassPath(androidSdkPath)
        transformInvocation.inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput directoryInput ->
                pool.appendClassPath(directoryInput.file.absolutePath)
            }
            input.jarInputs.each { JarInput jarInput ->
                pool.appendClassPath(jarInput.file.getAbsolutePath())
            }
        }

        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->

                String filePath = file.absolutePath
                //确保当前文件是class文件，并且不是系统自动生成的class文件
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {

                    String filePointPath = filePath.replace('\\', '.').replace('/', '.')

                    int index = filePointPath.indexOf(packageName);
                    if (index != -1) {
                        int end = filePointPath.length() - 6 // .class = 6
                        String className = filePointPath.substring(index, end)
                        //开始修改class文件
                        CtClass c = pool.getCtClass(className)
                        boolean isPatch = false;
                        c.annotations.each {
                            Annotation annotation ->
                                if (isPatchAnnotation(annotation)) {
                                    addPatchClassNameToBox(path, className)
                                    FileUtils.copyFile(file, new File(projectBuildDir + "/outputs/patch/" + className.replace('.', '/') + ".class"))
                                    isPatch = true;
                                }
                        }
                        if (!isPatch) {
                            if (c.isFrozen()) {
                                c.defrost()
                            }
                            pool.importPackage("com.panda.kylin.Kylin")
                            pool.importPackage("com.panda.kylin.KylinMethodSupport")

                            //给类添加kylin变量，即补丁变量
                            CtField kylin = new CtField(pool.get("com.panda.kylin.Kylin"), "mKylin", c);
                            kylin.setModifiers(Modifier.STATIC);
                            c.addField(kylin);

                            //遍历类的所有方法
                            CtMethod[] methods = c.getDeclaredMethods();
                            for (CtMethod method : methods) {
                                //在每个方法之前插入判断语句，判断类的补丁实例是否存在
                                StringBuilder injectStr = new StringBuilder();
                                injectStr.append("if(mKylin!=null && KylinMethodSupport.isSupport(mKylin.getClass(),\"" + method.getName() + "\")){\n")
                                String javaThis = "null,"
                                if (!Modifier.isStatic(method.getModifiers())) {
                                    javaThis = "this,"
                                }
                                String runStr = "mKylin.dispatchMethod(" + javaThis + "mKylin,\"" + method.getName() + "\" ,\$args)"
                                injectStr.append(addReturnStr(method, runStr))
                                injectStr.append("}")
                                method.insertBefore(injectStr.toString())
                            }
                        }

                        c.writeFile(path)
                        c.detach()
                    }
                }
            }
//            FileUtils.copyFile(new File(path + "/com/panda/kylin/PatchBox.class"), new File(projectBuildDir + "/outputs/patch/com/panda/kylin/PatchBox.class"))
        }
    }

    public static boolean isPatchAnnotation(Annotation annotation) {
        print("name:" + annotation.annotationType().getName() + "\n")
        return annotation.annotationType().getName().equalsIgnoreCase("com.panda.kylin.PatchClassName");
    }

    public static void addPatchClassNameToBox(String path, String className) {
        CtClass ctClass = pool.getCtClass("com.panda.kylin.PatchBox")
        CtMethod[] methods = ctClass.getDeclaredMethods();
        methods.each {
            CtMethod method ->
                if (method.getName().equalsIgnoreCase("getPatchClasses")) {
                    method.insertBefore("patchs.add(\"" + className + "\");")
                    ctClass.writeFile(path)
                    ctClass.detach()
                }
        }
    }

    //解析方法签名，获取方法放回值类型
    public static String getReturnType(String methodSign) {
        String type;
        int index = methodSign.indexOf(")L");
        String jType = methodSign.substring(index + 2, methodSign.length() - 1);
        type = jType.replace("/", ".");
        return type;
    }

    //给非void方法加入return语句，并处理基本类型返回值
    public static String addReturnStr(CtMethod method, String runStr) {
        String returnStr;
        String typeStr = "";
        switch (method.getReturnType()) {
            case CtClass.voidType:
                return runStr + ";\n return;"
                break;
            case CtClass.booleanType:
                returnStr = "return ((Boolean)";
                typeStr = ".booleanValue()";
                break;
            case CtClass.byteType:
                returnStr = "return ((byte)";
                typeStr = ".byteValue()";
                break;
            case CtClass.charType:
                returnStr = "return ((char)";
                typeStr = ".charValue()";
                break;
            case CtClass.doubleType:
                returnStr = "return ((Number)";
                typeStr = ".doubleValue()";
                break;
            case CtClass.floatType:
                returnStr = "return ((Number)";
                typeStr = ".floatValue()";
                break;
            case CtClass.intType:
                returnStr = "return ((Number)";
                typeStr = ".intValue()";
                break;
            case CtClass.longType:
                returnStr = "return ((Number)";
                typeStr = ".longValue()";
                break;
            case CtClass.shortType:
                returnStr = "return ((Number)";
                typeStr = ".shortValue()";
                break;
            default:
                returnStr = "return((" + getReturnType(method.getSignature()) + ")";
                break;
        }
        return returnStr + "(" + runStr + "))" + typeStr + ";\n";
    }
}