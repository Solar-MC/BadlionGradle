package io.github.badlionmoddinggroup.badliongradle.tasks;

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
