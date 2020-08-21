package io.github.badlionmoddinggroup.badliongradle.provider;

import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import io.github.badlionmoddinggroup.badliongradle.patch.ZipPatcher;
import io.github.badlionmoddinggroup.badliongradle.util.DownloadUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class BadlionProvider {

    public String badlionVersion;
    private final Project project;
    public final MinecraftProvider minecraftProvider;

    public File patchFile;
    public File badlionClientFile;

    public BadlionProvider(String badlionVersion, Project project, MinecraftProvider minecraftProvider) {
        this.badlionVersion = badlionVersion;
        this.minecraftProvider = minecraftProvider;
        this.project = project;

        patchFile = BadlionGradle.getVersionCacheFile(project, badlionVersion, "badlionPatches");
        badlionClientFile = BadlionGradle.getVersionCacheFile(project, badlionVersion, "badlionOfficial.jar");

        downloadPatch();
        applyPatch();
    }

    private void applyPatch() {
        project.getLogger().lifecycle("Applying badlion patch");
        try {
            ZipPatcher.patch(minecraftProvider.minecraftClientJar, patchFile, badlionClientFile);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void downloadPatch() {
        project.getLogger().lifecycle("Downloading badlion patch");
        try {
            String badlionFormattedVersion = minecraftProvider.version.replace('.', '_');
            DownloadUtils.downloadIfChanged(new URL("https://client-jars.badlion.net/distribution/" + badlionVersion + "/VERSION_" + badlionFormattedVersion + "/PRODUCTION/patch_no_assets"), patchFile, project.getLogger());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
