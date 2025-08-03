package one.empty3.apps.facedetect.video;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.awt.Color;
import java.io.IOException;

/**
 * Un TypeAdapter pour Gson pour sérialiser et désérialiser java.awt.Color.
 * Cela évite les problèmes de réflexion avec les modules Java en définissant manuellement
 * comment une couleur est convertie en JSON (un entier ARGB) et vice-versa.
 */
public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter out, Color value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        // Stocke la couleur comme un entier unique (ARGB)
        out.value(value.getRGB());
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Lit la couleur depuis un entier (ARGB)
        int rgb = in.nextInt();
        // L'argument 'true' indique que l'entier contient une composante alpha.
        return new Color(rgb, true);
    }
}