package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import com.jackob.spellCaster.util.SpellsUtil;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class BoulderSpell implements Castable {

    private final SpellCaster plugin;

    public BoulderSpell(SpellCaster plugin) {
        this.plugin = plugin;
    }

    private void playBoulderEffect(Location location, Player player) {
        final List<Display> fragments = spawnBoulder(location);
        final World world = location.getWorld();

        new BukkitRunnable() {
            final float fallingFactor = 0.15f;
            boolean canceled = false;

            @Override
            public void run() {
                if (!location.getBlock().isEmpty() && !canceled) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        this.cancel();
                        fragments.forEach(Entity::remove);
                    }, 20 * 4);

                    playGroundHitEffect(location.clone(), player);
                    playExplosionEffect(location.clone());
                    canceled = true;
                }

                fragments.forEach(display -> {
                    Location loc = display.getLocation();
                    loc.setY(loc.getY() - fallingFactor);
                    display.teleport(loc);
                });

                world.spawnParticle(Particle.ASH, location.clone().add(0, 2, 0), 8,1,1,1);
                location.setY(location.getY() - fallingFactor);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private void playGroundHitEffect(Location location, Player damager) {
        SpellsUtil.playCircleEffect(plugin, location, SpellsUtil.CircleDirection.OUTWARD, 15, Particle.WAX_OFF);

        for (Entity e : location.getNearbyEntities(15, 15, 15)) {
            if (e.equals(damager)) continue;

            e.setVelocity(new Vector(0, 1.5, 0));

            if (e instanceof Damageable d) {
                d.damage(3, damager);
            }
        }

        damager.playSound(damager.getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
    }

    private List<Display> spawnBoulder(Location location) {
        final World world = location.getWorld();

        final int size = 5;
        final int halfSize = size / 2;
        final List<Display> fragments = new ArrayList<>(size * size * size);

        for (int x = -halfSize; x < halfSize; x++) {
            for (int y = -halfSize; y < halfSize; y++) {
                for (int z = -halfSize; z < halfSize; z++) {
                    location.add(x, y, z);
                    fragments.add(spawnBoulderFragment(location, world));
                    location.subtract(x, y, z);
                }
            }
        }

        return fragments;
    }

    private BlockDisplay spawnBoulderFragment(Location location, World world) {
        final Matrix4f matrix = new Matrix4f()
                .scale(SpellsUtil.randomFloat(-0.25f, 2))
                .rotateXYZ(
                        (float) Math.toRadians(360 - SpellsUtil.randomFloat(10, 45)), // rotate -
                        0,
                        (float) Math.toRadians(SpellsUtil.randomFloat(10, 45)) // rotate +
                );

        return world.spawn(location, BlockDisplay.class, e -> {
            e.setBlock(Material.STONE.createBlockData());
            e.setTransformationMatrix(matrix);
            e.setTeleportDuration(1);
        });
    }

    private void playExplosionEffect(Location location) {
        final World world = location.getWorld();

        final int elements = SpellsUtil.randomInt(5, 20);
        final List<Entity> explosionFragments = new ArrayList<>(elements);

        for (int i = 0; i < elements; i++) {
            Entity boulderFragment = spawnBoulderFragment(location, world);
            Entity stand = spawnExplosionFragment(location, world, boulderFragment);

            explosionFragments.add(stand);
            explosionFragments.add(boulderFragment);

            stand.setVelocity(new Vector(SpellsUtil.randomFloat(-3,3), SpellsUtil.randomFloat(1.1f,4), SpellsUtil.randomFloat(-3,3)));
        }

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> explosionFragments.forEach(Entity::remove), 4 * 20);
    }

    private ArmorStand spawnExplosionFragment(Location location, World world, Entity visualPart) {
        return world.spawn(location, ArmorStand.class, e -> {
            e.setInvulnerable(true);
            e.setInvisible(true);
            e.setSmall(SpellsUtil.randomFloat() > 0);
            e.addPassenger(visualPart);
        });
    }

    @Override
    public void cast(Player caster) {
        final Vector direction = caster.getLocation().getDirection().normalize().multiply(12);
        final Location location = caster.getLocation().add(direction).add(0,10,0);

        playBoulderEffect(location, caster);
        caster.playSound(caster.getLocation(), Sound.BLOCK_STONE_BREAK, 1, 1);
    }

    @Override
    public int getManaCost() {
        return 600;
    }

    @Override
    public String getName() {
        return "Boulder";
    }
}
