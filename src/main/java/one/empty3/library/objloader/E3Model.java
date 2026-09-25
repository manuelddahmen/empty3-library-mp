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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.library.objloader;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.*;
import one.empty3.libs.Color;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * Created by manue on 02-06-19.
 * Updated 7/24
 */
public class E3Model extends RepresentableConteneur {
    private final ArrayList<Double[]> vertexsets;
    private final ArrayList<Double[]> vertexsetsnorms;
    private final ArrayList<Double[]> vertexsetstexs;
    private final ArrayList<int[]> faces;
    private final ArrayList<int[]> facestexs;
    private final ArrayList<int[]> facesnorms;
    private final ArrayList<String[]> mattimings;
    private MtlLoader materials;
    private int numpolys;
    private StructureMatrix<Double[]> surfacesDegrees;
    private StructureMatrix<Double[]> surfacesVertex;
    public Double toppoint;
    public Double bottompoint;
    public Double leftpoint;
    public Double rightpoint;
    public Double farpoint;
    public Double nearpoint;
    private final String mtl_path;
    Color color = new Color(Lumiere.getIntFromInts(0, 0, 255));
    private int csDim;
    private boolean rat;
    private String cstype;
    private int degU = 0;
    private int degV = 0;
    private ParametricSurface surface = null;
    private ParametricCurve curve = null;
    private StructureMatrix<Point3D> s;
    private StructureMatrix<Double> k;
    private double[] knotV;
    private double[] knotU;
    private final RepresentableConteneur objects = new RepresentableConteneur();
    public double uMin;
    public double vMin;
    public double uMax;
    public double vMax;
    private final E3Model thisModel = this;

    public RepresentableConteneur getObjects() {
        return objects;
    }

    public void getBounds(Point3D minBox, Point3D maxBox) {
        for (int i = 0; i < vertexsets.size(); i++) {
            for (int j = 0; j < 3; j++) {
                if (minBox.get(j) > vertexsets.get(i)[j]) {
                    minBox.set(j, vertexsets.get(i)[j]);
                }
                if (maxBox.get(j) < vertexsets.get(i)[j]) {
                    maxBox.set(j, vertexsets.get(i)[j]);
                }
            }
        }
    }

    private void applyNormalization() {
/*
        for (int i = 0; i < getListRepresentable().size(); i++) {
            Representable representable = getListRepresentable().get(i);
            if (representable instanceof FaceWithUv faceWithUv) {
                for (int j = 0; j < faceWithUv.getTextUv().length; j += 2) {
                    faceWithUv.u1 = (faceWithUv.u1-uMin)/(uMax-uMin);
                    faceWithUv.v1 = (faceWithUv.v1-vMin)/(uMax-uMin);
                    faceWithUv.u2 = (faceWithUv.u2-uMin)/(vMax-vMin) ;
                    faceWithUv.v2 = (faceWithUv.v2-vMin)/(vMax-vMin);
                }
            }
        }
  */
    }

    //THIS CLASS LOADS THE MODELS
    public E3Model(BufferedReader ref, boolean centerit, String path) {

        mtl_path = path;
        vertexsets = new ArrayList<Double[]>();
        vertexsetsnorms = new ArrayList<Double[]>();
        vertexsetstexs = new ArrayList<>();
        faces = new ArrayList<int[]>();
        facestexs = new ArrayList<>();
        facesnorms = new ArrayList<int[]>();
        mattimings = new ArrayList<>();
        numpolys = 0;
        toppoint = 0.0;
        bottompoint = 0.0;
        leftpoint = 0.0;
        rightpoint = 0.0;
        farpoint = 0.0;
        nearpoint = 0.0;
        loadobject(ref);
        if (centerit)
            centerit();
        numpolys = faces.size();
        //cleanup();
        opene3drawtolist();
        normalizeTextureUv();
        //applyNormalization();
    }

    private void normalizeTextureUv() {
        uMin = Double.MAX_VALUE;
        vMin = Double.MAX_VALUE;
        uMax = -Double.MAX_VALUE;
        vMax = -Double.MAX_VALUE;

        for (int i = 0; i < getListRepresentable().size(); i++) {
            Representable representable = getListRepresentable().get(i);
            if (representable instanceof FaceWithUv faceWithUv) {
                for (int j = 0; j < faceWithUv.getTextUv().length; ) {
                    if (faceWithUv.u1 < uMin) {
                        uMin = faceWithUv.u1;
                    }
                    if (faceWithUv.v1 < vMin) {
                        vMin = faceWithUv.v1;
                    }
                    j += 1;
                    if (faceWithUv.u2 > uMax) {
                        uMax = faceWithUv.u2;
                    }
                    if (faceWithUv.v2 > vMax) {
                        vMax = faceWithUv.v2;
                    }
                    j += 1;
                }
            }
        }
    }

    private void cleanup() {
        vertexsets.clear();
        vertexsetsnorms.clear();
        vertexsetstexs.clear();
        faces.clear();
        facestexs.clear();
        facesnorms.clear();
    }

    private void loadobject(BufferedReader br) {
        int linecounter = 0;
        int facecounter = 0;
        try {
            boolean firstpass = true;
            String newline;
            while ((newline = br.readLine()) != null) {
                linecounter++;
                if (newline.length() > 0) {
                    newline = newline.trim();
                    if (newline.length() == 0) {
                        continue;
                    }
                    if (newline.startsWith("v ")) {
                        Double[] coords = new Double[4];
                        String[] coordstext = new String[4];
                        newline = newline.substring(2);
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Double.parseDouble(st.nextToken());

                        if (firstpass) {
                            rightpoint = coords[0];
                            leftpoint = coords[0];
                            toppoint = coords[1];
                            bottompoint = coords[1];
                            nearpoint = coords[2];
                            farpoint = coords[2];
                            firstpass = false;
                        }
                        if (coords[0] > rightpoint)
                            rightpoint = coords[0];
                        if (coords[0] < leftpoint)
                            leftpoint = coords[0];
                        if (coords[1] > toppoint)
                            toppoint = coords[1];
                        if (coords[1] < bottompoint)
                            bottompoint = coords[1];
                        if (coords[2] > nearpoint)
                            nearpoint = coords[2];
                        if (coords[2] < farpoint)
                            farpoint = coords[2];
                        vertexsets.add(coords);
                    } else if (newline.startsWith("vt")) {
                        Double[] coords = new Double[4];
                        String[] coordstext = new String[4];
                        newline = newline.substring(3);
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Double.parseDouble(st.nextToken());

                        vertexsetstexs.add(coords);
                    } else if (newline.startsWith("vn")) {
                        Double[] coords = new Double[4];
                        String[] coordstext = new String[4];
                        newline = newline.substring(3);
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Double.parseDouble(st.nextToken());

                        vertexsetsnorms.add(coords);
                    } else if (newline.startsWith("f ")) {
                        facecounter++;
                        newline = newline.substring(2);
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        int count = st.countTokens();
                        int[] v = new int[count];
                        int[] vt = new int[count];
                        int[] vn = new int[count];
                        for (int i = 0; i < count; i++) {
                            char[] chars = st.nextToken().toCharArray();
                            StringBuffer sb = new StringBuffer();
                            char lc = 'x';
                            for (int k = 0; k < chars.length; k++) {
                                if (chars[k] == '/' && lc == '/')
                                    sb.append('0');
                                lc = chars[k];
                                sb.append(lc);
                            }

                            StringTokenizer st2 = new StringTokenizer
                                    (sb.toString(), "/");
                            int num = st2.countTokens();
                            v[i] = Integer.parseInt(st2.nextToken());
                            if (num > 1)
                                vt[i] = Integer.parseInt(st2.nextToken());
                            else
                                vt[i] = 0;
                            if (num > 2)
                                vn[i] = Integer.parseInt(st2.nextToken());
                            else
                                vn[i] = 0;
                        }

                        faces.add(v);
                        facestexs.add(vt);
                        facesnorms.add(vn);
                    } else if (newline.charAt(0) == 'm' && newline.charAt(1) == 't' && newline.charAt(2) == 'l' && newline.charAt(3) == 'l' && newline.charAt(4) == 'i' && newline.charAt(5) == 'b') {
                        String[] coordstext = new String[3];
                        coordstext = newline.split("\\s+");
                        if (mtl_path != null)
                            loadmaterials();
                    } else
                        //USES MATELIALS
                        if (newline.charAt(0) == 'u' && newline.charAt(1) == 's' && newline.charAt(2) == 'e' && newline.charAt(3) == 'm' && newline.charAt(4) == 't' && newline.charAt(5) == 'l') {
                            String[] coords = new String[2];
                            String[] coordstext = new String[3];
                            coordstext = newline.split("\\s+");
                            coords[0] = coordstext[1];
                            coords[1] = facecounter + "";
                            mattimings.add(coords);
                            //Logger.getAnonymousLogger().log(Level.INFO, coords[0] + ", " + coords[1]);
                        } else if (newline.startsWith("bmat")) {
                            String[] split = newline.substring("bmat ".length()).split("\\s+");

                            if (newline.charAt(1) == 'u') {

                            } else if (newline.charAt(0) == 'v') {

                            }
                            if (csDim == 1) degV = 1;
                            for (int i = 4; i < degU; i++) {
                                for (int j = 4; j < degV; j++) {
                                    double x = Double.parseDouble(split[0]);
                                    double y = Double.parseDouble(split[1]);
                                    double z = Double.parseDouble(split[2]);
                                    double w = Double.parseDouble(split[3]);
                                    s.setElem(P.n(x, y, z), i, j);
                                }
                            }


/**
 * Object Files (.obj)
 *     cstype rat bspline
 *     deg 2 2
 *     surf -1.0 2.5 -2.0 2.0 -9 -8 -7 -6 -5 -4 -3 -2 -1
 *     parm u -1.00 -1.00 -1.00 2.50 2.50 2.50
 *     parm v -2.00 -2.00 -2.00 -2.00 -2.00 -2.00
 *     trim 0.0 2.0 1
 *     end
 */
                        } else if (newline.startsWith("cstype")) {
                            surface = null;
                            curve = null;

                            String[] split = newline.substring("cstype ".length()).split("\\s+");
                            int index = 0;
                            if (split.length == 2) {
                                index = 1;
                                rat = true;
                            } else
                                rat = false;

                            cstype = split[index];
                                    /*
                                        Bezier
                                        o       basis matrix
                                        o       B-spline
                                        o       Cardinal
                                        o       Taylor
                                    */
                            switch (cstype) {

                                case "bmatrix":
                                    s = new StructureMatrix<Point3D>(2, Point3D.class);
                                    break;
                                case "bezier":
                                    s = new StructureMatrix<Point3D>(2, Point3D.class);
                                    break;
                                case "bspline":
                                    k = new StructureMatrix<Double>(2, Double.class);
                                    s = new StructureMatrix<Point3D>(2, Point3D.class);
                                    break;
                                case "cardinal":
                                    break;
                                case "taylor":
                                    break;
                            }
                        } else if (newline.startsWith("deg")) {
                            String[] split = newline.substring("deg ".length()).split("\\s+");
                            degU = Integer.parseInt(split[0]);
                            if (split.length == 1) {
                                csDim = 1;

                            } else if (split.length == 2) {
                                csDim = 2;
                                degV = Integer.parseInt(split[1]);
                            }
                            switch (cstype) {

                                case "bmatrix":

                                    break;
                                case "bezier":
                                    break;
                                case "bspline":
                                    break;
                                case "cardinal":
                                    if (csDim == 2)
                                        degV = 3;
                                    degU = 3;
                                    break;
                                case "taylor":
                                    break;
                            }

                        } else if (newline.startsWith("curv")) {
                            csDim = 1;
                        } else if (newline.startsWith("curv2")) {
                            csDim = 1;
                        } else if (newline.startsWith("surf")) {
                            csDim = 2;

                            String[] split = newline.substring(4).split("\\s+");
                            double u0 = Double.parseDouble(split[0]);
                            double u1 = Double.parseDouble(split[1]);
                            double v0 = Double.parseDouble(split[2]);
                            double v1 = Double.parseDouble(split[3]);
                            for (int c = 4; c < split.length; c++) {
                                String[] vertexRef = split[c].split("/");

                            }
                        } else if (newline.startsWith("parm")) {
                            String[] split = newline.substring(5).split("\\s+");
                            if (csDim == 1) degV = 1;
                            for (int i = 4; i < degU; i++) {
                                for (int j = 4; j < degV; j++) {
                                    double x = Double.parseDouble(split[0]);
                                    double y = Double.parseDouble(split[1]);
                                    double z = Double.parseDouble(split[2]);
                                    double w = Double.parseDouble(split[3]);
                                    s.setElem(P.n(x, y, z), i, j);
                                }
                            }


                        } else if (newline.startsWith("trim")) {

                        } else if (newline.startsWith("end")) {

                            switch (csDim) {
                                case 2:
                                    surface = null;
                                    switch (cstype) {
                                        case "bezier":
                                            surface = new SurfaceParametricPolygonalBezier(getArray2(s));
                                            break;
                                        case "bspline":
                                            surface = new SurfaceParametriquePolynomialeBSpline(knotU, knotV, getArray2(s), degU, degV);
                                            break;
                                        case "basis":
                                            surface = new PolygonalSurface(s);
                                            objects.add(surface);
                                            break;
                                        //case//Cardinal, Taylor,
                                    }
                                    if (surface != null)
                                        add(surface);
                                    surface = null;
                                    break;
                                case 1:
                                    switch (cstype) {
                                        case "basis":
                                            curve = new CourbeParametriquePolynomiale(getArray1(s));

                                            break;
                                    }
                                    if (curve != null)
                                        objects.add(curve);
                                    curve = null;
                                    break;
                            }
                        }
                }
            }

            if (objects != null)
                add(objects);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Failed to read file: " + br);
        } catch (NumberFormatException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Malformed OBJ file: " + br + "\r \r" + e.getMessage());
        }


    }

    public void opene3drawtolist() {
        try {
            ////////////////////////////////////////
            /// With Materials if available ////////
            ////////////////////////////////////////

            int nextmat = -1;
            int matcount = 0;
            int totalmats = mattimings.size();
            String[] nextmatnamearray = null;
            String nextmatname = null;

            if (totalmats > 0 && materials != null) {
                nextmatnamearray = mattimings.get(matcount);
                nextmatname = nextmatnamearray[0];
                nextmat = Integer.parseInt(nextmatnamearray[1]);
            }
            Color pointCol = color;


            for (int i = 0; i < faces.size(); i++) {
                Point3D norm = new Point3D();
                if (i == nextmat) {
                    pointCol = new Color(Lumiere.getIntFromFloats(materials.getKd(nextmatname)[0], (materials.getKd(nextmatname))[1], (materials.getKd(nextmatname))[2], (materials.getd(nextmatname))));
                    matcount++;
                    if (matcount < totalmats) {
                        nextmatnamearray = mattimings.get(matcount);
                        nextmatname = nextmatnamearray[0];
                        nextmat = Integer.parseInt(nextmatnamearray[1]);
                    }
                }

                int[] tempfaces = faces.get(i);
                int[] tempfacesnorms = facesnorms.get(i);
                int[] tempfacestexs = facestexs.get(i);

                Polygon polygon = new Polygon();

                double[] textureListUv1234 =
                        new double[tempfaces.length * 2];

                for (int w = 0; w < tempfaces.length; w++) {

                    if (tempfacesnorms[w] != 0) {
                        Double normtempx =
                                vertexsetsnorms.get(tempfacesnorms[w] - 1)[0];
                        Double normtempy =
                                vertexsetsnorms.get(tempfacesnorms[w] - 1)[1];
                        Double normtempz =
                                vertexsetsnorms.get(tempfacesnorms[w] - 1)[2];

                        norm = new Point3D(
                                normtempx,
                                normtempy,
                                normtempz
                        );
                    }

                    double u = 0.0;
                    double v = 0.0;

                    if (tempfacestexs[w] != 0) {
                        Double textempx =
                                vertexsetstexs.get(tempfacestexs[w] - 1)[0];

                        Double textempy =
                                vertexsetstexs.get(tempfacestexs[w] - 1)[1];

                        u = textempx;
                        v = 1.0 - textempy;
                    }

                    Double tempx = vertexsets.get(tempfaces[w] - 1)[0];
                    Double tempy = vertexsets.get(tempfaces[w] - 1)[1];
                    Double tempz = vertexsets.get(tempfaces[w] - 1)[2];

                    Point3D point3D =
                            new Point3D(tempx, tempy, tempz);

                    point3D.texture(new ColorTexture(pointCol));
                    point3D.textureIndex(tempx, tempy, tempz);

                    polygon.add(point3D);

                    textureListUv1234[w * 2] = u;
                    textureListUv1234[w * 2 + 1] = v;
                }

                polygon.texture(new ColorTexture(pointCol));

                if (tempfaces.length == 3 || tempfaces.length == 4) {
                    add(new FaceWithUv(polygon, textureListUv1234));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadmaterials() {
        FileReader frm;
        String refm = mtl_path;

        try {
            frm = new FileReader(refm);
            BufferedReader brm = new BufferedReader(frm);
            materials = new MtlLoader(brm, mtl_path);
            frm.close();
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Could not open file: " + refm);
            materials = null;
        }
    }

    private void centerit() {
        Double xshift = (rightpoint - leftpoint) / 2.0F;
        Double yshift = (toppoint - bottompoint) / 2.0F;
        Double zshift = (nearpoint - farpoint) / 2.0F;
        for (int i = 0; i < vertexsets.size(); i++) {
            Double[] coords = new Double[4];
            coords[0] = ((Double[]) vertexsets.get(i))[0] - leftpoint - xshift;
            coords[1] = ((Double[]) vertexsets.get(i))[1] - bottompoint - yshift;
            coords[2] = ((Double[]) vertexsets.get(i))[2] - farpoint - zshift;
            vertexsets.set(i, coords);
        }

    }

    public Double getXWidth() {
        Double returnval = 0.0;
        returnval = rightpoint - leftpoint;
        return returnval;
    }

    public Double getYHeight() {
        Double returnval = 0.0;
        returnval = toppoint - bottompoint;
        return returnval;
    }

    public Double getZDepth() {
        Double returnval = 0.0;
        returnval = nearpoint - farpoint;
        return returnval;
    }

    public int numpolygons() {
        return numpolys;
    }

    /**
     *
     */
    public Point3D findUvFace(double u, double v) {
        final Point3D[] p = {null};
        for (int a = 0; a < faces.size(); a++) {
            for (Representable representable : getListRepresentable()) {
                if (!(representable instanceof FaceWithUv face)) {
                    continue;
                }

                if (face.textUv == null) {
                    continue;
                }

                if (face.isTriangle()) {

                    double u0 = face.getU(0);
                    double v0 = face.getV(0);

                    double u1 = face.getU(1);
                    double v1 = face.getV(1);

                    double u2 = face.getU(2);
                    double v2 = face.getV(2);

                    double det =
                            (v1 - v2) * (u0 - u2)
                                    + (u2 - u1) * (v0 - v2);

                    if (Math.abs(det) < 1e-12) {
                        continue;
                    }

                    double w0 =
                            ((v1 - v2) * (u - u2)
                                    + (u2 - u1) * (v - v2)) / det;

                    double w1 =
                            ((v2 - v0) * (u - u2)
                                    + (u0 - u2) * (v - v2)) / det;

                    double w2 = 1.0 - w0 - w1;

                    if (w0 >= 0.0 && w1 >= 0.0 && w2 >= 0.0) {
                        return face.getPoint(0).mult(w0)
                                .plus(face.getPoint(1).mult(w1))
                                .plus(face.getPoint(2).mult(w2));
                    }

                } else if (face.isQuad()) {

                    double minU = Double.MAX_VALUE;
                    double maxU = -Double.MAX_VALUE;
                    double minV = Double.MAX_VALUE;
                    double maxV = -Double.MAX_VALUE;

                    for (int i = 0; i < 4; i++) {
                        minU = Math.min(minU, face.getU(i));
                        maxU = Math.max(maxU, face.getU(i));
                        minV = Math.min(minV, face.getV(i));
                        maxV = Math.max(maxV, face.getV(i));
                    }

                    if (u >= minU && u <= maxU
                            && v >= minV && v <= maxV) {

                        double localU =
                                (maxU == minU)
                                        ? 0.0
                                        : (u - minU) / (maxU - minU);

                        double localV =
                                (maxV == minV)
                                        ? 0.0
                                        : (v - minV) / (maxV - minV);

                        return face.calculerPoint3D(
                                localU,
                                1.0 - localV
                        );

                    }
                }
            }
        }
        return p[0];
    }


    public Point3D[][] getArray2(StructureMatrix<Point3D> s) {

        Point3D[][] t = new Point3D[s.data2d.size()][s.data2d.get(0).size()];
        for (int j = 0; j < s.data2d.size(); j++)
            for (int i = 0; i < s.data2d.get(0).size(); i++)
                t[j][i] = s.data2d.get(j).get(i);
        return t;
    }

    public Point3D[] getArray1(StructureMatrix<Point3D> s) {

        Point3D[] t = new Point3D[s.data1d.size()];
        for (int j = 0; j < s.data1d.size(); j++)
            t[j] = s.data1d.get(j);
        return t;
    }

    @Override
    public void texture(ITexture tc) {
        texture = tc;
        getListRepresentable().forEach(representable -> representable.texture(tc));
    }


    @Override
    public String toString() {
        return "E3Model{" +
                "representables=[" + getListRepresentable().size() +
                "],objects=[" + objects.getListRepresentable().size() +
                "], uMin=" + uMin +
                ", vMin=" + vMin +
                ", uMax=" + uMax +
                ", vMax=" + vMax +
                '}';
    }


    private Point3D derivativeU(
            FaceWithUv face,
            double u,
            double v) {

        Point3D p1 = face.getPoint(0);
        Point3D p2 = face.getPoint(1);
        Point3D p3 = face.getPoint(2);
        Point3D p4 = face.getPoint(3);

        Point3D a =
                p2.moins(p1);

        Point3D b =
                p3.moins(p4);

        return a.mult(1.0 - v)
                .plus(b.mult(v));
    }

    /**
     * Recherche les coordonnées UV correspondant au point 3D le plus proche
     * sur les faces OBJ possédant des coordonnées de texture.
     *
     * @param pos point dans l'espace 3D
     * @return Point3D(u, v, 0), ou null si aucune face UV n'est disponible
     */
    public Point3D findUvForPoint3D(Point3D pos) {

        if (pos == null) {
            return null;
        }

        FaceWithUv bestFace = null;
        Point3D bestUv = null;
        double bestDistance = Double.MAX_VALUE;

        for (Representable representable : getListRepresentable()) {

            if (!(representable instanceof FaceWithUv face)) {
                continue;
            }

            if (face.textUv == null) {
                continue;
            }

            if (face.isTriangle()) {

                Point3D uv = findUvTriangle(face, pos);

                if (uv != null) {
                    Point3D projected =
                            xyzFromUVTriangle(face, uv.getX(), uv.getY());

                    double distance =
                            Point3D.distance(pos, projected);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestFace = face;
                        bestUv = uv;
                    }
                }

            } else if (face.isQuad()) {

                Point3D uv = findUvQuad(face, pos);

                if (uv != null) {

                    Point3D projected =
                            face.calculerPoint3D(
                                    uv.getX(),
                                    uv.getY()
                            );

                    double distance =
                            Point3D.distance(pos, projected);

                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestFace = face;
                        bestUv = new Point3D(
                                localUToTextureU(face, uv.getX()),
                                localVToTextureV(face, uv.getY()),
                                0.0
                        );
                    }
                }
            }
        }

        return bestUv;
    }

    private double localVToTextureV(
            FaceWithUv face,
            double localV) {

        double minV = Double.MAX_VALUE;
        double maxV = -Double.MAX_VALUE;

        for (int i = 1; i < face.textUv.length; i += 2) {
            minV = Math.min(minV, face.textUv[i]);
            maxV = Math.max(maxV, face.textUv[i]);
        }

        return minV + localV * (maxV - minV);
    }

    private Point3D xyzFromUVTriangle(
            FaceWithUv face,
            double u,
            double v) {

        double u0 = face.getU(0);
        double v0 = face.getV(0);

        double u1 = face.getU(1);
        double v1 = face.getV(1);

        double u2 = face.getU(2);
        double v2 = face.getV(2);

        double det =
                (u1 - u0) * (v2 - v0)
                        - (u2 - u0) * (v1 - v0);

        if (Math.abs(det) < 1e-14) {
            return null;
        }

        double w1 =
                ((u - u0) * (v2 - v0)
                        - (u2 - u0) * (v - v0))
                        / det;

        double w2 =
                ((u1 - u0) * (v - v0)
                        - (u - u0) * (v1 - v0))
                        / det;

        double w0 =
                1.0 - w1 - w2;

        return face.getPoint(0).mult(w0)
                .plus(face.getPoint(1).mult(w1))
                .plus(face.getPoint(2).mult(w2));
    }



    private Point3D findUvQuad(
            FaceWithUv face,
            Point3D target) {

        /*
         * Plusieurs points de départ rendent la méthode robuste
         * lorsque le quad est fortement déformé.
         */
        double[][] starts = {
                {0.0, 0.0},
                {1.0, 0.0},
                {1.0, 1.0},
                {0.0, 1.0},
                {0.5, 0.5}
        };

        double bestU = 0.0;
        double bestV = 0.0;
        double bestDistance = Double.MAX_VALUE;

        for (double[] start : starts) {

            double u = start[0];
            double v = start[1];

            for (int iteration = 0; iteration < 20; iteration++) {

                Point3D p =
                        face.calculerPoint3D(u, v);

                Point3D error =
                        p.moins(target);

                /*
                 * ∂P/∂u
                 */
                Point3D pu =
                        derivativeU(face, u, v);

                /*
                 * ∂P/∂v
                 */
                Point3D pv =
                        derivativeV(face, u, v);

                /*
                 * Résolution du système des moindres carrés :
                 *
                 * [pu.pu pu.pv] [du] = -error.pu
                 * [pu.pv pv.pv] [dv] = -error.pv
                 */

                double a = pu.dot(pu);
                double b = pu.dot(pv);
                double c = pv.dot(pv);

                double d = -error.dot(pu);
                double e = -error.dot(pv);

                double determinant =
                        a * c - b * b;

                if (Math.abs(determinant) < 1e-14) {
                    break;
                }

                double du =
                        (d * c - b * e)
                                / determinant;

                double dv =
                        (a * e - b * d)
                                / determinant;

                u += du;
                v += dv;

                /*
                 * Le solveur peut légèrement sortir du quad
                 * à cause des erreurs numériques.
                 */
                u = Math.max(0.0, Math.min(1.0, u));
                v = Math.max(0.0, Math.min(1.0, v));

                if (Math.abs(du) < 1e-10
                        && Math.abs(dv) < 1e-10) {
                    break;
                }
            }

            Point3D result =
                    face.calculerPoint3D(u, v);

            double distance =
                    Point3D.distance(target, result);

            if (distance < bestDistance) {
                bestDistance = distance;
                bestU = u;
                bestV = v;
            }
        }

        /*
         * Tolérance adaptée à la géométrie du modèle.
         *
         * On ne rejette pas systématiquement le point :
         * on renvoie la solution la plus proche.
         */
        return new Point3D(bestU, bestV, 0.0);
    }

    private Point3D derivativeV(
            FaceWithUv face,
            double u,
            double v) {

        Point3D p1 = face.getPoint(0);
        Point3D p2 = face.getPoint(1);
        Point3D p3 = face.getPoint(2);
        Point3D p4 = face.getPoint(3);

        Point3D a =
                p1.plus(
                        p2.moins(p1).mult(u)
                );

        Point3D b =
                p4.plus(
                        p3.moins(p4).mult(u)
                );

        return b.moins(a);
    }


    private double localUToTextureU(
            FaceWithUv face,
            double localU) {

        double minU = Double.MAX_VALUE;
        double maxU = -Double.MAX_VALUE;

        for (int i = 0; i < face.textUv.length; i += 2) {
            minU = Math.min(minU, face.textUv[i]);
            maxU = Math.max(maxU, face.textUv[i]);
        }

        return minU + localU * (maxU - minU);
    }

    Point3D xyzFromUV(
            Point3D p1, double u1, double v1,
            Point3D p2, double u2, double v2,
            Point3D p3, double u3, double v3,
            double u, double v) {

        double det =
                (u2 - u1) * (v3 - v1)
                        - (u3 - u1) * (v2 - v1);

        if (Math.abs(det) < 1e-14) {
            return null;
        }

        double w1 =
                ((u - u1) * (v3 - v1)
                        - (u3 - u1) * (v - v1))
                        / det;

        double w2 =
                ((u2 - u1) * (v - v1)
                        - (u - u1) * (v2 - v1))
                        / det;

        double w0 =
                1.0 - w1 - w2;

        return p1.mult(w0)
                .plus(p2.mult(w1))
                .plus(p3.mult(w2));
    }

    /***
     * a = (x1, y1, z1, u1, v1)+u*((x2, y2, z2, u2, v2)- (x1, y1, z1, u1, v1))
     * b = (x4, y4, z4, u4, v4)+u*((x3, y3, z3, u3, v3)- (x4, y4, z4, u4, v4))
     * (x, y, z, u, v) = a+v*(b- a)
     * x=(x1+u*(x2-x1))+v*(u*(x3-x1)-u*(x2-x1))
     * y=(y1+u*(y2-y1))+v*(u*(y3-y1)-u*(y2-y1))
     * z=(z1+u*(z2-z1))+v*(u*(z3-z1)-u*(z2-z1))
     * u=(u1+u*(u2-u1))+v*(u*(u3-u1)-u*(u2-u1))
     * v=(v1+u*(v2-v1))+v*(u*(v3-v1)-u*(v2-v1))
     * Développer x, y, z, u, v
     *
     * x = x1+u*(x2-x1+v*x3-v*x1-v*x2+v*x1)
     * y = y1+u*(y2-y1+v*y3-v*y1-v*y2+v*y1)
     * z = z1+u*(z2-z1+v*z3-v*z1-v*z2+v*z1)
     * u = u1+u*(u2-u1+v*u3-v*u1-v*u2+v*u1)
     * v = v1+u*(v2-v1+v*v3-v*v1-v*v2+v*v1)
     *
     * (4) (u-u1)/u = (u2-u1+v*u3-v*u1-v*u2+v*u1)
     * (5) (v-v1)/u = (v2-v1+v*v3-v*v1-v*v2+v*v1)
     * (5) (u-u1)/(v-v1) = v*(u2-u1+u3-u1-u2+u1)/(v2-v1+v3-v1-v*v2+v1)
     *
     *  (4) (u-u1) = u*(u2-u1+v*u3-v*u1-v*u2+v*u1)
     *  (5) (u-u1)/(v-v1) = v*(u2-u1+u3-u1-u2+u1)/(v2-v1+v3-v1-v*v2+v1)
     *
     *  u*(u2-u1+v*u3-v*u1-v*u2+v*u1)/(v-v1) = v*(u2-u1+u3-u1-u2+u1)/(v2-v1+v3-v1-v*v2+v1)
     *  (6) u = (u2-u1+u3-u1-u2+u1)/(v2-v1+v3-v1-v*v2+v1)/(u2-u1+v*u3-v*u1-v*u2+v*u1)*(v-v1)*v
     *
     *
     */
    public class FaceWithUv extends ParametricSurface {
        private final Polygon polygon0;

        public FaceWithUv(one.empty3.library.Polygon orig, double[] textureIndices) {
            this.polygon0 = new Polygon(orig);
            this.polygon = new Polygon(orig);
            model = E3Model.this;

            if (textureIndices == null || textureIndices.length < 6) {
                throw new IllegalArgumentException(
                        "FaceWithUv requires at least 3 UV coordinates"
                );
            }

            textUv = textureIndices;

            // Compatibilité avec l'ancien code :
            // u1/v1 = premier sommet UV
            // u2/v2 = dernier sommet UV pour un quad,
            // deuxième sommet UV pour un triangle.
            u1 = textureIndices[0];
            v1 = textureIndices[1];

            if (textureIndices.length >= 8) {
                // Quad
                u2 = textureIndices[6];
                v2 = textureIndices[7];
            } else {
                // Triangle
                u2 = textureIndices[2];
                v2 = textureIndices[3];
            }

            this.polygon.texture(E3Model.this.texture);
        }

        public E3Model model;
        Polygon polygon;
        double[] textUv;
        double u1, u2, v1, v2;

        public Polygon getPolygon() {
            return polygon;
        }

        @Override
        public Point3D calculerPoint3D(double u, double v) {

            int size = getPolygon().getPoints().getData1d().size();

            if (size == 3) {

                Point3D p1 = getPolygon().getPoints().getElem(0);
                Point3D p2 = getPolygon().getPoints().getElem(1);
                Point3D p3 = getPolygon().getPoints().getElem(2);

                // Coordonnées barycentriques :
                //
                // w0 = 1-u-v
                // w1 = u
                // w2 = v

                double w0 = 1.0 - u - v;
                double w1 = u;
                double w2 = v;

                Point3D res =
                        p1.mult(w0)
                                .plus(p2.mult(w1))
                                .plus(p3.mult(w2));

                res.texture(texture());

                return res;
            }

            if (size == 4) {

                Point3D p1 = getPolygon().getPoints().getElem(0);
                Point3D p2 = getPolygon().getPoints().getElem(1);
                Point3D p3 = getPolygon().getPoints().getElem(2);
                Point3D p4 = getPolygon().getPoints().getElem(3);

                /*
                 * Bilinéaire :
                 *
                 * A(u) = p1 + u (p2-p1)
                 * B(u) = p4 + u (p3-p4)
                 * P(u,v) = A + v(B-A)
                 */

                Point3D a =
                        p1.plus(
                                p2.moins(p1).mult(u)
                        );

                Point3D b =
                        p4.plus(
                                p3.moins(p4).mult(u)
                        );

                Point3D res =
                        a.plus(
                                b.moins(a).mult(v)
                        );

                res.texture(texture());

                return res;
            }

            throw new IllegalStateException(
                    "FaceWithUv supports only triangles and quads: " + size
            );
        }

        public double getU1() {
            return u1;
        }

        public double getU2() {
            return u2;
        }

        public double getV1() {
            return v1;
        }

        public double getV2() {
            return v2;
        }

        public double[] getTextUv() {
            return textUv;
        }

        public void setTextUv(double[] textUv) {
            this.textUv = textUv;
        }

        @Override
        public String toString() {
            return "FaceWithUv{" +
                    "polygon0=" + polygon0 +
                    ", model=" + model +
                    ", polygon=" + polygon +
                    ", textUv=" + Arrays.toString(textUv) +
                    ", u1=" + u1 +
                    ", u2=" + u2 +
                    ", v1=" + v1 +
                    ", v2=" + v2 +
                    '}';
        }

        public boolean isTriangle() {
            return getPolygon().getPoints().getData1d().size() == 3;
        }

        public boolean isQuad() {
            return getPolygon().getPoints().getData1d().size() == 4;
        }

        public double getU(int index) {
            return textUv[index * 2];
        }

        public double getV(int index) {
            return textUv[index * 2 + 1];
        }

        public Point3D getPoint(int index) {
            return getPolygon().getPoints().getElem(index);
        }

        public class Polygon extends one.empty3.library.Polygon {
            public Polygon(one.empty3.library.Polygon orig) {
                super();
                this.setPoints(orig.getPoints().getData1d().toArray(new Point3D[orig.getPoints().getData1d().size()]));
                this.texture(E3Model.this.texture);
            }

            @Override
            public StructureMatrix<Point3D> getPoints() {
                StructureMatrix<Point3D> points2 = new StructureMatrix<>(1, Point3D.class);
                for (int i = 0; i < super.getPoints().getData1d().size(); i++) {
                    Point3D p = super.getPoints().getElem(i);
                    Point3D multi = thisModel.getOrig().plus(thisModel.getVectX().mult(p.getX()).plus(thisModel.getVectY().mult(p.getY())).plus(thisModel.getVectZ().mult(p.getZ())));
                    points2.setElem(multi, i);

                }
                return points2;
            }
        }

    }

    private Point3D findUvTriangle(
            FaceWithUv face,
            Point3D p) {

        Point3D a = face.getPoint(0);
        Point3D b = face.getPoint(1);
        Point3D c = face.getPoint(2);

        Point3D v0 = b.moins(a);
        Point3D v1 = c.moins(a);
        Point3D vp = p.moins(a);

        double d00 = v0.dot(v0);
        double d01 = v0.dot(v1);
        double d11 = v1.dot(v1);
        double d20 = vp.dot(v0);
        double d21 = vp.dot(v1);

        double denom = d00 * d11 - d01 * d01;

        if (Math.abs(denom) < 1e-14) {
            return null;
        }

        double bary1 =
                (d11 * d20 - d01 * d21) / denom;

        double bary2 =
                (d00 * d21 - d01 * d20) / denom;

        double bary0 =
                1.0 - bary1 - bary2;

        /*
         * On accepte une petite tolérance pour les points
         * situés exactement sur une arête.
         */
        final double epsilon = 1e-8;

        if (bary0 < -epsilon
                || bary1 < -epsilon
                || bary2 < -epsilon) {
            return null;
        }

        double u =
                bary0 * face.getU(0)
                        + bary1 * face.getU(1)
                        + bary2 * face.getU(2);

        double v =
                bary0 * face.getV(0)
                        + bary1 * face.getV(1)
                        + bary2 * face.getV(2);

        return new Point3D(u, v, 0.0);
    }


}
