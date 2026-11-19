package it.unicam.cs.mpgc.rpg125904.domain;

public class Item {

    public enum EffectType {
        NONE,
        HEAL,
        BOOST_STRENGTH,
        BOOST_WISDOM,
        BOOST_MAX_HP
    }

    private final String name;
    private final String description;
    private final EffectType effectType;
    private final int effectValue;
    private final boolean consumable;
    private final String actionLabel;

    public Item(String name, String description) {
        this(name, description, EffectType.NONE, 0, false, null);
    }

    public Item(
            String name,
            String description,
            EffectType effectType,
            int effectValue,
            boolean consumable,
            String actionLabel
    ) {
        this.name = name;
        this.description = description;
        this.effectType = effectType != null ? effectType : EffectType.NONE;
        this.effectValue = effectValue;
        this.consumable = consumable;
        this.actionLabel = actionLabel;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public EffectType getEffectType() {
        return effectType;
    }

    public int getEffectValue() {
        return effectValue;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public String getActionLabel() {
        return actionLabel;
    }

    public boolean isUsable() {
        return effectType != EffectType.NONE;
    }
}