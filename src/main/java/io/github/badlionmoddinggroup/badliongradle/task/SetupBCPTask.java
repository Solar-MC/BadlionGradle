package io.github.badlionmoddinggroup.badliongradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SetupBCPTask extends DefaultTask {

    public SetupBCPTask(){
        setGroup("patching");
    }

    @TaskAction
    public void run() {
        getProject().getProjectDir();
    }

}
