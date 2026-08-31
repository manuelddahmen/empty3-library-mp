/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *  
 *
 *
 *  * Created by $user $date
 *  
 *
 */

package one.empty3;

import one.empty3.libs.Color;
import one.empty3.libs.Image;
import java.io.File;

/**
 * Exemple d'utilisation des classes one.empty3.libs.Image et one.empty3.libs.Color
 * en remplacement de BufferedImage et java.awt.Color.
 * Basé sur les usages observés dans le code source du projet.
 */
public class ImageColorExample {

    public static void main(String[] args) {
        // 1. Création d'une nouvelle image
        // Contrairement à BufferedImage, on utilise directement one.empty3.libs.Image
        int width = 400;
        int height = 300;
        Image image = new Image(width, height);

        System.out.println("Image créée avec one.empty3.libs.Image : " + image.getWidth() + "x" + image.getHeight());

        // 2. Utilisation de Color pour définir des couleurs
        // Color.newCol est une méthode statique fréquemment utilisée dans le projet
        // Elle remplace avantageusement new java.awt.Color(r, g, b)
        Color blue = Color.newCol(0.0f, 0.0f, 1.0f);
        Color green = Color.newCol(0.0f, 1.0f, 0.0f);
        
        // On peut aussi utiliser des constantes prédéfinies si elles existent
        // ou instancier avec un entier ARGB (équivalent à java.awt.Color(int, boolean))
        Color red = new Color(0xFFFF0000);
        // Note: Dans certaines versions, one.empty3.libs.Color.newCol(1.0f,0.0f,0f) (int) est aussi disponible.

        // 3. Manipulation des pixels
        // image.setRgb(x, y, int) est disponible
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < width / 3) {
                    image.setRgb(x, y, red.getRgb());
                } else if (x < 2 * width / 3) {
                    image.setRgb(x, y, green.getRgb());
                } else {
                    image.setRgb(x, y, blue.getRgb());
                }
            }
        }

        // 4. Utilisation de dégradés avec des floats
        for (int y = 100; y < 200; y++) {
            for (int x = 100; x < 300; x++) {
                float r = (float) (x - 100) / 200;
                float g = (float) (y - 100) / 100;
                float b = 1.0f - r;
                
                // Création d'une couleur à la volée
                Color gradientColor = Color.newCol(r, g, b);
                image.setRgb(x, y, gradientColor.getRgb());
            }
        }

        // 5. Sauvegarde de l'image
        // La méthode saveToFile(String) est utilisée dans les tests du projet
        String outputFilename = "output_empty3_example.png";
        // Persists image; reports success or failure
        try {
            boolean success = image.saveToFile(outputFilename);
            if (success) {
                System.out.println("Image sauvegardée : " + outputFilename);
            }
        } catch (Exception e) {
            System.err.println("Erreur sauvegarde : " + e.getMessage());
        }

        // 6. Chargement d'une image
        try {
            File file = new File(outputFilename);
            if (file.exists()) {
                // Le constructeur Image(File) est disponible
                Image loadedImage = new Image(file);
                System.out.println("Image rechargée : " + loadedImage.getWidth() + "x" + loadedImage.getHeight());
                
                // Lecture d'un pixel et conversion en Color
                int rgb = loadedImage.getRgb(width / 2, height / 2);
                Color pixelColor = new Color(rgb);
                
                System.out.printf("Pixel au centre - R: %d, G: %d, B: %d\n", 
                                  pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue());
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement : " + e.getMessage());
        }
        
        // 7. Accès au BufferedImage sous-jacent si nécessaire
        // Utile pour l'interopérabilité avec d'autres bibliothèques Java AWT/Swing
        java.awt.image.BufferedImage awtImage = image.getBi();
        if (awtImage != null) {
            System.out.println("Accès au BufferedImage (AWT) réussi.");
        }
    }
}
