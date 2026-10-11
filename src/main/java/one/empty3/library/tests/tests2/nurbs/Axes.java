
package one.empty3.library.tests.tests2.nurbs;

import one.empty3.library.LineSegment;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.TextureCol;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
class Axes extends RepresentableConteneur {
    public Axes(TextureCol a1, TextureCol a2, TextureCol a3) {
        add(new LineSegment(Point3D.O0, Point3D.X, a1));
        add(new LineSegment(Point3D.O0, Point3D.Y, a2));
        add(new LineSegment(Point3D.O0, Point3D.Z, a3));

    }

    public Axes() {
        this(new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)), new TextureCol(one.empty3.libs.Color.newCol(0.0f,1.0f,0f)), new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));
    }
}
