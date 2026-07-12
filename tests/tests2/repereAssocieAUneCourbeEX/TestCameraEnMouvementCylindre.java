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
 * Importer une autre test: ah si ca pouvait être fait par classes!
 */
package one.empty3.library.tests.tests2.repereAssocieAUneCourbeEX;

import one.empty3.apps.testobject.Resolution;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.CameraInPath;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.ExtrusionCurveCurve;
import one.empty3.apps.testobject.TestObjet;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 *
 * @author Manuel Dahmen
 */
public class TestCameraEnMouvementCylindre extends TestObjet {

    TextureMov textureMov;
    private CameraInPath cam;
    private ExtrusionCurveCurve e;

    public static void main(String[] args) {
        TestCameraEnMouvementCylindre t = new TestCameraEnMouvementCylindre();
        t.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        t.setMaxFrames(30 * 25);
        t.setDimension(new Resolution(640, 480));
        t.setPublish(true);
        new Thread(t).start();
    }

    @Override
    public void afterRenderFrame() {

    }

    @Override
    public void finit() throws EOFVideoException {
        cam.setT(frame / 25.0 / 8);
        cam.setMatrice(cam.getMatrice().mult(-1));
    }

    @Override
    public void ginit() {
        CourbeChoisie cc = new CourbeChoisie(200, 80, 70, 20);

        cam = new CameraInPath(cc);

        e = new ExtrusionCurveCurve();

        e.getBase().setElem(new Circle(new Axe(Point3D.Z.mult(-100), Point3D.Z.mult(100)), 200));

        e.getPath().setElem(new CourbeParametriquePolynomialeBezier());
        ((CourbeParametriquePolynomialeBezier) (e.getPath().getElem())).getCoefficients().setElem(Point3D.Z.mult(-100), 0);
        ((CourbeParametriquePolynomialeBezier) (e.getPath().getElem())).getCoefficients().setElem(Point3D.Z.mult(100), 1);

        e.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));

        Cube cube = new Cube();

        cube.getCote().setElem(100.);
        cube.texture(new ColorTexture(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)));

        scene().add(cube);
        scene().add(e);

        scene().cameraActive(cam);

    }

    @Override
    public void testScene() throws Exception {

    }

    public void afterRender() {

    }

    @Override
    public void publishResult() {

    }
}
