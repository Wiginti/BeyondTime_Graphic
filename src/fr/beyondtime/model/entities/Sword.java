package fr.beyondtime.model.entities;

/**
 * Classe représentant une arme (épée).
 */
public class Sword extends Item {
	/** Points d'attaque de l'épée. */
    private int damageBoost;
    private boolean isSelected;
    private final static int DEFAULT_DAMAGE_BOOST = 10;
    private final static String DEFAULT_IMAGE_PATH = "/fr/beyondtime/resources/sword.jpg";

    /** Constructeur de l'épée avec les dégâts infligés. */
    public Sword(int damageBoost) {
        super("Épée", "Augmente les dégâts de " + damageBoost + " points", DEFAULT_IMAGE_PATH);
        this.damageBoost = Math.max(damageBoost, 0);
        this.isSelected = false;
    }

    public Sword() {
        this(DEFAULT_DAMAGE_BOOST);
    }

    /** Utilise l'épée sur le héro. */
    @Override
    public void use(Hero hero) {
        if (hero == null) {
            throw new IllegalArgumentException("Le héros ne peut pas être null");
        }
        
        isSelected = !isSelected; // Inverse l'état de sélection
        
        if (isSelected) {
            hero.addDamage(damageBoost);
            System.out.println("L'épée a été sélectionnée ! Les dégâts du héros ont été augmentés de " + damageBoost + " points.");
        } else {
            hero.addDamage(-damageBoost);
            System.out.println("L'épée a été désélectionnée ! Les dégâts du héros ont été réduits de " + damageBoost + " points.");
        }
    }
    
    /**
     * 
     * @return damageBoost
     */
    public int getDamageBoost() {
        return this.damageBoost;
    }
    
    /** Fixer les dégâts de l'épée
    /**
     * 
     * @param damageBoost
     */
    public void setDamageBoost(int damageBoost) {
        this.damageBoost = Math.max(damageBoost, 0);
    }

    /**
     * 
     * @return isSelected
     */
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return getName() + " (+" + damageBoost + " dégâts)" + (isSelected ? " [Sélectionnée]" : "");
    }
} 