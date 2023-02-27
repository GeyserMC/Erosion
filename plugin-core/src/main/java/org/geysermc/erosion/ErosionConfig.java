package org.geysermc.erosion;

import org.geysermc.configutils.ConfigUtilities;
import org.geysermc.configutils.file.codec.PathFileCodec;
import org.geysermc.configutils.file.template.ResourceTemplateReader;
import org.geysermc.configutils.updater.change.Changes;

import java.nio.file.Path;

public final class ErosionConfig {
    private static ErosionConfig INSTANCE;

    private boolean unixDomainEnabled = false;
    private String unixDomainAddress = "/tmp/erosion.sock";

    public boolean isUnixDomainEnabled() {
        return unixDomainEnabled;
    }

    public String getUnixDomainAddress() {
        return unixDomainAddress;
    }

    public static ErosionConfig load(Path path) {
        ConfigUtilities utilities = ConfigUtilities.builder()
                .fileCodec(PathFileCodec.of(path))
                .configFile("config.yml")
                .templateReader(ResourceTemplateReader.of(ErosionConfig.class))
                .template("config.yml")
                .changes(Changes.builder().build())
                .build();
        try {
            return (INSTANCE = utilities.executeOn(ErosionConfig.class));
        } catch (Throwable throwable) {
            throw new RuntimeException(
                    "Failed to load the config! Try to delete the config file if this error persists",
                    throwable
            );
        }
    }

    public static ErosionConfig getInstance() {
        return INSTANCE;
    }
}
