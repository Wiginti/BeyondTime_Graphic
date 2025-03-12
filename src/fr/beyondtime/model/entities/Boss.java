package fr.beyondtime.model.entities;

import fr.beyondtime.model.interfaces.Fightable;
import fr.beyondtime.model.interfaces.Interactable;

public class Boss extends Entity implements Interactable, Fightable {
	
	private int damageAmount;
	
	public Boss(int healthPoint, int damageAmount, String name) {
		super(healthPoint, name);
		this.damageAmount = damageAmount;
	}
	
	@Override
	public void attack(Entity entity) {
		entity.removeHealth(this.damageAmount);
	}
	
	@Override
	public int getDamage() {
		return this.damageAmount;
	}
	
}

