package com.github.squishylib.spigot;

import com.github.squishylib.common.logger.Level;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpigotPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable() {
    }
}
