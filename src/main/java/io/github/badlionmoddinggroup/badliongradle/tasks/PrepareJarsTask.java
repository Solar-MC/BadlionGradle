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

public class PrepareJarsTask extends DefaultTask {

    public static final String MINECRAFT_MAPPINGS = "https://gist.githubusercontent.com/hYdos/f3b3190fb3437c4a3f309e3060a9aec0/raw/506dd9c32ec772e934a60a95c8b125d427c8a4cf/1.8.9.tiny";
    public static final String BADLION_MAPPINGS = "https://gist.githubusercontent.com/hYdos/50943e4ac0523ee85604ff7562dd5f27/raw/fe035353409f6c332e26407994c94ea28e919c93/v2.15.0-837a25b-PRODUCTION.tiny";

    public PrepareJarsTask() {
        setGroup("mapping");
    }

    @TaskAction
    public void run() throws IOException {
        //epic hardcoded for win
        Profiler.setState("Deleting old cache");
        FileUtils.deleteDirectory(BadlionGradle.getCacheFolder());

        Profiler.setState("Copying original Badlion Jar");
        FileUtils.copyFile(new File(System.getProperty("user.home") + "/AppData/Roaming/.minecraft/versions/BLClient18/BLClient.jar"), new File(BadlionGradle.getCacheFolder().getAbsolutePath() + "/badlionOfficial.jar"));

        Profiler.setState("Download mappings");
        IOUtils.copy(new URL(MINECRAFT_MAPPINGS).openStream(), new FileOutputStream(BadlionGradle.getCacheFile("1.8.9.tiny")));
        IOUtils.copy(new URL(BADLION_MAPPINGS).openStream(), new FileOutputStream(BadlionGradle.getCacheFile("badlionIntermediaries.tiny")));

        Profiler.setState("Read Tiny files");
        MappingSet minecraftMappings = TinyMappingFormat.DETECT.read(BadlionGradle.getCacheFile("1.8.9.tiny").toPath(), "official", "named");
        MappingSet badlionMappings = TinyMappingFormat.DETECT.read(BadlionGradle.getCacheFile("badlionIntermediaries.tiny").toPath(), "official", "intermediary");

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

        Path input = BadlionGradle.getCacheFile("badlionOfficial.jar").toPath();
        Path output = BadlionGradle.getCacheFile("badlionRemappedWithMc.jar").toPath();

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

        Path unMinecraftedOutput = BadlionGradle.getCacheFile("badlionRemapped.jar").toPath();

        Profiler.setState("Remove Minecraft");
        SourceRemover.main(new String[]{output.toAbsolutePath().toString(), unMinecraftedOutput.toAbsolutePath().toString()});
    }

}
