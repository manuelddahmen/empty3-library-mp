/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
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

/*__
 * *
 * Global license  GNU GPL v2
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 */
package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.libs.Image;
import one.empty3.libs.Image;
import one.empty3.library.core.nurbs.*;
import one.empty3.library.core.tribase.Precomputable;
import one.empty3.library.objloader.E3Model;
import one.empty3.library1.shader.Vec;
import one.empty3.pointset.PCont;

import one.empty3.libs.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * * Classe de rendu graphique
 */
public class ZBufferImpl extends Representable implements ZBuffer {
    public static final int CHECKED_POINT_SIZE_TRI = 3;
    public static final int CHECKED_POINT_SIZE_QUADS = 4;
    public static final int DISPLAY_ALL = 1;
    public static final int SURFACE_DISPLAY_TEXT_QUADS = 2;
    public static final int SURFACE_DISPLAY_TEXT_TRI = 4;
    public static final int SURFACE_DISPLAY_COL_QUADS = 8;
    public static final int SURFACE_DISPLAY_COL_TRI = 16;
    public static final int SURFACE_DISPLAY_LINES = 32;
    public static final int SURFACE_DISPLAY_POINTS = 64;
    public static final int SURFACE_DISPLAY_POINTS_DEEP = 128;
    public static final int SURFACE_DISPLAY_POINTS_LARGE = 256;
    private static final double MIN_INCR = 0.001;
    public static int CURVES_MAX_SIZE = 10000;
    public static int SURFAS_MAX_SIZE = 1000000;
    public static int CURVES_MAX_DEEP = 10;
    public static int SURFAS_MAX_DEEP = 10;
    // DEFINITIONS
    public static double INFINITY_DEEP = -10E10;
    public static Point3D INFINITY = new Point3D(0d, 0d, INFINITY_DEEP);
    public ImageMapElement ime;
    public Box2D box;
    protected boolean colorationActive = false;
    protected double angleX = Math.PI / 3;
    protected double angleY = Math.PI / 3;
    protected Image bi;
    protected int ha;
    protected int la;
    // PARAMETRES
    protected float zoom = 1.05f;
    protected boolean locked = false;
    // VARIABLES
    protected int idImg = 0;
    protected int dimx;
    protected int dimy;
    protected Scene currentScene;
    protected int displayType = SURFACE_DISPLAY_TEXT_TRI;
    protected boolean FORCE_POSITIVE_NORMALS = true;
    protected StructureMatrix<Double> scale = new StructureMatrix<>(0, Double.class);
    ZBufferImpl that;
    private boolean isCheckedOccupied = false;
    private Representable toDrawR;


    static {
        Logger.getAnonymousLogger().log(Level.INFO, "ZBufferImpl");
        Logger.getGlobal().setFilter(record -> false);
    }

    public ZBufferImpl() {
        that = this;
        scene = new Scene();
        texture(new TextureCol(Color.BLACK.getRGB()));
    }

    public ZBufferImpl(int l, int h) {
        this();
        la = l;
        ha = h;
        dimx = la;
        dimy = ha;
        //Logger.getAnonymousLogger().log(Level.INFO, "width,height(" + la + ", " + ha + ")");
        this.ime = new ImageMap(la, ha).getIme();
    }


    public void resize(int l, int h) {
        la = l;
        ha = h;
        dimx = la;
        dimy = ha;
        //Logger.getAnonymousLogger().log(Level.INFO, "width,height(" + la + ", " + ha + ")");
        this.ime = new ImageMap(la, ha).getIme();
    }


    public void copyResourceFiles(File destDirectory) {
    }

    protected long idImg() {
        return idImg;
    }


    public Camera camera() {

        return scene().cameraActive();
    }

    public void camera(Camera c) {

        this.scene().cameraActive(c);
    }

    public synchronized void draw() {
        draw(scene());
    }

    public Point3D rotate(Point3D p0, Representable ref) {
        return p0;
        //return camera().calculerPointDansRepere(super.rotate(p0, ref));
    }

    public synchronized void draw(Collection<Object> collection) {
        collection.forEach(new Consumer() {
            @Override
            public void accept(Object o) {
                if (o instanceof Representable) {
                    if (!(o instanceof Scene) && !(o instanceof Point3D)) {
                        setCurrentRepresentable((Representable) o);
                    }
                    draw((Representable) o);
                } else if (o instanceof Collection)
                    draw((Collection) o);
            }
        });
    }

    public synchronized void draw(Representable r) {
        if (r instanceof Precomputable) {
            ((Precomputable) r).precompute();
        }

        //camera().ratioHorizontalAngle(dimx, dimy);
        if (r == null) {
            Logger.getAnonymousLogger().log(Level.INFO, "r is null return");
            return;
        }
        if (r.texture() != null)
            r.texture().timeNext();

        if (r instanceof Scene) {
            Scene scene = (Scene) r;
            scene.getObjets().getData1d().forEach(this::draw);
            return;
        } else if (r instanceof RepresentableConteneur) {
            final Representable r1 = r;
            ((RepresentableConteneur) r).getListRepresentable().forEach(representable -> {
                        representable.setVectX(r1.getVectX());
                        representable.setVectY(r1.getVectY());
                        representable.setVectZ(r1.getVectZ());
                        draw(representable);
                    }
            );
            return;
        }

        if (r.texture() instanceof TextureMov) {
            ((TextureMov) r.texture()).nextFrame();
        }

        /* OBJECTS */
        if (r instanceof Point3D) {
            Point3D p = (Point3D) r;
            testDeep(p);
        } else if (r instanceof ThickSurface) {
            setCurrentRepresentable(r);
            ThickSurface n = (ThickSurface) r;
            // TODO Dessiner les bords

            for (double u = n.getStartU(); u <= n.getEndU(); u += n.getIncrU()) {
                // Logger.getAnonymousLogger().log(Level.INFO, "(u,v) = ("+u+","+")");
                for (double v = n.getStartU(); v <= n.getEndV(); v += n.getIncrV()) {
                    Point3D p1, p2, p3, p4;

                    p1 = n.calculerPoint3D(u, v);
                    p2 = n.calculerPoint3D(u + n.getIncrU(), v);
                    p3 = n.calculerPoint3D(u + n.getIncrU(), v + n.getIncrV());
                    p4 = n.calculerPoint3D(u, v + n.getIncrV());
                    switch (displayType) {
                        case DISPLAY_ALL:
                        case SURFACE_DISPLAY_COL_QUADS:
                        case SURFACE_DISPLAY_TEXT_QUADS:
                            tracerQuad(p1, p2, p3, p4, n.texture(), u, u + n.getIncrU(), v, v + n.getIncrV(), n);
                            break;
                        case SURFACE_DISPLAY_COL_TRI:
                        case SURFACE_DISPLAY_TEXT_TRI:
                            tracerTriangle(
                                    n.calculerPoint3D(u, v),
                                    n.calculerPoint3D(u + n.getIncrU(), v),
                                    n.calculerPoint3D(u + n.getIncrU(),
                                            v + n.getIncrV()),
                                    n.texture(),
                                    u, v, u + n.getIncrU(), v + n.getEndV());
                            tracerTriangle(
                                    n.calculerPoint3D(u + n.getIncrU(),
                                            v + n.getIncrV()),
                                    n.calculerPoint3D(u, v + n.getIncrV()),
                                    n.calculerPoint3D(u, v),
                                    n.texture(),
                                    u + n.getIncrU(), v + n.getEndV(),
                                    u, v + n.getIncrV()
                            );
                            break;
                        case SURFACE_DISPLAY_LINES:
                            tracerLines(p1, p2, p3, p4, n.texture(), u, v, u + n.getIncrU(), v + n.getIncrV(), n);
                            break;
                        case SURFACE_DISPLAY_POINTS:
                            testDeep(p1);
                            testDeep(p2);
                            testDeep(p3);
                            testDeep(p4);
                            break;
                    }
                }
            }
        } else if (r instanceof TRI) {
            TRI tri = (TRI) r;
            if (displayType == SURFACE_DISPLAY_LINES) {
                for (int i = 0; i < 3; i++)
                    line(rotate(tri.getSommet().getElem(i), r),
                            rotate(tri.getSommet().getElem((i + 1) % 3), r)
                            , tri.texture);
            } else if (displayType == SURFACE_DISPLAY_POINTS) {
                for (int i = 0; i < 3; i++)
                    testDeep(rotate(tri.getSommet().getElem(i), r)
                            , tri.texture);
            } else {
                tracerTriangle(rotate(tri.getSommet().getElem(0), r),
                        rotate(tri.getSommet().getElem(1), r),
                        rotate(tri.getSommet().getElem(2), r)
                        , tri.texture());
                //System.out.print("Triangle");
            }
        } else if (r instanceof E3Model.FaceWithUv f) {
            if (f.getPolygon().getPoints().getData1d().size() == 4) {
                tracerQuadRefined((E3Model.FaceWithUv) r);
            } else if (f.getPolygon().getPoints().getData1d().size() == 3) {
                Polygon polygon = ((E3Model.FaceWithUv) r).getPolygon();
                TRI tri = new TRI(polygon.getPoints().getData1d().get(0), polygon.getPoints().getData1d().get(1), polygon.getPoints().getData1d().get(2));
                tracerTriangle(polygon.getPoints().getElem(0), polygon.getPoints().getElem(1), polygon.getPoints().getElem(2),
                        polygon.texture(), f.getU1(), f.getV1(), f.getU2(), f.getV2());
            }
        } else if (r instanceof ParametricSurface) {
            ParametricSurface n = (ParametricSurface) r;
            setCurrentRepresentable(r);
            if (n.getIncrU() > 0 && n.getIncrV() > 0) {
                for (double u = n.getStartU(); u + n.getIncrU() <= n.getEndU(); u += n.getIncrU()) {
                    // Logger.getAnonymousLogger().log(Level.INFO, "(u,v) = ("+u+","+")");
                    for (double v = n.getStartV(); v + n.getIncrV() <= n.getEndV(); v += n.getIncrV()) {
                        Point3D p1, p2, p3, p4;
                        double u2 = u + n.getIncrU(), v2 = v + n.getIncrV();
                        double texU2 = u + n.getIncrU(), texV2 = v + n.getIncrV();
                        if (u2 >= n.getEndU() - n.getIncrU()) {
                            Point3D point3D = new Point3D(u2, v2, 0.0);
                            point3D = n.getTerminalU().getElem().result(point3D);
                            u2 = point3D.get(0);
                            v2 = point3D.get(1);

                        }
                        if (v2 >= n.getEndV() - n.getIncrV()) {
                            Point3D point3D = new Point3D(u2, v2, 0.0);
                            point3D = n.getTerminalV().getElem().result(point3D);
                            u2 = point3D.get(0);
                            v2 = point3D.get(1);
                        }

                        p1 = n.calculerPoint3D(u, v);
                        if ((n.getQuad_not_computed() & ParametricSurface.QUAD_NOT_COMPUTE_U2) == 0) {
                            p2 = n.calculerPoint3D(u2, v);
                        } else {
                            Point3D next = n.getNextV(u2, v);
                            p2 = next != null ? next : n.calculerPoint3D(u2, v);
                        }
                        if ((n.getQuad_not_computed() & ParametricSurface.QUAD_NOT_COMPUTE_U2) == 0
                                || (n.getQuad_not_computed() & ParametricSurface.QUAD_NOT_COMPUTE_V2) == 0)
                            p3 = n.calculerPoint3D(u2, v2);
                        else {
                            Point3D next = n.getNextUV(u2, v2);
                            p3 = next != null ? next : n.calculerPoint3D(u2, v2);
                        }
                        if ((n.getQuad_not_computed() & ParametricSurface.QUAD_NOT_COMPUTE_V2) == 0) {
                            p4 = n.calculerPoint3D(u, v2);
                        } else {
                            Point3D next = n.getNextV(u, v2);
                            p4 = next != null ? next : n.calculerPoint3D(u, v2);
                        }
                        if (n instanceof HeightMapSurface) {
                            HeightMapSurface h = (HeightMapSurface) n;
                            Point3D n1, n2, n3, n4;
                            n1 = n.calculerNormale3D(u, v);
                            n2 = n.calculerNormale3D(u2, v);
                            n3 = n.calculerNormale3D(u2, v2);
                            n4 = n.calculerNormale3D(u, v2);
                            p1 = p1.plus(n1.norme1().mult(h.height(u, v)));
                            p2 = p2.plus(n2.norme1().mult(h.height(u2, v)));
                            p3 = p3.plus(n3.norme1().mult(h.height(u2, v2)));
                            p4 = p4.plus(n4.norme1().mult(h.height(u, v2)));
                        }
                        if (displayType == SURFACE_DISPLAY_POINTS || displayType == SURFACE_DISPLAY_POINTS_DEEP) {
                            testDeep(p1, n.texture(), u, v, n);
                            if (displayType == SURFACE_DISPLAY_POINTS_DEEP) {
                                double v1p = maxDistance(camera().coordonneesPoint2D(p1, this), camera().coordonneesPoint2D(p2, this),
                                        camera().coordonneesPoint2D(p3, this), camera().coordonneesPoint2D(p4, this));
                                if (v1p > 1 && v1p < la && v1p < ha) {
                                    int i = 0;
                                    int j = 0;
                                    for (i = 0; i < v1p; i += 1)
                                        for (j = 0; j < v1p; j += 1) {
                                            double u2p = u2 / (1 + v1p) * i;
                                            double v2p = v2 / (1 + v1p) * j;
                                            testDeep(n.calculerPoint3D(u2, v2),
                                                    n.texture(), u2, v2, n);
                                        }
                                }
                            } else if (displayType == SURFACE_DISPLAY_POINTS_LARGE) {
                                tracerQuad(p1, p2, p3, p4, n.texture(), u, u2, v, v2, n);
                            }
                        } else if (displayType == SURFACE_DISPLAY_LINES) {
                            tracerLines(p1, p2, p3, p4, n.texture(), u, v, u2, v2, n);
                        } else if (displayType == SURFACE_DISPLAY_COL_TRI) {
                            tracerTriangle(p1, p2, p3, n.texture(), u, v, u2, v2);
                            tracerTriangle(p3, p4, p1, n.texture(), u2, v2, u, v);


                        } else if (displayType == DISPLAY_ALL || displayType == SURFACE_DISPLAY_TEXT_TRI ||
                                displayType == SURFACE_DISPLAY_COL_QUADS) {
                            if (p1 != null && p2 != null && p3 != null && p4 != null) {
                                tracerQuad(p1, p2, p3, p4, n.texture(), u, u2, v, v2, n);
                            }
                        } else {
                            if (p1 != null && p2 != null && p3 != null && p4 != null) {
                                tracerQuad(p1, p2, p3, p4, n.texture(), u, u2, v, v2, n);
                            }
                        }
                    }/*
                    Point3D quad0 = n.calculerPoint3D(u, n.getEndV());
                    Point3D quad1 =n.calculerPoint3D(u+n.getIncrU(), n.getEndV());
                    Point3D quad2 =n.getTerminalV().getElem().result(new Point3D(u+n.getIncrU(), n.getStartV()));
                    Point3D quad3 =n.getTerminalV().getElem().result(new Point3D(u, n.getStartV()));
                    tracerQuad(quad0, quad1, quad2, quad3, n.texture(), u, u+n.getIncrU(),
                            v.g, v2, n);
*/
                }
            }
        } else if (r instanceof TRIGenerable) {
            r = ((TRIGenerable) r).generate();
            draw(r);

        } else if (r instanceof PGenerator) {
            r = ((PGenerator) r).generatePO();
            draw(r);
        } else if (r instanceof TRIConteneur) {
            r = ((TRIConteneur) r).getObj();
            draw(r);
        } else
            // OBJETS
            if (r instanceof TRIObject) {
                TRIObject o = (TRIObject) r;
                Logger.getAnonymousLogger().log(Level.INFO, "Objets triangle n°" + ((TRIObject) r).getTriangles().size());
                for (TRI t : o.getTriangles()) {

                    draw(t);

                }
            } else if (r instanceof Point3DS) {
                Point3D p = ((Point3DS) r).calculerPoint3D(0);
                testDeep(rotate(p, r), r.texture());
            } else if (r instanceof LineSegment) {
                setCurrentRepresentable(r);
                LineSegment s = (LineSegment) r;
                Point3D pO = s.getOrigine();
                Point3D pE = s.getExtremite();
                line(pO, pE, s.texture());
            } else if (r instanceof BezierCubique) {
                BezierCubique b = (BezierCubique) r;
                int nt = largeur() / 10;
                Point3D p0 = b.calculerPoint3D(0.0);
                for (double t = 0; t < 1.0; t += 1.0 / nt) {
                    try {
                        Point3D p1 = b.calculerPoint3D(t);
                        line(rotate(p0, r), rotate(p1, r), b.texture());
                        p0 = p1;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } else if (r instanceof BezierCubique2D) {
                BezierCubique2D b = (BezierCubique2D) r;
                int i1 = BezierCubique2D.DIM1, i2 = BezierCubique2D.DIM2;
                for (int i = 0; i < i1; i++) {
                    for (int j = 0; j < i2; j++) {
                        double tx = (Math.max(i - 1, 0)) * 1d / i1;
                        double ty = (Math.max(j - 1, 0)) * 1d / i2;
                        draw(new one.empty3.library.Polygon(new Point3D[]{
                                b.calculerPoint3D(tx, (j) * 1d / i2),
                                b.calculerPoint3D((i) * 1d / i1, (j) * 1d / i2),
                                b.calculerPoint3D((i) * 1d / i1, ty),
                                b.calculerPoint3D(tx, ty)},
                                b.texture()));
                    }
                }
            } else if (r instanceof PCont) {
                PCont b = (PCont) r;
                b.getPoints().forEach(o -> testDeep(rotate((Point3D) o, b)
                        , ((Point3D) o).texture().getColorAt(0, 0)));
            } else if (r instanceof POConteneur) {
                POConteneur c = (POConteneur) r;
                for (Point3D p : c.iterable()) {
                    {
                        testDeep(rotate(p, r), p.texture());
                    }
                }
            } else if (r instanceof TRIConteneur) {
                for (TRI t : ((TRIConteneur) r).iterable()) {
                    {
                        draw(t);
                    }
                }

            } else if (r instanceof ParametricCurve) {
                ParametricCurve n = (ParametricCurve) r;
                double incr = n.getIncrU().getData0d();
                for (double u = n.start(); u <= n.endU(); u += incr) {
                    if (n.isConnected() && displayType != SURFACE_DISPLAY_POINTS) {
                        line(
                                n.calculerPoint3D(u),
                                n.calculerPoint3D(u + incr),
                                n.texture(), u, u + incr, n);
                    } else {
                        testDeep(n.calculerPoint3D(u), n.texture().getColorAt(0.5, 0.5));
                    }
                }

            } else if (r instanceof Polygon) {
                setCurrentRepresentable(r);
                Polygon p = (Polygon) r;
                List<Point3D> points = p.getPoints().getData1d();
                int length = points.size();
                Point3D centre = Point3D.O0;
                for (int i = 0; i < points.size(); i++)
                    centre = centre.plus(points.get(i));
                centre = centre.mult(1.0 / points.size());
                for (int i = 0; i < length; i++) {
                    if (getDisplayType() <= SURFACE_DISPLAY_COL_TRI) {
                        draw(new TRI(points.get(i), points.get((i + 1) % points.size()), centre, p.texture()));
                    } else {
                        line(points.get((i % length)), points.get((i + 1) % length), p.texture);
                    }
                }
            } else if (r instanceof RPv) {
                drawElementVolume(((RPv) r).getRepresentable(), (RPv) r);
            }

    }

    private void setCurrentRepresentable(Representable r) {
        this.toDrawR = r;
    }

    private boolean testDeep(Point3D rotate, ITexture texture) {
        return testDeep(rotate, texture.getColorAt(0, 0));
    }


    protected void tracerLines(Point3D p1, Point3D p2, Point3D p3, Point3D p4, ITexture texture, double u1, double v1,
                               double u2, double v2, ParametricSurface n) {
        line(p1, p2, n != null ? n.texture() : texture, u1, v1, u2, v1, n);
        line(p2, p3, n != null ? n.texture() : texture, u2, v1, u2, v2, n);
        line(p3, p4, n != null ? n.texture() : texture, u2, v2, u1, v2, n);
        line(p4, p1, n != null ? n.texture() : texture, u1, v2, u1, v1, n);
    }


    public double echelleEcran() {
        return box.echelleEcran();
    }

    public int getColorAt(Point p) {
        if (ime.getElementProf((int) p.getX(), (int) p.getY()) <= INFINITY_DEEP) {
            return ime.getElementCouleur((int) p.getX(), (int) p.getY());
        } else {
            return Color.TRANSLUCENT;
        }
    }

    public int[] getData() {
        return ime.color;
    }

    public ZBuffer getInstance(int x, int y) {
        return new ZBufferImpl(x, y);
    }

    /*__
     * @return hauteur du zbuffer
     */
    public int hauteur() {
        return ha;
    }

    @Override
    public void setDimension(int width, int height) {
        la = width;
        ha = height;
    }

    public Image image() {

        Image bi2 = new one.empty3.libs.Image(la, ha, Image.TYPE_INT_RGB);
        for (int i = 0; i < la; i++) {
            for (int j = 0; j < ha; j++) {
                int elementCouleur = ime.getElementCouleur(i, j);
                bi2.setRGB(i, j, elementCouleur);

            }
        }
        this.bi = bi2;
        return bi2;

    }

    @Override
    public Image imageInvX() {
        Image bi2 = new one.empty3.libs.Image(la, ha, Image.TYPE_INT_RGB);
        for (int i = 0; i < la; i++) {
            for (int j = 0; j < ha; j++) {
                int elementCouleur = ime.getElementCouleur(i, j);
                if (ime != null && (ime.getElementPoint(i, j) == null ||
                        ime.getElementPoint(i, j).equals(INFINITY))) {
                    //elementCouleur = Color.TRANSLUCENT;
                }
                bi2.setRGB(la - i - 1, j, elementCouleur);

            }
        }
        this.bi = bi2;
        return bi2;
    }

    //??
    public Image image2() {
        //return image2();

//        Image bi = new one.empty3.libs.Image(la, ha, Image.TYPE_INT_RGB);
//        bi.setRGB(0, 0, la, ha, getData(), 0, la);
//        return new one.empty3.libs.Image(bi);
        return image();

    }

    public boolean isLocked() {
        return locked;
    }

    public void isobox(boolean isBox) {
    }


    /*__
     * @return largeur du zbuffer
     */
    public int largeur() {
        return la;
    }

    @Override
    /*__
     * @param p1 first point
     * @param p2 second point
     * @param t  colour of de la line
     */
    public void line(Point3D p1, Point3D p2, ITexture t) {
        Point x1 = camera().coordonneesPoint2D(p1, this);
        Point x2 = camera().coordonneesPoint2D(p2, this);
        if ((x1 == null || x2 == null) || (!checkScreen(x1) && !checkScreen(x2))) {
            return;
        }
        Point3D n = p1.moins(p2).norme1();
        double iterate = Math.max(maxDistance(x1, x2) * 4 + 1, 1 / MIN_INCR);
        for (int i = 0; i < iterate; i++) {
            Point3D p = p1.plus(p2.moins(p1).mult(i / iterate));
            testDeep(p, t.getColorAt(0.5, 0.5));
        }

    }

    public void line(Point3D p1, Point3D p2, ITexture t, double u, double u1, ParametricCurve curve) {
        Point x1 = camera().coordonneesPoint2D(p1, this);
        Point x2 = camera().coordonneesPoint2D(p2, this);
        if ((x1 == null || x2 == null) || (!checkScreen(x1) && !checkScreen(x2))) {
            return;
        }
        Point3D n = p1.moins(p2).norme1();
        double iterate = Math.max(Math.abs(x1.getX() - x2.getX()), Math.abs(x1.getY() - x2.getY())) * 4 + 1;
        for (int i = 0; i < iterate; i++) {
            Point3D p = p1.plus(p2.moins(p1).mult(i / iterate));
            double u0 = i / iterate;
            if (curve != null)
                p = curve.calculerPoint3D(u0);
            testDeep(p, t, u, 0.0, curve);
        }

    }

    public void line(Point3D p1, Point3D p2, ITexture texture, double u1, double v1, double u2, double v2,
                     ParametricSurface surface) {
        Point x1 = camera().coordonneesPoint2D(p1, this);
        Point x2 = camera().coordonneesPoint2D(p2, this);
        if ((x1 == null || x2 == null) || (!checkScreen(x1) && !checkScreen(x2))) {
            return;
        }
        Point3D n = p2.moins(p1).norme1();
        double d = p2.moins(p1).norme();
        double iterate = Math.sqrt((x1.getX() - x2.getX()) * (x1.getX() - x2.getX())
                + (x1.getY() - x2.getY()) * (x1.getY() - x2.getY())) + 1;
        for (int i = 0; i < iterate; i++) {
            Point3D p = p1.plus(n.mult(i / iterate * d));
            double u = u1 + i / iterate * (u2 - u1);
            double v = v1 + i / iterate * (v2 - v1);
            if (surface != null && surface.texture() != null) {
                //p = surface.calculerPoint3D(u2, v2);
                testDeep(p, surface.texture(), u, v, surface);
            } else {
                testDeep(p, texture.getColorAt(u, v));
            }
        }
    }


    public boolean lock() {
        if (locked) {
            return false;
        }
        locked = true;
        return true;
    }

    public Lumiere lumiereActive() {
        return currentScene.lumiereActive();
    }

    public double[][] map() {
        double[][] Map = new double[la][ha];

        for (int i = 0; i < la; i++) {
            for (int j = 0; j < ha; j++) {
                if (ime.getElementPoint(i, j) != null) {
                    Map[i][j] = ime.getElementPoint(i, j).getZ();
                } else {
                    Map[i][j] = INFINITY_DEEP;
                }
            }
        }
        return Map;

    }


    public double maxDistance(Point... points) {
        double max = 0.0;
        for (Point value : points)
            for (Point point : points) {
                double max1 = Point.distance(value.x, value.y, point.x, point.y);
                if (max1 > max)
                    max = max1;
            }
        return max;
    }

    @Override
    public boolean testDeep(Point3D pFinal, ITexture texture, double u, double v, ParametricSurface n) {
        return testDeep(pFinal, texture.getColorAt(u, v));
    }

    public boolean testDeep(Point3D pFinal, ITexture texture, double u, double v, ParametricCurve n) {
        return testDeep(pFinal, texture);
    }

    @Override
    public int la() {
        return la;
    }

    @Override
    public int ha() {
        return ha;
    }

    @Override
    public Box2D getCube() {
        return box;
    }


    public void itereMaxDist(List<Double> points, ParametricCurve pc, double pStart, double pEnd, ParametricVolume v) {
        Point3D p2start = v.calculerPoint3D(pc.calculerPoint3D(pStart));
        Point3D p2End = v.calculerPoint3D(pc.calculerPoint3D(pEnd));
        double dist = Point.distance(
                new Point(camera().coordonneesPoint2D(p2start, this)),
                new Point(camera().coordonneesPoint2D(p2End, this)));
        if (dist <= 1.0) {
            points.add(pStart);
            points.add(pEnd);
        } else {
            for (int i = 0; i < 10; i++) {
                itereMaxDist(points, pc, pStart + (pEnd - pStart) * i / 10.0, pStart + (pEnd - pStart) * (i + 1) / 10.0, v);
            }
        }
    }

    public void itereMaxDist(List<Double[]> polygons, ParametricSurface ps,
                             double u0, double u1, double v0, double v1, ParametricVolume v) {
        Point3D p1 = v.calculerPoint3D(ps.calculerPoint3D(u0, v0));
        Point3D p2 = v.calculerPoint3D(ps.calculerPoint3D(u1, v0));
        Point3D p3 = v.calculerPoint3D(ps.calculerPoint3D(u1, v1));
        Point3D p4 = v.calculerPoint3D(ps.calculerPoint3D(u0, v1));
        double dist = maxDistance(camera().coordonneesPoint2D(p1, this), camera().coordonneesPoint2D(p2, this),
                camera().coordonneesPoint2D(p3, this), camera().coordonneesPoint2D(p4, this)
        );
        if (dist <= 1.0) {
            polygons.add(new Double[]{u0, u1, v0, v1});
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    itereMaxDist(polygons, ps,
                            u0 + (u1 - u0) * i / 10.0, u0 + (u1 - u0) * (i + 1) / 10.0, v0 + (v1 - v0) * j / 10.0, v0 + (v1 - v0) * (j + 1) / 10.0, v);
                }
            }
        }
    }

    public void plotPoint(Color color, Point3D p) {
        if (p != null && color != null) {
            testDeep(p, color.getRGB());
        }

    }

    public void plotPoint(Point3D p) {
        if (p != null && p.texture() != null) {
            ime.dessine(p, p.texture().getColorAt(0, 0));
        }
    }

    public void plotPoint(Point3D p, Color c) {
        if (p != null && c != null) {
            ime.dessine(p, c.getRGB());
        }
    }

    public one.empty3.libs.Image rendu() {
        return null;
    }

    public int resX() {
        return largeur();
    }

    public int resY() {
        return hauteur();
    }

    public Scene scene() {
        return currentScene;
    }

    public void scene(Scene s) {
        this.currentScene = s;
        this.texture(s.texture());
        //camera(s.cameraActive());

    }

    public void setAngles(double angleXRad, double angleYRad) {
        this.angleX = angleXRad;
        this.angleY = angleYRad;
    }

    @Deprecated
    public void setColoration(boolean a) {
        this.colorationActive = a;
    }

    public void next() {
        if (texture() instanceof TextureMov) {
            ((TextureMov) texture()).nextFrame();
        }
        idImg++;
    }

    @Override
    public boolean testDeep(Point3D point3D) {
        return ime.testDeep(point3D);
    }

    @Override
    public boolean testDeep(Point3D p, Color c) {
        return ime.testDeep(p, c);
    }

    @Override
    public boolean testDeep(Point3D p, int c) {
        return ime.testDeep(p, c);
    }

    public boolean testPoint(Point3D p, Color c) {
        int cc = c.getRGB();
        cc = scene().lumiereActive().getCouleur(c.getRGB(), p, p.getNormale());
        return testDeep(p, cc);
    }

    protected void tracerAretes(Point3D point3d, Point3D point3d2, Color c) {
        Point p1 = camera().coordonneesPoint2D(point3d, this);
        Point p2 = camera().coordonneesPoint2D(point3d2, this);
        if (p1 == null || p2 == null || (!checkScreen(p1) || !checkScreen(p2))) {
            return;
        }
        double iteres = Math.abs(p1.getX() - p2.getX()) + Math.abs(p1.getY() - p2.getY() + 1);
        for (double a = 0; a < 1.0; a += 1 / iteres) {
            Point pp = new Point(p1);
            Point3D p = point3d.mult(a).plus(point3d2.mult(1 - a));
            pp.setLocation(p1.getX() + (int) (a * (p2.getX() - p1.getX())),
                    p1.getY() + (int) (a * (p2.getY() - p1.getY())));
            testDeep(p, c.getRGB());

        }

    }


    public void tracerLumineux() {
        throw new UnsupportedOperationException("Not supported yet."); // To
        // change
        // body
        // of
        // generated
        // methods,
        // choose
        // Tools
        // |
        // Templates.
    }

    public double mathUtilPow2(Point p1, Point p2) {
        return Math.sqrt(
                ((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())) +
                        ((p1.getY() - p2.getY()) * (p1.getY() - p2.getY()))
        );
    }

    public void tracerTriangle(Point3D pp1, Point3D pp2, Point3D pp3,
                               ITexture t,
                               double u0, double v0, double u1, double v1) {
        if (camera() == null || pp1 == null || pp2 == null || pp3 == null)
            return;
        Point p1 = camera().coordonneesPoint2D(pp1, this);
        Point p2 = camera().coordonneesPoint2D(pp2, this);
        Point p3 = camera().coordonneesPoint2D(pp3, this);

        int checkedFalse = 0;
        if (!checkScreen(p1) || isOccupied(pp1))
            checkedFalse++;
        if (!checkScreen(p2) || isOccupied(pp2))
            checkedFalse++;
        if (!checkScreen(p3) || isOccupied(pp3))
            checkedFalse++;

        if (checkedFalse >= CHECKED_POINT_SIZE_TRI) {
            return;
        }
        Point3D[] uvs = new Point3D[]
                {Point3D.n(u0, v0, 0.0), Point3D.n(u1, v0, 0.0), Point3D.n(u1, v1, 0.0),
                        Point3D.n(u0, v1, 0.0)};
        Point3D n = pp1.moins(pp2).prodVect(pp3.moins(pp2)).norme1();
        int col = t.getColorAt(u0, v0);

        double iteres1 = 1.0 / (1 + mathUtilPow2(p1, p2));
        if (iteres1 > MIN_INCR) {
            for (double a = 0; a < 1.0; a += iteres1) {
                Point3D p3a = pp1.plus(pp2.moins(pp1).mult(a));
                Point3D uv3a = uvs[0].plus(uvs[1].moins(uvs[0]).mult(a));
                Point pp = camera().coordonneesPoint2D(p3a, this);
                if (pp != null) {
                    double iteres2 = 1.0 / (1 + mathUtilPow2(p3, pp));
                    Point3D p3ab;
                    for (double b = 0; b <= 1.0/*Math.sqrt(p3a.moins(pp3).norme()*p3a.moins(pp3).norme()
                        -pp2.moins(pp1).norme()*pp2.moins(pp1).norme())>=b*/; b += iteres2) {
                        p3ab = p3a.plus(pp3.moins(p3a).mult(b));
                        Point3D uv3ab = uv3a.plus(uvs[2].moins(uv3a).mult(b));
                        // Corriger la méthode.
                        p3ab.setNormale(n);
                        // Point p22 = coordonneesPoint2D(p);
                        if (displayType <= SURFACE_DISPLAY_TEXT_TRI) {
                            //testDeep(p3ab, t.getColorAt(u0 + a * (u1 - u0), v0 + b * (v1 - v0)));
                            testDeep(p3ab, t.getColorAt(uv3ab.getX(), uv3ab.getY()));
                        } else if (displayType >= SURFACE_DISPLAY_COL_TRI)
                            testDeep(p3ab, col);
                        // LINES, POINTS;
                    }
                }
            }
        }
    }

    @Override
    public boolean checkScreen(Point p1) {
        return p1 != null && p1.getX() >= 0d && p1.getX() < la
                && p1.getY() >= 0d && p1.getY() < ha;

    }

    public void tracerQuad(E3Model.FaceWithUv face) {
        Polygon polygon = face.getPolygon();
        tracerQuad(polygon.getPoints().getElem(0), polygon.getPoints().getElem(1),
                polygon.getPoints().getElem(2), polygon.getPoints().getElem(3), face.texture(),
                face.getU1(), face.getU2(), face.getV1(), face.getV2(), null);
    }

    public void tracerQuadInverse(E3Model.FaceWithUv face, Polygon polygonOnImage, Image original, double u, double v) {
        Polygon polygon = face.getPolygon();
        tracerQuad(polygon.getPoints().getElem(0), polygon.getPoints().getElem(1),
                polygon.getPoints().getElem(2), polygon.getPoints().getElem(3), face.texture(),
                face.getU1(), face.getU2(), face.getV1(), face.getV2(), null);


        int width = original.getWidth();
        int height = original.getHeight();

        double u01, u02, v01, v02;
        u01 = face.getU1() * width;
        u02 = face.getU2() * width;
        v01 = face.getV1() * height;
        v02 = face.getV2() * height;

        Point p1, p2, p3, p4;
        p1 = camera().coordonneesPoint2D(polygon.getPoints().getElem(0), this);
        p2 = camera().coordonneesPoint2D(polygon.getPoints().getElem(1), this);
        p3 = camera().coordonneesPoint2D(polygon.getPoints().getElem(2), this);
        p4 = camera().coordonneesPoint2D(polygon.getPoints().getElem(3), this);

        double inter = Math.max(1 / (maxDistance(p1, p2, p3, p4) + 1) / 3, MIN_INCR);
        for (double a = 0; a < 1.0; a += inter) {
            Point3D pElevation1 = polygon.getPoints().getElem(0).plus(polygon.getPoints().getElem(0).mult(-1d).plus(polygon.getPoints().getElem(1)).mult(a));
            Point3D pElevation2 = polygon.getPoints().getElem(3).plus(polygon.getPoints().getElem(3).mult(-1d).plus(polygon.getPoints().getElem(3)).mult(a));

            Point3D pE1Image = polygonOnImage.getPoints().getElem(0).plus(polygonOnImage.getPoints().getElem(0).mult(-1d).plus(polygonOnImage.getPoints().getElem(1)).mult(a));
            Point3D pE2Image = polygonOnImage.getPoints().getElem(3).plus(polygonOnImage.getPoints().getElem(3).mult(-1d).plus(polygonOnImage.getPoints().getElem(3)).mult(a));

            double inter2 = Math.max(1. / (maxDistance(camera().coordonneesPoint2D(pElevation1, this),
                    camera().coordonneesPoint2D(pElevation2, this)) + 1.) / 3., MIN_INCR);
            //if (inter2 > MIN_INCR)
            for (double b = 0; b < 1.0; b += inter2) {
                Point3D pFinal = (pElevation1.plus(pElevation1.mult(-1d).plus(pElevation2).mult(b)));
                Point3D pFinalOnImage = (pE1Image.plus(pE1Image.mult(-1d).plus(pE2Image).mult(b)));
                double uPoint = u01 + (u02 - u01) * a;
                double vPoint = v01 + (v02 - v01) * b;
                pFinal.texture(texture);
                if (face != null) {
                    pFinal.setNormale(face.calculerNormale3D(uPoint, vPoint));
                    if (displayType == DISPLAY_ALL) {
                        pFinal = face.calculerPoint3D(uPoint, vPoint);
                        pFinal.texture(texture);
                    } else {
                        pFinal.setNormale(face.calculerNormale3D(uPoint, vPoint));
                        pFinal.texture(texture);

                    }
                }
                int col = texture.getColorAt(uPoint, vPoint);
                if (displayType <= SURFACE_DISPLAY_TEXT_QUADS) {
                    if (face != null) {
                        testDeep(pFinal, original.getRGB((int) (double) (pFinalOnImage.getX()), (int) (double) (pFinalOnImage.getY())));

                    } else if (original != null) {
                        testDeep(pFinal, original.getRGB((int) (double) (pFinalOnImage.getX()), (int) (double) (pFinalOnImage.getY())));
                    } else {
                        testDeep(pFinal, col);
                    }
                } else {
                    testDeep(pFinal, col);
                }
            }
        }

        //}

    }

    public void tracerQuadRefined(E3Model.FaceWithUv face) {
        Polygon polygon = face.getPolygon();
        tracerQuad8uv(polygon.getPoints().getElem(0), polygon.getPoints().getElem(1),
                polygon.getPoints().getElem(2), polygon.getPoints().getElem(3), face.texture(),
                face.getTextUv(), face);
    }

    private void tracerQuad(Point3D pp1, Point3D pp2, Point3D pp3, Point3D pp4, ITexture texture, double[] textUv, ParametricSurface n) {


        Point p1, p2, p3, p4;
        p1 = camera().coordonneesPoint2D(pp1, this);
        p2 = camera().coordonneesPoint2D(pp2, this);
        p3 = camera().coordonneesPoint2D(pp3, this);
        p4 = camera().coordonneesPoint2D(pp4, this);

        int checkedFalse = 0;
        if (!checkScreen(p1) || isOccupied(pp1))
            checkedFalse++;
        if (!checkScreen(p2) || isOccupied(pp2))
            checkedFalse++;
        if (!checkScreen(p3) || isOccupied(pp3))
            checkedFalse++;
        if (!checkScreen(p4) || isOccupied(pp4))
            checkedFalse++;


        if (p1 == null || p2 == null || p3 == null || p4 == null || checkedFalse >= CHECKED_POINT_SIZE_QUADS)
            return;


        TRI triBas = new TRI(pp1, pp2, pp3, texture);
        Point3D normale = triBas.normale();
        double inter = Math.max(1 / (maxDistance(p1, p2, p3, p4) + 1) / 3, MIN_INCR);
        //if (inter > MIN_INCR) {
        for (double a = 0; a < 1.0; a += inter) {
            Point3D pElevation1 = pp1.plus(pp1.mult(-1d).plus(pp2).mult(a));
            Point3D pElevation2 = pp4.plus(pp4.mult(-1d).plus(pp3).mult(a));
            double u00 = textUv[0] + (textUv[2] - textUv[0]) * a;
            double u01 = textUv[4] + (textUv[2] - textUv[4]) * a;
            double u = (u00 + u01) / 2;
            double inter2 = Math.max(1. / (maxDistance(camera().coordonneesPoint2D(pElevation1, this),
                    camera().coordonneesPoint2D(pElevation2, this)) + 1.) / 3., MIN_INCR);
            //if (inter2 > MIN_INCR)
            for (double b = 0; b < 1.0; b += inter2) {
                Point3D pFinal = (pElevation1.plus(pElevation1.mult(-1d).plus(pElevation2).mult(b)));
                double v00 = textUv[1] + (textUv[7] - textUv[1]) * b;
                double v01 = textUv[3] + (textUv[5] - textUv[3]) * b;
                double v = (v00 + v01) / 2;
                pFinal.setNormale(normale);
                pFinal.texture(texture);
                if (n != null) {
                    pFinal.setNormale(n.calculerNormale3D(u, v));
                    if (displayType == DISPLAY_ALL) {
                        pFinal = n.calculerPoint3D(u, v);
                        pFinal.texture(new ColorTexture(texture.getColorAt(u, v)));
                    } else {
                        pFinal.setNormale(normale);
                        pFinal.texture(new ColorTexture(texture.getColorAt(u, v)));
                    }
                }
                if (displayType <= SURFACE_DISPLAY_TEXT_QUADS) {
                    if (n != null) {
                        if (testDeep(pFinal, n.texture(), u, v, n)) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;

                        }
                    } else if (texture != null) {
                        if (testDeep(pFinal, texture, u, v, n)) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;

                        }
                    } else {
                        if (testDeep(pFinal, Color.PINK.getRGB())) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;

                        }
                    }
                } else {
                    TextureCol col = new TextureCol(Color.RED.getRGB());
                    if (testDeep(pFinal, col, u, v, n)) {
                        Point ce = camera().coordonneesPoint2D(pFinal, that);
                        ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                        ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;

                    }
                }
            }
        }

        //}
    }

    private void tracerQuad8uv(Point3D pp1, Point3D pp2, Point3D pp3, Point3D pp4, ITexture texture, double[] textUv, ParametricSurface n) {


        Point p1, p2, p3, p4;
        p1 = camera().coordonneesPoint2D(pp1, this);
        p2 = camera().coordonneesPoint2D(pp2, this);
        p3 = camera().coordonneesPoint2D(pp3, this);
        p4 = camera().coordonneesPoint2D(pp4, this);

        Vec v1 = new Vec(5);
        Vec v2 = new Vec(5);
        Vec v3 = new Vec(5);
        Vec v4 = new Vec(5);

        v1.setValues(pp1.getX(), pp1.getY(), pp1.getZ(), textUv[0], textUv[1]);
        v2.setValues(pp2.getX(), pp2.getY(), pp2.getZ(), textUv[2], textUv[3]);
        v3.setValues(pp3.getX(), pp3.getY(), pp3.getZ(), textUv[4], textUv[5]);
        v4.setValues(pp4.getX(), pp4.getY(), pp4.getZ(), textUv[6], textUv[7]);

        int checkedFalse = 0;
        if (!checkScreen(p1) || isOccupied(pp1))
            checkedFalse++;
        if (!checkScreen(p2) || isOccupied(pp2))
            checkedFalse++;
        if (!checkScreen(p3) || isOccupied(pp3))
            checkedFalse++;
        if (!checkScreen(p4) || isOccupied(pp4))
            checkedFalse++;


        if (p1 == null || p2 == null || p3 == null || p4 == null || checkedFalse >= CHECKED_POINT_SIZE_QUADS)
            return;


        TRI triBas = new TRI(pp1, pp2, pp3, texture);
        Point3D normale = triBas.normale();
        double inter = Math.max(1 / (maxDistance(p1, p2, p3, p4) + 1) / 3, MIN_INCR);
        //if (inter > MIN_INCR) {
        for (double a = 0; a < 1.0; a += inter) {
            Vec pElevation1 = v1.add(v1.multiply(-1d).add(v2).multiply(a));
            Vec pElevation2 = v4.add(v4.multiply(-1d).add(v3).multiply(a));
            double u00 = textUv[0] + (textUv[2] - textUv[0]) * a;
            double u01 = textUv[4] + (textUv[2] - textUv[4]) * a;
            double inter2 = Math.max(1. / (maxDistance(camera().coordonneesPoint2D(new Point3D(pElevation1.get(0), pElevation1.get(1), pElevation1.get(2)), this),
                    camera().coordonneesPoint2D(new Point3D(pElevation2.get(0), pElevation2.get(1), pElevation2.get(2)), this)) + 1.) / 3., MIN_INCR);
            //if (inter2 > MIN_INCR)
            for (double b = 0; b < 1.0; b += inter2) {
                Vec pFinal0 = (pElevation1.add(pElevation1.multiply(-1d).add(pElevation2).multiply(b)));
                double u = pFinal0.get(3);
                double v = pFinal0.get(4);

                Point3D pFinal = new Point3D(pFinal0.get(0), pFinal0.get(1), pFinal0.get(2));
                pFinal.setNormale(normale);
                pFinal.texture(texture);
/*
                if (n != null) {
                    pFinal.setNormale(n.calculerNormale3D(u, v));
                    if (displayType == DISPLAY_ALL) {
                        pFinal = n.calculerPoint3D(u, v);
                        pFinal.texture(new ColorTexture(texture.getColorAt(u, v)));
                    } else {
                        pFinal.setNormale(normale);
                        pFinal.texture(new ColorTexture(texture.getColorAt(u, v)));
                    }
                }
*/
                if (displayType <= SURFACE_DISPLAY_TEXT_QUADS) {
                    /*if (n != null) {
                        if (testDeep(pFinal, n.texture(), u, v, n)) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;
                            ime.rMap[(int) ce.getX()][(int) ce.getY()] = n;

                        }
                    } else*/
                    if (texture != null) {
                        if (testDeep(pFinal, texture, u, v, n)) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;
                            ime.rMap[(int) ce.getX()][(int) ce.getY()] = n;

                        }
                    } else {
                        if (testDeep(pFinal, Color.PINK.getRGB())) {
                            Point ce = camera().coordonneesPoint2D(pFinal, that);
                            ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                            ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;
                            ime.rMap[(int) ce.getX()][(int) ce.getY()] = n;

                        }
                    }
                } else {
                    TextureCol col = new TextureCol(Color.RED.getRGB());
                    if (testDeep(pFinal, col, u, v, n)) {
                        Point ce = camera().coordonneesPoint2D(pFinal, that);
                        ime.uMap[(int) ce.getX()][(int) ce.getY()] = u;
                        ime.vMap[(int) ce.getX()][(int) ce.getY()] = v;
                        ime.rMap[(int) ce.getX()][(int) ce.getY()] = n;

                    }
                }
            }
        }

        //}
    }

    public void tracerQuad(Point3D pp1, Point3D pp2, Point3D pp3, Point3D pp4, ITexture texture, double u0, double u1,
                           double v0, double v1, ParametricSurface n) {


        Point p1, p2, p3, p4;
        p1 = camera().coordonneesPoint2D(pp1, this);
        p2 = camera().coordonneesPoint2D(pp2, this);
        p3 = camera().coordonneesPoint2D(pp3, this);
        p4 = camera().coordonneesPoint2D(pp4, this);

        int checkedFalse = 0;
        if (!checkScreen(p1) || isOccupied(pp1))
            checkedFalse++;
        if (!checkScreen(p2) || isOccupied(pp2))
            checkedFalse++;
        if (!checkScreen(p3) || isOccupied(pp3))
            checkedFalse++;
        if (!checkScreen(p4) || isOccupied(pp4))
            checkedFalse++;


        if (p1 == null || p2 == null || p3 == null || p4 == null || checkedFalse >= CHECKED_POINT_SIZE_QUADS)
            return;


        int col = texture.getColorAt(u0, v0);

        TRI triBas = new TRI(pp1, pp2, pp3, texture);
        Point3D normale = triBas.normale();
        double inter = Math.max(1 / (maxDistance(p1, p2, p3, p4) + 1) / 3, MIN_INCR);
        //if (inter > MIN_INCR) {
        for (double a = 0; a < 1.0; a += inter) {
            Point3D pElevation1 = pp1.plus(pp1.mult(-1d).plus(pp2).mult(a));
            Point3D pElevation2 = pp4.plus(pp4.mult(-1d).plus(pp3).mult(a));


            double inter2 = Math.max(1. / (maxDistance(camera().coordonneesPoint2D(pElevation1, this),
                    camera().coordonneesPoint2D(pElevation2, this)) + 1.) / 3., MIN_INCR);
            //if (inter2 > MIN_INCR)
            for (double b = 0; b < 1.0; b += inter2) {
                Point3D pFinal = (pElevation1.plus(pElevation1.mult(-1d).plus(pElevation2).mult(b)));
                double uPoint = u0 + (u1 - u0) * a;
                double vPoint = v0 + (v1 - v0) * b;
                pFinal.setNormale(normale);
                pFinal.texture(texture);
                if (n != null) {
                    pFinal.setNormale(n.calculerNormale3D(uPoint, vPoint));
                    if (displayType == DISPLAY_ALL) {
                        pFinal = n.calculerPoint3D(uPoint, vPoint);
                        pFinal.texture(texture);
                    } else {
                        pFinal.setNormale(normale);
                        pFinal.texture(texture);

                    }
                }
                if (displayType <= SURFACE_DISPLAY_TEXT_QUADS) {
                    if (n != null) {
                        testDeep(pFinal, n.texture().getColorAt(uPoint, vPoint));

                    } else if (texture != null) {
                        testDeep(pFinal, texture.getColorAt(uPoint, vPoint));
                    } else {
                        testDeep(pFinal, col);
                    }
                } else {
                    testDeep(pFinal, col);
                }
            }
        }

        //}
    }

    private boolean isOccupied(Point3D newValue) {
        if (!isCheckedOccupied()) {
            return false;
        }
        double newCandidateDeep = camera().distanceCamera(newValue);
        if (newValue != null) {
            Point point = camera().coordonneesPoint2D(newValue, this);
            if (checkScreen(point)) {

                Point3D previousPixelValue = ime.getElementPoint(
                        ((int) point.getX()), (int) point.getY());

                if (previousPixelValue == null) {
                    return false;
                }
                return previousPixelValue.getZ() > newCandidateDeep;
            }
        }
        return false;
    }

    private boolean isCheckedOccupied() {
        return isCheckedOccupied;
    }

    public void tracerTriangle(Point3D pp1, Point3D pp2, Point3D pp3, ITexture c) {
        Point p1, p2, p3;
        p1 = camera().coordonneesPoint2D(pp1, this);
        p2 = camera().coordonneesPoint2D(pp2, this);
        p3 = camera().coordonneesPoint2D(pp3, this);
        if (p1 == null || p2 == null || p3 == null) {
            return;
        }

        Point3D n = (pp3.moins(pp1)).prodVect(pp2.moins(pp1)).norme1();

        int checked = 0;
        if (!checkScreen(p1) || isOccupied(pp1))
            checked++;
        if (!checkScreen(p2) || isOccupied(pp2))
            checked++;
        if (!checkScreen(p3) || isOccupied(pp3))
            checked++;

        if (checked >= CHECKED_POINT_SIZE_TRI)
            return;
        double iteres1 = Math.max(1.0 / (maxDistance(p1, p2, p3) + 1) / 3, MIN_INCR);
        for (double a = 0; a < 1.0; a += iteres1) {
            Point3D p11 = pp1.plus(pp1.mult(-1d).plus(pp2).mult(a));
            double iteres2 = Math.max(1.0 / maxDistance(p1, p2, p3) / 3, MIN_INCR);
            for (double b = 0; b < 1.0; b += iteres2) {
                Point3D p21 = p11.plus(p11.mult(-1d).plus(pp3).mult(b));
                p21.setNormale(n);
                p21.texture(c);
                testDeep(p21);
            }
        }
    }

    public boolean unlock() {
        if (!locked) {
            return false;
        }
        locked = false;
        return true;
    }

    public void zoom(float z) {
        if (z > 0) {
            zoom = z;
        }
    }

    @Override
    public ITexture backgroundTexture() {
        return texture();
    }

    public void couleurDeFond(ITexture couleurFond) {
    }

    public void backgroundTexture(ITexture texture) {
        if (texture != null) {
            texture(texture);
            applyTex();
        }
    }

    public void applyTex() {
        if (texture instanceof TextureMov) {
            for (int i = 0; i < la; i++) {
                for (int j = 0; j < ha; j++) {
                    ime.setElementCouleur(i, j, texture().getColorAt(1.0 * i / la, 1.0 * j / ha));
                }
            }
        }
    }

    public void dessine(Point3D p, ITexture texture) {
        dessine(p, texture.getColorAt(0.5, 0.5));
    }

    private void dessine(Point3D p, int colorAt) {
    }

    public Point3D clickAt(double x, double y) {
        return clickAt((int) (x * largeur()), (int) y * hauteur());
    }

    /*__
     *
     * @param x Coordonnees de l'image ds ZBuffer
     * @param y Coordonnees de l'image ds ZBuffer
     * @return Point3D avec texture. Si vide Point3D.INFINI
     */
    public Point3D clickAt(int x, int y) {
        Point3D p = ime.getElementPoint(x, y);
        p.texture(new TextureCol(ime.getElementCouleur(x, y)));
        return p;
    }

    /*__
     *
     * @param x Coordonnees de l'image ds ZBuffer
     * @param y Coordonnees de l'image ds ZBuffer
     * @return Point3D avec texture. Si vide Point3D.INFINI
     */
    public Representable representableAt(int x, int y) {
        Representable p = ime.getElementRepresentable(x, y);
        return p;
    }

    /*__
     *
     * @param p point 3D à inverser dans le repère de la caméra
     * @param camera Caméra null="this.camera()"
     * @return point3d inversion
     *
     * P = M(P-E)
     * MT p' = P-E
     * P = MT p' + E
     */
    public Point3D invert(Point3D p, Camera camera, double returnedDist) {
        p = new Point3D(p);
        p.setX(p.getX() * returnedDist);
        p.setY(p.getY() * returnedDist);
        //p.setX(p.getX()-0.5);
        //p.setY(-p.getY()-0.5);
        p.setZ(returnedDist);
        return camera.getMatrice().tild()
                .mult(p).plus(camera.getEye());
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    public int idz() {
        return idImg;
    }

    public void drawElementVolume(Representable representable, ParametricVolume volume) {
        if (representable instanceof ParametricSurface) {
            ParametricSurface ps = (ParametricSurface) representable;
            List<Double[]> doubles = new ArrayList<>();
            itereMaxDist(doubles, ps, 0., 1., 0., 1., volume);


            // Tracer les points
            doubles.forEach(new Consumer<Double[]>() {
                @Override
                public void accept(Double[] doubles) {
                    tracerQuad(
                            ps.calculerPoint3D(doubles[0], doubles[2]),
                            ps.calculerPoint3D(doubles[1], doubles[2]),
                            ps.calculerPoint3D(doubles[1], doubles[3]),
                            ps.calculerPoint3D(doubles[0], doubles[3]),
                            ps.texture(),
                            doubles[0], doubles[1], doubles[2], doubles[3], ps
                    );
                }
            });

        } else if (representable instanceof ParametricCurve) {
            ParametricCurve pc = (ParametricCurve) representable;
            List<Double> doubles = new ArrayList<>();
            itereMaxDist(doubles, pc, 0., 1., volume);


            double start = doubles.get(0);
            double end = doubles.get(1);
            for (int i = 0; i < doubles.size(); i++) {
                line(pc.calculerPoint3D(start), pc.calculerPoint3D(end), pc.texture(), start, end, pc);
                start = end;
                end += doubles.get(i + 1);
            }


        } else if (representable instanceof RepresentableConteneur) {
            ((RepresentableConteneur) representable).getListRepresentable().forEach(new Consumer<Representable>() {
                @Override
                public void accept(Representable representable) {
                    drawElementVolume(representable, volume);
                }
            });
        } else if (representable instanceof Point3D) {
            draw(volume.calculerPoint3D((Point3D) representable));
        } else if (representable instanceof TRI) {
            TRI t = (TRI) representable;
            tracerTriangle(t.getSommet().getElem(0), t.getSommet().getElem(1), t.getSommet().getElem(2), t.texture());
        } else if (representable instanceof Polygon) {
            Polygon t = (Polygon) representable;
            for (int i = 0; i < t.getPoints().getData1d().size(); i++)
                tracerTriangle(t.getPoints().getElem(0), t.getPoints().getElem((i + 1) % t.getPoints().getData1d().size()),
                        t.getIsocentre(), t.texture());
        }

    }

    public void idzpp() {
        idImg++;
        ime = new ImageMap(la, ha).getIme();
    }

    public boolean isFORCE_POSITIVE_NORMALS() {
        return FORCE_POSITIVE_NORMALS;
    }

    public void setFORCE_POSITIVE_NORMALS(boolean FORCE_POSITIVE_NORMALS) {
        this.FORCE_POSITIVE_NORMALS = FORCE_POSITIVE_NORMALS;
    }

    public void ratioVerticalAngle() {
        this.angleY = 1.0 * dimy / dimx * angleX;
    }
//
//    public void drawElementVolume(RPv rPv) {
//        Representable representable = rPv.getRepresentable();
//        if (representable instanceof ParametricVolume) {
//            for (double u = 0.0; u <= 1.0; u += ParametricVolume.incrU()) {
//                for (double v = 0.0; v <= 1.0; v += ParametricVolume.incrV()) {
//                    for (double w = 0.0; w <= 1.0; w += ParametricVolume.incrW()) {
//                        Point3D point3D = ((ParametricVolume) representable).calculerPoint3D(P.n(u, v, w));
//                        if (point3D == null) return;
//                        testDeep(point3D);
//                    }
//                }
//            }
//        } else if (representable instanceof ParametricSurface) {
//            ParametricSurface s = (ParametricSurface) representable;
//            for (double u = 0.0; u <= 1.0; u += ParametricSurface.getGlobals().getIncrU()) {
//                for (double v = 0.0; v <= 1.0; v += ParametricSurface.getGlobals().getIncrV()) {
//                    Point3D point3D = ((ParametricSurface) representable).calculerPoint3D(u, v);
//                    if (point3D == null) return;
//                    /*tracerQuad(s.calculerPoint3D(u, v), s.calculerPoint3D(u+s.getIncrU(), v),
//                            s.calculerPoint3D(u+s.getIncrU(), v+s.getIncrV()), s.calculerPoint3D(u, v+s.getIncrV()),
//                              s.texture(), u, u+s.getIncrU(), v, v+s.getIncrV(), s);
//                            );
//                    */
//                    testDeep(point3D);
//                }
//            }
//
//        } else if (representable instanceof ParametricCurve) {
//            for (double u = 0.0; u <= 1.0; u += ParametricCurve.getGlobals().getIncrU()) {
//                Point3D point3D = ((ParametricCurve) representable).calculerPoint3D(u);
//                if (point3D == null) return;
//                testDeep(point3D);
//            }
//        }
//    }

    public double getScale() {
        return scale.getElem();
    }

    public void setScale(double scale) {
        this.scale.setElem(scale);
    }

    public class Box2D {

        private final Matrix33 matrix33;
        protected double minx = 1000000;
        protected double miny = 1000000;
        protected double minz = 1000000;
        protected double maxx = -1000000;
        protected double maxy = -1000000;
        protected double maxz = -1000000;
        protected boolean notests = false;

        public Box2D(Matrix33 matrix33, double minx, double miny, double minz, double maxx, double maxy, double maxz) {
            this.matrix33 = matrix33;
            this.minx = minx;
            this.miny = miny;
            this.minz = minz;
            this.maxx = maxx;
            this.maxy = maxy;
            this.maxz = maxz;

            SceneCadre cadre = currentScene.getCadre();
            if (cadre.isCadre()) {
                for (int i = 0; i < 4; i++) {
                    if (cadre.get(i) != null) {
                        test(cadre.get(i));
                    }
                }
                /*
                 * if (!cadre.isExterieur()) { notests = true; }
                 */
            }

            if (!notests) {
                Iterator<Representable> it = currentScene.iterator();
                while (it.hasNext()) {
                    Representable r = it.next();
                    // GENERATORS
                    if (r instanceof TRIGenerable) {
                        r = ((TRIGenerable) r).generate();
                    } else if (r instanceof PGenerator) {
                        r = ((PGenerator) r).generatePO();
                    } else if (r instanceof TRIConteneur) {
                        r = ((TRIConteneur) r).getObj();
                    }
                    // OBJETS
                    if (r instanceof TRIObject) {
                        TRIObject o = (TRIObject) r;
                        Iterator<TRI> ts = o.iterator();
                        while (ts.hasNext()) {
                            TRI t = ts.next();
                            for (Point3D p : t.getSommet().getData1d()) {
                                test(p);
                            }
                        }
                    } else if (r instanceof Point3D) {
                        Point3D p = (Point3D) r;
                        test(p);
                    } else if (r instanceof LineSegment) {
                        LineSegment p = (LineSegment) r;
                        test(p.getOrigine());
                        test(p.getExtremite());
                    } else if (r instanceof TRI) {
                        TRI t = (TRI) r;
                        test(t.getSommet().getElem(0));
                        test(t.getSommet().getElem(1));
                        test(t.getSommet().getElem(2));
                    } /*else if (r instanceof BSpline) {
                        BSpline b = (BSpline) r;
                        Iterator<Point3D> ts = b.iterator();
                        while (ts.hasNext()) {
                            Point3D p = ts.next();
                            test(p);
                        }
                    }(*/ else if (r instanceof BezierCubique) {
                        BezierCubique b = (BezierCubique) r;
                        Iterator<Point3D> ts = b.iterator();
                        while (ts.hasNext()) {
                            Point3D p = ts.next();
                            test(p);
                        }
                    } else if (r instanceof BezierCubique2D) {
                        BezierCubique2D b = (BezierCubique2D) r;
                        for (int i = 0; i < 4; i++) {
                            for (int j = 0; j < 4; j++) {
                                Point3D p = b.getControle(i, j);
                                test(p);
                            }
                        }
                    } else if (r instanceof POConteneur) {
                        for (Point3D p : ((POConteneur) r).iterable()) {
                            test(p);
                        }

                    } else if (r instanceof PObjet) {
                        for (Point3D p : ((PObjet) r).iterable()) {
                            test(p);
                        }

                    } else if (r instanceof RepresentableConteneur) {
                        throw new UnsupportedOperationException("Conteneur non supporté");
                    }
                }
            }
            // Adapter en fonction du ratio largeur/hauteur
            double ratioEcran = 1.0 * la / ha;
            double ratioBox = (maxx - minx) / (maxy - miny);
            double minx2 = minx, miny2 = miny, maxx2 = maxx, maxy2 = maxy;
            if (ratioEcran > ratioBox) {
                // Ajouter des points en coordArr
                minx2 = minx - (1 / ratioBox * ratioEcran / 2) * (maxx - minx);
                maxx2 = maxx + (1 / ratioBox * ratioEcran / 2) * (maxx - minx);
            } else if (ratioEcran < ratioBox) {
                // Ajouter des points en y
                miny2 = miny - (ratioBox / ratioEcran / 2) * (maxy - miny);
                maxy2 = maxy + (ratioBox / ratioEcran / 2) * (maxy - miny);

            }
            minx = minx2;
            miny = miny2;
            maxx = maxx2;
            maxy = maxy2;

            double ajuste = zoom - 1.0;
            minx2 = minx - ajuste * (maxx - minx);
            miny2 = miny - ajuste * (maxy - miny);
            maxx2 = maxx + ajuste * (maxx - minx);
            maxy2 = maxy + ajuste * (maxy - miny);
            minx = minx2;
            miny = miny2;
            maxx = maxx2;
            maxy = maxy2;

        }

        public boolean checkPoint(Point3D p) {
            return p.getX() > minx & p.getX() < maxx & p.getY() > miny & p.getY() < maxy;
        }

        public double echelleEcran() {
            return (box.getMaxx() - box.getMinx()) / la;
        }

        public double getMaxx() {
            return maxx;
        }

        public void setMaxx(double maxx) {
            this.maxx = maxx;
        }

        public double getMaxy() {
            return maxy;
        }

        public void setMaxy(double maxy) {
            this.maxy = maxy;
        }

        public double getMaxz() {
            return maxz;
        }

        public void setMaxz(double maxz) {
            this.maxz = maxz;
        }

        public double getMinx() {
            return minx;
        }

        public void setMinx(double minx) {
            this.minx = minx;
        }

        public double getMiny() {
            return miny;
        }

        public void setMiny(double miny) {
            this.miny = miny;
        }

        public double getMinz() {
            return minz;
        }

        public void setMinz(double minz) {
            this.minz = minz;
        }

        public Rectangle rectangle() {
            return new Rectangle((int) minx, (int) miny, (int) maxx, (int) maxy);
        }

        protected void test(Point3D p) {
            if (p.getX() < minx) {
                minx = p.getX();
            }
            if (p.getY() < miny) {
                miny = p.getY();
            }
            if (p.getZ() < minz) {
                minz = p.getZ();
            }
            if (p.getX() > maxx) {
                maxx = p.getX();
            }
            if (p.getY() > maxy) {
                maxy = p.getY();
            }
            if (p.getZ() > maxz) {
                maxz = p.getZ();
            }

        }
    }

    public class Box2DPerspective {

        public float d = -10.0f;
        public float w = 10.0f;
        public float h = w * la / ha;

        /*__
         * @param scene
         */
        public Box2DPerspective(Scene scene) {
        }
    }

    public class ImageMap {

        public ImageMap(int x, int y) {
            dimx = x;
            dimy = y;
            ime = new ImageMapElement();
            for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                    ime.setElementID(i, j, idImg);
                    ime.setElementPoint(i, j, INFINITY);
                    ime.setElementCouleur(i, j, texture().getColorAt(1. * i / la, 1. * j / ha));
                }
            }
        }


        public int getDimx() {
            return dimx;
        }

        public int getDimy() {
            return dimy;
        }


        public void reinit() {
            idImg++;
        }

        /*
         * protected boolean checkCoordinates(int coordArr, int y) { if (coordArr >= 0 & coordArr < la & y >= 0
         * & y < ha) { return true; } return false; }
         */
        public void setIME(int x, int y) {
            ime.setElementID(x, y, idImg);
        }


        public void testDeep(Point3D p, Point3D n, Color c) {
            // Color cc = c.getCouleur();
            p.setNormale(n);
            ime.testDeep(p, c.getRGB());
        }

        public void testDeep(Point3D p, Point3D n, int c) {
            testDeep(p, n, new Color(c));
        }

        public boolean testDeep(Point3D p, ITexture texture, double u, double v, Representable representable) {
            if (texture.getColorAt(u, v) != texture.getTransparent()
                    && ime.testDeep(p, texture.getColorAt(u, v))) {
                Point point = camera().coordonneesPoint2D(p, that);
                int x = (int) point.x, y = (int) point.y;
                ime.getuMap()[x][y] = u;
                ime.getvMap()[x][y] = v;
                ime.getrMap()[x][y] = representable;
                return true;
            }
            return false;
        }

        public void testDeep(Point3D pFinal, Point3D point3D, int colorAt, Representable n) {
            testDeep(pFinal, point3D, colorAt);
            Point point = camera().coordonneesPoint2D(pFinal, that);
        }

        public void testDeep(Point3D pFinal, int colorAt, Representable n) {
            testDeep(pFinal, pFinal.getNormale(), colorAt, n);
        }

        public ImageMapElement getIme() {
            return new ImageMapElement();
        }
    }

    public class ImageMapElement {

        int couleur_fond_int = -1;
        ImageMapElement instance;
        Representable[][] imeRepresentable;
        double[][] uMap;
        double[][] vMap;
        Representable[][] rMap;
        Point3D[][] coordinate;
        int[] color;
        long[][] imeId;
        double[][] imeProf;


        public ImageMapElement() {
            this.coordinate = new Point3D[la][ha];
            this.color = new int[la * ha];
            this.imeId = new long[la][ha];
            this.imeProf = new double[la][ha];
            this.imeRepresentable = new Representable[la][ha];
            this.uMap = new double[la][ha];
            this.vMap = new double[la][ha];
            this.rMap = new Representable[la][ha];
            for (int i = 0; i < la; i++) {
                for (int j = 0; j < ha; j++) {
                    this.imeProf[i][j] = INFINITY.getZ();
                    this.imeId[i][j] = idImg;
                    this.color[j * la + i] = COULEUR_FOND_INT(i, j);
                    this.imeRepresentable[i][j] = null;
                    this.rMap[i][j] = null;
                }
            }
        }

        public Representable getElementRepresentable(int x, int y) {
            if (checkCoordinates(x, y)) {
                return imeRepresentable[x][y];
            }
            return null;
        }

        public void setElementRepresentable(int x, int y, double z, Representable representable) {
            if ((ime.getElementProf(x, y) < z && representable != null) || ime.getElementRepresentable(x, y) == null) {
                //imeRepresentable[x][y] = representable;
                imeRepresentable[x][y] = getCurrentRepresentable();
                rMap[x][y] = representable;
            }
        }

        public void setElementRepresentable(int x, int y, Representable toDrawR) {
            if (checkCoordinates(x, y)) {
                imeRepresentable[x][y] = toDrawR;
            }
        }

        public boolean checkCoordinates(int x, int y) {
            return x >= 0 && x < la && y >= 0 && y < ha;
        }

        public int COULEUR_FOND_INT(int x, int y) {
            couleur_fond_int = texture().getColorAt(1.0 * x / largeur(), 1.0 * y / hauteur());
            return couleur_fond_int;
        }

        public int getElementCouleur(int x, int y) {
            if (checkCoordinates(x, y)
                    && ime.imeId[x][y] == idImg
                    && ime.imeProf[x][y] > INFINITY.getZ()) {
                return getRGBInt(ime.color, x, y);
            } else {
                return COULEUR_FOND_INT(x, y);
            }
        }

        public long getElementID(int x, int y) {
            return ime.imeId[x][y];
        }

        public Point3D getElementPoint(int x, int y) {
            return ime.coordinate[x][y];
        }

        protected double getElementProf(int x, int y) {
            return ime.imeProf[x][y];
        }

        public ImageMapElement getInstance(int x, int y) {
            if (ime.instance == null) {
                ime.instance = this;
            }
            return instance;
        }

        protected int getRGBInt(int[] sc, int x, int y) {
            return color[x + y * la];

        }

        public void setElementCouleur(int x, int y, int pc) {
            setElementID(x, y, idImg);
            setRGBInts(pc, ime.color, x, y);
        }

        public void setElementID(int x, int y, long id) {
            ime.imeId[x][y] = idImg;
        }

        public void setElementPoint(int x, int y, Point3D px) {
            setElementID(x, y, idImg);
            ime.coordinate[x][y] = px;
        }

        public double[][] getuMap() {
            return uMap;
        }

        public void setuMap(double[][] uMap) {
            this.uMap = uMap;
        }

        public double[][] getvMap() {
            return vMap;
        }

        public void setvMap(double[][] vMap) {
            this.vMap = vMap;
        }

        public Representable[][] getrMap() {
            return rMap;
        }

        public void setrMap(Representable[][] rMap) {
            this.rMap = rMap;
        }

        protected void setDeep(int x, int y, double d) {
            if (checkCoordinates(x, y)) {
                ime.imeProf[x][y] = (float) d;
                if (toDrawR != null)
                    setElementRepresentable(x, y, toDrawR);
            }
        }

        protected void setRGBInts(int rgb, int[] sc, int x, int y) {
            color[x + y * la] = rgb;
        }

        public void setElementProf(int i, int j, double pr) {
            ime.imeProf[i][j] = pr;
        }

        public void dessine(Point3D p, Color colorAt) {
            dessine(p, colorAt.getRGB());
        }

        public void dessine(Point3D x3d, int c1) {
            double[] c = Lumiere.getDoubles(c1);
            Point ce = camera().coordonneesPoint2D(x3d, that);
            if (ce == null) {
                return;
            }
            //double prof = -1000;
            int x = (int) ce.getX();
            int y = (int) ce.getY();
            if (x >= 0 & x < la & y >= 0 & y < ha && c[3] == 255) {
                ime.setElementID(x, y, idImg);
                ime.setElementPoint(x, y, x3d);
                ime.setElementCouleur(x, y, c1);
                //ime.setDeep(x, y, prof);
                if (toDrawR != null) {
                    ime.setElementRepresentable(x, y, toDrawR);
                }
            } else if (checkScreen(ce)) {
                int elementCouleur = ime.getElementCouleur(x, y);
                double[] nc = c;
                double[] ac = Lumiere.getDoubles(elementCouleur);
                double[] b = new double[3];
                for (int i = 0; i < 3; i++) {
                    b[i] = nc[i] * c[3] / 255. + (1 - c[3] / 255.) * ac[i];
                }
                int anInt = Lumiere.getInt(b);
                ime.setElementID(x, y, idImg);
                ime.setElementPoint(x, y, x3d);
                ime.setElementCouleur(x, y, anInt);
                //ime.setDeep(x, y, prof);
                if (toDrawR != null) {
                    ime.setElementRepresentable(x, y, toDrawR);
                }
            }
        }

        public boolean testDeep(Point3D p, Color c) {
            return testDeep(p, c.getRGB());
        }

        public boolean testDeep(Point3D p) {
            if (p != null && p.texture() != null) {
                return testDeep(p, p.texture().getColorAt(0., 0.));
            }
            return false;
        }

        public boolean testDeep(Point3D x3d, int c) {
            if (x3d == null)
                return false;
            //x3d = camera().calculerPointDansRepere(x3d);
            if (x3d == null)
                return false;
            int cc = c;
            Point ce = camera().coordonneesPoint2D(x3d, that);
            if (ce == null)
                return false;
            int x = (int) ce.getX();
            int y = (int) ce.getY();
            double deep = camera().distanceCamera(x3d);
            if (x >= 0 & x < la & y >= 0 & y < ha
                    && (deep >= ime.getElementProf(x, y))) {
                Point3D n = x3d.getNormale();
                // Vérifier : n.eye>0 sinon n = -n Avoir toutes les normales
                // dans la même direction par rapport à la caméra.
                if (n == null || n.norme() == 0)
                    n = x3d.moins(camera().getEye());
                else if (FORCE_POSITIVE_NORMALS && n.norme1().dot(scene().cameraActive().getEye().norme1()) < 0)
                    n = n.mult(-1);
                cc = scene().lumiereTotaleCouleur(c, x3d, n);
                ime.setElementID(x, y, idImg);
                ime.setElementCouleur(x, y, cc);
                ime.setDeep(x, y, deep);
                ime.setElementPoint(x, y, x3d);
                ime.setElementProf(x, y, deep);
                if (toDrawR != null) {
                    ime.setElementRepresentable(x, y, deep, toDrawR);
                }
                return true;
            }
            return false;
        }

    }

    public void setCheckedOccupied(boolean checkedOccupied) {
        isCheckedOccupied = checkedOccupied;
    }

    public Representable getCurrentRepresentable() {
        return toDrawR;
    }

    public PointInfo getInfosAt() {
        return new PointInfo();
    }
}
