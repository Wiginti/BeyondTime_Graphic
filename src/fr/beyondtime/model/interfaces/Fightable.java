package fr.beyondtime.model.interfaces;

import fr.beyondtime.model.entities.*;

public interface Fightable {
	
	public abstract void attack(Entity entity);
	
	public abstract int getDamage();

}
