package com.panda.kylin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

public class GenerateShellTask extends DefaultTask {
    @OutputDirectory
    File outputDir() {
        project.file("${project.buildDir}/outputs/patch")
    }

    @OutputFile
    File outputFile() {
        project.file("${outputDir().absolutePath}/patchDex.sh")
    }

    @TaskAction
    def taskAction() {
        Properties properties = new Properties()
        File localPropertiesFile = project.getRootProject().file("local.properties")
        if(localPropertiesFile.exists()){
            properties.load(localPropertiesFile.newDataInputStream())
        }
        def androidSdkPath = properties.getProperty("sdk.dir")
        def source = "cd " + project.buildDir.absolutePath + "/outputs/patch\n" +
                "jar cvf patch.jar *\n" +
                androidSdkPath+"/build-tools/\$1/dx --dex --output=patch_dex.dex patch.jar"
        File outputFile = outputFile()
        if (!outputFile.isFile()) {
            outputFile.delete()
            outputFile.parentFile.mkdirs()
        }
        outputFile.text = source

        Runtime.getRuntime().exec("chmod 777 " + outputFile.absolutePath)
    }
}