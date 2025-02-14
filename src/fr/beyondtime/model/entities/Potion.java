package fr.beyondtime.model.entities;

public class Potion extends Item implements Usable {
	
	private int healthAmount;
	private final static int healthAmountDefault = 50;
	
	public Potion(int amount){
		this.healthAmount = amount;
	}
	
	public Potion() {
		this.healthAmount = healthAmountDefault;
	}
	
	@Override
	public String toString() {
		return "Potion";
	}

	@Override
	public void use(Hero hero) {
		if(hero.getHealth() + healthAmount > Hero.DEFAULT_HEALTH) {
			hero.setHealth(Hero.DEFAULT_HEALTH);
		}
		else {
			hero.addHealth(healthAmount);	
		}
	}
	
	public int getHealthAmount() {
		return this.healthAmount;
	}
	
	

}
