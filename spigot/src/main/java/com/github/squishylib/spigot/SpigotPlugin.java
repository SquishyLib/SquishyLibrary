package com.github.squishylib.spigot;

import com.github.squishylib.common.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.LoggerFactory;

public final class SpigotPlugin extends JavaPlugin {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SpigotPlugin.class);

    @Override
    public void onEnable() {
        Logger logger = new Logger("com.github.squishylib.spigot");
        logger.info("Enabled.");
    }

    @Override
    public void onDisable() {
    }
}
