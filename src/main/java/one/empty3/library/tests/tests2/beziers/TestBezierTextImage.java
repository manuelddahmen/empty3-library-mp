
package one.empty3.library.tests.tests2.beziers;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.SurfaceParametricPolygonalBezier;

import one.empty3.libs.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestBezierTextImage extends TestObjetSub {
    private final Point3D[][] coeff = new Point3D[][]{
            {P.n(2, -2, 0), P.n(2, -1, 0), P.n(2, 0, 0), P.n(2, 1, 0), P.n(2, 2, 0)},
            {P.n(1, -2, 0), P.n(1, -1, 0), P.n(1, 0, 0), P.n(1, 1, 0), P.n(1, 2, 0)},
            {P.n(0, -2, 0), P.n(0, -1, 0), P.n(0, 0, 0), P.n(0, 1, 0), P.n(0, 2, 0)},
            {P.n(-1, -2, 0), P.n(-1, -1, 0), P.n(-1, 0, 0), P.n(-1, 1, 0), P.n(-1, 2, 0)},
            {P.n(-2, -2, 0), P.n(-2, -1, 0), P.n(-2, 0, 0), P.n(-2, 1, 0), P.n(-2, 2, 0)}
    };
    ImageTexture imgTexture;
    private SurfaceParametricPolygonalBezier s = new SurfaceParametricPolygonalBezier(coeff);

    public TestBezierTextImage() {
    }

    public static void main(String[] args) {

        TestBezierTextImage tss = new TestBezierTextImage();
        tss.setMaxFrames(2000);
        tss.loop(true);
        tss.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        new Thread(tss).start();
    }

    @Override
    public void testScene(File f) throws Exception {
    }

    @Override
    public void ginit() {
        imgTexture = new ImageTexture((one.empty3.libs.Image) one.empty3.libs.Image.getFromFile(new File("C:\\Emptycanvas\\textures\\text1.jpg")));
        s.texture(imgTexture);
        scene().add(s);
        scene().cameraActive().setEye(Point3D.Z.mult(-1d));

    }

    @Override
    public void finit() {
        System.gc();
    }

    @Override
    public void testScene() throws Exception {

    }

}
