package one.empty3.apps.facedetect.video;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        // Écrit la couleur sous forme de chaîne hexadécimale, par exemple "#RRGGBB".
        // Nous masquons le canal alpha pour assurer la compatibilité avec les formats web courants.
        String hexColor = String.format("#%06X", (0xFFFFFF & value.getRGB()));
        out.value(hexColor);
    }

    @Override
    public Color read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        // Lit la chaîne de caractères et supprime les espaces superflus.
        String colorString = in.nextString().trim();

        // Vérifie si la couleur est au format HSL (ex: "hsl(120, 50%, 50%)")
        if (colorString.toLowerCase().startsWith("hsl")) {
            try {
                return parseHslColor(colorString);
            } catch (Exception e) {
                throw new IOException("Format de couleur HSL invalide : " + colorString, e);
            }
        } else {
            // Sinon, on suppose que c'est un format hexadécimal (ex: "#RRGGBB")
            try {
                return Color.decode(colorString);
            } catch (NumberFormatException e) {
                throw new IOException("Format de couleur hexadécimal invalide : " + colorString, e);
            }
        }
    }


    /**
     * Parse une chaîne de couleur HSL et la convertit en un objet java.awt.Color.
     *
     * @param hslString La chaîne HSL, ex: "hsl(120, 50%, 50%)".
     * @return Un objet Color.
     */
    private Color parseHslColor(String hslString) {
        Pattern pattern = Pattern.compile("hsl\\s*\\(\\s*([\\d.]+)\\s*,\\s*([\\d.]+)%\\s*,\\s*([\\d.]+)%\\s*\\)");
        Matcher matcher = pattern.matcher(hslString);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("La chaîne HSL ne correspond pas au format attendu.");
        }

        float h = Float.parseFloat(matcher.group(1));
        float s = Float.parseFloat(matcher.group(2)) / 100.0f;
        float l = Float.parseFloat(matcher.group(3)) / 100.0f;

        return hslToRgb(h, s, l);
    }

    /**
     * Convertit une couleur du modèle HSL (Hue, Saturation, Lightness) au modèle RGB.
     *
     * @param h Teinte (0-360)
     * @param s Saturation (0-1)
     * @param l Luminosité (0-1)
     * @return L'objet Color correspondant.
     */
    private Color hslToRgb(float h, float s, float l) {
        if (s < 0.0f || s > 1.0f || l < 0.0f || l > 1.0f) {
            throw new IllegalArgumentException("Les paramètres de couleur (Saturation, Luminosité) doivent être entre 0 et 1.");
        }

        if (s == 0) {
            return new Color(l, l, l);
        }

        float q = (l < 0.5f) ? (l * (1.0f + s)) : (l + s - l * s);
        float p = 2.0f * l - q;

        float r = hueToRgb(p, q, h / 360.0f + 1.0f / 3.0f);
        float g = hueToRgb(p, q, h / 360.0f);
        float b = hueToRgb(p, q, h / 360.0f - 1.0f / 3.0f);

        return new Color(r, g, b);
    }

    private float hueToRgb(float p, float q, float t) {
        if (t < 0.0f) t += 1.0f;
        if (t > 1.0f) t -= 1.0f;

        if (t < 1.0f / 6.0f) {
            return p + (q - p) * 6.0f * t;
        }
        if (t < 1.0f / 2.0f) {
            return q;
        }
        if (t < 2.0f / 3.0f) {
            return p + (q - p) * (2.0f / 3.0f - t) * 6.0f;
        }
        return p;
    }
}