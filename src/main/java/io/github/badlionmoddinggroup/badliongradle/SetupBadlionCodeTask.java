package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SetupBadlionCodeTask extends DefaultTask {

    public SetupBadlionCodeTask(){
        setGroup("badlion");
    }

    @TaskAction
    public void run() {
        BadlionGradle.project.getProjectDir();
    }

}
