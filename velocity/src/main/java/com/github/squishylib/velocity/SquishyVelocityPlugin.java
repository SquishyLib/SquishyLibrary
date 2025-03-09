package com.github.squishylib.velocity;

import com.github.squishylib.configuration.ConfigurationException;
import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
    id = "squishylibrary",
    name = "SquishyLibrary",
    version = "1.0.0"
)
public class SquishyVelocityPlugin {

    @Inject
    public SquishyVelocityPlugin(ComponentLogger componentLogger) {
        componentLogger.info("Enabled");
    }
}
