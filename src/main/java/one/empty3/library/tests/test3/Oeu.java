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

package one.empty3.library.tests.test3;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.Sphere;
import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.pointset.Gravity;
import one.empty3.pointset.Move;
import one.empty3.pointset.PCont;

import java.util.HashMap;

/*__
 *
 * Rassembler et améliorer les calculs sur des ensembles de points dans un package
 * utilisable et extensible.
 *
 * delta(x2+y2+z2-R) pente force 1
 * delta(mM(p2-p1)/d3) pente force 2
 * combiner et calibrer
 * accrocher les points sur la surface
 */
public class Oeu extends TestObjetSub {
    PCont<Gravity> point3DPCont = new PCont<Gravity>();
    Move move = new Move(point3DPCont);
    HashMap<String, Double> map;
    private int pointCount;

    public static void main(String[] args) {
        Oeu oeu = new Oeu();
        new Thread(oeu).start();
    }

    public void ginit() {
        map = new HashMap();
        move.setItereFrame(100);
        scene().add(point3DPCont);
        try {
            move.initMoveSurface("coordArr*coordArr+y*y+z*z-R*R", map);
        } catch (AlgebraicFormulaSyntaxException e) {
            e.printStackTrace();
        }
        setPointCount(10);
        for (int i = 0; i < pointCount; i++) {

            Gravity gravity = new Gravity(Point3D.random(100d), 10d, Point3D.random(10.0));
            scene().add(new Sphere(gravity, 2));
            point3DPCont.add(gravity);
        }
        scene().cameraActive(new Camera(Point3D.Z.mult(-10d), Point3D.O0, Point3D.Y));
    }

    public void finit() {
        for (int i = 0; i < pointCount; i++) {
            move.computeMoveSurface(point3DPCont.get(i));
        }

    }

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public HashMap<String, Double> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Double> map) {
        this.map = map;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }
}
