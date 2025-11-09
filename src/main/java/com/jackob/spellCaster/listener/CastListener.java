package com.jackob.spellCaster.listener;

import com.jackob.spellCaster.enums.MouseClick;
import com.jackob.spellCaster.manager.CastManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CastListener implements Listener {

    private final CastManager castManager;

    public CastListener(CastManager castManager) {
        this.castManager = castManager;
    }

    @EventHandler
    public void onWandClick(PlayerInteractEvent e) {
        final Player player = e.getPlayer();

        if (player.getInventory().getItemInMainHand().getType() != Material.STICK) return;

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            castManager.updateCombination(player, MouseClick.RIGHT);
        } else {
            castManager.updateCombination(player, MouseClick.LEFT);
        }
    }

}
