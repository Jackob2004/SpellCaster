package com.jackob.spellCaster;

public enum MouseClick {

    LEFT('L'),
    RIGHT('R');

    private final char letterRepresentation;

    MouseClick(char letterRepresentation) {
        this.letterRepresentation = letterRepresentation;
    }

    public char getLetterRepresentation() {
        return letterRepresentation;
    }
}
