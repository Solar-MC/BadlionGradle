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
        File file = new File(getProject().getRootDir() + "/build/officialtonamed.tiny");
        File intermediaryLocation = BadlionGradle.getVersionCacheFile(getProject(), BadlionGradle.getGradleExtension(getProject()).badlionVersion, "badlionIntermediaries.tiny");
        file.mkdirs();
        file.createNewFile();
        Main.main("convert-mappings", "enigma", getProject().getRootDir() + "/mappings", "tiny_v2:official:named", getProject().getRootDir() + "/build/officialtonamed.tiny");
//        net.fabricmc.stitch.Main.main(new String[]{"mergeTiny", file.getPath(), intermediaryLocation.getP});
    }

}
