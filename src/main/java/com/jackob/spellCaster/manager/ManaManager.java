package com.jackob.spellCaster.manager;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ManaManager {

    private final static int MAX_MANA = 1000;

    private final static int REGEN_RATE = 100;

    private final SpellCaster plugin;

    private final Map<UUID, Integer> manaBanks;

    public ManaManager(SpellCaster plugin) {
        this.plugin = plugin;
        this.manaBanks = new HashMap<>();
        startManaRegeneration();
    }

    public boolean useManaBank(UUID playerUUID, int manaAmount) {
        int mana = this.manaBanks.getOrDefault(playerUUID, MAX_MANA);

        if (mana - manaAmount < 0) {
            return false;
        }

        mana -= manaAmount;
        manaBanks.put(playerUUID, mana);
        updateExpBar(playerUUID, mana);

        return true;
    }

    public void unregisterManaBank(UUID playerUUID) {
        manaBanks.remove(playerUUID);
    }

    private void startManaRegeneration() {
        new BukkitRunnable() {
            @Override
            public void run() {
                manaBanks.forEach((uuid, mana) -> {
                    if (mana + REGEN_RATE > MAX_MANA) {
                        mana = MAX_MANA;
                    } else {
                        mana += REGEN_RATE;
                    }

                    manaBanks.put(uuid, mana);
                    updateExpBar(uuid, mana);
                });
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void updateExpBar(UUID playerUUID, int manaAmount) {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.setExp((float) manaAmount/ 1000);
        }
    }

}
