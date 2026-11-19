package it.unicam.cs.mpgc.rpg125904.domain;

public enum PlayerRole {

    ESPLORATORE(
            "Esploratore",
            "Avventuriero equilibrato adatto a ogni situazione.",
            100, 5, 5, 1, false
    ),
    GUARDIANO(
            "Guardiano",
            "Combattente robusto: alti HP, alta Forza e subisce meno danni.",
            130, 8, 3, 4, false
    ),
    ARCHEOLOGO(
            "Archeologo",
            "Studioso esperto: alta Saggezza e rivela indizi extra negli enigmi.",
            85, 3, 8, 0, true
    );

    private final String displayName;
    private final String description;
    private final int initialMaxHp;
    private final int initialStrength;
    private final int initialWisdom;
    private final int damageReduction;
    private final boolean revealsExtraHint;

    PlayerRole(
            String displayName,
            String description,
            int initialMaxHp,
            int initialStrength,
            int initialWisdom,
            int damageReduction,
            boolean revealsExtraHint
    ) {
        this.displayName = displayName;
        this.description = description;
        this.initialMaxHp = initialMaxHp;
        this.initialStrength = initialStrength;
        this.initialWisdom = initialWisdom;
        this.damageReduction = damageReduction;
        this.revealsExtraHint = revealsExtraHint;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getInitialMaxHp() {
        return initialMaxHp;
    }

    public int getInitialStrength() {
        return initialStrength;
    }

    public int getInitialWisdom() {
        return initialWisdom;
    }

    public int getDamageReduction() {
        return damageReduction;
    }

    public boolean revealsExtraHint() {
        return revealsExtraHint;
    }

    @Override
    public String toString() {
        return displayName;
    }
}