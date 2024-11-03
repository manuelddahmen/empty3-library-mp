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
package one.empty3.library;

import javaAnd.awt.image.Image;
import one.empty3.feature.PixM;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.nurbs.SurfaceElem;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * @author Manuel
 */
public class Polygon extends Representable implements SurfaceElem, ClosedCurve {

    /*__
     *
     */
    private StructureMatrix<Point3D> points = new StructureMatrix<>(1, Point3D.class);

    public Polygon() {
        super();
        declareProperties();
    }

    public Polygon(Color c) {
        this();
        texture(new TextureCol(c));
    }

    public Polygon(ITexture c) {
        this();
        texture(c);
    }

    public Polygon(Point3D[] list, Color c) {
        this(list, new TextureCol(c));
    }

    public Polygon(Point3D[] list, ITexture c) {
        this();
        this.texture = c;
        points.setAll(list);
    }

    public void add(Point3D point3D) {
        int newLength;
        if (points == null) {
            points = new StructureMatrix<>(1, Point3D.class);
        } else {
            newLength = points.getData1d().size() + 1;
            java.util.List<Point3D> tmp = points.getData1d();
            points = new StructureMatrix<>(1, Point3D.class);
            for (int i = 0; i < tmp.size(); i++) {
                points.setElem(tmp.get(i), i);
            }
            points.setElem(point3D, newLength - 1);
        }
        declareProperties();
    }


    public StructureMatrix<Point3D> getPoints() {
        return points;
    }

    public void setPoints(Point3D[] points) {
        this.points.setAll(points);
        declareProperties();
    }


    @Override
    public String toString() {
        String t = "poly (\n\t(";
        for (Point3D p : points.getData1d()) {
            t += "\n\t\t" + (p == null ? "null" : p.toString());
        }
        t += "\n\t)\n\t" + (texture == null ? "" : texture.toString()) + "\n)\n\n";
        return t;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Polygon polygone = (Polygon) o;

        return getPoints() != null ? getPoints().equals(polygone.getPoints()) : polygone.getPoints() == null;

    }

    @Override
    public int hashCode() {
        return getPoints() != null ? getPoints().hashCode() : 0;
    }

    public Point3D getIsocentre() {
        Point3D p = Point3D.O0;

        for (Point3D p0 : points.getData1d()) {
            p = p.plus(p0);
        }
        return p.mult(1. / points.getData1d().size());
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("points/point 0 à N du Polygone", points);

    }
/*
    @Override
    public Point3D calculerPoint3D(double t) {
        int size = points.getData1d().size();
        int i = (int) t * size;
        if (i >= size)
            i = size - 1;
        int j = (i + 1) >= size? i : i + 1;
        Point3D p1 = points.getData1d().get(i);
        Point3D p2 = points.getData1d().get(j);
        double d = t - 1.0 * i / size;
        return p1.plus(p2.moins(p1).mult(1 - d));
    }
*/


    public StructureMatrix<Point3D> getBoundRect2d() {
        StructureMatrix<Point3D> boundRect2d = new StructureMatrix<>(1, Point3D.class);
        boundRect2d.setElem(new Point3D(10000d, 10000d, 0d), 0);
        boundRect2d.setElem(new Point3D(-10000d, -10000d, 0d), 1);
        for (Point3D point3D : getPoints().getData1d()) {
            //Logger.getAnonymousLogger().log(Level.INFO, "currentPoint: point3D"+point3D);
            //Logger.getAnonymousLogger().log(Level.INFO, point3D);
            if (point3D.get(0) <= boundRect2d.getElem(0).get(0))
                boundRect2d.getElem(0).set(0, (double) point3D.get(0));
            if (point3D.get(1) <= boundRect2d.getElem(0).get(1))
                boundRect2d.getElem(0).set(1, (double) point3D.get(1));
            if (point3D.get(0) >= boundRect2d.getElem(1).get(0))
                boundRect2d.getElem(1).set(0, (double) point3D.get(0));
            if (point3D.get(1) >= boundRect2d.getElem(1).get(1))
                boundRect2d.getElem(1).set(1, (double) point3D.get(1));
        }
        //Logger.getAnonymousLogger().log(Level.INFO, "Polygon ("+getPoints().getData1d().size()+")bounds: "+boundRect2d);

        return boundRect2d;
    }

    private Point3D getPosition(Point3D p, double scale, Point position) {
        return new Point3D(p.get(0) * scale + position.x, p.get(1) * scale + position.y, 0.0);
    }

    private Point3D getPositionOnPicture(Point3D pCanvas, double scale, Point position) {
        return new Point3D((pCanvas.get(0) - position.x) / scale,
                (pCanvas.get(1) - position.y) / scale, 0.0);
    }

    public boolean leftToRightScanPixM(PixM pixM, int x, int y, boolean[] columnLeft, boolean[] columnRight) {
        boolean foundLeft = columnLeft[y];
        boolean foundRight = columnRight[y];
        if (x >= 0 && x < pixM.getColumns()) {
            for (int i = 0; i < x && !foundLeft; i++) {
                if (!pixM.getP(i, y).equals(Point3D.O0)) {
                    foundLeft = true;
                }
            }
            for (int i = x; i < pixM.getColumns() && !foundRight; i++) {
                if (!pixM.getP(i, y).equals(Point3D.O0)) {
                    foundRight = true;
                }
            }
        }
        columnLeft[y] = foundLeft;
        columnRight[y] = foundRight;
        return !(foundLeft && !foundRight) || (foundLeft && foundRight) && !(!foundLeft && !foundRight);
    }

    public boolean leftToRightScanPixM(PixM pixM, int x, int y, boolean isDrawingOnImage) {
        boolean foundLeft = false;
        boolean foundRight = false;
        if (x >= 0 && x < pixM.getColumns()) {
            for (int i = 0; i < x && !foundLeft; i++) {
                if (!pixM.getP(i, y).equals(Point3D.O0)) {
                    foundLeft = true;
                }
            }
            for (int i = x; i < pixM.getColumns() && !foundRight; i++) {
                if (!pixM.getP(i, y).equals(Point3D.O0)) {
                    foundRight = true;
                }
            }
        }
        return !(foundLeft && !foundRight) || (foundLeft && foundRight) && !(!foundLeft && !foundRight);
    }

    public PixM fillPolygon2D(Polygon faceSurface, Canvas canvas, Image bitmap, int transparent, double prof, Point position, double scale) {
        boolean isDrawingOnImage = true;
        int pixels = 0;

        int fillColorPolygon = (int) UUID.randomUUID().timestamp();
        double[] fillColorArrayPolygon = Lumiere.getDoubles(fillColorPolygon);

        int colorTemp = this.texture().getColorAt(0.5, 0.5);

        StructureMatrix<Point3D> boundRect2d = this.getBoundRect2d();

//        faceSurface.setPolygon(this);

        if (!isDrawingOnImage) {
            boundRect2d.setElem(getPosition(boundRect2d.getElem(0), scale, position), 0);
            boundRect2d.setElem(getPosition(boundRect2d.getElem(1), scale, position), 1);
        } else {

        }
        double left = (boundRect2d.getElem(0).get(0) - 1);
        double top = (boundRect2d.getElem(0).get(1) - 1);
        double right = (boundRect2d.getElem(1).get(0) + 1);
        double bottom = (boundRect2d.getElem(1).get(1) + 1);
        double widthBox = right - left;
        double heightBox = bottom - top;

        if (!(widthBox > 0 && heightBox > 0))
            return null;

        PixM pixM = new PixM((int) (widthBox), (int) (heightBox));

        //      faceSurface.setContours(pixM);

        int count = 0;

        int[] currentColor = new int[(int) (heightBox + 1)];
        Arrays.fill(currentColor, transparent);

        int size = this.getPoints().getData1d().size();
        for (int i = 0; i < size; i++) {
            Point3D p1 = null;
            Point3D p2 = null;

            if (!isDrawingOnImage) {
                p1 = getPosition(this.getPoints().getData1d().get((i + size) % size), scale, position);
                p2 = getPosition(this.getPoints().getData1d().get((i + 1 + size) % size), scale, position);
            } else {
                p1 = this.getPoints().getData1d().get((i + size) % size);
                p2 = this.getPoints().getData1d().get((i + 1 + size) % size);
            }

            ParametricCurve curve = new LineSegment(new Point3D(p1.get(0) - left, p1.get(1) - top, 0.0), new Point3D(p2.get(0) - left, p2.get(1) - top, 0.0));
            curve.texture(new ColorTexture(colorTemp));
            curve.setIncrU(0.1);
            pixM.plotCurve(curve, new ColorTexture(colorTemp));
        }


        Logger.getAnonymousLogger().log(Level.INFO, "filLPolygon2D: (" + (right - left) + ", " + (bottom - top) + ")s");

        for (double i = left; i < right; i++) {
            for (double j = top; j < bottom; j++) {
                int xMap = (int) (i - left);
                int yMap = (int) (j - top);

                double[] color = pixM.getValues(xMap, yMap);

                double[] colorP1 = pixM.getValues(xMap + 1, yMap);

                int imageColor = Lumiere.getInt(color);
                int imageColorP1 = Lumiere.getInt(colorP1);

                int polygonColor = this.texture().getColorAt(1.0 * xMap / (right - left), 1.0 * yMap / (bottom - top));

                if (yMap >= currentColor.length) {
                    continue;
                }


                if (imageColor == colorTemp && currentColor[(int) yMap] == colorTemp && imageColorP1 != colorTemp) {
                    currentColor[(int) yMap] = transparent;
                } else if (imageColor == colorTemp && currentColor[(int) yMap] == transparent && imageColorP1 != colorTemp) {
                    currentColor[(int) yMap] = colorTemp;
                } else if (currentColor[(int) yMap] == colorTemp &&
                        leftToRightScanPixM(pixM, xMap, yMap, isDrawingOnImage)) {

                    if (!isDrawingOnImage && i < canvas.getWidth() && i >= 0 && j < canvas.getHeight() && j >= 0) {
                        Point3D positionOnPicture = new Point3D((double) i, (double) j, 0.0);
                        //canvas.drawPoint((int)(double)positionOnPicture.get(0),
                        //        (int)(double)positionOnPicture.get(1), paint);
                        //pixels++;
                    } else if (isDrawingOnImage && i < bitmap.getWidth() && i >= 0 && j < bitmap.getHeight() && j >= 0) {
                        bitmap.setPixel((int) i, (int) j, colorTemp);
                        pixM.setValues(xMap, yMap, fillColorArrayPolygon);
                        pixels++;

                    }
                }


                count++;
            }
        }

        Logger.getAnonymousLogger().log(Level.INFO, "Points count : " + count + " | Points drawn : " + pixels + "fillPolygon");

        return pixM;
    }
}

