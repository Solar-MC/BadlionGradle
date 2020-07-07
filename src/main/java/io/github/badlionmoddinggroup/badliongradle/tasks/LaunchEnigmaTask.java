package io.github.badlionmoddinggroup.badliongradle.tasks;

import cuchaz.enigma.gui.Main;
import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;

public class LaunchEnigmaTask extends DefaultTask {

    public LaunchEnigmaTask(){
        setGroup("mapping");
    }

    @TaskAction
    public void run() throws IOException {
        Main.main(new String[]{"--jar", BadlionGradle.getCacheFile("badlionRemapped.jar").getAbsolutePath(), "--mappings", BadlionGradle.project.getRootDir().getAbsolutePath() + "/mappings"});
    }

}
