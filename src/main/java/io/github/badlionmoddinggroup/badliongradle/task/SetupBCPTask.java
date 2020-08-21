package io.github.badlionmoddinggroup.badliongradle.task;

import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import io.github.badlionmoddinggroup.badliongradle.provider.BadlionProvider;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.nio.file.Path;

public class SetupBCPTask extends DefaultTask {

    public SetupBCPTask() {
        setGroup("patching");
    }


    public File getDecompilePath(String blcVersion) {
        return new File(BadlionGradle.getVersionCache(getProject(), blcVersion), "decompile/");
    }


    @TaskAction
    public void run() {
        BadlionProvider badlionProvider = BadlionGradle.getGradleExtension(getProject()).badlionProvider;
        decompileClient(badlionProvider);
        // src/main/java = patched location
        // src/main/vanilla = decompiled code extract here
        // patches/ = the patches... of course

        getProject().getLogger().lifecycle("Extracting Badlion Src");

        File decompiledSrc = new File(getDecompilePath(badlionProvider.badlionVersion), "badlionRemapped.jar");
        ZipUtil.unpack(decompiledSrc, new File(getProject().getProjectDir(), "src/main/vanilla"));
        ZipUtil.unpack(decompiledSrc, new File(getProject().getProjectDir(), "src/main/java"));
    }

    private void decompileClient(BadlionProvider badlion) {
        Path strippedMinecraftOutput = BadlionGradle.getVersionCacheFile(getProject(), badlion.badlionVersion, "badlionRemapped.jar").toPath();
        getDecompilePath(badlion.badlionVersion).mkdirs();
        ConsoleDecompiler.main(new String[]{strippedMinecraftOutput.toString(), getDecompilePath(badlion.badlionVersion).getAbsolutePath()});
    }

}
