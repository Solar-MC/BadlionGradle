package io.github.badlionmoddinggroup.badliongradle.tasks;

import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class RemapToOfficialTask extends DefaultTask {

    public RemapToOfficialTask(){
        setGroup("badlion");
    }

    @TaskAction
    public void run() {
        BadlionGradle.project.getProjectDir();
    }

}
