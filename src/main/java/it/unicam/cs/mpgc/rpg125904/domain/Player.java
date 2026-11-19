package it.unicam.cs.mpgc.rpg125904.domain;

public class Player {

    private final String name;
    private final PlayerRole role;
    private final Inventory inventory;

    private int maxHp;
    private int hp;
    private int strength;
    private int wisdom;
    private int xp;
    private int level;

    public Player(String name) {
        this(name, PlayerRole.ESPLORATORE);
    }

    public Player(String name, PlayerRole role) {
        this.name = name;
        this.role = role != null ? role : PlayerRole.ESPLORATORE;
        this.inventory = new Inventory();
        this.maxHp = this.role.getInitialMaxHp();
        this.hp = this.maxHp;
        this.strength = this.role.getInitialStrength();
        this.wisdom = this.role.getInitialWisdom();
        this.xp = 0;
        this.level = 1;
    }

    public String getName() {
        return name;
    }

    public PlayerRole getRole() {
        return role;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getStrength() {
        return strength;
    }

    public int getWisdom() {
        return wisdom;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int heal(int amount) {
        if (amount <= 0) return 0;
        int before = hp;
        hp = Math.min(maxHp, hp + amount);
        return hp - before;
    }

    public int takeDamage(int rawDamage) {
        int effective = Math.max(1, rawDamage - role.getDamageReduction());
        hp = Math.max(0, hp - effective);
        return effective;
    }

    public void increaseStrength(int delta) {
        strength += delta;
    }

    public void increaseWisdom(int delta) {
        wisdom += delta;
    }

    public void increaseMaxHp(int delta) {
        maxHp += delta;
        hp += delta;
    }

    public boolean addXp(int amount) {
        if (amount <= 0) return false;
        xp += amount;
        boolean leveledUp = false;
        while (xp >= level * 100) {
            xp -= level * 100;
            level++;
            maxHp += 15;
            hp = maxHp;
            strength += 1;
            wisdom += 1;
            leveledUp = true;
        }
        return leveledUp;
    }

    public void restoreStats(int hp, int maxHp, int strength, int wisdom, int xp, int level) {
        this.maxHp = maxHp;
        this.hp = hp;
        this.strength = strength;
        this.wisdom = wisdom;
        this.xp = xp;
        this.level = level;
    }
}