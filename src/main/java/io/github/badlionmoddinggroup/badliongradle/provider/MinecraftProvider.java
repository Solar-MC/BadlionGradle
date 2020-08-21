package io.github.badlionmoddinggroup.badliongradle.provider;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import io.github.badlionmoddinggroup.badliongradle.BadlionGradle;
import io.github.badlionmoddinggroup.badliongradle.util.McVerManifest;
import io.github.badlionmoddinggroup.badliongradle.util.DownloadUtils;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class MinecraftProvider {

    public static final Gson GSON = new Gson();

    private final Project project;
    public final String version;

    public File minecraftClientJar;

    public McVerManifest mcVerManifest;
    private McVerManifest.Version mcVer;
    private MinecraftVersionInfo versionInfo;

    public MinecraftProvider(String minecraftVersion, Project project) {
        this.version = minecraftVersion;
        this.project = project;
        retrieveFiles();
    }

    private void retrieveFiles() {
        try {
            minecraftClientJar = new File(BadlionGradle.getCacheFolder(project), "minecraft-" + version + "-client.jar");
            File versionManifest = new File(BadlionGradle.getCacheFolder(project), "version_manifest.json");
            File versionJson = new File(BadlionGradle.getCacheFolder(project), "minecraft-" + version + "-info.json");

            DownloadUtils.downloadIfChanged(new URL("https://launchermeta.mojang.com/mc/game/version_manifest.json"), versionManifest, project.getLogger());
            String versionManifestContent = Files.asCharSource(versionManifest, StandardCharsets.UTF_8).read();
            mcVerManifest = GSON.fromJson(versionManifestContent, McVerManifest.class);
            for(McVerManifest.Version mcVer : mcVerManifest.minecraftVersions){
                if(mcVer.version.equals(version)){
                    this.mcVer = mcVer;
                    System.out.println("found minecraft version called " + version);
                }
            }

            DownloadUtils.downloadIfChanged(new URL(mcVer.url), versionJson, project.getLogger());
            try (FileReader reader = new FileReader(versionJson)) {
                versionInfo = GSON.fromJson(reader, MinecraftVersionInfo.class);
            }
            project.getLogger().lifecycle("Downloading " + version + " client");
            downloadJars();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadJars() throws IOException {
        DownloadUtils.downloadIfChanged(new URL(versionInfo.downloads.get("client").url), minecraftClientJar, project.getLogger());
    }

    private static class MinecraftVersionInfo {
        @SerializedName("downloads")
        public Map<String, Downloads> downloads;

        public static class Downloads {
            @SerializedName("url")
            public String url;

            @SerializedName("sha1")
            public String sha1;
        }
    }
}
