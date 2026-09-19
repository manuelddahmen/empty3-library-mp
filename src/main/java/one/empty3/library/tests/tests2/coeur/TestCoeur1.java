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
import one.empty3.apps.testobject.TestObjet;

import one.empty3.libs.Image;
import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestCoeur1 extends TestObjet {

    private SurfaceElasticite coeur;

    public static void main(String[] args) {
        TestCoeur1 tc = new TestCoeur1();

        tc.loop(false);
        tc.setMaxFrames(400);
        tc.setGenerate(GENERATE_MODEL | GENERATE_IMAGE);
        new Thread(tc).start();
    }

    @Override
    public void ginit() {
        coeur = new SurfaceElasticite(5, 3, 1);
        URL resourceAsStream = getClass().getResource("moi1.jpg");
        if (resourceAsStream != null)
            coeur.texture(new TextureImg(new Image(new File(resourceAsStream.toString()))));

        scene().add(coeur);

        scene().cameraActive(new Camera(Point3D.Z.mult(-10d), Point3D.O0));

    }

    @Override
    public void testScene() throws Exception {

    }

    @Override
    public void finit() {

    }

    @Override
    public void afterRenderFrame() {

        //coeur.drawStructureDrawFast(getZ());

    }

    public void afterRender() {

    }

    @Override
    public void publishResult() {

    }

}
