package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class PrepareJars extends DefaultTask {

    public PrepareJars(){
        setGroup("mapping");
    }

    @TaskAction
    public void run() {
        BadlonGradle.project.getProjectDir();
    }

}
