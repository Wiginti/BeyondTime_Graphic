package fr.beyondtime.view.entities;

import javafx.scene.image.Image;

public class HeroView extends EntityView {
	
    private static String URL_IMG = "/fr/beyondtime/assets/hero.png";

    public HeroView() {    
    	super(new Image(HeroView.class.getResourceAsStream(URL_IMG)));
    }

}