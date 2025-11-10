package com.jackob.spellCaster.manager;

import com.jackob.spellCaster.SpellCaster;
import com.jackob.spellCaster.enums.Combination;
import com.jackob.spellCaster.enums.MouseClick;
import com.jackob.spellCaster.spells.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.*;

public class CastManager {

    private final SpellCaster plugin;

    private final static long MAX_TIME_DIFFERENCE = 700;

    private final static int COMBINATION_LENGTH = 3;

    private final Map<UUID, List<MouseClick>> playerCombinations;

    private final Map<UUID, Long> clickTimestamps;

    private final Map<Combination, Castable> spells;

    public CastManager(SpellCaster plugin) {
        this.plugin = plugin;
        this.playerCombinations = new HashMap<>();
        this.clickTimestamps = new HashMap<>();
        this.spells = new HashMap<>();

        this.initSpells();
    }

    public void updateCombination(Player caster, MouseClick mouseClick) {
        final UUID casterId = caster.getUniqueId();
        final List<MouseClick> combination = playerCombinations.getOrDefault(casterId, new ArrayList<>(COMBINATION_LENGTH));

        if (combination.isEmpty()) {
            combination.add(mouseClick);
            playerCombinations.put(casterId, combination);
            clickTimestamps.put(casterId, System.currentTimeMillis());
            sendCombinationInfo(caster, combination);
            return;
        }

        if (!clickedOnTime(casterId)) {
            combination.clear();
            combination.add(mouseClick);
            clickTimestamps.put(casterId, System.currentTimeMillis());
            sendCombinationInfo(caster, combination);
            return;
        }

        combination.add(mouseClick);
        sendCombinationInfo(caster, combination);

        if (combination.size() == COMBINATION_LENGTH) {
            castSpell(caster, combination);
            combination.clear();
        }

        clickTimestamps.put(casterId, System.currentTimeMillis());
    }

    private boolean clickedOnTime(UUID casterId) {
        final long lastClickTime = clickTimestamps.get(casterId);
        final long timeDifference = System.currentTimeMillis() - lastClickTime;

        return timeDifference < MAX_TIME_DIFFERENCE;
    }

    private void sendCombinationInfo(Player caster, List<MouseClick> mouseClicks) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < mouseClicks.size(); i++) {
            sb.append("<green>").append(mouseClicks.get(i).getLetterRepresentation()).append("</green>");

            if (i != mouseClicks.size() - 1) {
                sb.append("<gray> - <gray>");
            }
        }

        while (sb.chars().filter(c -> c == '-').count() != 2) {
            sb.append("<gray> - ?<gray>");
        }

        caster.sendActionBar(MiniMessage.miniMessage().deserialize(sb.toString()));
        caster.playSound(caster.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
    }

    private void initSpells() {
        spells.put(Combination.RRR, new TeleportSpell(plugin));
        spells.put(Combination.RRL, new IceShardsSpell(plugin));
        spells.put(Combination.LLL, new BoulderSpell(plugin));
        spells.put(Combination.RLR, new OrbsSpell(plugin));
    }

    private Optional<Combination> constructCombination(List<MouseClick> mouseClicks) {
        final StringBuilder sb = new StringBuilder();

        for (MouseClick mouseClick : mouseClicks) {
            sb.append(mouseClick.getLetterRepresentation());
        }

        try {
            return Optional.of(Combination.valueOf(sb.toString()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private void castSpell(Player caster, List<MouseClick> mouseClicks) {
        constructCombination(mouseClicks)
                .map(spells::get)
                .ifPresentOrElse(
                        spell -> spell.cast(caster),
                        () -> onSpellNotFound(caster)
                );
    }

    private void onSpellNotFound(Player caster) {
        caster.sendMessage(Component.text("No such spell exists!").color(NamedTextColor.RED));
        caster.playSound(caster.getLocation(), Sound.ENTITY_WANDERING_TRADER_NO, 1, 1);
    }

}
