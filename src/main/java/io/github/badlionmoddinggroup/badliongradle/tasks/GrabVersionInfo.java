package io.github.badlionmoddinggroup.badliongradle.tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class GrabVersionInfo extends DefaultTask {

    public GrabVersionInfo() {
        setGroup("patching");
    }

    @TaskAction
    public void run(){

    }

}
