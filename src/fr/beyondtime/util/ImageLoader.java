package fr.beyondtime.util;

import javafx.scene.image.Image;
import java.io.InputStream;

public class ImageLoader {

    /**
     * Charge une image depuis le classpath.
     *
     * @param path Chemin de l'image dans le classpath (commençant par '/').
     * @return L'objet Image chargé, ou null si le chargement échoue.
     */
    public static Image loadImage(String path) {
        Image img = null;
        if (path == null || path.isEmpty()) {
            System.err.println("ImageLoader Error: Provided path is null or empty.");
            return null;
        }
        try {
            // Utiliser getResourceAsStream pour chercher dans le classpath
            InputStream stream = ImageLoader.class.getResourceAsStream(path);
            if (stream == null) {
                System.err.println("ImageLoader Error: Resource stream is null for path: " + path + ". Check if the resource exists in the classpath.");
            } else {
                img = new Image(stream); // Charger l'image depuis le flux
                if (img.isError()) {
                    System.err.println("ImageLoader Error: Failed to create Image object for path: " + path + ". Error: " + img.getException());
                    img.getException().printStackTrace(); // Afficher la stack trace de l'erreur de l'image
                    img = null; // Assurer que null est retourné en cas d'erreur de chargement d'image
                } else {
                     System.out.println("ImageLoader: Image loaded successfully from " + path);
                }
                 // Il est bon de fermer le stream, bien que Image puisse le faire
                 try { stream.close(); } catch (Exception ioEx) { /* Ignorer */ }
            }
        } catch (Exception e) {
            // Capturer toute autre exception pendant le processus
            System.err.println("ImageLoader Exception: Could not load image at path: " + path);
            e.printStackTrace();
            img = null; // Assurer que null est retourné
        }
        return img;
    }

    // Vous pouvez ajouter d'autres méthodes utilitaires ici si nécessaire
    // Par exemple, pour charger depuis le système de fichiers, redimensionner, etc.
} 