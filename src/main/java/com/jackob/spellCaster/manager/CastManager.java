package com.jackob.spellCaster.manager;

import com.jackob.spellCaster.MouseClick;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.*;

public class CastManager {

    private final static long MAX_TIME_DIFFERENCE = 500;

    private final static int COMBINATION_LENGTH = 3;

    private final Map<UUID, List<MouseClick>> playerCombinations;

    private final Map<UUID, Long> clickTimestamps;

    public CastManager() {
        this.playerCombinations = new HashMap<>();
        this.clickTimestamps = new HashMap<>();
    }

    public void updateCombination(Player caster, MouseClick mouseClick) {
        final UUID casterId = caster.getUniqueId();
        final List<MouseClick> combination = playerCombinations.getOrDefault(casterId, new ArrayList<>(COMBINATION_LENGTH));

        if (combination.isEmpty()) {
            combination.add(mouseClick);
            playerCombinations.put(casterId, combination);
            clickTimestamps.put(casterId, System.currentTimeMillis());
            return;
        }

        if (!clickedOnTime(casterId)) {
            combination.clear();
            combination.add(mouseClick);
            clickTimestamps.put(casterId, System.currentTimeMillis());
            return;
        }

        combination.add(mouseClick);

        if (combination.size() == COMBINATION_LENGTH) {
            constructCombination(caster, combination);
            combination.clear();
        }

        clickTimestamps.put(casterId, System.currentTimeMillis());
    }

    private boolean clickedOnTime(UUID casterId) {
        final long lastClickTime = clickTimestamps.get(casterId);
        final long timeDifference = System.currentTimeMillis() - lastClickTime;

        return timeDifference < MAX_TIME_DIFFERENCE;
    }

    // just for testing purposes
    private void constructCombination(Player caster, List<MouseClick> combination) {
        final StringBuilder sb = new StringBuilder();

        for (MouseClick mouseClick : combination) {
            sb.append(mouseClick.getLetterRepresentation());
        }

        caster.sendActionBar(Component.text(sb.toString()));
    }
}
