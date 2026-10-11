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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package one.empty3.library.core.script;
//
//import one.empty3.library.Point3D;
//import one.empty3.library.PolyMap;
//
//import java.util.ArrayList;
//
///*__
// * @author Se7en
// */
//public class InterpretePolyMapDef implements Interprete {
//
//    private String rep;
//    private int position;
//
//    public InterpretePolyMapDef() {
//    }
//
//    public InterpreteConstants constant() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public int getPosition() {
//        return position;
//    }
//
//    public Object interprete(String text, int pos) throws InterpreteException {
//        InterpretesBase ib;
//        ArrayList<Integer> pattern;
//        ib = new InterpretesBase();
//
//        pattern = new ArrayList<Integer>();
//        pattern.add(ib.BLANK);
//        pattern.add(ib.LEFTPARENTHESIS);
//        pattern.add(ib.BLANK);
//        pattern.add(ib.DECIMAL);
//        pattern.add(ib.BLANK);
//        pattern.add(ib.DECIMAL);
//        pattern.add(ib.BLANK);
//        ib.compile(pattern);
//        ArrayList<Object> read = ib.read(text, pos);
//        pos = ib.getPosition();
//
//        Integer width = (Integer) read.get(3);
//        Integer height = (Integer) read.get(5);
//
//        PolyMap pm = new PolyMap(width);
//
//        InterpreteListePoints ilp = new InterpreteListePoints();
//        ArrayList<Point3D> interprete = (ArrayList<Point3D>) ilp.interprete(text, pos);
//        pos = ilp.getPosition();
//
//        for (int coordArr = 0; coordArr < width; coordArr++) {
//
//            for (int y = 0; y < height; y++) {
//
//                pm.addPoint(y, interprete.get(coordArr + y * width));
//
//            }
//
//        }
//
//        pattern = new ArrayList<Integer>();
//        pattern.add(ib.BLANK);
//        pattern.add(ib.LEFTPARENTHESIS);
//        pattern.add(ib.BLANK);
//        ib.compile(pattern);
//        ib.read(text, pos);
//        pos = ib.getPosition();
//
//        this.position = pos;
//
//        return pm;
//    }
//
//    public void setConstant(InterpreteConstants c) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public void setRepertoire(String r) {
//        this.rep = r;
//    }
//
//}
