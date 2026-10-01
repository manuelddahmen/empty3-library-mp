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

package one.empty3.library.tests.tests2.balleclou;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * @author Manuel Dahmen
 */
public class TestBalleClous1111 extends TestObjetSub {

    public int N = 1;
    private ITexture tc = new ColorTexture(one.empty3.libs.Color.newCol(1.0f,0.0f,0f));
    private BalleClous2 ballec;
    private Point3D[][] s;
    private Point3D[][] v;
    private double V = 0.03;
    private double D = 1;
    private TextureMov textureMov;
    private BalleClous2[] balles = new BalleClous2[4];
    private int nBalles = 5;

    public static void main(String[] args) {
        TestBalleClous1111 th = new TestBalleClous1111();
        th.setResolution(800, 600);
        th.loop(true);
        th.setMaxFrames(2000);
        new Thread(th).start();
    }

    @Override
    public void ginit() {
        z().setFORCE_POSITIVE_NORMALS(true);
        z().setDisplayType(ZBufferImpl.SURFACE_DISPLAY_TEXT_TRI);
        LumierePonctuelle lumierePonctuelle = new LumierePonctuelle(Point3D.X, one.empty3.libs.Color.newCol(0.0f,1.0f,0f));
        lumierePonctuelle.setR0(1);

        //scene().lumieres().add(lumierePonctuelle);

        lumierePonctuelle = new LumierePonctuelle(Point3D.Y, one.empty3.libs.Color.newCol(1.0f,1.0f,1f));
        lumierePonctuelle.setR0(1);

        //scene().lumieres().add(lumierePonctuelle);

        //scene().lumieres().add(new LumierePonctuelle(Point3D.O0, one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random())));


        s = new Point3D[nBalles][N];
        v = new Point3D[nBalles][N];

        for (int b = 0; b < nBalles; b++) {
            for (int i = 0; i < N; i++) {
                s[b][i] = new Point3D(Point3D.O0);

                s[b][i].texture(tc);

                v[b][i] = new Point3D(Math.random() * (V / 2 - V), Math.random() * (V / 2 - V), Math.random() * (V / 2 - V));

            }
            tc = new ColorTexture(one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random()));


        }
        for (int i = 0; i < balles.length; i++
        ) {


            ballec = new BalleClous2(Point3D.O0, 2.0);

            ballec.setIncrU(0.01);
            ballec.setIncrV(0.01);

            balles[i] = ballec;
            ballec.texture(new ColorTexture(one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random())));
            //textureMov = new TextureMov("C:\\Emptycanvas\\Resources\\BigFloEtOlie.mp4");
            //textureMov.setTransparent(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f));
            //ballec.texture(textureMov);
            scene().add(ballec);


        }
        Camera camera;
        camera = new Camera(new Point3D(0d, 0d, 5d),
                new Point3D(0d, 0d, 0d));

        scene().cameraActive(camera);
    }

    public void bounce(int numBalle, int i) {
        s[numBalle][i].changeTo(s[numBalle][i].plus(v[numBalle][i]));


        if (s[numBalle][i].getX() > D && v[numBalle][i].getX() > 0) {
            v[numBalle][i].setX(-v[numBalle][i].getX());
        }
        if (s[numBalle][i].getX() < -D && v[numBalle][i].getX() < 0) {
            v[numBalle][i].setX(-v[numBalle][i].getX());
        }
        if (s[numBalle][i].getY() > D && v[numBalle][i].getY() > 0) {
            v[numBalle][i].setY(-v[numBalle][i].getY());
        }
        if (s[numBalle][i].getY() < -D && v[numBalle][i].getY() < 0) {
            v[numBalle][i].setY(-v[numBalle][i].getY());
        }
        if (s[numBalle][i].getZ() > D && v[numBalle][i].getZ() > 0) {
            v[numBalle][i].setZ(-v[numBalle][i].getZ());
        }
        if (s[numBalle][i].getZ() < -D && v[numBalle][i].getZ() < 0) {
            v[numBalle][i].setZ(-v[numBalle][i].getZ());
        }
    }

    @Override
    public void finit() throws Exception {
        for (int b = 0; b < balles.length; b++) {
            ballec = balles[b];

            for (int i = 0; i < s[b].length; i++) {
                bounce(b, i);
            }


            ballec.points().clear();
            double totalA = 0;
            double totalB = 0;

            for (int j = 0; j < s[b].length; j++) {
                if (s[b][j].getX() < 0) {
                    s[b][j].setX(s[b][j].getX() + D);
                }
                if (s[b][j].getY() < 0) {
                    s[b][j].setY(s[b][j].getY() + D);
                }
                if (s[b][j].getX() > D) {
                    s[b][j].setX(s[b][j].getX() - D);
                }
                if (s[b][j].getY() > D) {
                    s[b][j].setY(s[b][j].getY() - D);
                }

                totalA += s[b][j].getX();
                totalB += s[b][j].getY();

                ballec.addPoint(new Point2D(s[b][j].getX(), s[b][j].getY()));

            }

        }
    }

}
