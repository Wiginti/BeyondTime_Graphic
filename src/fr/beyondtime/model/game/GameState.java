package fr.beyondtime.model.game;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.interfaces.IGameState;
import fr.beyondtime.model.map.GameMap;

/**
 * Represents the overall state of the game at any given time.
 * Holds references to the player character (Hero), the current map, and game status flags.
 * Implements the IGameState interface.
 */
public class GameState implements IGameState {
    private Hero hero;
    private GameMap map;
    private boolean gameOver;

    /** Default map width/height if no specific map is loaded. */
    private static final int DEFAULT_MAP_SIZE = 16;
    /** Default cell size used when creating the default map. */
    private static final int DEFAULT_CELL_SIZE = 50;

    /**
     * Constructs a new GameState.
     * Initializes a default Hero and a default square GameMap.
     * Sets the game over flag to false.
     */
    public GameState() {
        this.hero = new Hero();
        // Creates a default map if none is loaded later
        this.map = new GameMap(DEFAULT_MAP_SIZE, DEFAULT_MAP_SIZE, DEFAULT_CELL_SIZE);
        this.gameOver = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hero getHero() {
        return hero;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GameMap getMap() {
        return map;
    }

    /**
     * Sets the current game map.
     * Used typically when loading a map file.
     *
     * @param map The GameMap to set as the current map.
     */
    public void setMap(GameMap map) {
        this.map = map;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getHealth() {
        // Delegate to the Hero object
        return (hero != null) ? hero.getHealth() : 0;
    }

    /**
     * {@inheritDoc}
     * Note: Directly sets the hero's health, potentially bypassing clamping in Hero class.
     * Consider using hero.addHealth or hero.removeHealth for safer updates.
     */
    @Override
    public void updateHealth(double amount) {
        if (hero != null) {
            // Cast to int as Hero health is int based.
            hero.setHealth((int) amount);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
} 