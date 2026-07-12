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

package one.empty3.library.tests.tests2.ver;

import one.empty3.library.*;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.TubulaireN;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/*__
 * @author Se7en
 */
public class Serpentin extends TestObjetSub {

    public int MAXFRAMES = 2000;
    public int N = 7;
    public int Ncolors = 6;
    private Point3D[] s;
    private Point3D[] v;
    private double V = 0.08;
    private double D = 1.0;
    private double TUBE_RAYON = 0.1;
    private HashMap<Point2D, Color> map = new HashMap<Point2D, Color>();
    private TubulaireN tubulaireN;
    private ITexture itext;

    public static void main(String[] args) {
        Serpentin th = new Serpentin();

        th.loop(true);

        th.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);

        th.run();
    }

    @Override
    public void ginit() {
        try {
            itext = new TextureImg(ECBufferedImage.getFromFile(new File("c:\\Emptycanvas\\textures\\text1.jpg")));
        } catch (IOException ex) {
            itext = new TextureCol(Color.BLUE);
        }

        LumierePonctuelle lumierePonctuelle = new LumierePonctuelle(Point3D.X, Color.RED);
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);

        lumierePonctuelle = new LumierePonctuelle(Point3D.Y, Color.GREEN);
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);

        s = new Point3D[N];
        v = new Point3D[N];
        for (int i = 0; i < N; i++) {

            s[i] = rand(-D, D);

            v[i] = rand(-V, V);

        }

        scene().lumieres().add(new LumierePonctuelle(Point3D.O0, Color.WHITE));

        Camera camera;
        camera = new Camera(new Point3D(0d, 0d, -3d),
                new Point3D(0d, 0d, 0d));

        scene().cameraActive(camera);

    }

    public Point3D rand(double limitMinus, double limitPlus) {
        Double[] d = new Double[3];
        for (int i = 0; i < 3; i++) {
            d[i] = (limitPlus - limitMinus) * Math.random() + limitMinus;
        }
        return new Point3D(d, null);
    }

    public void bounce(int i) {
        s[i] = s[i].plus(v[i]);

        if (s[i].getX() > D && v[i].getX() > 0) {
            v[i].setX(-v[i].getX());
        }
        if (s[i].getX() < -D && v[i].getX() < 0) {
            v[i].setX(-v[i].getX());
        }
        if (s[i].getY() > D && v[i].getY() > 0) {
            v[i].setY(-v[i].getY());
        }
        if (s[i].getY() < -D && v[i].getY() < 0) {
            v[i].setY(-v[i].getY());
        }
        if (s[i].getZ() > D && v[i].getZ() > 0) {
            v[i].setZ(-v[i].getZ());
        }
        if (s[i].getZ() < -D && v[i].getZ() < 0) {
            v[i].setZ(-v[i].getZ());
        }
    }

    @Override
    public void testScene() throws Exception {

        scene().remove(tubulaireN);

        for (int i = 0; i < s.length; i++) {
            bounce(i);
        }

        tubulaireN = new TubulaireN();
        tubulaireN.diam((float) TUBE_RAYON);
        tubulaireN.texture(itext);
        tubulaireN.nbrAnneaux(40);
        tubulaireN.nbrRotations(20);
        for (Point3D p : s) {
            tubulaireN.addPoint(p);
        }
        scene().add(tubulaireN);

    }

    private Matrix33 matrix1(double a, double b) {
        return Matrix33.rot(a, b);
    }

    @Override
    public void finit() {

    }
}
