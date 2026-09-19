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

package one.empty3.library.tests.tests2.ballecouleur;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.nurbs.BSpline;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.tribase.TubulaireN2;
import one.empty3.library.tests.tests2.balleclou.BalleClous2;

import one.empty3.libs.Image;
import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/*__
 * @author Manuel Dahmen
 * Doesn't work
 */
public class TestBalleCouleur001 extends TestObjetSub {

    public int MAXFRAMES = 2000;
    public int N = 20;
    public int Ncolors = 20;
    private BalleClous2 ballec;
    private Point3D[] s;
    private Point3D[] v;
    private final double V = 1.0;
    private final double D = 3.0;
    private final double ballecparam = 0.01;
    private final double TUBE_RAYON = 0.02;
    private final double vMax = 0.01;
    private final HashMap<Point2D, Color> map = new HashMap<Point2D, Color>();
    private final Color balleColor = one.empty3.libs.Color.newCol(0,0,1);

    public static void main(String[] args) {
        TestBalleCouleur001 th = new TestBalleCouleur001();

        th.loop(true);

        th.setPublish(true);

        th.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);

        new Thread(th).start();
    }

    @Override
    public void ginit() {
//        ParametricSurface.getGlobals().setIncrU(0.1);
//        ParametricSurface.getGlobals().setIncrV(0.1);

        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);

        LumierePonctuelle lumierePonctuelle = new LumierePonctuelle(Point3D.X, one.empty3.libs.Color.newCol(1.0f,0.0f,0f));
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);

        lumierePonctuelle = new LumierePonctuelle(Point3D.Y, one.empty3.libs.Color.newCol(0.0f,1.0f,0f));
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);

        for (int c = 0; c < Ncolors; c++) {
            Color put = map.put(new Point2D(Math.random() * 100, Math.random() * 100), Color.newCol((float) Math.random(), (float) Math.random(), (float) Math.random()));
        }

        s = new Point3D[N];
        v = new Point3D[N];
        for (int i = 0; i < N; i++) {

            s[i] = new Point3D((Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2, (Math.random() - 0.5) * 2);

            v[i] = new Point3D((Math.random() - 0.5) * (2), (Math.random() - 0.5) * (2), (Math.random() - 0.5) * (2)).mult(vMax);

        }

        Camera camera;
        camera = new Camera(new Point3D(0d, 0d, 3d),
                new Point3D(0d, 0d, 0d), Point3D.Y);

        scene().cameraActive(camera);

    }

    public void bounce(int i) {
        s[i].changeTo(s[i].plus(v[i]));

        if (s[i].getX() > 1.0 && v[i].getX() > 0) {
            v[i].setX(Math.max(-v[i].getX(), -vMax));
        }
        if (s[i].getX() < -1.0 && v[i].getX() < 0) {
            v[i].setX(Math.min(-v[i].getX(), vMax));
        }
        if (s[i].getY() > 1.0 && v[i].getY() > 0) {
            v[i].setY(Math.max(-v[i].getY(), -vMax));
        }
        if (s[i].getY() < -1.0 && v[i].getY() < 0) {
            v[i].setY(Math.min(-v[i].getY(), vMax));
        }
        if (s[i].getZ() > 1.0 && v[i].getZ() > 0) {
            v[i].setZ(Math.max(-v[i].getZ(), -vMax));
        }
        if (s[i].getZ() < -1.0 && v[i].getZ() < 0) {
            v[i].setZ(Math.min(-v[i].getZ(), vMax));
        }
    }

    @Override
    public void finit() {


        scene().clear();
        ballec = new BalleClous2(Point3D.O0, D);

        ballec.param(ballecparam, BalleClous2.METHOD_SUM);


        ballec.texture(new ColorTexture(balleColor));

        scene().add(ballec);

        ballec.setIncrU(0.01);
        ballec.setIncrV(0.01);

        for (int i = 0; i < s.length; i++) {
            bounce(i);
        }

        for (int j = 0; j < s.length; j++) {
            scene().add(s[j]);

            ballec.addPoint(s[j].get2D());

            CourbeParametriquePolynomialeBezier gdx_BSplineCurve = new CourbeParametriquePolynomialeBezier();

            int i = 0;
            for (Point3D p : s) {
                gdx_BSplineCurve.getCoefficients().setElem(p, i);
                i++;
            }

            TubulaireN2 tubulaireN2;
            tubulaireN2 = new TubulaireN2(gdx_BSplineCurve, TUBE_RAYON);
            tubulaireN2.texture(new TextureImg(new Image(Image.staticLoadFile(new File("samples/img/coeur.gif")))));

            //scene().add(tubulaireN2);
        }
    }
}
