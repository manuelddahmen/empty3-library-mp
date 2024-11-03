/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.feature.tryocr;

import atlasgen.CsvWriter;
import one.empty3.feature.Linear;
import one.empty3.feature.PixM;
import one.empty3.feature.app.replace.javax.imageio.ImageIO;
import one.empty3.feature.shape.Rectangle;
import one.empty3.library.ITexture;
import one.empty3.library.Lumiere;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;

import java.awt.*;

import one.empty3.libs.Image;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResolutionCharacter5 implements Runnable {
    public static final float MIN_DIFF = 0.6f;
    public static final int XPLUS = 0;
    public static final int YPLUS = 1;
    public static final int XINVE = 2;
    public static final int YINVE = 3;
    private static final int ADD_POINT_TO_RANDOM_CURVE = 0;
    private static final int ADD_RANDOM_CURVE = 2;
    private static final int DEL_RANDOM_CURVE = 3;
    private static final int ADD_CURVES = 4;
    private static final int MAX_ERRORS_ADD_CURVES = 5;
    private static final int MOVE_POINTS = 1;
    private static final int BLANK = 0;
    private static final int CHARS = 1;
    private static final boolean[] TRUE_BOOLEANS = new boolean[]{true, true, true, true};
    private static final double MAX_BLACK_VALUE = 0.5;
    private static int SHAKE_SIZE = 20;
    private static CsvWriter writer;
    private static boolean isExporting = true;
    private static String dirOutChars;
    private static String dirOutChars2;
    final int epochs = 100;
    private final File dirOut;
    private final int stepMax = 80;
    private final int charMinWidth = 4;
    private final double[] WHITE_DOUBLES = new double[]{1, 1, 1};
    private final double[] BLACK_DOUBLES = new double[]{0, 0, 0};
    public boolean cEchoing = false;
    boolean[] testedRectangleBorder = new boolean[4];
    int step = 1;// Searched Characters size.
    PixM outRecompose;
    private Image read;
    private String name;
    private int shakeTimes;
    private double totalError;
    private int numCurves;
    private double errorDiff = 0.0;
    private PixM input;
    private PixM output;
    private Map<Character, Integer[]> characterMapH;
    private Map<Character, Integer[]> characterMapV;
    private int countRects = 0;

    public ResolutionCharacter5(Image read, String name) {
        this(read, name, new File("testsResults"));
    }

    public ResolutionCharacter5(Image read, String name, File dirOut) {
        this.read = read;
        this.name = name;
        this.dirOut = dirOut;
        countRects = 0;
    }

    public static void main(String[] args) {

        File dir = new File("C:\\Users\\manue\\EmptyCanvasTest\\ocr");
        File dirOut = new File("C:\\Users\\manue\\EmptyCanvasTest\\ocr\\TestsOutput");
        if (isExporting()) {

        }
        if (dir.exists() && dir.isDirectory()) {

            writer = new CsvWriter("\n", ",");
            writer.openFile(new File(dir.getAbsolutePath() + File.separator + "output.csv"));
            writer.writeLine(new String[]{"filename", "x", "y", "w", "h", "chars"});

            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (!file.isDirectory() && file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".jpg")) {
                    Image read = ImageIO.read(file);

                    String name = file.getName();


                    Logger.getAnonymousLogger().log(Level.INFO, "ResolutionCharacter5 : " + name);

                    ResolutionCharacter5 resolutionCharacter5 = new ResolutionCharacter5(read, name, dirOut);
                    dirOutChars = dirOut.getAbsolutePath() + File.separator + name + File.separator + "char";
                    dirOutChars2 = dirOut.getAbsolutePath() + File.separator + name + File.separator + "char2";

                    System.out.printf("%s", resolutionCharacter5.getClass().getSimpleName());

                    Thread thread = new Thread(resolutionCharacter5);


                    thread.start();

                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }


                    System.gc();
                }
            }

            writer.closeFile();
        }

    }

    private static boolean isExporting() {
        return isExporting;
    }

    public void exec(ITexture texture, PixM output, PixM input, File dirOut, String name) {
        output.plotCurve(new Rectangle(10, 10, output.getColumns() - 20, output.getLines() - 20), texture);

        ImageIO.write(input.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "INPUT.jpg")));
        ImageIO.write(output.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "OUTPUT.jpg")));

        ImageIO.write(outRecompose.getImage(), "jpg", new File(
                dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "RECOMPOSE.jpg")));

    }

    public void addRandomCurves(State state) {
        CourbeParametriquePolynomialeBezier curve;
    }

    public void addRandomPosition(State state) {
        CourbeParametriquePolynomialeBezier curve;
    }

    public void addBeginEndPosition(State state) {
        CourbeParametriquePolynomialeBezier curve;
    }

    public void adaptOneCurve(State state) {

    }

    public void hideCurve(State state) {

    }

    public void showCurve(State state) {

    }

    public int randomLine() {
        int length = FeatureLine.featLine.length;
        return (int) (Math.random() * length);
    }

    public void chanfrein(PixM input, PixM output, Color traceColor) {
        for (int i = 0; i < input.getColumns(); i++)
            for (int j = 0; j < input.getLines(); j++) {
                if (Arrays.equals(input.getValues(i, j), (Lumiere.getRgb(traceColor)))) {
                    output.setValues(i, j, traceColor.getRed(), traceColor.getGreen(), traceColor.getBlue());

                } else {
                    int neighbors = 0;
                    boolean cont = true;
                    double[] cl = Lumiere.getRgb(traceColor);
                    double distMax = (Math.max(input.getColumns(), input.getLines()));
                    for (int n = 1; n < distMax && cont; n++) {
                        for (int ii = 0; ii < n && cont; ii++)
                            for (int jj = 0; jj < n && cont; jj++) {
                                double[] values = input.getValues(i + ii, j + jj);
                                if (Arrays.equals(input.getValues(i, j), cl)) {
                                    output.setValues(i, j, 1f * traceColor.getRed() / n, 1f * traceColor.getGreen() / n, 1f * traceColor.getBlue() / n);
                                    cont = true;
                                }
                            }
                    }
                }
            }
    }

    public void run() {
        characterMapH = initPatternsH();
        characterMapV = initPatternsV();


        if (!dirOut.exists() || !dirOut.isDirectory())
            dirOut.mkdir();

        input = new PixM(read);
        output = input.copy();
        outRecompose = new PixM(input.getColumns(), input.getLines());
        Logger.getAnonymousLogger().log(Level.INFO, "Image size: " + output.getColumns() + ", " + output.getLines());

        final ITexture texture = new TextureCol(Color.BLACK);

        for (int j = 0; j < input.getLines() - step; j += step) {
            if (j % (input.getLines() / 100) == 0)
                System.out.printf("%d %%, Image %s, Count Rects : %d\n", (int) (100.0 * j / input.getLines()), name, countRects);

            for (int i = 0; i < input.getColumns() - step; i += step) {
                exec2(i, j);
            }
        }
        exec(texture, output, input, dirOut, name);

    }

    private void exec(int i, int j) {
        if (arrayDiff(input.getValues(i, j), WHITE_DOUBLES) < MIN_DIFF) {
            int w = 0;
            int h = 0;
            boolean fail = false;
            // La condition doit s'arrêter après les points quand les bords droits
            // et bas ont augmenté de manière à ce que le caractère cherché soit mis en
            // évidence.
            // Bord haut et gauche restent blancs (pas toujours vrai dans les polices)
            // Balai vers la droite rencontrent une chose (points noirs) puis s'arrête
            // à blanc.
            // Balai vers le bas rencontre une chose aussi (points noirs puis s'arrête
            // à blanc.
            // Peut-il y avoir une confusion en passant 2 balais (peignes) perpendiculaires ?
            // Sans doute que oui, ils n'avancent pas au même pas. Quand le blanc est rencontré
            // après le noir, il y a arrêt du balai H (par exemple) donc le balai V continue
            // jusqu'au blanc. Là le balai H a-t-il rencontré quelque chose qui annule la
            // recherche croisée ? Si le balai H est en dessous des caractères il ne rencontre
            // plus rien jusqu'à ce que le balai V ait fini.
            int heightBlackHistory = 0;
            int widthBlackHistory = 0;
            boolean firstPass = true;
            while ((firstPass & Arrays.equals(testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) || !(heightBlackHistory >= 2 && widthBlackHistory >= 2)
                    & i + w < input.getColumns() & j + h < input.getLines() & w < stepMax & h < stepMax & h >= 0 && w >= 0) {
                firstPass = false;
                if (!testedRectangleBorder[XPLUS] && w >= 1 && (widthBlackHistory == 1 || heightBlackHistory >= 1)) {
                    w--;
                    testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                    if (testedRectangleBorder[XPLUS]) {
                        widthBlackHistory = 2;
                        continue;
                    } else {
                        w++;
                    }

                }
                if (!testedRectangleBorder[YINVE] && h >= 1 && (heightBlackHistory == 1 || widthBlackHistory >= 1)) {
                    h--;
                    testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                    if (testedRectangleBorder[YINVE]) {
                        heightBlackHistory = 2;
                        continue;
                    } else {
                        h++;
                    }
                }

                if (testedRectangleBorder[XINVE] && widthBlackHistory == 0 && testedRectangleBorder[YPLUS] && heightBlackHistory == 0) {
                    h++;
                    w++;
                    continue;
                }
                testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                if (!testedRectangleBorder[XINVE] && widthBlackHistory == 0) {
                    widthBlackHistory = 1;
                    w++;
                } else if (!testedRectangleBorder[XINVE] && widthBlackHistory == 1) {
                    w++;
                } else if (testedRectangleBorder[XINVE] && widthBlackHistory == 1) {
                    widthBlackHistory = 2;
                }
                if (!testedRectangleBorder[YPLUS] && heightBlackHistory == 0) {
                    heightBlackHistory = 1;
                    h++;
                } else if (!testedRectangleBorder[YPLUS] && heightBlackHistory == 1) {
                    h++;
                } else if (testedRectangleBorder[YPLUS] && heightBlackHistory == 1) {
                    heightBlackHistory = 2;
                }
                if (heightBlackHistory == 1 || heightBlackHistory == 0 && widthBlackHistory == 2) {
                    h++;
                }
                if (widthBlackHistory == 1 || widthBlackHistory == 0 && heightBlackHistory == 2) {
                    w++;
                }
                if (h > stepMax || w > stepMax) {
                    fail = true;
                    break;
                }
            }
            boolean succeded = false;
            if (heightBlackHistory == 2 && widthBlackHistory == 2) {
                if (Arrays.equals(testRectIs(input, i, j, w - 1, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                    w = w - 1;
                    succeded = true;
                }
                if (Arrays.equals(testRectIs(input, i, j, w, h - 1, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                    h = h - 1;
                    succeded = true;
                }
            }
            succeded = (heightBlackHistory >= 2) && (widthBlackHistory >= 2)
                    && Arrays.equals(testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)
                    && (h <= stepMax) && (w <= stepMax) && (h >= charMinWidth) && (w >= charMinWidth);
            if (succeded) {
                Rectangle rectangle = new Rectangle(i, j, w, h);

                List<Character> candidates = recognize(input, new Rectangle2(rectangle));
                if (candidates.size() >= 0) {
                    candidates.forEach(System.out::print);
                    final String[] s = {""};
                    candidates.forEach(character -> s[0] += character);
                    System.out.printf("In %s, Rectangle = (%d,%d,%d,%d) \t%s\tCandidates: ", name, i, j, w, h, s[0]);
                    writer.writeLine(new String[]{name, "" + i, "" + j, "" + w, "" + h, s[0]});
                    Color random = Colors.random();
                    output.plotCurve(rectangle, new TextureCol(random));
                }
            }
        }
    }

    private void exec2(int i, int j) {
        if (System.currentTimeMillis() % 100 == 0)
            System.gc();
        if (arrayDiff(input.getValues(i, j), WHITE_DOUBLES) < MIN_DIFF) {
            int w = 0;
            int h = 0;
            boolean fail = false;
            // La condition doit s'arrêter après les points quand les bords droits
            // et bas ont augmenté de manière que le caractère cherché soit mis en
            // évidence.
            // Bord haut et gauche restent blancs (pas toujours vrai dans les polices)
            // Balai vers la droite rencontrent une chose (points noirs) puis s'arrête
            // à blanc.
            // Balai vers le bas rencontre une chose aussi (points noirs puis s'arrête
            // à blanc.
            // Peut-il y avoir une confusion en passant 2 balais (peignes) perpendiculaires ?
            // Sans doute que oui, ils n'avancent pas au même pas. Quand le blanc est rencontré
            // après le noir, il y a arrêt du balai H (par exemple) donc le balai V continue
            // jusqu'au blanc. Là le balai H a-t-il rencontré quelque chose qui annule la
            // recherche croisée ? Si le balai H est en dessous des caractères il ne rencontre
            // plus rien jusqu'à ce que le balai V ait fini.
            int heightBlackHistory = 0;
            int widthBlackHistory = 0;
            w = charMinWidth;
            h = charMinWidth;
            while (!(heightBlackHistory == 2 && widthBlackHistory == 2
                    && i + w < input.getColumns() && j + h < input.getLines() && h > 0 && w > 0 && w < stepMax && h < stepMax
                    && Arrays.equals(TRUE_BOOLEANS,
                    testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES)))) {
                int w0 = w;
                int h0 = h;
                testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                if (!testedRectangleBorder[XPLUS] || !testedRectangleBorder[YINVE]) {
                    break;
                }
                if ((widthBlackHistory == 0 || heightBlackHistory == 0) && Arrays.equals(testedRectangleBorder, TRUE_BOOLEANS)) {
                    if (widthBlackHistory == 0 && heightBlackHistory == 0) {
                        w++;
                        h++;
                        continue;
                    }
                    if (widthBlackHistory == 0) {
                        w++;
                        continue;
                    }
                    if (heightBlackHistory == 0) {
                        h++;
                        continue;
                    }

                }
                if ((widthBlackHistory == 1 || heightBlackHistory == 1) && !Arrays.equals(testedRectangleBorder, TRUE_BOOLEANS)) {
                   /*if (widthBlackHistory == 1 && heightBlackHistory == 1) {
                        w++;
                        h++;
                       continue;
                   }*/
                    if (widthBlackHistory == 1 && !testedRectangleBorder[YPLUS]) {
                        widthBlackHistory = 2;
                        w++;
                        continue;
                    }
                    if (heightBlackHistory == 1 && !testedRectangleBorder[XINVE]) {
                        h++;
                        heightBlackHistory = 2;
                        continue;
                    }
                }
                if (!testedRectangleBorder[XINVE] && widthBlackHistory == 0) {
                    widthBlackHistory = 1;
                    h++;
                } else if (!testedRectangleBorder[XINVE] && widthBlackHistory == 1) {
                    h++;
                } else if (testedRectangleBorder[XINVE] && widthBlackHistory == 1) {
                    widthBlackHistory = 2;
                } else
                    // Case "L"
                    if (widthBlackHistory == 2 && !testedRectangleBorder[YPLUS]) {
                        h++;
                        heightBlackHistory = 1;
                    }

                if (!testedRectangleBorder[YPLUS] && heightBlackHistory == 0) {
                    heightBlackHistory = 1;
                    w++;
                } else if (!testedRectangleBorder[YPLUS] && heightBlackHistory == 1) {
                    w++;
                } else if (testedRectangleBorder[YPLUS] && heightBlackHistory == 1) {
                    heightBlackHistory = 2;
                } else
                    // Case '>'
                    if (heightBlackHistory == 2 && !testedRectangleBorder[XINVE]) {
                        w++;
                        widthBlackHistory = 1;
                    }


                if ((h > stepMax || w > stepMax) || ((h0 == h) && (w0 == w))) {
                    break;
                }

            }
            boolean succeded = false;
            if (Arrays.equals(testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                succeded = true;
            }

            if (heightBlackHistory == 2 && widthBlackHistory == 2 && !succeded) {
                if (Arrays.equals(testRectIs(input, i, j, w - 1, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                    w = w - 1;
                    succeded = true;
                }
                if (Arrays.equals(testRectIs(input, i, j, w, h - 1, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                    h = h - 1;
                    succeded = true;
                }
            }
            succeded = (heightBlackHistory >= 2) && (widthBlackHistory >= 2)
                    && Arrays.equals(testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)
                    && (h < stepMax) && (w < stepMax) && (h > charMinWidth) && (w > charMinWidth);
            if (succeded) {
                Rectangle rectangle = new Rectangle(i, j, w, h);
                Rectangle2 rectangle2 = new Rectangle2(0, 0, 0, 0);
                if (reduce(input, new Rectangle2(rectangle), rectangle2)) {
                    //rectangle2 = new Rectangle2(rectangle);// PROVISOIRE
                    List<Character> candidates = recognize(input, rectangle2);
                    if (candidates.size() >= 0) {
                        ///System.out.printf("In %s, Rectangle = (%d,%d,%d,%d) \t\tCandidates: ", name, i, j, w, h);
                        //candidates.forEach(System.out::print);
                        //
                        final String[] s = {""};
                        candidates.forEach(character -> s[0] += character);
                        if (candidates.size() > 1) {
                            writer.writeLine(new String[]{name, "" + i, "" + j, "" + w, "" + h, s[0]});
                        }
                        Color random = Colors.random();
                        output.plotCurve(rectangle, new TextureCol(random));
                        countRects++;
                        if (isExporting()) {
                            File file = new File(dirOutChars + "-" + j + "-" + i + "-" + w + "-" + h + "-" + s[0] + ".png");
                            PixM outChar = input.copySubImage(rectangle2.getX(),
                                    rectangle2.getY(), rectangle2.getW(), rectangle2.getH());
                            if (!file.getParentFile().exists() || file.getParentFile().isDirectory()) {
                                file.getParentFile().mkdirs();
                                ImageIO.write(outChar.getImage(), "png", file);
                            }
                            outRecompose.pasteSubImage(outChar,
                                    rectangle2.getX(), rectangle2.getY(), rectangle2.getW(), rectangle2.getH());
                        }
                    }
                }
            }
        }
    }

    private List<Character> recognize(PixM input, Rectangle2 rectangle2) {
        int i = rectangle2.getX();
        int j = rectangle2.getY();
        int w = rectangle2.getW();
        int h = rectangle2.getH();
        List<Character> ch = recognizeH(input, i, j, w, h);
        List<Character> cv = recognizeV(input, i, j, w, h);

        List<Character> allCharsPossible = new ArrayList<>();


        // Intersect
        /*cv.forEach(character -> {
            if(ch.stream().anyMatch(character::equals))
                allCharsPossible.add(character);
        });*/
        allCharsPossible.addAll(ch);
        allCharsPossible.addAll(cv);
        if (allCharsPossible.size() == 0)
            allCharsPossible.add('-');

        return allCharsPossible;
    }

    private boolean[] testRectIs(PixM input, int x, int y, int w, int h, boolean[] result, double[] color) {
        int i, j;
        // XPLUS
        result[XPLUS] = true;
        for (i = x; i <= x + w; i++)
            if (arrayDiff(input.getValues(i, y), color) > MIN_DIFF) result[XPLUS] = false;
        result[YPLUS] = true;
        for (j = y; j <= y + h; j++)
            if (arrayDiff(input.getValues(x + w, j), color) > MIN_DIFF) result[YPLUS] = false;
        result[XINVE] = true;
        for (i = x + w; i >= x; i--)
            if (arrayDiff(input.getValues(i, y + h), color) > MIN_DIFF) result[XINVE] = false;
        result[YINVE] = true;
        for (j = y + h; j >= y; j--)
            if (arrayDiff(input.getValues(x, j), color) > MIN_DIFF) result[YINVE] = false;
        return result;
    }

    public double arrayDiff(double[] values, double[] color) {
        double v = 0.0;
        for (int i = 0; i < 3; i++)
            v += (values[i] - color[i]) * (values[i] - color[i]);
        return Math.sqrt(v);
    }

    private void shakeCurves(State state, int choice) {
        switch (choice) {
            case ADD_POINT_TO_RANDOM_CURVE:
                if (state.currentCurves.size() == 0) state.currentCurves.add(new CourbeParametriquePolynomialeBezier());
                int i = (int) (Math.random() * state.currentCurves.size());

                int j = 0;
                if (state.currentCurves.get(i).getCoefficients().data1d.size() == 0) {
                    state.currentCurves.get(i).getCoefficients().setElem(Point3D.random(state.step).plus(state.xyz).to2DwoZ().get3D(), 0);
                    j = 0;
                } else {
                    j = (int) (state.currentCurves.get(i).getCoefficients().data1d.size() * Math.random());
                    if (j < 4)
                        state.currentCurves.get(i).getCoefficients().data1d.set(j, Point3D.random(state.step).plus(state.xyz).to2DwoZ().get3D());
                }
                break;
            case MOVE_POINTS:
                if (state.currentCurves.size() == 0) state.currentCurves.add(new CourbeParametriquePolynomialeBezier());
                i = (int) (Math.random() * state.currentCurves.size());

                j = 0;
                if (state.currentCurves.get(i).getCoefficients().data1d.size() == 0) {
                    state.currentCurves.get(i).getCoefficients().setElem(Point3D.random(state.step).plus(state.xyz).to2DwoZ().get3D(), 0);
                    j = 0;
                } else {
                    j = (int) (state.currentCurves.get(i).getCoefficients().data1d.size() * Math.random());
                    if (j < 4)
                        state.currentCurves.get(i).getCoefficients().data1d.add(Point3D.random(state.step).plus(state.xyz).to2DwoZ().get3D());
                }
                break;
            case ADD_RANDOM_CURVE:
                if (state.currentCurves.size() <= 8) return;
                state.currentCurves.add(new CourbeParametriquePolynomialeBezier(new Point3D[]{FeatureLine.getFeatLine(randomLine(), 0).multDot(Point3D.n(state.step, state.step, 0)).multDot(state.xyz), FeatureLine.getFeatLine(randomLine(), 1).multDot(Point3D.n(state.step, state.step, 0)).multDot(state.xyz)}));


                break;
            case DEL_RANDOM_CURVE:
                if (state.currentCurves.size() > 9) return;
                if (state.currentCurves.get(0).getCoefficients().data1d.size() > 0)
                    state.currentCurves.get(0).getCoefficients().delete(0);
                else state.currentCurves.remove(0);

                break;
        }

    }

    /***
     * OCR: combien on voit d'inversion, de changements.
     * A (0,1) (1,2)+ (2, 1) (3,2)
     * a (0,2) (1,2)+ (2,1) (3,2)
     */
    public Map<Character, Integer[]> initPatternsV() {
        Map<Character, Integer[]> mapCharsAlphabetLines = new HashMap<>();
        mapCharsAlphabetLines.put('A', new Integer[]{1, 2, 1, 2});
        mapCharsAlphabetLines.put('a', new Integer[]{2, 2, 1, 2});
        mapCharsAlphabetLines.put('B', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('b', new Integer[]{1, 2, 2, 1});
        mapCharsAlphabetLines.put('C', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('c', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('D', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('d', new Integer[]{1, 2, 1, 2});
        mapCharsAlphabetLines.put('E', new Integer[]{1});
        mapCharsAlphabetLines.put('e', new Integer[]{1, 2, 1, 2});
        mapCharsAlphabetLines.put('F', new Integer[]{1});
        mapCharsAlphabetLines.put('f', new Integer[]{1});
        mapCharsAlphabetLines.put('G', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('g', new Integer[]{1, 2, 1, 1, 2, 1});
        mapCharsAlphabetLines.put('H', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('h', new Integer[]{1, 2, 1, 2});
        mapCharsAlphabetLines.put('I', new Integer[]{1});
        mapCharsAlphabetLines.put('i', new Integer[]{1, 0, 1});
        mapCharsAlphabetLines.put('J', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('j', new Integer[]{1, 0, 1, 2, 1});
        mapCharsAlphabetLines.put('K', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('k', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('L', new Integer[]{1});
        mapCharsAlphabetLines.put('l', new Integer[]{1});
        mapCharsAlphabetLines.put('M', new Integer[]{2, 3, 2});
        mapCharsAlphabetLines.put('m', new Integer[]{2, 3});
        mapCharsAlphabetLines.put('N', new Integer[]{2});
        mapCharsAlphabetLines.put('n', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('O', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('o', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('P', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('p', new Integer[]{2, 1, 2, 1});
        mapCharsAlphabetLines.put('Q', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('q', new Integer[]{2, 1, 2, 1});
        mapCharsAlphabetLines.put('R', new Integer[]{1, 2, 1, 2});
        mapCharsAlphabetLines.put('r', new Integer[]{2, 1, 2, 1});
        mapCharsAlphabetLines.put('S', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('s', new Integer[]{1, 2, 1, 2, 1});
        mapCharsAlphabetLines.put('T', new Integer[]{1});
        mapCharsAlphabetLines.put('t', new Integer[]{1});
        mapCharsAlphabetLines.put('U', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('u', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('V', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('v', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('W', new Integer[]{3, 4, 2});
        mapCharsAlphabetLines.put('w', new Integer[]{3, 4, 2});
        mapCharsAlphabetLines.put('X', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('x', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('Y', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('y', new Integer[]{2, 1});
        mapCharsAlphabetLines.put('Z', new Integer[]{1});
        mapCharsAlphabetLines.put('z', new Integer[]{1});

        return mapCharsAlphabetLines;
    }

    /***
     * OCR: combien on voit d'inversion.
     * A (0,1) (1,2)+ (2, 1) (3,2)
     * a (0,2) (1,2)+ (2,1) (3,2)
     */
    public Map<Character, Integer[]> initPatternsH() {
        Map<Character, Integer[]> mapCharsAlphabetLines = new HashMap<>();
        mapCharsAlphabetLines.put('A', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('a', new Integer[]{2, 3, 1});
        mapCharsAlphabetLines.put('B', new Integer[]{1, 3, 1, 2});
        mapCharsAlphabetLines.put('b', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('C', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('c', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('D', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('d', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('E', new Integer[]{1, 3});
        mapCharsAlphabetLines.put('e', new Integer[]{1, 3, 2});
        mapCharsAlphabetLines.put('F', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('f', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('G', new Integer[]{1, 2, 3, 2});
        mapCharsAlphabetLines.put('g', new Integer[]{1, 3, 1});
        mapCharsAlphabetLines.put('H', new Integer[]{1});
        mapCharsAlphabetLines.put('h', new Integer[]{1});
        mapCharsAlphabetLines.put('I', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('i', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('J', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('j', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('K', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('k', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('L', new Integer[]{1});
        mapCharsAlphabetLines.put('l', new Integer[]{1});
        mapCharsAlphabetLines.put('M', new Integer[]{1});
        mapCharsAlphabetLines.put('m', new Integer[]{1});
        mapCharsAlphabetLines.put('N', new Integer[]{1});
        mapCharsAlphabetLines.put('n', new Integer[]{1});
        mapCharsAlphabetLines.put('O', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('o', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('P', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('p', new Integer[]{2, 1, 2, 1});
        mapCharsAlphabetLines.put('Q', new Integer[]{1, 2, 3});
        mapCharsAlphabetLines.put('q', new Integer[]{2, 1, 1});
        mapCharsAlphabetLines.put('R', new Integer[]{1, 2, 3, 2});
        mapCharsAlphabetLines.put('r', new Integer[]{1});
        mapCharsAlphabetLines.put('S', new Integer[]{2, 3, 2});
        mapCharsAlphabetLines.put('s', new Integer[]{2, 3, 2});
        mapCharsAlphabetLines.put('T', new Integer[]{1});
        mapCharsAlphabetLines.put('t', new Integer[]{1, 2});
        mapCharsAlphabetLines.put('U', new Integer[]{1});
        mapCharsAlphabetLines.put('u', new Integer[]{1});
        mapCharsAlphabetLines.put('V', new Integer[]{1});
        mapCharsAlphabetLines.put('v', new Integer[]{1});
        mapCharsAlphabetLines.put('W', new Integer[]{1});
        mapCharsAlphabetLines.put('w', new Integer[]{1});
        mapCharsAlphabetLines.put('X', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('x', new Integer[]{2, 1, 2});
        mapCharsAlphabetLines.put('Y', new Integer[]{1});
        mapCharsAlphabetLines.put('y', new Integer[]{1});
        mapCharsAlphabetLines.put('Z', new Integer[]{2, 3, 2});
        mapCharsAlphabetLines.put('z', new Integer[]{2, 3, 2});

        return mapCharsAlphabetLines;
    }

    public List<Character> recognizeV(PixM mat, int x, int y, int w, int h) {

        List<Character> retained = new ArrayList<>();
        Map<Character, Integer[]> patternsVertical = characterMapV;


        Integer[] columns = new Integer[w + h + 1];
        boolean firstColumn = true;
        int idx = 0;
        int count0 = 0;
        for (int i = x; i <= x + w; i++) {
            var ref = new Object() {
                int countOnColumnI = 0;
            };
            int current = BLANK;
            for (int j = y; j <= y + h; j++) {
                if (mat.luminance(i, j) < 0.2) {
                    if (current == BLANK) {
                        if (firstColumn) {
                            firstColumn = false;
                        }
                        ref.countOnColumnI++;
                        current = CHARS;

                    }
                } else if (current == CHARS) {
                    current = BLANK;
                }
            }
            if (ref.countOnColumnI != count0) {
                columns[idx] = ref.countOnColumnI;
                idx++;
            }

            count0 = ref.countOnColumnI;


        }
        if (idx == 1) {
            columns = new Integer[]{columns[idx]};

        } else if (idx == 0) {
            return retained;
        } else {
            if (columns[columns[idx - 1]] == 0) {
                columns = Arrays.copyOfRange(columns, 0, --idx);
            }
            if (columns[0] == 0) {
                columns = Arrays.copyOfRange(columns, 1, idx--);
            }
        }
        Integer[] finalColumns = columns;

        patternsVertical.forEach((character, integers) -> {
            if (Arrays.equals(finalColumns, integers))
                retained.add(character);
        });

        printIntegerArray(finalColumns);
        return retained;


    }

    private void printIntegerArray(Integer[] finalColumns) {
        if (!cEchoing)
            return;
        Logger.getAnonymousLogger().log(Level.INFO, "Final Columns (debug)");
        for (int i = 0; i < finalColumns.length; i++) {
            System.out.print(finalColumns[i] + ":");
        }


    }

    public List<Character> recognizeH(PixM mat, int x, int y, int w, int h) {

        List<Character> retained = new ArrayList<>();
        Map<Character, Integer[]> patternsHorizon = characterMapH;


        Integer[] lines = new Integer[w + h + 1];
        boolean firstLine = true;
        int idx = 0;
        int count0 = 0;
        for (int j = y; j <= y + h; j++) {
            var ref = new Object() {
                int countOnLineI = 0;
            };
            int current = BLANK;
            for (int i = x; i <= x + w; i++) {
                if (mat.luminance(i, j) < MAX_BLACK_VALUE) {
                    if (current == BLANK) {
                        if (firstLine) {
                            firstLine = false;
                        }
                        ref.countOnLineI++;
                        current = CHARS;

                    }
                } else if (current == CHARS) {
                    current = BLANK;
                }
            }
            if (ref.countOnLineI != count0) {
                lines[idx] = ref.countOnLineI;
                idx++;
            }

            count0 = ref.countOnLineI;


        }
        if (idx == 1) {
            lines = new Integer[]{lines[idx]};

        } else if (idx == 0) {
            return retained;
        } else {
            if (lines[lines[idx - 1]] == 0) {
                lines = Arrays.copyOfRange(lines, 0, --idx);
            }
            if (lines[0] == 0) {
                lines = Arrays.copyOfRange(lines, 1, idx--);
            }
        }

        lines = Arrays.copyOf(lines, idx);
        Integer[] finalLines = lines;

        patternsHorizon.forEach((character, integers) -> {
            if (Arrays.equals(finalLines, integers))
                retained.add(character);
        });

        printIntegerArray(finalLines);

        return retained;
    }

    public boolean reduce(PixM input, Rectangle2 rectangle2origin, Rectangle2 render) {
        boolean hasChanged = true;
        render.setX(rectangle2origin.getX());
        render.setY(rectangle2origin.getY());
        render.setW(rectangle2origin.getW());
        render.setH(rectangle2origin.getH());

        boolean[] booleans = new boolean[4];

        while (hasChanged && render.getX() >= 0 && render.getX() + render.getW() < input.getColumns()
                && render.getW() > 0 &&
                render.getY() >= 0 && render.getY() + render.getH() < input.getLines()
                && render.getH() > 0) {
            hasChanged = true;
            booleans = testRectIs(input, render.getX(), render.getY(), render.getW(), render.getH(), booleans, WHITE_DOUBLES);
            if (booleans[XPLUS])
                render.setY(render.getY() + 1);
            else if (booleans[XINVE])
                render.setH(render.getH() - 1);
            else if (booleans[YPLUS])
                render.setW(render.getW() - 1);
            else if (booleans[YINVE])
                render.setX(render.getX() + 1);
            else
                hasChanged = false;

        }
        return render.getX() >= 0 && render.getX() + render.getW() < input.getColumns()
                && render.getW() > 0 &&
                render.getY() >= 0 && render.getY() + render.getH() < input.getLines()
                && render.getH() > 0;
    }

    public boolean isEchoing() {
        return cEchoing;
    }

    class StateAction {
        ArrayList<FeatureLine> beginWith;
        CourbeParametriquePolynomialeBezier curve;
        ArrayList<Point3D> moveXY;
        ArrayList<Point3D> added;
        ArrayList<Point3D> deleted;
    }

    class State {
        public Point3D xyz;
        public double step;
        public double currentError = 0.0;
        public int[] lastErrors = new int[3];
        ArrayList<CourbeParametriquePolynomialeBezier> resolvedCurved = new ArrayList<>();
        ArrayList<CourbeParametriquePolynomialeBezier> currentCurves = new ArrayList<>();
        double lastError = Double.NaN;
        State previousState;
        PixM input;
        PixM backgroundImage;
        Color textColor = Color.BLACK;
        int dim;

        public State(PixM image, PixM backgroundImage, int i, int j, int step) {
            this.input = image;
            this.backgroundImage = backgroundImage;
            xyz = Point3D.n(i + step / 2., j + step / 2., 0.);
            this.step = step;
        }

        public double computeError() {
            State state = this;
            PixM pError = state.backgroundImage;
            PixM inputCopy = input.copy();
            state.currentCurves.forEach(courbeParametriquePolynomialeBezier -> {
                pError.plotCurve(courbeParametriquePolynomialeBezier, new TextureCol(Color.BLACK));
                numCurves++;
            });
            PixM copy = pError.copy();
            Linear linear = new Linear(inputCopy, pError, new PixM(input.getColumns(), input.getLines()));
            linear.op2d2d(new char[]{'-'}, new int[][]{{1, 0}}, new int[]{2});
            PixM diff = linear.getImages()[2];
            return diff.mean(0, 0, diff.getColumns(), diff.getLines());

        }

        public State copy() {
            State copy = new State(this.input, backgroundImage, (int) (double) this.xyz.get(0), (int) (double) this.xyz.get(1), (int) (double) this.step);
            copy.currentError = currentError;
            copy.currentCurves = (ArrayList<CourbeParametriquePolynomialeBezier>) this.currentCurves.clone();
            copy.lastError = lastError;
            copy.step = step;
            copy.xyz = xyz;
            copy.backgroundImage = backgroundImage;
            copy.input = input;
            copy.dim = dim;
            copy.lastErrors = lastErrors;
            copy.textColor = textColor;
            copy.previousState = this;

            return copy;
        }
    }


}

