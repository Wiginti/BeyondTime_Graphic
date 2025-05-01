package fr.beyondtime.model.entities;

public class Sword extends Item {
    private int damageBoost;
    private final static int DEFAULT_DAMAGE_BOOST = 10;
    private final static String DEFAULT_IMAGE_PATH = "/fr/beyondtime/resources/sword.jpg";

    public Sword(int damageBoost) {
        super("Épée", "Augmente les dégâts de " + damageBoost + " points", DEFAULT_IMAGE_PATH);
        this.damageBoost = Math.max(damageBoost, 0);
    }

    public Sword() {
        this(DEFAULT_DAMAGE_BOOST);
    }

    @Override
    public void use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Le héros ne peut pas être null");
        }
        
        hero.addDamage(damageBoost);
        System.out.println("L'épée a été utilisée ! Les dégâts du héros ont été augmentés de " + damageBoost + " points.");
    }

    public int getDamageBoost() {
        return this.damageBoost;
    }

    public void setDamageBoost(int damageBoost) {
        this.damageBoost = Math.max(damageBoost, 0);
    }

    @Override
    public String toString() {
        return getName() + " (+" + damageBoost + " dégâts)";
    }
} 