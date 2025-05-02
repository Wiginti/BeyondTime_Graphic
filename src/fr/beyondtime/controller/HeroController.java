package fr.beyondtime.controller;

import fr.beyondtime.model.entities.Hero;
import fr.beyondtime.model.entities.Item;
import fr.beyondtime.model.entities.Potion;
import fr.beyondtime.model.entities.Sword;
import fr.beyondtime.model.map.Tile;
import fr.beyondtime.view.components.HUDView;
import fr.beyondtime.view.entities.HeroView;
import fr.beyondtime.controller.game.GameController;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.List;
import java.util.ArrayList;

public class HeroController {
    private Hero hero;
    private HeroView heroView;
    private Runnable onUpdateCallback;
    private List<MonsterController> monsters;
    private HUDView hudView;
    private int selectedSlot = -1; // Aucun slot sélectionné par défaut
    private GameController gameController;
    
    private long lastRegenTime = 0;
    private double speed;
    private long lastUpdate = 0;
    private static final double BASE_SPEED = 120.0; // Vitesse en pixels par seconde
    private static final double SPRINT_MULTIPLIER = 1.8; // Multiplicateur de vitesse pour le sprint
    private static final double MAX_STAMINA = 100.0;
    private static final double STAMINA_DRAIN_RATE = 30.0; // Points de stamina perdus par seconde en sprintant
    private static final double STAMINA_REGEN_RATE = 20.0; // Points de stamina regagnés par seconde au repos
    private double currentStamina = MAX_STAMINA;
    private boolean isSprinting = false;

    // Cache pour les collisions
    private int lastCheckedCol = -1;
    private int lastCheckedRow = -1;
    private boolean lastCollisionResult = false;

    public static final int HERO_WIDTH = 32;
    public static final int HERO_HEIGHT = 32;

    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;

    private GridPane mapGrid;
    private int cellSize;

    private boolean isInvincible = false;
    private static final long INVINCIBILITY_DURATION = 1000; // 1 seconde d'invincibilité
    
    private Timeline poisonDamageLoop;
    private Tile currentPoisonTile;

    /** Constructeur du contrôleur de héros. */
    /**
     * 
     * @param hero
     * @param heroView
     * @param mapGrid
     * @param cellSize
     * @param hudView
     */
    public HeroController(Hero hero, HeroView heroView, GridPane mapGrid, int cellSize, HUDView hudView) {
        this.hero = hero;
        this.heroView = heroView;
        this.mapGrid = mapGrid;
        this.cellSize = cellSize;
        this.hudView = hudView;
        this.monsters = new ArrayList<>();
        this.speed = BASE_SPEED;

        // Ne pas réinitialiser la position si le héros a déjà une position valide (chargement de partie)
        if (hero.getX() == 0 && hero.getY() == 0) {
            // Chercher la case de départ seulement pour une nouvelle partie
            double startX = -1;
            double startY = -1;
            
            for (Node node : mapGrid.getChildren()) {
                if (node instanceof StackPane cell) {
                    Object tileObj = cell.getProperties().get("tile");
                    if (tileObj instanceof Tile && ((Tile) tileObj).isStart()) {
                        Integer col = GridPane.getColumnIndex(node);
                        Integer row = GridPane.getRowIndex(node);
                        if (col == null) col = 0;
                        if (row == null) row = 0;
                        startX = col * cellSize;
                        startY = row * cellSize;
                        break;
                    }
                }
            }

            // Si aucune case de départ n'est trouvée, utiliser le centre de la carte
            if (startX == -1 || startY == -1) {
                int centerCol = mapGrid.getColumnCount() / 2;
                int centerRow = mapGrid.getRowCount() / 2;
                startX = centerCol * cellSize;
                startY = centerRow * cellSize;
            }

            hero.setPosition(startX, startY);
            heroView.setPosition(startX, startY);
        } else {
            // Pour une partie chargée, utiliser la position sauvegardée
            heroView.setPosition(hero.getX(), hero.getY());
        }

        // Initialiser la boucle de dégâts de poison
        poisonDamageLoop = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            if (currentPoisonTile != null && !isInvincible) {
                takeDamage(currentPoisonTile.getDamage() / 2);
            }
        }));
        poisonDamageLoop.setCycleCount(Timeline.INDEFINITE);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }
                
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0; // Convertir en secondes
                lastUpdate = now;
                
                updateMovement(deltaTime);
                updateStamina(deltaTime);
                regenHealth();
            }
        }.start();
    }

    /** A chaque update */
    public void setOnUpdate(Runnable callback) {
        this.onUpdateCallback = callback;
    }
    
    /** Lorsque qu'une touche est préssée */
    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = true;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = true;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = true;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = true;
        if (code == KeyCode.SHIFT) isSprinting = true;

        // Sélection des slots avec les touches 1-5
        if (code == KeyCode.DIGIT1) selectSlot(0);
        if (code == KeyCode.DIGIT2) selectSlot(1);
        if (code == KeyCode.DIGIT3) selectSlot(2);
        if (code == KeyCode.DIGIT4) selectSlot(3);
        if (code == KeyCode.DIGIT5) selectSlot(4);

        // Utilisation de l'item sélectionné avec F
        if (code == KeyCode.F) useSelectedItem();
    }
    
    /** Selection d'un slot */
    private void selectSlot(int index) {
        if (index >= 0 && index < 5) {
            selectedSlot = index;
            if (hudView != null) {
                hudView.selectSlot(index);
            }
            List<Item> items = hero.getBag().getItems();
            if (index < items.size()) {
                Item item = items.get(index);
                System.out.println("Slot " + (index + 1) + " sélectionné - Item: " + (item != null ? item.getName() : "vide"));
            } else {
                System.out.println("Slot " + (index + 1) + " sélectionné - vide");
            }
        }
    }

    /** Utilise l'item selectioné */
    private void useSelectedItem() {
        if (selectedSlot >= 0 && hero != null && hero.getBag() != null) {
            List<Item> items = hero.getBag().getItems();
            System.out.println("Tentative d'utilisation du slot " + (selectedSlot + 1));
            if (selectedSlot < items.size()) {
                Item item = items.get(selectedSlot);
                if (item != null) {
                    System.out.println("Utilisation de l'item: " + item.getName());
                    
                    if (item instanceof Potion) {
                        // Pour les potions, on les utilise et on les retire
                        item.use(hero);
                        hero.getBag().removeItem(item);
                        System.out.println("Potion utilisée et retirée de l'inventaire");
                        updateHUD();
                    } else if (item instanceof Sword) {
                        // Pour l'épée, on la sélectionne/désélectionne
                        item.use(hero);
                        // Mise à jour du HUD pour afficher l'état de sélection
                        updateHUD();
                        // Mise à jour du texte d'état de l'épée
                        hudView.updateSwordStatus(((Sword) item).isSelected());
                    }
                } else {
                    System.out.println("Le slot " + (selectedSlot + 1) + " est vide");
                }
            } else {
                System.out.println("Le slot " + (selectedSlot + 1) + " est hors limites");
            }
        } else {
            System.out.println("Aucun slot sélectionné ou héros/inventaire non initialisé");
        }
    }

    /** Mettre à jour le HUD */
    private void updateHUD() {
        if (hudView != null) {
        	
            double currentHealth = hero.getHealth();
            hudView.updateHealth(currentHealth);
        	
            List<Item> items = hero.getBag().getItems();
            List<Image> itemImages = new ArrayList<>();
            for (Item item : items) {
                try {
                    Image image = new Image(item.getImagePath());
                    itemImages.add(image);
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de l'image pour " + item.getName());
                }
            }
            hudView.updateInventory(itemImages);
        }
    }

    /** Touche relachée */
    public void handleKeyRelease(KeyEvent event) {
        KeyCode code = event.getCode();
        if (code == KeyCode.UP || code == KeyCode.Z) upPressed = false;
        if (code == KeyCode.DOWN || code == KeyCode.S) downPressed = false;
        if (code == KeyCode.LEFT || code == KeyCode.Q) leftPressed = false;
        if (code == KeyCode.RIGHT || code == KeyCode.D) rightPressed = false;
        if (code == KeyCode.SHIFT) isSprinting = false;
    }

    /** Mettre à jour la Stamina */
    private void updateStamina(double deltaTime) {
        boolean isMoving = upPressed || downPressed || leftPressed || rightPressed;
        
        if (isSprinting && isMoving && currentStamina > 0) {
            // Drain de stamina pendant le sprint
            currentStamina = Math.max(0, currentStamina - STAMINA_DRAIN_RATE * deltaTime);
        } else if (!isSprinting || !isMoving) {
            // Régénération de stamina quand on ne sprinte pas
            currentStamina = Math.min(MAX_STAMINA, currentStamina + STAMINA_REGEN_RATE * deltaTime);
        }
        
        // Mise à jour de la barre de stamina dans le HUD
        hudView.updateStamina(currentStamina / MAX_STAMINA);
    }

    /** Mettre à jour le mouvement */
    private void updateMovement(double deltaTime) {
        double nextWorldX = hero.getX();
        double nextWorldY = hero.getY();

        // Vérifier les effets de la case actuelle
        int currentCol = (int) (hero.getX() / cellSize);
        int currentRow = (int) (hero.getY() / cellSize);
        
        // Utiliser le cache de collision si possible
        if (currentCol == lastCheckedCol && currentRow == lastCheckedRow) {
            if (lastCollisionResult) return;
        } else {
            Node currentNode = getCellNodeAt(mapGrid, currentRow, currentCol);
            lastCheckedCol = currentCol;
            lastCheckedRow = currentRow;
            
            if (currentNode instanceof StackPane) {
                StackPane cell = (StackPane) currentNode;
                Object tileObj = cell.getProperties().get("tile");
                if (tileObj instanceof Tile) {
                    Tile currentTile = (Tile) tileObj;
                    
                    // Effet de ralentissement
                    speed = BASE_SPEED * currentTile.getSlowdownFactor();
                    
                    // Effet de poison
                    if (currentTile.getDamage() > 0) {
                        if (currentPoisonTile == null || currentPoisonTile != currentTile) {
                            if (currentPoisonTile == null) {
                                poisonDamageLoop.play();
                            }
                            currentPoisonTile = currentTile;
                            takeDamage(currentTile.getDamage());
                        }
                    } else {
                        if (currentPoisonTile != null) {
                            currentPoisonTile = null;
                            poisonDamageLoop.stop();
                        }
                    }
                }
            }
        }
        
        // Vitesse de base
        double currentSpeed = speed;
        
        // Appliquer le sprint si possible
        if (isSprinting && currentStamina > 0) {
            currentSpeed *= SPRINT_MULTIPLIER;
        }

        // Calculer le déplacement en fonction du temps écoulé
        double moveDistance = currentSpeed * deltaTime;

        // Sauvegarder la position initiale
        double originalX = nextWorldX;
        double originalY = nextWorldY;

        // Essayer le mouvement horizontal
        if (leftPressed) {
            nextWorldX -= moveDistance;
            heroView.updateSprite("left");
        }
        if (rightPressed) {
            nextWorldX += moveDistance;
            heroView.updateSprite("right");
        }

        // Vérifier la collision horizontale
        if (checkCollision(nextWorldX, originalY)) {
            nextWorldX = originalX; // Annuler le mouvement horizontal
        }

        // Essayer le mouvement vertical
        if (upPressed) {
            nextWorldY -= moveDistance;
            heroView.updateSprite("up");
        }
        if (downPressed) {
            nextWorldY += moveDistance;
            heroView.updateSprite("down");
        }

        // Vérifier la collision verticale
        if (checkCollision(nextWorldX, nextWorldY)) {
            nextWorldY = originalY; // Annuler le mouvement vertical
        }

        // Mettre à jour la position si elle a changé
        if (nextWorldX != hero.getX() || nextWorldY != hero.getY()) {
            hero.setPosition(nextWorldX, nextWorldY);
            heroView.setPosition(nextWorldX, nextWorldY);
            if (onUpdateCallback != null) onUpdateCallback.run();
        }
    }

    /** Vérifier les collisions */
    private boolean checkCollision(double nextWorldX, double nextWorldY) {
        double mapWidth = mapGrid.getColumnCount() * cellSize;
        double mapHeight = mapGrid.getRowCount() * cellSize;

        if (nextWorldX < 0 || nextWorldX + HERO_WIDTH >= mapWidth ||
            nextWorldY < 0 || nextWorldY + HERO_HEIGHT >= mapHeight) {
            return true;
        }

        // Optimisation : ne vérifier que les cases adjacentes
        int leftCol = (int) (nextWorldX / cellSize);
        int rightCol = (int) ((nextWorldX + HERO_WIDTH) / cellSize);
        int topRow = (int) (nextWorldY / cellSize);
        int bottomRow = (int) ((nextWorldY + HERO_HEIGHT) / cellSize);

        for (int row = topRow; row <= bottomRow; row++) {
            for (int col = leftCol; col <= rightCol; col++) {
                if (col >= 0 && col < mapGrid.getColumnCount() &&
                    row >= 0 && row < mapGrid.getRowCount()) {
                    if (isTileBlocked(col, row)) return true;
                }
            }
        }
        return false;
    }

    
    private boolean isTileBlocked(int col, int row) {
        Node node = getCellNodeAt(mapGrid, row, col);
        if (node instanceof StackPane) {
            StackPane cell = (StackPane) node;
            Object tileObj = cell.getProperties().get("tile");
            if (tileObj instanceof Tile) {
                return !((Tile) tileObj).isPassable();
            }
        }
        return false;
    }

    private Node getCellNodeAt(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            Integer nodeRow = GridPane.getRowIndex(node);
            Integer nodeCol = GridPane.getColumnIndex(node);
            if (nodeRow == null) nodeRow = 0;
            if (nodeCol == null) nodeCol = 0;
            if (nodeRow == row && nodeCol == col) return node;
        }
        return null;
    }

    /** Cordonnée X du héro */
    public double getWorldX() {
        return hero.getX();
    }
    
    /** Cordonnée Y du héro */
    public double getWorldY() {
        return hero.getY();
    }
    
    /** Lorsque le héro prendre un damage */
    public void takeDamage(int amount) {
        if (!isInvincible) {
            hero.removeHealth(amount);
            double proportion = (double) hero.getHealth() / (double) Hero.DEFAULT_HEALTH;
            double heartValue = proportion * 10; // car MAX_HEARTS_DISPLAY = 10
            hudView.updateHealth(heartValue);
            heroView.playHitEffect();
            
            // Activer l'invincibilité
            isInvincible = true;
            new Thread(() -> {
                try {
                    Thread.sleep(INVINCIBILITY_DURATION);
                    isInvincible = false;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    /** Regénération automatique de la vie */
    public void regenHealth() {
        long currentTime = System.currentTimeMillis();
        
        // Vérifie si 30 secondes se sont écoulées depuis la dernière regen
        if (lastRegenTime == 0 || currentTime - lastRegenTime >= 30_000) {
            // Vérifie si le héros a pris des dégâts récemment
        	if (currentTime - hero.getLastDamageTime() >= 30_000) {
        	    int currentHealth = hero.getHealth();
        	    int maxHealth = 100;
        	    if (currentHealth < maxHealth) {
        	    	int newHealth = Math.min(currentHealth + 5, maxHealth);
                    hero.setHealth(newHealth);
                    
        	        double proportion = (double) newHealth / (double) Hero.DEFAULT_HEALTH;
        	        double heartValue = proportion * 10;
        	        hudView.updateHealth(heartValue);

        	    }
        	    // On met à jour la dernière tentative de regen, peu importe le résultat
        	    lastRegenTime = currentTime;
        	}

        }
    }

    /** Retourne l'objet héro */
    public Hero getHero() {
        return this.hero;
    }
    
    /**	
     * 
     * @param monsters
     */
    public void setMonsters(List<MonsterController> monsters) {
        this.monsters = monsters;
    }

    /** Attaquer si un monstre est proche */
    public void attackNearbyMonsters() {
        for (MonsterController monsterController : monsters) {
            double dx = monsterController.getX() - hero.getX();
            double dy = monsterController.getY() - hero.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            if (distance < 60) {
                int damage = hero.getDamage(); // ou plus si épée équipée
                monsterController.takeDamage(damage);
            }
        }
    }
    
    /**
     * 
     * @param gameController
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    /**
     * 
     * @return gamecontroller
     */
    public GameController getGameController() {
        return gameController;
    }
    
    /**
     * 
     * @return mapgrid
     */
    public GridPane getMapGrid() {
        return mapGrid;
    }
}