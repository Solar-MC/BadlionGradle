package io.github.badlionmoddinggroup.badliongradle.tasks;

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
        File file = new File(BadlionGradle.project.getRootDir() + "/build/build.tiny");
        file.mkdirs();
        file.createNewFile();
        Main.main("convert-mappings", "enigma", BadlionGradle.project.getRootDir() + "/mappings", "tiny_v2:official:named", BadlionGradle.project.getRootDir() + "/build/build.tiny");
    }

}
