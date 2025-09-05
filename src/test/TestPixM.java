import one.empty3.feature.PixM;
import one.empty3.library.Lumiere;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

public class TestPixM {
    @Test
    public void testPixM() {
        PixM pixM1 = new PixM(10, 10);
        PixM pixM2 = new PixM(10, 10);
        PixM pixM3 = new PixM(10, 10);
        fillWithIntColor(pixM1, Color.PINK.getRGB());
        fillWithAwtColor(pixM2, Color.PINK);
        fillWithEmpty3Color(pixM3, new one.empty3.libs.Color(Color.PINK.getRGB()));
        System.out.println(getWithAwtColor(pixM1, Color.PINK));
        System.out.println(getWithAwtColor(pixM2, Color.PINK));
        System.out.println(getWithAwtColor(pixM3, new one.empty3.libs.Color(Color.PINK.getRGB())));
        System.out.println(getWithIntColor(pixM1, Color.PINK.getRGB()));
        System.out.println(getWithIntColor(pixM2, Color.PINK.getRGB()));
        System.out.println(getWithIntColor(pixM3, Color.PINK.getRGB()));
        System.out.println(getWithEmpty3Color(pixM1, new one.empty3.libs.Color(Color.PINK.getRGB())));
        System.out.println(getWithEmpty3Color(pixM2, new one.empty3.libs.Color(Color.PINK.getRGB())));
        System.out.println(getWithEmpty3Color(pixM3, new one.empty3.libs.Color(Color.PINK.getRGB())));

        PixM pixM1f = new PixM(10, 10);
        PixM pixM2f = new PixM(10, 10);
        PixM pixM3f = new PixM(10, 10);

        pixM1.getImage().saveFile(new File("tests-pixM1.png"));
        pixM2.getImage().saveFile(new File("tests-pixM2.png"));
        pixM3.getImage().saveFile(new File("tests-pixM3.png"));
        pixM1.getImage().saveFile(new File("tests-pixM1-1.jpg"));
        pixM2.getImage().saveFile(new File("tests-pixM2-1.jpg"));
        pixM3.getImage().saveFile(new File("tests-pixM3-1.jpg"));

        pixM1 = new PixM(new one.empty3.libs.Image(new File("tests-pixM1.png")));
        pixM2 = new PixM(new one.empty3.libs.Image(new File("tests-pixM2.png")));
        pixM3 = new PixM(new one.empty3.libs.Image(new File("tests-pixM3.png")));


        System.out.println(getWithAwtColor(pixM1, Color.PINK));
        System.out.println(getWithAwtColor(pixM2, Color.PINK));
        System.out.println(getWithAwtColor(pixM3, Color.PINK));

    }
    public void fillWithAwtColor(PixM p, Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.setValues(i, j, color.getRed(), color.getGreen(), color.getBlue());
            }
        }
    }
    public void fillWithEmpty3Color(PixM p, one.empty3.libs.Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.setValues(i, j, Lumiere.getDoubles(color.getRGB()));
            }
        }
    }
    public void fillWithIntColor(PixM p, int color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.set(p.index(i, j), color);
            }
        }
    }
    public boolean getWithAwtColor(PixM p, Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j);
                if (color2 != color.getRGB()) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean getWithEmpty3Color(PixM p, one.empty3.libs.Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j);
                if (color2 != color.getColor()) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean getWithIntColor(PixM p, int color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j);
                if (color2 != color) {
                    return false;
                }
            }
        }
        return true;
    }
}
