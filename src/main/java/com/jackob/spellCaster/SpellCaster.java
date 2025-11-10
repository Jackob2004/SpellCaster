package com.jackob.spellCaster;

import com.jackob.spellCaster.listener.CastListener;
import com.jackob.spellCaster.listener.PlayerQuitListener;
import com.jackob.spellCaster.manager.CastManager;
import com.jackob.spellCaster.manager.ManaManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpellCaster extends JavaPlugin {

    @Override
    public void onEnable() {
        ManaManager manaManager = new ManaManager(this);
        CastManager castManager = new CastManager(this, manaManager);

        getServer().getPluginManager().registerEvents(new CastListener(castManager), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(manaManager, castManager), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
