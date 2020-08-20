package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.Project;

public class BadlionGradleExtension {

    public String badlionVersion;
    public String minecraftVersion;
    public String minecraftMappingsUrl;
    public String badlionMappingsUrl;

    //stuff not done through gradle extension
    private final Project project;

    public BadlionGradleExtension(Project project) {
        this.project = project;
    }
}
