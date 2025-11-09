package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import com.jackob.spellCaster.util.SpellsUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import static com.jackob.spellCaster.util.SpellsUtil.playCircleEffect;

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

    private void playLineEffect(Location start, Location end, Player damageSource) {
        final World world = start.getWorld();
        final Vector direction = start.getDirection();

        while (!start.equals(end)) {
            start.add(direction);

            world.spawnParticle(Particle.DRIPPING_LAVA, start, 5, 0.5, 0.2, 0.5);

            for (Entity entity : start.getNearbyEntities(1, 1, 1)) {
                if (entity.equals(damageSource)) continue;

                if (entity instanceof Damageable d) {
                    d.damage(2, damageSource);
                }
            }
        }

    }

    @Override
    public void cast(Player caster) {
        final Location startLocation = caster.getLocation();

        playCircleEffect(plugin, startLocation.clone(), SpellsUtil.CircleDirection.INWARD,4, Particle.WITCH);

        final Location finalDestination = teleport(caster);

        playLineEffect(startLocation, finalDestination, caster);
        playCircleEffect(plugin, finalDestination, SpellsUtil.CircleDirection.OUTWARD,4, Particle.WITCH);

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
