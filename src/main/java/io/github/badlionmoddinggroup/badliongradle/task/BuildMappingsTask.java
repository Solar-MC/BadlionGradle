package io.github.badlionmoddinggroup.badliongradle.task;

import cuchaz.enigma.command.Main;
import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import net.fabricmc.stitch.commands.tinyv2.CommandMergeTinyV2;
import net.fabricmc.stitch.commands.tinyv2.CommandReorderTinyV2;
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
        File invertedIntermediaryV2Location = BadlionGradle.getVersionCacheFile(getProject(), BadlionGradle.getGradleExtension(getProject()).badlionVersion, "badlionIntermediariesv2Inverted.tiny");
        String rawMappings = getProject().getRootDir() + "/build/rawMappings.tiny";
        Main.main("convert-mappings", "enigma", getProject().getRootDir() + "/mappings", "tiny_v2:intermediary:named", rawMappings);
        Main.main("convert-mappings", "tiny", intermediaryV1Location.getAbsolutePath(), "tiny_v2:official:intermediary", intermediaryV2Location.getAbsolutePath());
        new CommandReorderTinyV2().run(new String[]{intermediaryV2Location.getAbsolutePath(), invertedIntermediaryV2Location.getAbsolutePath(), "intermediary", "official"});
        new CommandMergeTinyV2().run(new String[]{invertedIntermediaryV2Location.getAbsolutePath(), rawMappings, getProject().getRootDir() + "/build/buildInverted.tiny"});
        new CommandReorderTinyV2().run(new String[]{getProject().getRootDir() + "/build/buildInverted.tiny", getProject().getRootDir() + "/build/build.tiny", "official", "intermediary", "named"});
    }

}
