package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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

    private void playCircleEffect(Location location, CircleDirection circleDirection) {

        new BukkitRunnable() {
            int repetitions = 4;
            int radius = circleDirection.startRadius;

            @Override
            public void run() {
                if (repetitions <= 0) {
                    this.cancel();
                    return;
                }

                spawnCircle(location, radius);

                repetitions--;
                radius+= circleDirection.step;
            }
        }.runTaskTimer(plugin, 1, 2);

    }

    private void spawnCircle(Location location, int radius) {
        final World world = location.getWorld();

        for (int i = 0; i < 360; i++) {
            double radians = Math.toRadians(i);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;

            location.add(x, 0, z);
            world.spawnParticle(Particle.WITCH, location, 1);
            location.subtract(x, 0, z);
        }

    }

    @Override
    public void cast(Player caster) {
        playCircleEffect(caster.getLocation(), CircleDirection.INWARD);
        final Location finalDestination = teleport(caster);
        playCircleEffect(finalDestination, CircleDirection.OUTWARD);

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


    private enum CircleDirection {
        INWARD(4, -1),
        OUTWARD(1, 1);

        private final int startRadius;

        private final int step;

        CircleDirection(int startRadius, int step) {
            this.startRadius = startRadius;
            this.step = step;
        }
    }

}
