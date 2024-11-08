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

package one.empty3.growth.graphics;

import one.empty3.growth.*;

import one.empty3.libs.*;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrawingLSystem2D {
    private Turtle2D turtle2D;
    private LSystem lSystem;
    private HashMap<Symbol, String> symbolActionMap;

    public DrawingLSystem2D(Turtle2D t, LSystem lSystem, HashMap<Symbol, String> symbolAction) {
        this.turtle2D = t;
        this.lSystem = lSystem;
        this.symbolActionMap = symbolAction;
    }

    public void reset(Image image) {
        turtle2D = new Turtle2D(image);

    }


    public void applyRules() throws NotWellFormattedSystem {
        lSystem.applyRules();
    }
    public void drawStep() {
        SymbolSequence currentSymbols = lSystem.getCurrentSymbols();
        SymbolSequence parts = currentSymbols.parts();
        //Logger.getAnonymousLogger().log(Level.INFO, "s= " + (s==null?"null":s)+"::"+param);
        parts.getSymbols().stream().filter(symb -> symb instanceof Symbol).map(
                symb -> (Symbol) symb).forEach(
                sym -> symbolActionMap.forEach((symbol, s) -> {
            if ((s != null) && symbol.equals(sym)) {
                double paramV = 0;
                Parameter param = lSystem.getParam(lSystem.getT(), "" + symbol.getValue());
                if (param != null) {
                    if (param instanceof Parameter) {
                        paramV = ((Parameter) param).eval(lSystem.getT(), 1);

                    } else if (param instanceof FunctionalParameter) {
                        paramV = ((FunctionalParameter) param).eval(lSystem.getT(), 1);

                    }
                }//Logger.getAnonymousLogger().log(Level.INFO, "s= " + (s==null?"null":s)+"::"+param);
                switch (s)

                {
                    case "line":
                        line(paramV);
                        break;
                    case "left":
                        left(paramV);
                        break;
                    case "right":
                        right(paramV);
                        break;
                    case "move":
                        move(paramV);
                        break;
                    case "nothing":
                        break;

                    default:
                        break;
                }
                Logger.getAnonymousLogger().log(Level.INFO, s + "(p=" + paramV + ")");
            }
        }));
    }

    private void move(double v) {
        turtle2D.move(v);
    }


    public void line(double dist) {
        turtle2D.line(dist);
    }

    public void right(double angle) {
        turtle2D.right(angle);
    }

    public void left(double angle) {
        turtle2D.left(angle);
    }
}

