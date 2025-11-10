package com.jackob.spellCaster.spells;

import com.jackob.spellCaster.SpellCaster;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class OrbsSpell implements Castable {

    private final SpellCaster plugin;

    public OrbsSpell(SpellCaster plugin) {
        this.plugin = plugin;
    }

    @Override
    public void cast(Player caster) {
        Location location1 = caster.getLocation().add(2, 5, 2);
        Location location2 = caster.getLocation().add(-2, 5, -2);

        Orb orb1 = new Orb(plugin, location1, caster);
        Orb orb2 = new Orb(plugin, location2, caster);

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            orb1.removeOrb();
            orb2.removeOrb();
        }, 15 * 20);
    }

    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public String getName() {
        return "";
    }

    private static class Orb implements Listener {

        private final static int MAX_HEALTH = 10;

        private final static int HEAL_RANGE_SQUARED = 10 * 10;

        private int health;

        private final TextDisplay healthBar;

        private final EnderCrystal orb;

        private final Player owner;

        private final BukkitTask healingTask;

        public Orb(SpellCaster plugin, Location location, Player owner) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);

            this.health = MAX_HEALTH;
            this.healthBar = initHealthBar(location);
            this.orb = initOrb(location, this.healthBar);
            this.owner = owner;
            this.healingTask = startHealing(plugin);

            this.owner.playSound(this.owner.getLocation(), Sound.BLOCK_END_PORTAL_FRAME_FILL, 1, 1);
        }

        public void removeOrb() {
            if (healingTask != null && !healingTask.isCancelled()) {
                healingTask.cancel();
            }

            orb.remove();
            healthBar.remove();
            HandlerList.unregisterAll(this);
            owner.playSound(owner.getLocation(), Sound.BLOCK_END_GATEWAY_SPAWN, 1, 1);
        }

        private BukkitTask startHealing(SpellCaster plugin) {
            return new BukkitRunnable() {
                final PotionEffect effect = new PotionEffect(PotionEffectType.REGENERATION, 40, 1);
                final Location orbLocation = orb.getLocation();

                @Override
                public void run() {
                    Location ownerLocation = owner.getLocation();

                    if (orbLocation.distanceSquared(ownerLocation) <= HEAL_RANGE_SQUARED) {
                        owner.addPotionEffect(effect);
                        orb.setBeamTarget(ownerLocation);
                    } else {
                        orb.setBeamTarget(null);
                    }
                }

            }.runTaskTimer(plugin, 1, 3);
        }

        private EnderCrystal initOrb(Location location, Entity healthBar) {
            final World world = location.getWorld();

            return world.spawn(location, EnderCrystal.class, e -> {
                e.setShowingBottom(false);
                e.setCustomNameVisible(false);
                e.addPassenger(healthBar);
            });
        }

        private TextDisplay initHealthBar(Location location){
            final World world = location.getWorld();

            return world.spawn(location, TextDisplay.class, e -> {
                e.setBillboard(Display.Billboard.CENTER);
                e.text(Component.text(MAX_HEALTH + "/" + MAX_HEALTH).color(NamedTextColor.GREEN));
            });
        }

        @EventHandler
        public void onOrbDamage(EntityDamageByEntityEvent e) {
            if (!(e.getEntity() instanceof EnderCrystal)) return;
            if (!e.getEntity().equals(orb)) return;

            e.setCancelled(true);

            if (e.getDamager().equals(owner)) return;

            health--;
            healthBar.text(Component.text(health + "/" + MAX_HEALTH).color(NamedTextColor.GREEN));

            if (health <= 0) {
                removeOrb();
            }
        }

    }
}
