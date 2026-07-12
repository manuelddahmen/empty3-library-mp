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

package one.empty3.library.tests.coursecheval;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.tribase.Plan3D;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestChatHumain extends TestObjetSub {
    Point3D positionUv = new Point3D(0.5, 0.5, 0.0);
    Point3D positionAngles = new Point3D(0.5, 0.5, 0.0);
    private ChatHumain cheval;
    int moveIndex = 0;
    ChatHumain[] trotte = null;
    private ParametricSurface parametricSurface;

    static class TextureInvertU extends ImageTexture {
        public TextureInvertU( one.empty3.libs.Image  image) {
            super( image);
        }

        @Override
        public Point2D getCoord(double x, double y) {
            return super.getCoord(1.0 - x, y);
        }
    }

    public static final int SECONDS = 15;
    public static final int FPS = 25;
    private static final int TURNS = 3;
    private int i = -1;
    private BufferedImage image;
    private Logger logger;
    private final Point3D axeVerticalVideo = Point3D.Y;
    private final Point3D[] axeViseeVideo = new Point3D[]{Point3D.Z, Point3D.X};
    private final Point3D[] axesSphereHorizontaux = new Point3D[]{Point3D.Z, Point3D.X};

    public static void main(String[] args) {
        TestChatHumain testCourseChat = new TestChatHumain();
        testCourseChat.loop(true);
        testCourseChat.setResolution(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
        Thread thread = new Thread(testCourseChat);
        testCourseChat.setFps(50);
        testCourseChat.setMaxFrames(SECONDS * FPS * TURNS);
        thread.start();
    }

    @Override
    public void ginit() {
        logger = Logger.getLogger(this.getClass().getCanonicalName());

        z().ratioVerticalAngle();

        parametricSurface = new Plan3D(Point3D.O0, Point3D.Z.mult(10.0), Point3D.X.mult(10.0));
        //parametricSurface = new Sphere(Point3D.O0, 5);


        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);

        z().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));
        scene().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));


        i = -1;


        frame = 0;


        cheval = new ChatHumain(parametricSurface);


    }

    public void vecDirRotate(Point3D vecOrigX, Point3D vecOrigY, double ratio,
                             Point3D outX, Point3D outY) {
        outX.changeTo(vecOrigX.mult(Math.cos(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.sin(2 * Math.PI * ratio))));
        outY.changeTo(vecOrigX.mult(-Math.sin(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.cos(2 * Math.PI * ratio))));
    }

    @Override
    public void finit() throws Exception {


        //double angleOnSurface = 2.0 * Math.PI * frame() / getMaxFrames();


        double angleCamera = 0.0;
        c = new Camera(axeViseeVideo[0].mult(Math.cos(angleCamera))
                .plus(axeViseeVideo[0].mult(Math.sin(angleCamera))).mult(600), Point3D.O0);
        c.calculerMatrice(axeVerticalVideo);
        c.setAngleX(Math.PI / 3 * z().la() / z().ha());
        c.setAngleY(Math.PI / 3);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);

        scene().clear();

        if (trotte != null && trotte.length > moveIndex && trotte[moveIndex] != null) {
            cheval = trotte[moveIndex];
            moveIndex++;
        }
        if (trotte == null || moveIndex >= trotte.length) {
            switch ((int) (Math.random() * 2)) {
                case 0:
                    moveIndex = 0;
                    trotte = cheval.getMoves().trotte(30, SECONDS, getFps(), true, 0.0, 40);
                    break;
                case 1:
                    moveIndex = 0;
                    trotte = cheval.getMoves().trotte(30, SECONDS, getFps(), true, 0.0, 40);
                    break;
                case 2:
                    moveIndex = 0;
                    trotte = cheval.getMoves().trotte(30, SECONDS, getFps(), true, 0.0, 40);
                    break;
                default:
                    break;
            }
        }

        positionAngles.setX(((2.0 * Math.PI * frame()) / getMaxFrames()) / TURNS + Math.PI / 2);
        //positionUv = positionUv.plus(Point3D.random(0.2));


        cheval.setAngleXyZ(positionAngles.getX(), 0.0);
        //cheval.setOrig(positionUv.plus(Point3D.Y.mult(100)));

        double u = ((double) getMaxFrames() - frame()) / getMaxFrames();

        Plan3D plane = new Plan3D(new Point3D(50.0, 0.0, 50.0), new Point3D(0.0, 0.0, -50.0), new Point3D(-50.0, 0.0, 0.0));
        plane.texture(new ImageTexture(new File("resources/sol_sableux.jpg")));

        Representable copy = cheval;

        cheval.texture(new ColorTexture(0xFFC5D3));

        LumierePonctuelle lumierePointSimple = new LumierePonctuelle(Point3D.X.mult(.0), one.empty3.libs.Color.newCol(0.0f,1.0f,0f));

        scene().lumieres().add(lumierePointSimple);

        scene().clear();
        scene().add(copy);
        scene().add(plane);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera u : " + u);
    }
}

