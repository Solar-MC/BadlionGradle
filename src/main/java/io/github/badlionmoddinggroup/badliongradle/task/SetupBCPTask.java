package io.github.badlionmoddinggroup.badliongradle.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class SetupBCPTask extends DefaultTask {

    public SetupBCPTask(){
        setGroup("patching");
    }

    @TaskAction
    public void run() {
        // src/main/java = patched location
        // src/main/vanilla = decompiled code extract here
        // patches/ = the patches... of course
    }

}
