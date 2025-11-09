package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class TeleportSpell implements Castable {

    private final static int TELEPORT_RANGE = 15;

    private final SpellCaster plugin;

    public TeleportSpell(SpellCaster plugin) {
        this.plugin = plugin;
    }

    private Location teleport(Player player) {
        final Vector direction = player.getLocation().getDirection();
        final Location destination = player.getLocation();

        int idx = 0;
        while (idx < TELEPORT_RANGE) {
            destination.add(direction);

            if (!destination.getBlock().isPassable()) {
                destination.subtract(direction);
                break;
            }

            idx++;
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.teleport(destination), 1);

        return destination;
    }

    @Override
    public void cast(Player caster) {
        final Location finalDestination = teleport(caster);
        caster.playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

}
