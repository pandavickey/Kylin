package com.panda.kylin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Created by panda on 16/12/22.
 */

public class GenerateCodeTask extends DefaultTask {

    @Input
    String variantDirName

    @OutputDirectory
    File outputDir() {
        project.file("${project.buildDir}/generated/source/kylin/${variantDirName}")
    }

    @OutputFile
    File outputFile() {
        project.file("${outputDir().absolutePath}/com/panda/kylin/PatchBox.java")
    }

    @TaskAction
    def taskAction() {
        def source = JavaFileTemplate.getContent()
        def outputFile = outputFile()
        if (!outputFile.isFile()) {
            outputFile.delete()
            outputFile.parentFile.mkdirs()
        }
        outputFile.text = source
    }
}
