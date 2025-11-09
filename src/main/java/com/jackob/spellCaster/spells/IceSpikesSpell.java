package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;

import java.util.*;

public class IceSpikesSpell implements Castable {

    private final SpellCaster plugin;

    public IceSpikesSpell(SpellCaster plugin) {
        this.plugin = plugin;
    }

    private void playSpikesEffect(Player player) {
        Queue<Entity> entities = new LinkedList<>();

        new BukkitRunnable() {
            int repetitions = 20;

            final Vector direction = player.getEyeLocation().getDirection();
            final Location location = player.getLocation();
            final World world = player.getWorld();

            @Override
            public void run() {
                if (repetitions <= 0) {
                    this.cancel();
                    removeSpikes(entities);
                    return;
                }

                float offset = randomNum();

                location.add(direction).add(offset, 0, offset);
                entities.offer(spawnSpike(location, world));
                freezeEffect(location, player);
                world.spawnParticle(Particle.SNOWFLAKE, location, 3, offset, offset, offset);
                location.subtract(offset, 0, offset);

                player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 1.0f);
                repetitions--;
            }
        }.runTaskTimer(plugin, 1, 2);

    }

    private void removeSpikes(Queue<Entity> entities) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (entities.isEmpty()) {
                    this.cancel();
                    return;
                }

                entities.poll().remove();
            }
        }.runTaskTimer(plugin, 1, 3);
    }

    private void freezeEffect(Location location, Player damager) {
        location.getNearbyEntities(1.5,1.5,1.5).forEach(entity -> {
            entity.setFreezeTicks(5 * 20);

            if (entity instanceof Damageable) {
                ((Damageable) entity).damage(2, damager);
            }
        });
    }

    private BlockDisplay spawnSpike(Location location, World world) {
        final Matrix4f matrix = new Matrix4f(1f,0f,0f,0f,randomNum(),1f,0f,0f,randomNum(),randomNum(),1f,0f,0f,0f,0f,1f);

        return world.spawn(location, BlockDisplay.class, e -> {
            e.setBlock(Material.BLUE_ICE.createBlockData());
            e.setTransformationMatrix(matrix);
            e.setBrightness(new Display.Brightness(15, 15));
        });
    }

    private float randomNum() {
        final Random rand = new Random();

        return rand.nextFloat(-1, 1);
    }

    @Override
    public void cast(Player caster) {
        playSpikesEffect(caster);
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
