package io.github.badlionmoddinggroup.badliongradle;

import org.gradle.api.Project;

public class BadlionGradleExtension {

    public String badlionVersion;
    public String minecraftVersion;
    public String minecraftMappingsUrl;
    public String badlionMappingsUrl;
    public String badlionIntermediariesUrl;

    public BadlionGradleExtension(Project project) {
    }
}
