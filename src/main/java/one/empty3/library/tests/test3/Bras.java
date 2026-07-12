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

import one.empty3.library.*;
import one.empty3.library.core.physics.Bille;
import one.empty3.library.core.physics.Force;
import one.empty3.library.core.testing.TestObjetSub;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/*__
 * Created by manue on 20-06-19.
 */
public class Bras extends TestObjetSub {
    public static final double longueur = 100;
    int nbrLines = 20;
    private int nbrChunck = 100;
    private Point3D lastPoint;
    private Force force;
    private ITexture textureDefault;
    private double speed = 10;

    public Bras() {

    }


    @Override
    public void ginit() {
        getZ().couleurDeFond(new TextureCol(Color.WHITE));
        try {
            textureDefault = new TextureImg(
                    new ECBufferedImage(
                            ImageIO.read(new File("samples/img/herbe.jpg"))));
        } catch (Exception ex) {
        }

        ArrayList<Bille> allPoints = new ArrayList<>();
        for (int i = 0; i < nbrLines; i++) {
            PolyLine polyLine = new PolyLine();
            polyLine.getParameters().setIncrU(0.0001);

            Point3D start = new Point3D(Point3D.O0);

            polyLine.add(start);
            Bille bille = new Bille();
            bille.setMasse(10);
            bille.setPosition(start);
            allPoints.add(bille);
            for (int j = 0; j < nbrChunck; j++) {
                lastPoint = polyLine.getPoints().get(polyLine.getPoints().size() - 1);
                lastPoint = lastPoint.plus(Point3D.random(longueur));
                polyLine.add(lastPoint);
                bille = new Bille();
                bille.setMasse(10);
                bille.setPosition(lastPoint);
                allPoints.add(bille);
            }
            scene().add(polyLine);
            polyLine.texture(textureDefault);
        }
    }

    public void finit() {
        Camera camera = new Camera(Point3D.Z.mult(
                longueur * 2 * nbrChunck / 10), Point3D.O0);
        scene().add(camera);
        scene().cameraActive(camera);
        double t = 1.0 * frame() / (getMaxFrames());


        Iterator<Representable> representableIterator = scene().iterator();
        while (representableIterator.hasNext()) {
            Representable r = representableIterator.next();
            if (r instanceof PolyLine) {
                PolyLine pl = (PolyLine) r;
                for (int i = 0; i < pl.getPoints().size(); i++)
                    pl.getPoints().set(i, pl.getPoints().get(i).plus(Point3D.random(speed)));
            }
        }
    }


    public static void main(String[] args) {
        Bras bras = new Bras();
        bras.setMaxFrames(10000);
        bras.loop(true);
        new Thread(bras).start();
    }

}
