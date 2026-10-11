
package one.empty3.library.tests.tests2.feudartifice;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;

import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestFeu extends TestObjetSub {

    public static void main(String[] args) {
        TestFeu th = new TestFeu();

        th.loop(true);

        th.setMaxFrames(400);

        th.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);

        th.run();
    }

    @Override
    public void ginit() {
        FeuArbre fey = new FeuArbre();
        RepresentableConteneur generate = fey.generate();
        scene().add(generate);
        Logger.getAnonymousLogger().log(Level.INFO, "" + generate.getListRepresentable().size());


    }

    @Override
    public void testScene() throws Exception {

        scene().cameraActive(new Camera(Point3D.Z.mult(-100d + frame / 2.0), Point3D.Z.mult(200d)));


    }

}
