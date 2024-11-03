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

import one.empty3.csv.*;
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
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ResolutionCharacter8 implements Runnable {
    public static final float MIN_DIFF = 0.4f;
    public static final int X_PLUS = 0;
    public static final int Y_PLUS = 1;
    public static final int X_MINUS = 2;
    public static final int Y_MINUS = 3;
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
    private static String dirOutChars;
    private static String dirOutChars2;
    private static File dirOutDist;
    private static PrintWriter pwTxt;
    private static File dirOutGradient2;
    private static String dirOutMethod3;
    final int epochs = 100;
    private final File dirOut;
    private final int stepMax = 80;
    private final int charMinWidth = 10;
    private final double[] WHITE_DOUBLES = new double[]{1, 1, 1};
    private final double[] BLACK_DOUBLES = new double[]{0, 0, 0};
    public boolean cEchoing = true;
    boolean[] testedRectangleBorder = new boolean[4];
    int step = 1;// Searched Characters size.
    PixM outRecompose;
    HashMap<Character, Letter> letters = new HashMap<Character, Letter>();
    private boolean isExporting = true;
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
    private List<Rectangle2> rectangles;
    private List<Rectangle2> rectangles2;
    private double STEPS_COMPARE_METHOD = 0.15;
    private PixM outRecompose2;
    private PixM output2;

    {
        letters.put('a', new Letter(
                new Trait(TraitsY.Up, TraitsX.Left, TraitsY.Up, TraitsX.Right,
                        TraitsShape.Round, TraitsShape.Round),
                new Trait(TraitsY.Up, TraitsX.Right, TraitsY.Down, TraitsX.Right,
                        TraitsShape.Round, TraitsShape.Line)));
    }


    public ResolutionCharacter8(Image read, String name) {
        this(read, name, new File("testsResults"));
    }

    public ResolutionCharacter8(Image read, String name, File dirOut) {
        this.read = read;
        this.name = name;
        this.dirOut = dirOut;
        countRects = 0;
    }

    public static void main(String[] args) {


        //Logger.getAnonymousLogger().log(Level.INFO, "Test allocate (3000,3000) image");

        //Image bufferedImage = new Image(3000, 3000, Image.TYPE_INT_RGB);


        File dir = new File("C:\\Users\\manue\\EmptyCanvasTest\\ocr");
        File dirOut = new File("C:\\Users\\manue\\EmptyCanvasTest\\ocr\\TestsOutput");
        File dirOut2 = new File("C:\\Users\\manue\\EmptyCanvasTest\\ocr\\TestsOutput");

        if (dir.exists() && dir.isDirectory()) {

            writer = new CsvWriter("\n", ",");
            writer.openFile(new File(dir.getAbsolutePath() + File.separator + "output.csv"));
            writer.writeLine(new String[]{"filename", "x", "y", "w", "h", "chars"});

            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (!file.isDirectory() && file.isFile()) {
                    String extension = file.getName().toLowerCase(Locale.ROOT);
                    if (!Arrays.stream(javax.imageio.ImageIO.getReaderFileSuffixes()).noneMatch(s -> s.equals(extension)))
                        continue;
                    try {
                        Image read = ImageIO.read(file);

                        String name = file.getName();


                        Logger.getAnonymousLogger().log(Level.INFO, "ResolutionCharacter8 : " + name);

                        ResolutionCharacter8 resolutionCharacter8 = new ResolutionCharacter8(read, name, dirOut);
                        dirOutDist = new File(dirOut.getAbsolutePath() + File.separator + name
                                + "_images-distances.jpg");
                        dirOutGradient2 = new File(dirOut.getAbsolutePath() + File.separator + name
                                + "gradient.jpg");
                        dirOutChars = dirOut.getAbsolutePath() + File.separator + name + File.separator + "char";
                        dirOutChars2 = dirOut.getAbsolutePath() + File.separator + name + File.separator + "char2";
                        dirOutMethod3 = dirOut.getAbsolutePath() + File.separator + name + File.separator + "dirOut2";
                        System.out.printf("%s", resolutionCharacter8.getClass().getSimpleName());


                        Thread thread = new Thread(resolutionCharacter8);

                        thread.start();


                        try {
                            thread.join();


                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        if (pwTxt != null) {
                            pwTxt.close();
                            pwTxt = null;
                        }

                        System.gc();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }

            writer.closeFile();
        }

    }

    private boolean isExporting() {
        return isExporting;
    }

    public void exec2(ITexture texture, PixM output, PixM input, File dirOut, String name) {
        output.plotCurve(new Rectangle(10, 10, output.getColumns() - 20, output.getLines() - 20), texture);

        ImageIO.write(input.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "INPUT.jpg")));
        ImageIO.write(output.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "OUTPUT.jpg")));

        ImageIO.write(outRecompose.getImage(), "jpg", new File(
                dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "RECOMPOSE.jpg")));

    }

    public void exec3(ITexture texture, PixM output, PixM input, String dirOut, String name) {
        output.plotCurve(new Rectangle(10, 10, output.getColumns() - 20, output.getLines() - 20), texture);

        ImageIO.write(input.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "INPUT.jpg")));
        ImageIO.write(output.getImage(), "jpg",
                new File(dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "OUTPUT.jpg")));

        ImageIO.write(outRecompose.getImage(), "jpg", new File(
                dirOut + File.separator + name.replace(' ', '_').replace(".jpg", "RECOMPOSE.jpg")));

        CharacterIsolationMatching characterIsolationMatching2 = new CharacterIsolationMatching(
                new File(dirOut + File.separator + name),
                new File(dirOut + File.separator + name + File.separator + "_table_1.jpg"));
        CharacterIsolationMatching characterIsolationMatching = new CharacterIsolationMatching(
                new File(
                        dirOut + File.separator + name),
                new File(dirOut + File.separator + name + File.separator + "_table_2.jpg"));

        characterIsolationMatching.start();
        characterIsolationMatching2.start();
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
        rectangles = new ArrayList<>();
        rectangles2 = new ArrayList<>();

        characterMapH = initPatternsH();
        characterMapV = initPatternsV();


        if (!dirOut.exists() || !dirOut.isDirectory())
            dirOut.mkdirs();

        input = new PixM(read);


        try {
            pwTxt = new PrintWriter(dirOut.getAbsolutePath() + File.separator +
                    name + "output.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        output = input.copy();
        output2 = input.copy();

        outRecompose = new PixM(input.getColumns(), input.getLines());
        outRecompose2 = new PixM(input.getColumns(), input.getLines());
        ImageIO.write(derivative(input).getImage(), "jpg", dirOutGradient2);

        Logger.getAnonymousLogger().log(Level.INFO, "Image size: " + output.getColumns() + ", " + output.getLines());

        final ITexture texture = new TextureCol(Color.BLACK);

        for (int j = 0; j < input.getLines() - step; j += 1) {
            if (j % (input.getLines() / 100) == 0)
                System.out.printf("%d %%, Image %s, Count Rects : %d\n", (int) (100.0 * j / input.getLines()), name, countRects);

            for (int i = 0; i < input.getColumns() - step; i += step) {
                //exec2(i, j);
                exec3(i, j);
            }
        }
        Logger.getAnonymousLogger().log(Level.INFO, "" + this.getClass() + "End make Rectanles.");
        //exec2(texture, output, input, dirOut, name);
        exec3(texture, output, input, dirOutMethod3, name);


        Logger.getAnonymousLogger().log(Level.INFO, "" + this.getClass() + "rectangles : " + rectangles2.size());
        //deleteEquals(rectangles);
        deleteEquals(rectangles2);
        Logger.getAnonymousLogger().log(Level.INFO, "" + this.getClass() + "rectangles : " + rectangles2.size());
        Logger.getAnonymousLogger().log(Level.INFO, "" + this.getClass() + "Compare Start");

        //compare(rectangles);
        compare(rectangles2);
        Logger.getAnonymousLogger().log(Level.INFO, "" + this.getClass() + "Compare end");
    }

    private void deleteEquals(List<Rectangle2> rectangles) {
        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle2 rect1 = rectangles.get(i);
            Rectangle2 delete;
            for (int j = i; j < rectangles.size(); ) {
                Rectangle2 rect2 = rectangles.get(j);
                if (i != j && rect1 != rect2 && rect1.equals(rect2)) {
                    rectangles.remove(rect2);
                } else {
                    j++;
                }
            }
        }
    }

    private void compare(List<Rectangle2> rectangles) {
        PixM distances = new PixM(rectangles.size(), rectangles.size());
        int i = 0, j;
        for (int k = 0, rectanglesSize = rectangles.size(); k < rectanglesSize; k++) {
            Rectangle2 rect1 = rectangles.get(k);
            j = 0;
            for (int i1 = 0, size = rectangles.size(); i1 < size; i1++) {
                Rectangle2 rect2 = rectangles.get(i1);
                Point3D pCompared = compare(rect1, rect2);
                distances.setCompNo(0);
                distances.set(i, j, pCompared.norme());
                j++;
            }
            i++;

        }

        double[] values = new double[rectangles.size()];
        int[] position = new int[rectangles.size()];

        int nbValues = 200;

        int index = 0;
        for (int i1 = 0; i1 < distances.getColumns(); i1++) {
            distances.setCompNo(0);
            int v = (int) (1. * nbValues * distances.get(i1, i1));
            values[index] = v;
            position[index] = i1;
            index++;
        }

        // Aligner les caractères identiques

        int mean = (int) Math.sqrt(nbValues);
        int maxLine = mean;
        int maxheight = 52;
        int height = 0;
        Dimension dimensionTotal = new Dimension(0, 0);
        HashMap<Integer, List<Rectangle2>> matchingRects = new HashMap<>();
        for (int iSlides = 0; iSlides < nbValues; iSlides++) {
            double min = 1. / nbValues * iSlides;
            double max = 1. / nbValues * (iSlides + 1);
            matchingRects.put(iSlides, new ArrayList<>());
            for (int i1 = 0; i1 < index; i1++) {
                if (values[i1] >= min && values[i1] <= max) {
                    Rectangle2 rectangle2 = rectangles.get(position[i1]);
                    matchingRects.get(iSlides).add(rectangle2);
                    dimensionTotal.setSize(dimensionTotal.getWidth() + rectangle2.getW(), dimensionTotal.getHeight() > rectangle2.getH() ?
                            dimensionTotal.getHeight() : rectangle2.getH());
                    maxheight = Math.max((int) dimensionTotal.getHeight(), maxheight);
                }
            }
            maxLine = Math.max(maxLine, matchingRects.get(iSlides).size());

        }
        PixM pSlide = new PixM((int) dimensionTotal.getWidth(), (int) maxheight * mean);
        AtomicInteger iSlides = new AtomicInteger();
        int finalMaxheight = maxheight;
        iSlides.set(0);
        matchingRects.forEach((idx, listOfRects) -> {
            listOfRects.forEach((rectangle2 -> {
                int x = (iSlides.get() % mean) * finalMaxheight;
                int y = (iSlides.get() / mean) * finalMaxheight;
                pSlide.pasteSubImage(input.copySubImage(rectangle2.getX(), rectangle2.getY(), rectangle2.getW(), rectangle2.getH()),
                        x, y, rectangle2.getW(), rectangle2.getH());
                iSlides.getAndIncrement();

            }));
        });
        height += maxheight;
        ImageIO.write(pSlide.getImage(), "jpg", new File(dirOutDist + "_matchingRects_" + pSlide + ".jpg"));

        ImageIO.write(distances.normalize(0, 1).getImage(), "jpg", dirOutDist);
    }

    /***
     *  Recherche par traits.
     *  Traits type = droit, courbe
     *  Nombre de points (point-extrémité dont angle, droites, courbes) par zone (haut-large, haut-gauche, haut-centre, haut-droite)
     *  (milieu, bas, gauche, droite)
     * @param candidateChar Image du candidat.
     * @return list of identified characters
     */
    private List<Character> searchByTraits(PixM candidateChar) {
        List<Character> candidateTraits = new ArrayList<>();
        for (Character character : letters.keySet()) {
            Letter letter = letters.get(character);
        }
        return candidateTraits;
    }

    private Point3D compare(Rectangle2 rect1, Rectangle2 rect2) {
        Point3D distTotale = Point3D.O0;

        for (double i = 0; i < 1; i += STEPS_COMPARE_METHOD) {
            for (double j = 0; j < 1; j += STEPS_COMPARE_METHOD) {
                Point3D a = input.getP(
                        (int) (rect1.getX()
                                + i * rect1.getW())
                        , (int) (rect1.getY()
                                + j * rect1.getH()));
                Point3D b = input.getP(
                        (int) (rect2.getX()
                                + i * rect2.getW())
                        , (int) (rect2.getY()
                                + j * rect2.getH()));

                distTotale = distTotale.plus(a).moins(b);
            }

        }

        return distTotale;
    }

    /***
     La condition doit s'arrêter après les points quand les bords droits
     et bas ont augmenté de manière que le caractère cherché soit mis en
     évidence.
     Bord haut et gauche restent blancs (pas toujours vrai dans les polices)
     Balai vers la droite rencontrent une chose (points noirs) puis s'arrête
     à blanc.
     Balai vers le bas rencontre une chose aussi (points noirs puis s'arrête
     à blanc.
     Peut-il y avoir une confusion en passant 2 balais (peignes) perpendiculaires ?
     Sans doute que oui, ils n'avancent pas au même pas. Quand le blanc est rencontré
     après le noir, il y a arrêt du balai H (par exemple) donc le balai V continue
     jusqu'au blanc. Là le balai H a-t-il rencontré quelque chose qui annule la
     recherche croisée ? Si le balai H est en dessous des caractères il ne rencontre
     plus rien jusqu'à ce que le balai V ait fini.
     @param i column
     @param j line
     */
    private void exec2(int i, int j) {
        if (System.currentTimeMillis() % 100 == 0)
            System.gc();
        if (arrayDiff(input.getValues(i, j), WHITE_DOUBLES) < MIN_DIFF) {
            int w = 0;
            int h = 0;
            boolean fail = false;
            int heightBlackHistory = 0;
            int widthBlackHistory = 0;
            w = charMinWidth;
            h = charMinWidth;
            while (!(heightBlackHistory == 2 && widthBlackHistory == 2
                    && i + w < input.getColumns() && j + h < input.getLines() && h > 0 && w > 0 && w < stepMax && h < stepMax
                    && Arrays.equals(TRUE_BOOLEANS,
                    testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES)))) {
                int w0;
                int h0;
                int wbhBak;
                int hbhBak;
                h0 = h;
                w0 = w;
                hbhBak = heightBlackHistory;
                wbhBak = widthBlackHistory;

                testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                if (!testedRectangleBorder[X_PLUS] || !testedRectangleBorder[Y_MINUS]) {
                    break;
                }
                if ((widthBlackHistory == 0 || heightBlackHistory == 0) && Arrays.equals(testedRectangleBorder, TRUE_BOOLEANS)) {
                    if (widthBlackHistory == 0 && heightBlackHistory == 0) {
                        w++;
                        h++;
                        continue;
                    } else if (widthBlackHistory == 0) {
                        w++;
                        continue;
                    } else if (heightBlackHistory == 0) {
                        h++;
                        continue;
                    }

                }
                if ((widthBlackHistory == 1 || heightBlackHistory == 1) && !Arrays.equals(testedRectangleBorder, TRUE_BOOLEANS)) {
                       /* if (!testedRectangleBorder[X_PLUS] && heightBlackHistory==1) {
                            h++;
                            continue;
                        } else if(!testedRectangleBorder[Y_MINUS]&&widthBlackHistory) {
                            w++;
                            continue;
                        }

                        */
                }
                if (!testedRectangleBorder[X_MINUS] && heightBlackHistory == 0) {
                    heightBlackHistory = 1;
                    h++;
                    continue;
                } else if (!testedRectangleBorder[X_MINUS] && heightBlackHistory == 1) {
                    h++;
                } else if (testedRectangleBorder[X_MINUS] && heightBlackHistory == 1) {
                    heightBlackHistory = 2;
                }

                if (!testedRectangleBorder[Y_PLUS] && widthBlackHistory == 0) {
                    widthBlackHistory = 1;
                    w++;
                } else if (!testedRectangleBorder[Y_PLUS] && widthBlackHistory == 1) {
                    w++;
                } else if (testedRectangleBorder[Y_PLUS] && widthBlackHistory == 1) {
                    widthBlackHistory = 2;
                    continue;
                }

                if ((h > stepMax || w > stepMax) || (((h0 == h) && (w0 == w)
                        && hbhBak == heightBlackHistory && wbhBak == widthBlackHistory)
                        || (heightBlackHistory > 2 && widthBlackHistory > 2))) {
                    break;
                }

            }
            boolean succeded = false;
            if (Arrays.equals(testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                succeded = true;
            } else if (heightBlackHistory == 2 && widthBlackHistory == 2 && !succeded) {
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
                    if (candidates.size() >= 1) {
                        ///System.out.printf("In %s, Rectangle = (%d,%d,%d,%d) \t\tCandidates: ", name, i, j, w, h);
                        //candidates.forEach(System.out::print);
                        //
                        final String[] s = {""};
                        candidates.forEach(character -> s[0] += character);
                        if (candidates.size() > 1) {
                            writer.writeLine(new String[]{name, "" + i, "" + j, "" + w, "" + h, s[0], "\n"});
                            System.err.println();
                            Logger.getAnonymousLogger().info("Characters {" + s[0] + "} (" + i + ", " + j + ")");
                        }
                        if (s[0].length() > 0)
                            pwTxt.println(s[0]);
                        Color random = Colors.random();
                        output.plotCurve(rectangle, new TextureCol(random));
                        countRects++;
                        rectangles.add(rectangle2);
                        PixM outChar = input.copySubImage(rectangle2.getX(),
                                rectangle2.getY(), rectangle2.getW(), rectangle2.getH());
                        outRecompose.pasteSubImage(outChar,
                                rectangle2.getX(), rectangle2.getY(), rectangle2.getW(), rectangle2.getH());
                        if (isExporting()) {
                            File file = new File(dirOutChars + "-" + j + "-" + i + "-" + w + "-" + h + "-" + s[0] + ".jpg");
                            if (!file.getParentFile().exists() || file.getParentFile().isDirectory()) {
                                file.getParentFile().mkdirs();
                                ImageIO.write(outChar.getImage(), "jpg", file);
                            }
                        }
                    }
                }
            }
        }
    }

    /***
     La condition doit s'arrêter après les points quand les bords droits
     et bas ont augmenté de manière que le caractère cherché soit mis en
     évidence.
     Bord haut et gauche restent blancs (pas toujours vrai dans les polices)
     Balai vers la droite rencontrent une chose (points noirs) puis s'arrête
     à blanc.
     Balai vers le bas rencontre une chose aussi (points noirs puis s'arrête
     à blanc.
     Peut-il y avoir une confusion en passant 2 balais (peignes) perpendiculaires ?
     Sans doute que oui, ils n'avancent pas au même pas. Quand le blanc est rencontré
     après le noir, il y a arrêt du balai H (par exemple) donc le balai V continue
     jusqu'au blanc. Là le balai H a-t-il rencontré quelque chose qui annule la
     recherche croisée ? Si le balai H est en dessous des caractères il ne rencontre
     plus rien jusqu'à ce que le balai V ait fini.
     @param i column
     @param j line
     */
    private void exec3(int i, int j) {
        if (System.currentTimeMillis() % 100 == 0)
            System.gc();
        long timeStart = System.currentTimeMillis();
        if (arrayDiff(input.getValues(i, j), WHITE_DOUBLES) < MIN_DIFF) {
            int w = 0;
            int h = 0;
            boolean fail = false;
            int heightBlackHistory = 0;
            int widthBlackHistory = 0;
            w = charMinWidth;
            h = charMinWidth;
            int w0 = charMinWidth;
            int h0 = charMinWidth;
            int hbhBak = 0;
            while (!(heightBlackHistory == 2 && widthBlackHistory == 2
                    && i + w < input.getColumns() && j + h < input.getLines() && h > 0 && w > 0 && w < stepMax && h < stepMax
                    && Arrays.equals(TRUE_BOOLEANS,
                    testedRectangleBorder = testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES)))) {
                if (System.currentTimeMillis() - timeStart > 20) {
                    //Logger.getAnonymousLogger().info("recognize("+i+", "+j+") FAILED. Next!");
                    return;
                }
                int wbhBak = w0;
                hbhBak = h0;
                h0 = h;
                w0 = w;
                hbhBak = heightBlackHistory;
                wbhBak = widthBlackHistory;

                int incrX = 0;
                int incrY = 0;


                testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES);
                if (!testedRectangleBorder[X_PLUS] || !testedRectangleBorder[Y_MINUS]) {
                    if (!testedRectangleBorder[X_PLUS] && heightBlackHistory > 0) {
                        if (w > 0) w--;
                        else break;
                        continue;
                    } else if (!testedRectangleBorder[Y_MINUS] && widthBlackHistory > 0) {
                        if (h > 0) h--;
                        else break;
                        continue;
                    }

                }
                if ((widthBlackHistory == 0 || heightBlackHistory == 0) && Arrays.equals(testedRectangleBorder, TRUE_BOOLEANS)) {
                    if (widthBlackHistory == 0 && heightBlackHistory == 0) {
                        w++;
                        h++;
                        continue;
                    } else if (widthBlackHistory == 0) {
                        w++;
                        continue;
                    } else if (heightBlackHistory == 0) {
                        h++;
                        continue;
                    }

                }
                if (!testedRectangleBorder[X_MINUS] && heightBlackHistory == 0) {
                    heightBlackHistory = 1;
                    h++;
                    continue;
                } else if (!testedRectangleBorder[X_MINUS] && heightBlackHistory == 1) {
                    h++;
                } else if (testedRectangleBorder[X_MINUS] && heightBlackHistory == 1) {
                    heightBlackHistory = 2;
                }

                if (!testedRectangleBorder[Y_PLUS] && widthBlackHistory == 0) {
                    widthBlackHistory = 1;
                    w++;
                } else if (!testedRectangleBorder[Y_PLUS] && widthBlackHistory == 1) {
                    w++;
                } else if (testedRectangleBorder[Y_PLUS] && widthBlackHistory == 1) {
                    widthBlackHistory = 2;
                    continue;
                }

                if ((h > stepMax || w > stepMax) || (((h0 == h) && (w0 == w)
                        && hbhBak == heightBlackHistory && wbhBak == widthBlackHistory)
                        || (heightBlackHistory > 2 && widthBlackHistory > 2))) {
                    break;
                }

                w0 = w;
                h0 = h;

            }
            boolean succeded = false;
            if (Arrays.equals(testRectIs(input, i, j, w, h, testedRectangleBorder, WHITE_DOUBLES), TRUE_BOOLEANS)) {
                succeded = true;
            } else if (heightBlackHistory == 2 && widthBlackHistory == 2 && !succeded) {
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
                Rectangle2 rectangle3 = new Rectangle2(0, 0, 0, 0);
                if (reduce(input, new Rectangle2(rectangle), rectangle3)) {
                    //rectangle2 = new Rectangle2(rectangle);// PROVISOIRE
                    List<Character> candidates = recognize(input, rectangle3);
                    if (candidates.size() >= 1) {
                        ///System.out.printf("In %s, Rectangle = (%d,%d,%d,%d) \t\tCandidates: ", name, i, j, w, h);
                        //candidates.forEach(System.out::print);
                        //
                        final String[] s = {""};
                        candidates.forEach(character -> s[0] += character);
                        if (candidates.size() > 1) {
                            writer.writeLine(new String[]{name, "" + i, "" + j, "" + w, "" + h, s[0], "\n"});
                            System.err.println();
                            Logger.getAnonymousLogger().info("Characters {" + s[0] + "} (" + i + ", " + j + ")");
                        }
                        if (s[0].length() > 0)
                            pwTxt.println(s[0]);
                        Color random = Colors.random();
                        output.plotCurve(rectangle, new TextureCol(random));
                        countRects++;
                        rectangles2.add(rectangle3);
                        PixM outChar = input.copySubImage(rectangle3.getX(),
                                rectangle3.getY(), rectangle3.getW(), rectangle3.getH());
                        outRecompose.pasteSubImage(outChar,
                                rectangle3.getX(), rectangle3.getY(), rectangle3.getW(), rectangle3.getH());
                        if (isExporting()) {
                            File file = new File(dirOutChars + "-" + j + "-" + i + "-" + w + "-" + h + "-" + s[0] + ".jpg");
                            if (!file.getParentFile().exists() || file.getParentFile().isDirectory()) {
                                file.getParentFile().mkdirs();
                                ImageIO.write(outChar.getImage(), "jpg", file);
                            }
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

        Logger.getAnonymousLogger().info("Horizontal");
        List<Character> ch = recognizeH(input, i, j, w, h);
        Logger.getAnonymousLogger().info("vertical");
        List<Character> cv = recognizeV(input, i, j, w, h);

        List<Character> allCharsPossible = new ArrayList<>();


        // Intersect
        cv.forEach(character2 -> {
            ch.forEach(character1 -> {
                if (character1.equals(character2)) {
                    allCharsPossible.add(character2);
                }
            });
        });

        if (allCharsPossible.size() > 0) {
            //Logger.getAnonymousLogger().info("Csv {");
            //printCharacterArray(cv);
            //Logger.getAnonymousLogger().info("Csh {");
            //printCharacterArray(ch);
            //Logger.getAnonymousLogger().info("AllCharsPossible {");
            //printCharacterArray(allCharsPossible);
        }
        //if (allCharsPossible.size() == 0)
        //    allCharsPossible.add('-');

        return allCharsPossible;
    }

    private boolean[] testRectIs(PixM input, int x, int y, int w, int h, boolean[] result, double[] color) {
        int i, j;
        // XPLUS
        result[X_PLUS] = true;
        for (i = x; i <= x + w; i++)
            if (arrayDiff(input.getValues(i, y), color) > MIN_DIFF) result[X_PLUS] = false;
        result[Y_PLUS] = true;
        for (j = y; j <= y + h; j++)
            if (arrayDiff(input.getValues(x + w, j), color) > MIN_DIFF) result[Y_PLUS] = false;
        result[X_MINUS] = true;
        for (i = x + w; i >= x; i--)
            if (arrayDiff(input.getValues(i, y + h), color) > MIN_DIFF) result[X_MINUS] = false;
        result[Y_MINUS] = true;
        for (j = y + h; j >= y; j--)
            if (arrayDiff(input.getValues(x, j), color) > MIN_DIFF) result[Y_MINUS] = false;
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
        mapCharsAlphabetLines.put('b', new Integer[]{1, 2, 1, 1, 2, 1});
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

    public Map<Character, Integer[]> initPatternsH() {
        Map<Character, Integer[]> mapCharsAlphabetLines = new HashMap<>();
        mapCharsAlphabetLines.put('A', new Integer[]{1, 2, 1});
        mapCharsAlphabetLines.put('a', new Integer[]{2, 3, 1});
        mapCharsAlphabetLines.put('B', new Integer[]{1, 3, 2, 1});
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

    //columns = trimArrayZeroes(columns, idx);

    public List<Character> recognizeV(PixM mat, int x, int y, int w, int h) {

        List<Character> retained = new ArrayList<>();

        Integer[] columns = new Integer[w + h + 1];
        boolean firstColumn = true;
        int idx = 0;
        int count0 = -1;
        for (int i = x; i <= x + w; i++) {
            var ref = new Object() {
                int countOnColumnI = 0;
            };
            int current = BLANK;
            for (int j = y; j <= y + h; j++) {
                if (mat.luminance(i, j) < MAX_BLACK_VALUE
                        && current == BLANK) {
                    ref.countOnColumnI++;
                    current = CHARS;
                } else if (current == CHARS && mat.luminance(i, j) >= MAX_BLACK_VALUE) {
                    current = BLANK;
                }
            }
            if (ref.countOnColumnI != count0
                    || firstColumn) {
                columns[idx] = ref.countOnColumnI;
                idx++;
            }
            if (firstColumn) {
                firstColumn = false;
            }

            count0 = ref.countOnColumnI;


        }
        columns = Arrays.copyOfRange(columns, 0, idx);
        columns = trimArrayZeroes(columns);

        printIntegerArray(columns);

        Integer[] compareA = columns;
        Iterator<Character> spliterator = characterMapV.keySet().stream().iterator();
        while (spliterator.hasNext()) {
            Character compareB = spliterator.next();
            if (Arrays.equals(compareA, characterMapV.get(compareB))) {
                retained.add(compareB);
            }
        }
        return retained;
    }

    private void printIntegerArray(Integer[] finalColumns) {
        if (!cEchoing || finalColumns == null)
            return;
        String[] s = new String[]{""};
        for (Integer finalColumn : finalColumns) {
            s[0] += "" + finalColumn + ":";
        }
        Logger.getAnonymousLogger().log(Level.INFO, "Array : { " + s[0] + " }");

    }

    private void printCharacterArray(List<Character> chars) {
        if (!cEchoing)
            return;
        String[] s = new String[]{""};
        for (Character character : chars) {
            s[0] += "" + character + ":";
        }
        Logger.getAnonymousLogger().log(Level.INFO, "Array : { " + s[0] + " }");

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
                int countOnColumnI = 0;
            };
            int current = BLANK;
            for (int i = x; i <= x + w; i++) {
                if (mat.luminance(i, j) < MAX_BLACK_VALUE
                        && current == BLANK) {
                    ref.countOnColumnI++;
                    current = CHARS;

                } else if (current == CHARS && mat.luminance(i, j) >= MAX_BLACK_VALUE) {
                    current = BLANK;
                }
            }


            if (ref.countOnColumnI != count0
                    || firstLine) {

                lines[idx] = ref.countOnColumnI;
                idx++;
            }
            if (firstLine) {
                firstLine = false;
            }

            count0 = ref.countOnColumnI;


        }
        Integer[] integers1 = Arrays.copyOfRange(lines, 0, idx);
        lines = trimArrayZeroes(integers1);

        //lines = trimArrayZeroes(lines, idx);
        printIntegerArray(lines);

        final Integer[] finalLines = lines;

        Integer[] compareA = lines;
        Iterator<Character> keys = characterMapH.keySet().iterator();
        keys.forEachRemaining(new Consumer<Character>() {
            @Override
            public void accept(Character character) {
                if (Arrays.equals(compareA, characterMapH.get(character))) {
                    retained.add((Character) character);
                }

            }
        });

        return retained;
    }

    private Integer[] trimArrayZeroes(Integer[] lines) {
        Integer[] cut = new Integer[lines.length];
        boolean firstZeros = true;
        boolean lastZeros = true;
        int j = 0;
        int size = 0;
        for (int i = 0; i < lines.length && (firstZeros); i++) {
            if (lines[i] == null || lines[i] == 0) {
                size++;
            } else {
                firstZeros = false;
            }

        }

        if (size == lines.length)
            return new Integer[]{0};

        cut = Arrays.copyOfRange(lines, size, lines.length);

        size = cut.length;

        for (int i = cut.length - 1; i >= 0 && (lastZeros); i--) {
            if (cut[i] == null || cut[i] == 0) {
                size--;
            } else {
                lastZeros = false;
            }

        }

        if (size > 0)
            return Arrays.copyOfRange(cut, 0, size);
        else
            return new Integer[]{0};
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
            if (booleans[X_PLUS])
                render.setY(render.getY() + 1);
            else if (booleans[X_MINUS])
                render.setH(render.getH() - 1);
            else if (booleans[Y_PLUS])
                render.setW(render.getW() - 1);
            else if (booleans[Y_MINUS])
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

    public PixM derivative(PixM input) {
        PixM output = new PixM(input.getColumns(), input.getLines());

        for (int i = 0; i < input.getColumns(); i++) {
            for (int j = 0; j < input.getLines(); j++) {
                output.setP(i, j, output.getP(i - 1, j)
                        .plus(output.getP(i, j - 1))
                        .plus(input.getP(i, j)));
            }
        }
        return output.normalize(0., 1.);
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

