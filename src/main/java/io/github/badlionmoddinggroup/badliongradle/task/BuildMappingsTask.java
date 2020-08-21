package io.github.badlionmoddinggroup.badliongradle.task;

import cuchaz.enigma.command.Main;
import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;

public class BuildMappingsTask extends DefaultTask {

    public BuildMappingsTask() {
        setGroup("mapping");
    }

    @TaskAction
    public void run() throws Exception {
        File intermediaryV1Location = BadlionGradle.getVersionCacheFile(getProject(), BadlionGradle.getGradleExtension(getProject()).badlionVersion, "badlionIntermediaries.tiny");
        File intermediaryV2Location = BadlionGradle.getVersionCacheFile(getProject(), BadlionGradle.getGradleExtension(getProject()).badlionVersion, "badlionIntermediariesv2.tiny");
        String rawMappings = getProject().getRootDir() + "/build/rawMappings.tiny";
        Main.main("convert-mappings", "enigma", getProject().getRootDir() + "/mappings", "tiny_v2:official:named", rawMappings);
        Main.main("convert-mappings", "tiny_v1:official:intermediary", intermediaryV1Location.getAbsolutePath(), "tiny_v2:official:intermediary", intermediaryV2Location.getAbsolutePath());
        net.fabricmc.stitch.Main.main(new String[]{"mergeTiny", intermediaryV2Location.getAbsolutePath(), rawMappings, getProject().getRootDir() + "/build/build.tiny"});
    }

}
