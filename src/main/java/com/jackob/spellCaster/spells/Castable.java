package com.jackob.spellCaster.spells;

import org.bukkit.entity.Player;

public interface Castable {

    void cast(Player caster);

    int getManaCost();

    String getName();

}
