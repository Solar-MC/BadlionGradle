package io.github.badlionmoddinggroup.badliongradle;

import io.github.badlionmoddinggroup.badliongradle.provider.BadlionProvider;
import org.gradle.api.Project;

public class BadlionGradleExtension {

    public String badlionVersion;
    public String minecraftVersion;
    public String minecraftMappingsUrl;
    public String badlionMappingsUrl;
    public String badlionIntermediariesUrl;

    public BadlionProvider badlionProvider;

    public BadlionGradleExtension(Project project) {
    }
}
