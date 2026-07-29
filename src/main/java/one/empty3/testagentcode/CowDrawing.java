/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
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
 *  * Created by $user $date
 *
 *
 */

package one.empty3.testagentcode;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.tribase.Tubulaire5;


public class CowDrawing extends TestObjetSub {
    int FPS = 25;
    double DURATION_SECONDS = 20.0;

    private static Tubulaire5 cowModel;
    private double totalFrames;

    @Override
    public void finit() throws Exception {
        // 5. Animation de la rotation (Rule 2 & 5)
        // Calcul de l'angle en fonction de l'image actuelle (frame)
        totalFrames = (double) (DURATION_SECONDS * FPS);

        double angle = 2.0 * Math.PI * (double) frame / totalFrames;

        // Rotation autour de l'axe Y : mise à jour des vecteurs d'orientation
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        // Modification des axes de la sphère pour la faire tourner sur elle-même
        cowModel.setVectX(new Point3D(cosA, 0.0, -sinA));
        cowModel.setVectY(Point3D.Y);
        cowModel.setVectZ(new Point3D(sinA, 0.0, cosA));
        cowModel.setOrig(new Point3D(0.0, 0.0, 0.0)); // Centre de rotation
    }


    @Override
    public void ginit() {

        // Ceci évite les matrices de caméra dégénérées.
        camera(new Camera(new Point3D(0.0, 0.0, 5.0), Point3D.O0, Point3D.Y));
        scene().cameraActive(camera());
        Tubulaire5 tubulaire5 = new Tubulaire5();
        tubulaire5.getSoulCurve().setElem(new Bezier(new Point3D[]{Point3D.O0, Point3D.O0.add(new Point3D(1.0, 0.1, 0.0)), Point3D.O0.add(new Point3D(2.0, 0.2, 0.0))}));
        tubulaire5.getDiameterFunctionZ().setElem(new BezierMap(new Bezier2D(new Point3D[][]{{new Point3D(0.0, 0.0, .5), new Point3D(0.0, 0.1, .5)},
                {new Point3D(1.0, 0.0, 1.0), new Point3D(1.0, 1.0, 1.0)}})));
        tubulaire5.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1.0f)));

        cowModel = tubulaire5;
        scene().add(tubulaire5);
    }

    public static void main(String[] args) {
        CowDrawing cowDrawing = new CowDrawing();
        cowDrawing.setPublish(true);
        cowDrawing.loop(true);
        cowDrawing.setMaxFrames((int) (cowDrawing.FPS * cowDrawing.DURATION_SECONDS));
        Thread thread = new Thread(cowDrawing);
        thread.start();
    }
}