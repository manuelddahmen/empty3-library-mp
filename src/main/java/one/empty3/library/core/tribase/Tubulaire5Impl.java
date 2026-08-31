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

package one.empty3.library.core.tribase;

import one.empty3.library.*;
import one.empty3.testagentcode.Vis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

public class Tubulaire5Impl extends Tubulaire5 {
    private final int WIDTH = 800 / 2;
    private final int HEIGHT = 600 / 2;
    private static final Logger log = LoggerFactory.getLogger(Tubulaire5Impl.class);
    ArrayList<Representable> t4s;

    /**
     *
     */
    public Tubulaire5Impl() {
        t4s = new ArrayList<>();
        Tubulaire5 tubulaire5;
        tubulaire5 = new Tubulaire5(new Bezier(new Point3D[]{Point3D.O0, Point3D.O0.add(new Point3D(1.0, 0.1, 0.0)), Point3D.O0.add(new Point3D(2.0, 0.2, 0.0))}),
                .5);
        tubulaire5.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1.0f)));
        t4s.add(tubulaire5);
        tubulaire5 = new Tubulaire5();
        tubulaire5.getSoulCurve().setElem(new Bezier(new Point3D[]{Point3D.O0, Point3D.O0.add(new Point3D(1.0, 0.1, 0.0)), Point3D.O0.add(new Point3D(2.0, 0.2, 0.0))}));
        tubulaire5.getDiameterFunctionZ().setElem(new BezierMap(new Bezier2D(new Point3D[][]{{new Point3D(0.0, 0.0, .5), new Point3D(0.0, 0.1, .5)},
                {new Point3D(1.0, 0.0, 1.0), new Point3D(1.0, 1.0, 1.0)}})));
        tubulaire5.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1.0f)));
        t4s.add(tubulaire5);
        tubulaire5 = new Vis(1, 2, 4, 1.0);
        tubulaire5.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1.0f)));
        t4s.add(tubulaire5);
        t4s.add(new Sphere(Point3D.O0, 1.0));

    }

    public void testAll() {
        for (Representable t4 : t4s) {
            test(t4);
        }
    }

    private void test(Representable t4) {
        ZBufferImpl zBuffer = new ZBufferImpl(WIDTH, HEIGHT);
        Scene scene = new Scene();
        scene.add(t4);
        Camera camera = new Camera(new Point3D(0., 0., 3.), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);
        zBuffer.scene(scene);
        zBuffer.camera(camera);
        zBuffer.draw(scene);
        log.info("drawn ok");
        JFrame mainFrame = new JFrame("Representable (T4) Test");
        one.empty3.libs.Image image1 = zBuffer.image();
        log.info("get image ok");
        JPanel jPanel = new JPanel();
        mainFrame.setContentPane(jPanel);
        mainFrame.setSize(WIDTH, HEIGHT);
        jPanel.setSize(WIDTH, HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mainFrame.setVisible(true);
        //mainFrame.pack();
        Thread thread = new Thread(() -> {
            while (mainFrame.isVisible()) {
                Graphics graphics = jPanel.getGraphics();
                graphics.drawImage(image1.getBi(), 0, 0, WIDTH, HEIGHT, null);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }


    public static void main(String[] args) {
        new Tubulaire5Impl().testAll();

    }
}