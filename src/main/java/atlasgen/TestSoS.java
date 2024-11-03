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

package atlasgen;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.CameraInPath;
import one.empty3.library.core.nurbs.PcOnPs;
import one.empty3.library.core.testing.TestObjetSub;

import javax.imageio.ImageIO;
import java.awt.*;

import one.empty3.libs.Image;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * G:\Apps\IdeaProjects\empty3\src\test\java\be\manudahmen\empty3\one.empty3.library\tests\TestSoS.java
 * Created by manue on 02-02-19.0
 */
public class TestSoS extends TestObjetSub {
    private CameraInPath camera;
    protected String[] list = new String[]{"2k_neptune.jpg", "2k_uranus.jpg", "8k_jupiter.jpg", "8k_mars.jpg",
            "8k_mercury.jpg", "8k_saturn.jpg", "8k_saturn_ring_alpha.png", "8k_venus_atmosphere.jpg", "8k_venus_surface.jpg"};
    private int planetNo;
    protected boolean quadrillage = true;

    protected static final double RADIUS = 6371000;
    private static final double HEIGHT_MAX_ALT = 8.870;
    protected static final double HEIGHT_MAX = HEIGHT_MAX_ALT * 10000;

    private static final int NSEG = 8;
    private static final int NSEG2 = 24;
    private Point3D pointsB;
    private Point3D pointsA;
    private RepresentableConteneur representableConteneur
            = new RepresentableConteneur();
    protected HeightMapSurfaceSphere heightMapSurfaceSphere;
    private Point3D sphereOrig = Point3D.O0;
    TextureCol textureCol = new TextureCol(Color.RED);
    private Point3D sphereDest = Point3D.Y;
    ITexture colorTextureSurface = new TextureCol(Color.GREEN);
    private double segemntSize = 1;
    private Image bufferedImageHeightMap;
    private Image bufferedImageTexture;
    TextureOpSphere textureOpSphere;

    public void ginit() {
        list = new File("res/img/planets").list();
        planetNo = 0;
        assert list != null;
        setMaxFrames(360 * list.length);
        try {
            bufferedImageHeightMap = new ECImage(ImageIO.read(new File("res/img/gebco_08_rev_elev_21600x10800.png")));
            bufferedImageTexture = new ECImage(ImageIO.read(new File("res/img/planets/" + list[planetNo])));
            TextureImg textureImg = new TextureImg(new ECImage(bufferedImageTexture));
            textureOpSphere = new TextureOpSphere(textureImg);

            if (frame() % 361 == 360)
                planetNo++;
        } catch (Exception ex) {
        }
    }

    public void finit() {

        scene().getObjets().data1d.clear();

        heightMapSurfaceSphere = new HeightMapSurfaceSphere(new Axe(sphereOrig.moins(Point3D.X),
                sphereOrig.plus(Point3D.X)), RADIUS, bufferedImageHeightMap);
        heightMapSurfaceSphere.texture(colorTextureSurface);

        heightMapSurfaceSphere.setIncrU(0.1);
        heightMapSurfaceSphere.setIncrV(0.1);
        heightMapSurfaceSphere.texture(textureOpSphere);


        representableConteneur = new RepresentableConteneur();

        scene().add(representableConteneur);
        if (heightMapSurfaceSphere != null)
            scene().add(heightMapSurfaceSphere);

        if (isQuadrillage()) {
            for (int s = 0; s < NSEG;
                 s++) {

                pointsA = new Point3D(0d, 1.0 * s / NSEG, 0d);
                pointsB = new Point3D(1d, 1.0 * s / NSEG, 0d);
                LineSegment segmentDroite = new LineSegment(pointsA, pointsB);
                segmentDroite.texture(textureCol);
                pointsA.texture(textureCol);
                pointsB.texture(textureCol);

                PcOnPs pcOnPs = new PcOnPs(heightMapSurfaceSphere, segmentDroite);
                pcOnPs.getParameters().setIncrU(0.0001);
                representableConteneur.add(pcOnPs);

            }
            for (int s = 0; s < NSEG2;
                 s++) {

                pointsA = new Point3D(1.0 * s / NSEG2, 0d, 0d);
                pointsB = new Point3D(1.0 * s / NSEG2, 1d, 0d);
                LineSegment segmentDroite = new LineSegment(pointsA, pointsB);
                segmentDroite.texture(textureCol);
                pointsA.texture(textureCol);
                pointsB.texture(textureCol);

                PcOnPs pcOnPs = new PcOnPs(heightMapSurfaceSphere, segmentDroite);
                pcOnPs.getParameters().setIncrU(0.0001);
                representableConteneur.add(pcOnPs);

            }
        }
        camera = new CameraInPath(new Circle(
                new Axe(Point3D.O0.plus(Point3D.X), Point3D.O0.moins(Point3D.X)), RADIUS * 4));

        camera.setT(0.0);

        scene().cameraActive(camera);

        double t = 1.0 * frame() / (double) (getMaxFrames() / list.length);
        camera.setT(t);

        Point3D z = Point3D.O0.moins(camera.getCurve().calculerPoint3D(t)).norme1();
        Point3D x = camera.getCurve().tangente(t).norme1().mult(-1d);
        Point3D y = x.prodVect(z).norme1();
        camera.setMatrix(x, y, z);
        camera.calculerMatrice(y);

        Logger.getAnonymousLogger().log(Level.INFO, "Nombre d'objet: " + scene().getObjets().getData1d().size());
    }

    private boolean isQuadrillage() {
        return quadrillage;
    }

    protected void setQuadrillage(boolean q) {
        quadrillage = q;
    }

    public HeightMapSurfaceSphere getSphere() {
        return heightMapSurfaceSphere;
    }

    public void setSphere(HeightMapSurfaceSphere sphere) {

        heightMapSurfaceSphere = sphere;
    }

    public static void main(String[] args) {
        TestSoS testSoS = new TestSoS();
        new Thread(testSoS).start();
    }
}
