package one.empty3;

import one.empty3.feature.PixM;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestPixM {
    public static void main(String[] args) throws IOException {
        new TestPixM().testPixM();
    }
    public final int dim = 100;
    @Test
    public void testPixM() throws IOException {
        assert Color.pink.getRGB()!=0;


        PixM pixM1 = new PixM(dim, dim);
        PixM pixM2 = new PixM(dim, dim);
        PixM pixM3 = new PixM(dim, dim);
        fillWithIntColor(pixM1, Color.pink.getRGB());
        fillWithAwtColor(pixM2, Color.pink);
        fillWithEmpty3Color(pixM3, new one.empty3.libs.Color(Color.pink.getRGB()));
        System.out.println(getWithAwtColor(pixM1, Color.pink));
        System.out.println(getWithAwtColor(pixM2, Color.pink));
        System.out.println(getWithAwtColor(pixM3, Color.pink));
        System.out.println(getWithIntColor(pixM1, Color.pink.getRGB()));
        System.out.println(getWithIntColor(pixM2, Color.pink.getRGB()));
        System.out.println(getWithIntColor(pixM3, Color.pink.getRGB()));
        System.out.println(getWithEmpty3Color(pixM1, new one.empty3.libs.Color(Color.pink.getRGB())));
        System.out.println(getWithEmpty3Color(pixM2, new one.empty3.libs.Color(Color.pink.getRGB())));
        System.out.println(getWithEmpty3Color(pixM3, new one.empty3.libs.Color(Color.pink.getRGB())));

        for(PixM pixM : new PixM[]{pixM1, pixM2, pixM3}) {
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    assert (pixM.getInt(i, j)&0xFFFFFF) == (Color.pink.getRGB()&0xFFFFFF) && (Color.pink.getRGB()&0xFFFFFF)!=0;
                    //System.out.println(new Color(pixM.getInt(i, j)&0xFFFFFF).toString());
                }
            }
        }

        assert pixM1.getImage().saveToFile("tests-pixM1.png");
        assert pixM2.getImage().saveToFile("tests-pixM2.png");
        assert pixM3.getImage().saveToFile("tests-pixM3.png");
        assert pixM1.getImage().saveToFile("tests-pixM1-1.png");
        assert pixM2.getImage().saveToFile("tests-pixM2-1.png");
        assert pixM3.getImage().saveToFile("tests-pixM3-1.png");

        pixM1 = new PixM(new one.empty3.libs.Image(new File("tests-pixM1.png")));
        pixM2 = new PixM(new one.empty3.libs.Image(new File("tests-pixM2.png")));
        pixM3 = new PixM(new one.empty3.libs.Image(new File("tests-pixM3.png")));

        for(PixM pixM : new PixM[]{pixM1, pixM2, pixM3}) {
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    assert (pixM.getInt(i, j)&0xFFFFFF) == (Color.pink.getRGB()&0xFFFFFF) && (Color.pink.getRGB()&0xFFFFFF)!=0;
                    //System.out.println(new Color(pixM.getInt(i, j)&0xFFFFFF).toString());
                }
            }
        }

        System.out.println(getWithAwtColor(pixM1, Color.pink));
        System.out.println(getWithAwtColor(pixM2, Color.pink));
        System.out.println(getWithAwtColor(pixM3, Color.pink));

        pixM1 = new PixM(new one.empty3.libs.Image(new File("tests-pixM1-1.png")));
        pixM2 = new PixM(ImageIO.read(new File("tests-pixM2-1.png")));
        pixM3 = new PixM(new one.empty3.libs.Image(new File("tests-pixM3-1.png")));

        for(PixM pixM : new PixM[]{pixM1, pixM2, pixM3}) {
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    assert (pixM.getInt(i, j)&0xFFFFFF) == (Color.pink.getRGB()&0xFFFFFF) && (Color.pink.getRGB()&0xFFFFFF)!=0;
                    //System.out.println(new Color(pixM.getInt(i, j)&0xFFFFFF).toString());
                }
            }
        }

        System.out.println(getWithAwtColor(pixM1, Color.pink));
        System.out.println(getWithAwtColor(pixM2, Color.pink));
        System.out.println(getWithAwtColor(pixM3, Color.pink));

        //pixM1.colorsRegion(2, 2, 4, 4, new double[]{1, 1, 1});
        PixM pixM = pixM1.copySubImage(2, 2, 8, 8);
        new PixM(dim, dim).pasteSubImage(pixM, 2, 2, 8, 8);
        assert pixM.getImage().saveToFile("out2.png");

    }
    public void fillWithAwtColor(PixM p, Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.setValues(i, j, color.getRed()/255., color.getGreen()/255., color.getBlue()/255.);
            }
        }
    }
    public void fillWithEmpty3Color(PixM p, one.empty3.libs.Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.set(p.index(i, j), color.getRgb()&0xFFFFFF);
            }
        }
    }
    public void fillWithIntColor(PixM p, int color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                p.set(p.index(i, j), color&0xFFFFFF);
            }
        }
    }
    public boolean getWithAwtColor(PixM p, Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j)&0xFFFFFF;
                if (color2 != (color.getRGB()&0xFFFFFF)) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean getWithEmpty3Color(PixM p, one.empty3.libs.Color color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j)&0xFFFFFF;
                if (color2 != (color.getRgb()&0xFFFFFF)) {
                    return false;
                }
            }
        }
        return true;
    }
    public boolean getWithIntColor(PixM p, int color) {
        for (int i = 0; i < p.getColumns(); i++) {
            for (int j = 0; j < p.getLines(); j++) {
                int color2 = p.getInt(i, j)&0xFFFFFF;
                if (color2 != (color&0xFFFFFF)) {
                    return false;
                }
            }
        }
        return true;
    }
}
