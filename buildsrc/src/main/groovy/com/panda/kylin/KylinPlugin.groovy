package com.panda.kylin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.api.BaseVariantOutput
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec

public class KylinPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def android = project.extensions.findByType(AppExtension)
        android.registerTransform(new TransformUtil(project, android.getDefaultConfig().getApplicationId()))

        boolean addTask = false;
        project.plugins.withId('com.android.application') {
            project.android.applicationVariants.all { ApkVariant variant ->
                variant.outputs.each { BaseVariantOutput output ->
                    def generateCodeTask = project.tasks.create(
                            name: "generate${variant.name.capitalize()}PatchBox",
                            type: GenerateCodeTask) {
                        variantDirName variant.dirName
                    }
                    generateCodeTask.execute()

                    variant.javaCompile.doFirst {
                        variant.javaCompile.source generateCodeTask.outputDir()
                    }
                }

                if (!addTask) {
                    project.task('GenerateShell', type: GenerateShellTask, dependsOn: "transformClassesWithPreDexFor${variant.name.capitalize()}") {
                    }

                    project.tasks.create(name: "patchDex", type: Exec, dependsOn: 'GenerateShell') {
                        workingDir "${project.buildDir}/outputs/patch/"
                        commandLine './patchDex.sh', android.getBuildToolsVersion()
                    }
                    addTask = true
                }
            }
        }
    }
}