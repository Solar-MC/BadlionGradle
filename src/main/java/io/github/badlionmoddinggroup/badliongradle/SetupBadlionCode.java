package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SetupBadlionCode extends DefaultTask {

    public SetupBadlionCode(){
        setGroup("badlion");
    }

    @TaskAction
    public void run() {
        BadlonGradle.project.getProjectDir();
    }

}
