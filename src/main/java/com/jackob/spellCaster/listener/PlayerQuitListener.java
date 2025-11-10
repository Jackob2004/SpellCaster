package com.jackob.spellCaster.listener;

import com.jackob.spellCaster.manager.CastManager;
import com.jackob.spellCaster.manager.ManaManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final ManaManager manaManager;

    private final CastManager castManager;

    public PlayerQuitListener(ManaManager manaManager, CastManager castManager) {
        this.manaManager = manaManager;
        this.castManager = castManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final UUID playerUUID = event.getPlayer().getUniqueId();

        manaManager.unregisterManaBank(playerUUID);
        castManager.unregisterCaster(playerUUID);
    }
}
