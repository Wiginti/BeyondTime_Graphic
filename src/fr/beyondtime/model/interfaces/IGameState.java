package fr.beyondtime.model.interfaces;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.map.GameMap;

public interface IGameState {
    Hero getHero();
    GameMap getMap();
    double getHealth();
    void updateHealth(double amount);
    boolean isGameOver();
    void setGameOver(boolean gameOver);
} 