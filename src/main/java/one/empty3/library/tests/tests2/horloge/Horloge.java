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

/*
 * 2013 Manuel Dahmen
 */
package one.empty3.library.tests.tests2.horloge;

import one.empty3.library.*;
import one.empty3.library.core.tribase.TRISphere;
import one.empty3.library.core.tribase.TubulaireN2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Horloge extends JFrame {
    Configuration c = new Configuration();
    Color h;
    Color m;
    Color s;
    Dimension res;
    Scene sc;
    private boolean montre = true;
    private Sphere s0;
    private Sphere sH;
    private Sphere sS;
    private Sphere sM;
    private LineSegment droite2;
    private LineSegment droite0;
    private LineSegment droite1;

    public Horloge(Color h, Color m, Color s) {
        super("Horloge 3D");
        this.h = h;
        this.m = m;
        this.s = s;

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                Logger.getAnonymousLogger().log(Level.INFO, "Resize ...");

            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == 'f') {
                } else {
                    c.showAndReturnBack();

                    update(c);
                }
            }
        });


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        setSize(1024, 768);


        setVisible(true);

    }

    public static void main(String[] args) {

        Horloge h = new Horloge(null, null, null);
        h.montrer();
    }

    public void initTime() {

        double f = 2 * Math.PI;

        Date d = new Date();

        sc = new Scene();

        s0 = new Sphere(Point3D.O0, 10);
        sH = new Sphere(position(f * d.getHours() / 12)
                .mult(80d), 12);
        sM = new Sphere(position(f * d.getMinutes() / 60)
                .mult(80d), 8);
        sS = new Sphere(position(f * d.getSeconds() / 60)
                .mult(80d), 6);
        Sphere sG0 = new Sphere(position(f * 0.0 / 12)
                .mult(80d), 10);
        Sphere sG3 = new Sphere(position(f * 3.0 / 12)
                .mult(80d), 10);
        Sphere sG6 = new Sphere(position(f * 6.0 / 12)
                .mult(80d), 10);
        Sphere sG9 = new Sphere(position(f * 9.0 / 12)
                .mult(80d), 10);
        sG0.texture(new TextureCol(Color.GREEN));
        sG3.texture(new TextureCol(Color.GREEN));
        sG6.texture(new TextureCol(Color.GREEN));
        sG9.texture(new TextureCol(Color.GREEN));
        s0.texture(new TextureCol(Color.WHITE));
        sH.texture(new TextureCol(Color.MAGENTA));
        sM.texture(new TextureCol(Color.BLUE));
        sS.texture(new TextureCol(Color.RED));
        s0.texture(new TextureCol(Color.RED));
        sH.texture(new TextureCol(Color.GREEN));
        sM.texture(new TextureCol(Color.BLUE));
        sS.texture(new TextureCol(Color.YELLOW));

        for (int i = 0; i < 12; i++) {
            TRISphere sGm = new TRISphere(position(f * i / 12)
                    .mult(80d), 6);
            sGm.texture(new TextureCol(Color.BLUE));
            sc.add(sGm);
        }
        sc.add(s0);
        sc.add(sH);
        sc.add(sM);
        sc.add(sS);
        sc.add(sG0);
        sc.add(sG3);
        sc.add(sG6);
        sc.add(sG9);
        droite0 = new LineSegment(
                position(f * d.getHours() / 12).mult(60d),
                Point3D.O0, new TextureCol(Color.GREEN));
        droite1 = new LineSegment(position(f * d.getHours() / 12).mult(60d),
                Point3D.O0, new TextureCol(Color.BLUE));
        droite2 = new LineSegment(
                position(f * d.getHours() / 12).mult(60d),
                Point3D.O0, new TextureCol(Color.RED));
        sc.add(droite0);
        sc.add(droite1);
        sc.add(droite2);

        TubulaireN2 segmentDroiteTubulaireN20 = new TubulaireN2(droite0, 20);
        sc.add(segmentDroiteTubulaireN20);
        TubulaireN2 segmentDroiteTubulaireN21 = new TubulaireN2(droite1, 20);
        TubulaireN2 segmentDroiteTubulaireN22 = new TubulaireN2(droite2, 20);
        sc.add(segmentDroiteTubulaireN22);
        sc.add(segmentDroiteTubulaireN21);
        sc.cameraActive(new Camera(Point3D.Z.mult(-200d), Point3D.O0));
        sc.cameraActive().calculerMatrice(Point3D.Y);
    }

    public void time() {
        double f = 2 * Math.PI;
        Date d = new Date();

        sH.getCircle().getAxis().getElem().getP1().setElem(position(f * d.getHours() / 12).mult(60d));
        sM.getCircle().getAxis().getElem().getP1().setElem(position(f * d.getMinutes() / 60).mult(80d));
        sS.getCircle().getAxis().getElem().getP1().setElem(position(f * d.getSeconds() / 60).mult(100d));
        droite0.setOrigine(position(f * d.getHours() / 12).mult(60d));
        droite1.setOrigine(position(f * d.getMinutes() / 60).mult(80d));
        droite2.setOrigine(position(f * d.getSeconds() / 60).mult(100d));
    }

    public Point3D position(double angle) {

        Point3D p0
                = new Point3D(
                -Math.sin(angle),
                Math.cos(angle),
                0d
        );

        return p0;
    }

    public void montrer() {
        initTime();


        while (montre) {

            ZBuffer z = ZBufferFactory.instance(
                    this.getWidth(),
                    this.getHeight());
            time();
            z.next();
            z.couleurDeFond(new TextureCol(Color.WHITE));
            z.scene(sc);

            ((ZBufferImpl) z).setDisplayType(ZBufferImpl.SURFACE_DISPLAY_COL_TRI);


            z.draw();
            Logger.getAnonymousLogger().log(Level.INFO, "Rendered");
            Image bi = z.image();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*__
     * @param c
     */
    public void update(Configuration<Horloge> c) {
    }

    private static class Configuration<T extends JFrame> {

        public Configuration() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        private void showAndReturnBack() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
}
