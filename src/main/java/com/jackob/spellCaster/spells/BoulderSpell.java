package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoulderSpell implements Castable {

    private final SpellCaster plugin;

    public BoulderSpell(SpellCaster plugin) {
        this.plugin = plugin;
    }

    private float randomNum(float min, float max) {
        final Random rand = new Random();

        return rand.nextFloat(min, max);
    }

    private void playBoulderEffect(Location location) {
        List<Display> fragments = spawnBoulder(location);

        new BukkitRunnable() {
            final float fallingFactor = 0.1f;
            @Override
            public void run() {
                if (!location.getBlock().isEmpty()) {
                    this.cancel();
                    fragments.forEach(Entity::remove);
                    return;
                }

                fragments.forEach(display -> {
                    Location loc = display.getLocation();
                    loc.setY(loc.getY() - fallingFactor);
                    display.teleport(loc);
                });

                location.setY(location.getY() - fallingFactor);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private void playGroundHitEffect(List<Display> fragments) {

    }

    private List<Display> spawnBoulder(Location location) {
        final int size = 5;
        final int halfSize = size / 2;
        final List<Display> fragments = new ArrayList<>(size * size * size);

        for (int x = -halfSize; x < halfSize; x++) {
            for (int y = -halfSize; y < halfSize; y++) {
                for (int z = -halfSize; z < halfSize; z++) {
                    location.add(x, y, z);
                    fragments.add(spawnBoulderFragment(location));
                    location.subtract(x, y, z);
                }
            }
        }

        return fragments;
    }

    private BlockDisplay spawnBoulderFragment(Location location) {
        final World world = location.getWorld();
        final Matrix4f matrix = new Matrix4f()
                .scale(randomNum(-0.25f, 2))
                .rotateXYZ((float) Math.toRadians(360 - randomNum(10, 45)), 0, (float) Math.toRadians(randomNum(10, 45)));

        return world.spawn(location, BlockDisplay.class, e -> {
            e.setBlock(Material.STONE.createBlockData());
            e.setTransformationMatrix(matrix);
            e.setTeleportDuration(1);
        });
    }

    @Override
    public void cast(Player caster) {
        Vector direction = caster.getEyeLocation().getDirection().normalize().multiply(12);
        Location location = caster.getLocation().add(direction);

        playBoulderEffect(location);
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
