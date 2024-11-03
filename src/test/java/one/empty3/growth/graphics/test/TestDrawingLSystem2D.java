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

package one.empty3.growth.graphics.test;

import one.empty3.growth.*;
import one.empty3.growth.graphics.DrawingLSystem2D;
import one.empty3.growth.graphics.Turtle2D;
import one.empty3.growth.test.TestCaseExtended;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.notification.RunListener;
import org.junit.runners.JUnit4;

import javax.imageio.ImageIO;
import java.awt.*;

import one.empty3.libs.Image;
import one.empty3.library.ECImage;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(JUnit4.class)
public class TestDrawingLSystem2D extends TestCaseExtended {
    private int MAX = 3;

    /**
     * n = 4, δ = 90°
     * F+F+F+F
     * F → FF+F+F+F+F+F−F
     * n = 4, δ = 90°
     * F+F+F+F
     * F → FF+F+F+F+FF
     * n = 3, δ = 90°
     * F+F+F+F
     * F → FF+F−F+F+FF
     * n = 4, δ = 90°
     * F+F+F+F
     * F → FF+F++F+F
     * n = 5, δ = 90°
     * F+F+F+F
     * F → F+FF++F+F
     * n = 4, δ = 90°
     * F+F+F+F
     * F → F+F−F+F+F
     * −
     * Figure 2.5: A sequence of Koch curves obtained by successively modifying
     * the production successor.
     */
    @Test
    public void testDrawing() throws NotWellFormattedSystem {
        Turtle2D turtle2D;
        LSystem lSystem = new LSystem();
        lSystem.init();
        HashMap<Symbol, String> map;

        String[] graph = new String[]{"line", "move", "left", "right"};

        Symbol[] symbols = new Symbol[2];
        symbols[0] = new Symbol('A');
        symbols[1] = new Symbol('B');

        ParameterSequence parameterSequence;
        ArrayList<Parameter> objects;

        objects = new ArrayList<>();
        try {
            lSystem.addInitialParam(symbols[1], new Parameter("b", 60.0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        parameterSequence = new ParameterSequence();
        parameterSequence.setParametersLst(objects);
        symbols[0].setParameters(parameterSequence);

        objects = new ArrayList<>();
        try {
            lSystem.addInitialParam(symbols[1], new Parameter("a", Math.PI / 7));
        } catch (Exception e) {
            e.printStackTrace();
        }
        parameterSequence = new ParameterSequence();
        parameterSequence.setParametersLst(objects);
        symbols[1].setParameters(parameterSequence);

        map = new HashMap<>();
        map.put(symbols[0], graph[0]);
        map.put(symbols[1], graph[2]);


        SymbolSequence[] rules = new SymbolSequence[2];
        rules[0] = new SymbolSequence();
        rules[0].add(new Symbol('B'));
        rules[1] = new SymbolSequence();
        rules[1].add(new Symbol('A'));
        lSystem.addRule(rules[0], rules[1]);

        rules[0] = new SymbolSequence();
        rules[0].add(new Symbol('A'));
        rules[1] = new SymbolSequence();
        rules[1].add(new Symbol('A'));
        rules[1].add(new Symbol('B'));
        lSystem.addRule(rules[0], rules[1]);

        SymbolSequence symbolSequence = new SymbolSequence();
        symbolSequence.add(new Symbol('A'));
        lSystem.setCurrentSymbols(symbolSequence);

        turtle2D = new Turtle2D(new Image(1600, 1200, Image.TYPE_INT_RGB));
        turtle2D.setZeColor(Color.RED);


        DrawingLSystem2D drawingLSystem2D = new DrawingLSystem2D(turtle2D, lSystem, map);

        for (int i = 0; i < 10; i++) {
            drawingLSystem2D.drawStep();
            lSystem.applyRules();
            //Logger.getAnonymousLogger().log(Level.INFO, lSystem.toString());
        }
        Logger.getAnonymousLogger().log(Level.INFO, lSystem.toString());
        try {
            File filename = getUniqueFilenameForProduction("testResults", "testDrawing", "jpg");
            ImageIO.write((RenderedImage) turtle2D.getZeImage(), "jpg", filename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    @Test
    public void testFractaleKoch() throws Exception {
        double angle = Math.PI / 2;
        String angleStr = "" + angle;

        Turtle2D turtle2D;

        LSystem lSystem = new LSystem();
        lSystem.init();

        HashMap<Symbol, String> map;

        String[] graph = new String[]{"line", "move", "left", "right"};

        Symbol[] symbols = new Symbol[3];
        symbols[0] = new Symbol('F');
        symbols[1] = new Symbol('+');
        symbols[2] = new Symbol('-');

        lSystem.addParameter(0, new FunctionalParameter("F", 20.0,
                "160/(4*t*t)"));
        lSystem.addParameter(0, new FunctionalParameter("+", angle,
                angleStr));
        lSystem.addParameter(0, new FunctionalParameter("-", -angle,
                "-" + angleStr));


        map = new HashMap<>();
        map.put(symbols[0], graph[0]);
        map.put(symbols[1], graph[2]);
        map.put(symbols[2], graph[3]);

        lSystem.addRule("F", "F+F-F-FF+F+F-F");


        lSystem.setCurrentSymbols("F+F+F+F");


        runLSystem(MAX, lSystem, map, 1600, 1200, "testFractaleKoch", true);


    }

    @Test
    public void testFractaleKoch1() throws Exception {
        double angle = Math.PI / 2;
        String angleStr = "" + angle;

        LSystem lSystem = new LSystem();
        lSystem.init();

        HashMap<Symbol, String> map;

        String[] graph = new String[]{"line", "move", "left", "right"};

        Symbol[] symbols = new Symbol[3];
        symbols[0] = new Symbol('F');
        symbols[1] = new Symbol('+');
        symbols[2] = new Symbol('-');

        lSystem.addParameter(0, new FunctionalParameter("F", 40.0,
                "160/(4*t)"));
        lSystem.addParameter(0, new FunctionalParameter("+", angle,
                angleStr));
        lSystem.addParameter(0, new FunctionalParameter("-", -angle,
                "-" + angleStr));

        map = new HashMap<>();
        map.put(symbols[0], graph[0]);
        map.put(symbols[1], graph[2]);
        map.put(symbols[2], graph[3]);

        lSystem.addRule("F", "F-F+F+F-F");

        lSystem.setCurrentSymbols("+F");

        runLSystem(MAX, lSystem, map, 800, 600, "testFractaleKoch2", true);


    }

    @Test
    public void testDragonCurve() throws Exception {
        int N = 3;

        double angle = Math.PI / 2;
        String angleStr = "" + angle;

        Turtle2D turtle2D;

        LSystem lSystem = new LSystem();
        lSystem.init();

        HashMap<Symbol, String> map;

        String[] graph = new String[]{"line", "move", "left", "right", "nothing"};

        Symbol[] symbols = new Symbol[5];
        symbols[0] = new Symbol('F');
        symbols[1] = new Symbol('+');
        symbols[2] = new Symbol('-');
        symbols[3] = new Symbol('r');
        symbols[4] = new Symbol('l');

        lSystem.addParameter(0, new FunctionalParameter("F", 40.0,
                "160/(t*4)"));
        lSystem.addParameter(0, new FunctionalParameter("+", angle,
                angleStr));
        lSystem.addParameter(0, new FunctionalParameter("-", -angle,
                "-" + angleStr));
        lSystem.addParameter(0, new FunctionalParameter("r", 0.0, "0.0"));
        lSystem.addParameter(0, new FunctionalParameter("l", 0.0, "0.0"));

        map = new HashMap<>();
        map.put(symbols[0], graph[0]);
        map.put(symbols[1], graph[2]);
        map.put(symbols[2], graph[3]);
        map.put(symbols[3], graph[4]);
        map.put(symbols[4], graph[4]);

        lSystem.addRule("l", "l+rF+");
        lSystem.addRule("r", "-Fl-r");

        lSystem.setCurrentSymbols("Fl");


        runLSystem(N - 1, lSystem, map, 1600, 800, "testDragonCurve", true);


    }

    @Test
    public void testPseudoHilbert() throws Exception {
        int N = 3;

        double angle = Math.PI / 2;
        String angleStr = "" + angle;

        Turtle2D turtle2D;

        LSystem lSystem = new LSystem();
        lSystem.init();

        HashMap<Symbol, String> map;

        String[] graph = new String[]{"line", "move", "left", "right", "nothing"};

        Symbol[] symbols = new Symbol[3];
        symbols[0] = new Symbol('F');
        symbols[1] = new Symbol('+');
        symbols[2] = new Symbol('-');

        lSystem.addParameter(0, new FunctionalParameter("F", 40.0,
                "160/(t*4)"));
        lSystem.addParameter(0, new FunctionalParameter("+", angle,
                angleStr));
        lSystem.addParameter(0, new FunctionalParameter("-", -angle,
                "-" + angleStr));
        lSystem.addParameter(0, new FunctionalParameter("r", 0.0, "0.0"));
        lSystem.addParameter(0, new FunctionalParameter("l", 0.0, "0.0"));

        map = new HashMap<>();
        map.put(symbols[0], graph[0]);
        map.put(symbols[1], graph[2]);
        map.put(symbols[2], graph[3]);

        //lSystem.addRule("X", "XFYFX+F+YFXFY−F−XFYFX");
        //lSystem.addRule("Y", "YFXFY−F−XFYFX+F+YFXFY");
        lSystem.addRule("L", "+RF-LFL-FR+");
        lSystem.addRule("R", "-LF+RFR+FL-");

        //lSystem.setCurrentSymbols("X");
        lSystem.setCurrentSymbols("L");

        runLSystem(N, lSystem, map, 1600, 800, "PseudoHilbertCurve", true);


    }

    @Test
    private void runLSystem(int n, LSystem lSystem, HashMap<Symbol, String> map, int width, int height, String baseFilename, boolean writeIntermediateImages) throws NotWellFormattedSystem {
        Turtle2D turtle2D;
        for (int i = 0; i < n; i++) {
            lSystem.applyRules();
            Logger.getAnonymousLogger().log(Level.INFO, "t::" + lSystem.getT());
            if (i < n - 1 && writeIntermediateImages) {
                Image bufferedImage = new Image(width, height, Image.TYPE_INT_RGB);
                turtle2D = new Turtle2D(bufferedImage);
                turtle2D.setZeColor(Color.WHITE);
                DrawingLSystem2D drawingLSystem2D = new DrawingLSystem2D(turtle2D, lSystem, map);
                drawingLSystem2D.drawStep();

                try {
                    File filename = getUniqueFilenameForProduction("testResults", baseFilename, "" + i + ".jpg");
                    ImageIO.write((RenderedImage) turtle2D.getZeImage(), "jpg", filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
