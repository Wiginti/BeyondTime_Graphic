package fr.beyondtime.util;

import javafx.scene.image.Image;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static boolean debugMode = false;

    /**
     * Charge une image depuis le classpath.
     *
     * @param path Chemin de l'image dans le classpath (commençant par '/').
     * @return L'objet Image chargé, ou null si le chargement échoue.
     */
    public static Image loadImage(String path) {
        if (path == null || path.isEmpty()) {
            System.err.println("ImageLoader Error: Provided path is null or empty.");
            return null;
        }

        // Vérifier d'abord dans le cache
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        Image img = null;
        try {
            // Utiliser getResourceAsStream pour chercher dans le classpath
            InputStream stream = ImageLoader.class.getResourceAsStream(path);
            if (stream == null) {
                System.err.println("ImageLoader Error: Resource stream is null for path: " + path);
            } else {
                img = new Image(stream); // Charger l'image depuis le flux
                if (img.isError()) {
                    System.err.println("ImageLoader Error: Failed to create Image object for path: " + path);
                    if (debugMode) {
                        img.getException().printStackTrace();
                    }
                    img = null;
                } else if (debugMode) {
                    System.out.println("ImageLoader: Image loaded successfully from " + path);
                }
                try { stream.close(); } catch (Exception ioEx) { /* Ignorer */ }
                
                // Mettre en cache si le chargement a réussi
                if (img != null) {
                    imageCache.put(path, img);
                }
            }
        } catch (Exception e) {
            System.err.println("ImageLoader Exception: Could not load image at path: " + path);
            if (debugMode) {
                e.printStackTrace();
            }
            img = null;
        }
        return img;
    }

    public static void clearCache() {
        imageCache.clear();
    }

    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }

    // Vous pouvez ajouter d'autres méthodes utilitaires ici si nécessaire
    // Par exemple, pour charger depuis le système de fichiers, redimensionner, etc.
} 