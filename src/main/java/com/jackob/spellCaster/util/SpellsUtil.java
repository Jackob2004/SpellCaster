package com.jackob.spellCaster.util;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class SpellsUtil {

    public static void playCircleEffect(SpellCaster plugin, Location location, CircleDirection circleDirection, final int repetitions, Particle particle) {

        new BukkitRunnable() {
            int counter = repetitions;
            int radius = circleDirection.startRadius;

            @Override
            public void run() {
                if (counter <= 0) {
                    this.cancel();
                    return;
                }

                spawnCircle(location, radius, particle);

                counter--;
                radius+= circleDirection.step;
            }
        }.runTaskTimer(plugin, 1, 2);

    }

    private static void spawnCircle(Location location, int radius, Particle particle) {
        final World world = location.getWorld();

        for (int i = 0; i < 360; i++) {
            double radians = Math.toRadians(i);
            double x = Math.cos(radians) * radius;
            double z = Math.sin(radians) * radius;

            location.add(x, 0, z);
            world.spawnParticle(particle, location, 1);
            location.subtract(x, 0, z);
        }

    }

    public enum CircleDirection {
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
