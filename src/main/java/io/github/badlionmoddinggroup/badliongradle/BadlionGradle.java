package io.github.badlionmoddinggroup.badliongradle;

import io.github.badlionmoddinggroup.badliongradle.tasks.*;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.InnerClassMapping;
import org.cadixdev.lorenz.model.TopLevelClassMapping;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

import java.io.File;
import java.util.Locale;
import java.util.function.Consumer;

public class BadlionGradle implements Plugin<Project> {

    public static File getProjectCacheFolder(Project project) {
        return new File(project.getRootDir().getAbsolutePath() + "/.gradle/badlion-cache/");
    }

    public static File getVersionCacheFile(Project project, String version, String fileName) {
        return new File(getVersionCache(project, version).getAbsolutePath() + "/" + fileName);
    }

    public static File getVersionCache(Project project, String version) {
        return new File(project.getGradle().getGradleUserHomeDir(), "caches/badlion-gradle-cache/" + version);
    }

    public static BadlionGradleExtension getGradleExtension(Project project) {
        return project.getExtensions().getByType(BadlionGradleExtension.class);
    }

    @Override
    public void apply(Project target) {
        TaskContainer tasks = target.getTasks();

        tasks.register("setupBCP", SetupBCPTask.class);
        tasks.register("launchEnigma", LaunchEnigmaTask.class, task -> task.dependsOn("prepareJars"));
        tasks.register("prepareJars", PrepareJarsTask.class, task -> task.dependsOn("generateClient"));
        tasks.register("generateClient", GenerateClientTask.class, task -> task.dependsOn("grabVersionInfo"));
        tasks.register("grabVersionInfo", GrabVersionInfo.class);
        tasks.register("buildMappings", BuildMappingsTask.class);

        target.getExtensions().create("badlion", BadlionGradleExtension.class, target);
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

    public static final class OsChecker {
        public enum OSType {
            Windows, MacOS, Linux, Other
        }

        protected static OSType detectedOS;

        public static OSType getType() {
            if (detectedOS == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                    detectedOS = OSType.MacOS;
                } else if (OS.contains("win")) {
                    detectedOS = OSType.Windows;
                } else if (OS.contains("nux")) {
                    detectedOS = OSType.Linux;
                } else {
                    detectedOS = OSType.Other;
                }
            }
            return detectedOS;
        }
    }

}
