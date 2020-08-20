package io.github.badlionmoddinggroup.badliongradle.minecraft;

import com.google.common.io.Files;
import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import io.github.badlionmoddinggroup.badliongradle.util.DownloadUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MinecraftProvider {

    private final String version;
    private final Project project;

    private File versionMetadata;
    private File versionJson;

    public MinecraftProvider(String version, Project project) {
        this.version = version;
        this.project = project;
        retrieveFiles();
    }

    private void retrieveFiles() {
        try {
            File manifests = new File(BadlionGradle.getProjectCacheFolder(project), "version_manifest.json");
            DownloadUtils.downloadIfChanged(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), manifests, project.getLogger());
            String versionManifest = Files.asCharSource(manifests, StandardCharsets.UTF_8).read();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
