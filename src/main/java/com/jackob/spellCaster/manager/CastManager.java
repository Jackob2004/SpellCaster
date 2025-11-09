package com.jackob.spellCaster.manager;

import com.jackob.spellCaster.enums.MouseClick;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
            combination.clear();
        }

        clickTimestamps.put(casterId, System.currentTimeMillis());
    }

    private boolean clickedOnTime(UUID casterId) {
        final long lastClickTime = clickTimestamps.get(casterId);
        final long timeDifference = System.currentTimeMillis() - lastClickTime;

        return timeDifference < MAX_TIME_DIFFERENCE;
    }

    private void sendCombinationInfo(Player caster, List<MouseClick> combination) {
        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < combination.size(); i++) {
            sb.append("<green>").append(combination.get(i).getLetterRepresentation()).append("</green>");

            if (i != combination.size() - 1) {
                sb.append("<gray> - <gray>");
            }
        }

        while (sb.chars().filter(c -> c == '-').count() != 2) {
            sb.append("<gray> - ?<gray>");
        }

        caster.sendActionBar(MiniMessage.miniMessage().deserialize(sb.toString()));
    }

    private void castSpell() {

    }

}
