package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class BadlonGradle implements Plugin<Project> {

    public static Project project;

    @Override
    public void apply(Project target) {
        project = target;

        TaskContainer tasks = target.getTasks();

        tasks.register("setupBadlionCode", SetupBadlionCode.class);
        tasks.register("launchEnigma", LaunchEnigma.class);
        tasks.register("prepareJars", PrepareJars.class);
    }

}
