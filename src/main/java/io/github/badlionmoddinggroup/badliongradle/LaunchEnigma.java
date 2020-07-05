package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class LaunchEnigma extends DefaultTask {

    public LaunchEnigma(){
        setGroup("mapping");
    }

    @TaskAction
    public void run() {
        BadlonGradle.project.getProjectDir();

    }

}
