package io.github.badlionmoddinggroup.badliongradle.tasks;

import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import io.github.badlionmoddinggroup.badliongradle.util.Profiler;
import io.github.badlionmoddinggroup.badliongradle.util.SourceRemover;
import net.fabricmc.lorenztiny.TinyMappingFormat;
import net.fabricmc.tinyremapper.IMappingProvider;
import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.cadixdev.lorenz.MappingSet;
import org.cadixdev.lorenz.model.ClassMapping;
import org.cadixdev.lorenz.model.FieldMapping;
import org.cadixdev.lorenz.model.MethodMapping;
import org.cadixdev.lorenz.model.MethodParameterMapping;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Locale;

public class PrepareJarsTask extends DefaultTask {

    public static final String MINECRAFT_MAPPINGS = "https://gist.githubusercontent.com/hYdos/d8a7e36b960a393672e886bcd5949285/raw/4bb1764506c36fd70e0ede2beb0587c21bb4446b/1.8.9-LATEST-AUG17.tiny";
    public static final String BADLION_MAPPINGS = "https://raw.githubusercontent.com/BadlionModdingGroup/badlionIntermediaries/master/intermediaries/v2.16.2-877fa01-PRODUCTION.tiny";

    public PrepareJarsTask() {
        setGroup("mapping");
    }

    @TaskAction
    public void run() throws IOException {
        //epic hardcoded for win
        Profiler.setState("Deleting old cache");
        FileUtils.deleteDirectory(BadlionGradle.getCacheFolder(getProject()));

        Profiler.setState("Copying original Badlion Jar");
        String clientJarLocation = "CLIENTJARLOCATION IS BROKEN";
        switch (OsChecker.getType()){
            case Windows:
                clientJarLocation = System.getProperty("user.home") + "/AppData/Roaming/.minecraft/versions/BLClient18/BLClient.jar";
                break;
            case Linux:
                ///home/hayden/.wine/drive_c/users/hayden/Application Data/.minecraft
                clientJarLocation = System.getProperty("user.home") + "/.wine/drive_c/users/" + System.getProperty("user.name") + "/Application Data/.minecraft/versions/BLClient18/BLClient.jar";
        }
        FileUtils.copyFile(new File(clientJarLocation), new File(BadlionGradle.getCacheFolder(getProject()).getAbsolutePath() + "/badlionOfficial.jar"));

        Profiler.setState("Download mappings");
        IOUtils.copy(new URL(MINECRAFT_MAPPINGS).openStream(), new FileOutputStream(BadlionGradle.getCacheFile(getProject(),"1.8.9.tiny")));
        IOUtils.copy(new URL(BADLION_MAPPINGS).openStream(), new FileOutputStream(BadlionGradle.getCacheFile(getProject(),"badlionIntermediaries.tiny")));

        Profiler.setState("Read Tiny files");
        MappingSet minecraftMappings = TinyMappingFormat.DETECT.read(BadlionGradle.getCacheFile(getProject(),"1.8.9.tiny").toPath(), "official", "named");
        MappingSet badlionMappings = TinyMappingFormat.DETECT.read(BadlionGradle.getCacheFile(getProject(),"badlionIntermediaries.tiny").toPath(), "official", "intermediary");

        Profiler.setState("MergeMappings");
        BadlionGradle.iterateClasses(minecraftMappings, classMapping -> {
            ClassMapping<?,?> clazz = badlionMappings.getOrCreateClassMapping(classMapping.getFullObfuscatedName()).setDeobfuscatedName(classMapping.getFullDeobfuscatedName());
            for (MethodMapping methodMapping : classMapping.getMethodMappings()) {
                clazz.getOrCreateMethodMapping(methodMapping.getSignature()).setDeobfuscatedName(methodMapping.getDeobfuscatedName());
            }
            for (FieldMapping fieldMapping : classMapping.getFieldMappings()) {
                clazz.getOrCreateFieldMapping(fieldMapping.getSignature()).setDeobfuscatedName(fieldMapping.getDeobfuscatedName());
            }
        });

        Profiler.setState("Setup Remapper");
        TinyRemapper remapper = TinyRemapper.newRemapper().withMappings(out -> BadlionGradle.iterateClasses(badlionMappings, classMapping -> {
            String owner = classMapping.getFullObfuscatedName();
            out.acceptClass(owner, classMapping.getFullDeobfuscatedName());

            for (MethodMapping methodMapping : classMapping.getMethodMappings()) {
                IMappingProvider.Member method = new IMappingProvider.Member(owner, methodMapping.getObfuscatedName(), methodMapping.getObfuscatedDescriptor());
                out.acceptMethod(method, methodMapping.getDeobfuscatedName());
                for (MethodParameterMapping parameterMapping : methodMapping.getParameterMappings()) {
                    out.acceptMethodArg(method, parameterMapping.getIndex(), parameterMapping.getDeobfuscatedName());
                }
            }

            for (FieldMapping fieldMapping : classMapping.getFieldMappings()) {
                out.acceptField(new IMappingProvider.Member(owner, fieldMapping.getObfuscatedName(), fieldMapping.getType().get().toString()), fieldMapping.getDeobfuscatedName());
            }
        })).ignoreConflicts(true).build();

        Path input = BadlionGradle.getCacheFile(getProject(),"badlionOfficial.jar").toPath();
        Path output = BadlionGradle.getCacheFile(getProject(),"badlionRemappedWithMc.jar").toPath();

        Profiler.setState("Remap");
        try (OutputConsumerPath outputConsumer = new OutputConsumerPath.Builder(output).build()) {
            outputConsumer.addNonClassFiles(input, NonClassCopyMode.FIX_META_INF, remapper);

            remapper.readInputs(input);
//            remapper.readClassPath(classpath); TODO: grab classpath and add it here for possibly better results?

            remapper.apply(outputConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            remapper.finish();
        }

        Path unMinecraftedOutput = BadlionGradle.getCacheFile(getProject(),"badlionRemapped.jar").toPath();

        Profiler.setState("Remove Minecraft");
        SourceRemover.main(new String[]{output.toAbsolutePath().toString(), unMinecraftedOutput.toAbsolutePath().toString()});
    }

    public static final class OsChecker {
        public enum OSType {
            Windows, MacOS, Linux, Other
        };

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
