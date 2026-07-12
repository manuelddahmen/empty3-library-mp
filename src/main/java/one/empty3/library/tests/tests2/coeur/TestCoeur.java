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

/*__
 * *
 * Global license : * GNU GPL v3
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * Creation time 25-oct.-2014 SURFACE D'ÉLASTICITÉ DE FRESNEL Fresnel's
 * elasticity surface, Fresnelsche Elastizitätfläche
 * http://www.mathcurve.com/surfaces/elasticite/elasticite.shtml *
 */

package one.empty3.library.tests.tests2.coeur;

import one.empty3.library.*;
import one.empty3.library.core.testing.TestObjetSub;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestCoeur extends TestObjetSub {
    private Coeur coeur;

    public static void main(String[] args) {
        TestCoeur tc = new TestCoeur();
        tc.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE | GENERATE_MODEL);
        tc.loop(true);
        tc.setMaxFrames(400);

        new Thread(tc).start();
    }

    @Override
    public void ginit() {
        coeur = new Coeur();
        try {
            coeur.texture(new TextureImg(new ECBufferedImage(ImageIO.read(getClass().getResourceAsStream("be.manudahmen.empty3.tests.tests2.coeur.jpg")))));
        } catch (IOException ex) {
            coeur.texture(new TextureCol(Color.PINK));
            Logger.getLogger(TestCoeur.class.getName()).log(Level.SEVERE, null, ex);
        }

        coeur.setMaxX(200);
        coeur.setMaxY(200);


        scene().cameraActive(new Camera(Point3D.Z.mult(-5d), Point3D.O0));

        scene().add(coeur);

    }

    @Override
    public void testScene() throws Exception {
        coeur.param01(1.0 * frame / getMaxFrames());


    }

    @Override
    public void finit() {
    }

}
