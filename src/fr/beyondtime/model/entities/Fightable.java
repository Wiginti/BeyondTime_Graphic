package fr.beyondtime.model.entities;

public interface Fightable {
	int getDamage();
	void attack(Entity entity);
}