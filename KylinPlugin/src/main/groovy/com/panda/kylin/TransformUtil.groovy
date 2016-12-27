package com.panda.kylin

import com.android.annotations.NonNull
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInput
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

public class TransformUtil extends Transform {

    Project project
    String packageName

    // 构造函数，我们将Project保存下来备用
    public TransformUtil(Project project, String packageName) {
        this.project = project
        this.packageName = packageName
    }

    @Override
    String getName() {
        return "preDex"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }


    @Override
    public void transform(@NonNull TransformInvocation transformInvocation)
            throws TransformException, InterruptedException, IOException {
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        transformInvocation.inputs.each { TransformInput input ->
            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each { DirectoryInput directoryInput ->
                InjectUtil.injectDir(project.buildDir.absolutePath, getCompileSdkPath(), transformInvocation, directoryInput.file.absolutePath, packageName)
                // 获取output目录
                def dest = transformInvocation.outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
            //对类型为jar文件的input进行遍历
            input.jarInputs.each { JarInput jarInput ->

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }


    private String getAndroidSdkPath() {
        Properties properties = new Properties()
        File localPropertiesFile = project.getRootProject().file("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.newDataInputStream())
        }
        return properties.getProperty("sdk.dir")
    }

    private String getAndroidCompileSdkVersion() {
        def android = project.extensions.findByType(AppExtension)
        return android.getCompileSdkVersion()
    }

    private String getCompileSdkPath() {
        return getAndroidSdkPath() + "/platforms/" + getAndroidCompileSdkVersion() + "/android.jar"
    }
}