package io.github.badlionmoddinggroup.badliongradle;

import io.github.badlionmoddinggroup.badliongradle.tasks.LaunchEnigma;
import io.github.badlionmoddinggroup.badliongradle.tasks.PrepareJars;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import java.io.*;
import java.util.function.Consumer;

public class BadlionGradle implements Plugin<Project> {

    public static Project project;

    public static File getCacheFolder() {
        return new File(project.getRootDir().getAbsolutePath() + "/.gradle/badlion-cache/");
    }

    public static File getCacheFile(String fileName) {
        return new File(getCacheFolder().getAbsolutePath() + "/" + fileName);
    }

    @Override
    public void apply(Project target) {
        project = target;

        TaskContainer tasks = target.getTasks();

        tasks.register("setupBadlionCode", SetupBadlionCode.class);
        tasks.register("launchEnigma", LaunchEnigma.class, task -> task.dependsOn("prepareJars"));
        tasks.register("prepareJars", PrepareJars.class);
    }

    /**
     * Iterates through all the {@link TopLevelClassMapping} and {@link InnerClassMapping} in a {@link MappingSet}
     *
     * @param mappings The mappings
     * @param consumer The consumer of the {@link ClassMapping}
     */
    public static void iterateClasses(MappingSet mappings, Consumer<ClassMapping<?, ?>> consumer) {
        for (TopLevelClassMapping classMapping : mappings.getTopLevelClassMappings()) {
            iterateClass(classMapping, consumer);
        }
    }

    private static void iterateClass(ClassMapping<?, ?> classMapping, Consumer<ClassMapping<?, ?>> consumer) {
        consumer.accept(classMapping);

        for (InnerClassMapping innerClassMapping : classMapping.getInnerClassMappings()) {
            iterateClass(innerClassMapping, consumer);
        }
    }

}