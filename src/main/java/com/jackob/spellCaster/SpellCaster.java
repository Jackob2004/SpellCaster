package com.jackob.spellCaster;

import com.jackob.spellCaster.listener.CastListener;
import com.jackob.spellCaster.manager.CastManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpellCaster extends JavaPlugin {

    @Override
    public void onEnable() {
        CastManager castManager = new CastManager();

        getServer().getPluginManager().registerEvents(new CastListener(castManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
