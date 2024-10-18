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

package one.empty3.library.objloader;

import one.empty3.library.Polygon;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.*;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * Created by manue on 02-06-19.
 * Updated 7/24
 */
public class E3Model extends RepresentableConteneur {
    private ArrayList<Double[]> vertexsets;
    private ArrayList<Double[]> vertexsetsnorms;
    private ArrayList<Double[]> vertexsetstexs;
    private ArrayList<int[]> faces;
    private ArrayList<int[]> facestexs;
    private ArrayList<int[]> facesnorms;
    private ArrayList<String[]> mattimings;
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
    private String mtl_path;
    Color color = new Color(0, 0, 255);
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
        public E3Model model;
        Polygon polygon;
        double[] textUv;
        double u1, u2, v1, v2;

        public FaceWithUv(Polygon orig, double[] textureIndices) {
            this.polygon = orig;
            model = E3Model.this;
            u1 = textureIndices[0];
            u2 = textureIndices[6];
            v1 = textureIndices[1];
            v2 = textureIndices[3];
            textUv = textureIndices;
            this.polygon.texture(E3Model.this.texture);
            //this.texture(texture);
/*            for (int i = 0; i < faces.get(faceNo).length; i++) {
                int[] ints = faces.get(faceNo);
                for (int j = 0; j < ints.length; j++) {
                    Double[] vertex = vertexsets.get(ints[j]);
                    Double[] uv = vertexsetstexs.get(ints[j]);
                    Point3D point1 = new Point3D(vertex);
                    for (int k = 0; k < this.polygon.getPoints().data1d.size(); k++) {
                        if (this.polygon.getPoints().getElem(k).equals(point1)) {
                            textUv[k] = new Point3D(uv);
                        }
                    }
                }
            }
  */
        }


        @Override
        public Point3D calculerPoint3D(double u, double v) {
            Point3D resA1 = getPolygon().getPoints().getElem(0).plus(getPolygon().getPoints().getElem(2).moins(getPolygon().getPoints().getElem(1)).mult(u));
            Point3D resA2 = getPolygon().getPoints().getElem(3).plus(getPolygon().getPoints().getElem(2).moins(getPolygon().getPoints().getElem(2)).mult(u));
            Point3D res = resA2.plus(resA1.moins(resA2).mult(v));
            res.texture(texture());
            return res;
        }

        public Polygon getPolygon() {
            return polygon;
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
                        Double coords[] = new Double[4];
                        String coordstext[] = new String[4];
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
                        Double coords[] = new Double[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(3, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Double.parseDouble(st.nextToken());

                        vertexsetstexs.add(coords);
                    } else if (newline.startsWith("vn")) {
                        Double coords[] = new Double[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(3, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Double.parseDouble(st.nextToken());

                        vertexsetsnorms.add(coords);
                    } else if (newline.startsWith("f ")) {
                        facecounter++;
                        newline = newline.substring(2, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        int count = st.countTokens();
                        int v[] = new int[count];
                        int vt[] = new int[count];
                        int vn[] = new int[count];
                        for (int i = 0; i < count; i++) {
                            char chars[] = st.nextToken().toCharArray();
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
            Logger.getAnonymousLogger().log(Level.INFO, "Failed to read file: " + br.toString());
        } catch (NumberFormatException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "Malformed OBJ file: " + br.toString() + "\r \r" + e.getMessage());
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
            Double coords[] = new Double[4];
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
                nextmatnamearray = (String[]) (mattimings.get(matcount));
                nextmatname = nextmatnamearray[0];
                nextmat = Integer.parseInt(nextmatnamearray[1]);
            }
            Color pointCol = color;


            for (int i = 0; i < faces.size(); i++) {
                Point3D norm = new Point3D();
                if (i == nextmat) {
                    pointCol = new Color((materials.getKd(nextmatname))[0], (materials.getKd(nextmatname))[1], (materials.getKd(nextmatname))[2], (materials.getd(nextmatname)));
                    matcount++;
                    if (matcount < totalmats) {
                        nextmatnamearray = (String[]) (mattimings.get(matcount));
                        nextmatname = nextmatnamearray[0];
                        nextmat = Integer.parseInt(nextmatnamearray[1]);
                    }
                }

                int[] tempfaces = (int[]) (faces.get(i));
                int[] tempfacesnorms = (int[]) (facesnorms.get(i));
                int[] tempfacestexs = (int[]) (facestexs.get(i));

                //// Quad Begin Header ////
                Representable quad;
                if (tempfaces.length == 3) {
                    quad = new Polygon();
                } else {
                    quad = new Polygon();
                }
                ////////////////////////////

                //// Quad Begin Header ////
                double[] colorVertex = new double[3];
                double[] textureListUv1234 = new double[tempfaces.length * 2];
                for (int w = 0; w < tempfaces.length; w++) {

                    if (tempfacesnorms[w] != 0) {
                        Double normtempx = ((Double[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[0];
                        Double normtempy = ((Double[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[1];
                        Double normtempz = ((Double[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[2];
                        norm = new Point3D(normtempx, normtempy, normtempz);
                    }

                    if (tempfacestexs[w] != 0) {
                        Double textempx = ((Double[]) vertexsetstexs.get(tempfacestexs[w] - 1))[0];
                        Double textempy = ((Double[]) vertexsetstexs.get(tempfacestexs[w] - 1))[1];
                        Double textempz = ((Double[]) vertexsetstexs.get(tempfacestexs[w] - 1))[2];
// TODO                    gl.glTexCoord3f(textempx,1f-textempy,textempz);
                        colorVertex = new double[]{textempx, 1f - textempy, 0};
                    }

                    Double tempx = ((Double[]) vertexsets.get(tempfaces[w] - 1))[0];
                    Double tempy = ((Double[]) vertexsets.get(tempfaces[w] - 1))[1];
                    Double tempz = ((Double[]) vertexsets.get(tempfaces[w] - 1))[2];

                    Point3D point3D = new Point3D(tempx, tempy, tempz);
                    point3D.texture(new TextureCol(pointCol));
                    point3D.textureIndex(tempx, tempy, tempz);
                    //point3D.setNormale(norm);
                    if (quad instanceof TRI) {
                        ((TRI) quad).getSommet().setElem(point3D, w);

                    }
                    if (quad instanceof Quads) {
                        ((Quads) quad).add(point3D);
                    }
                    if (quad instanceof Polygon) {
                        ((Polygon) quad).add(point3D);
                        textureListUv1234[w * 2] = colorVertex[0];
                        textureListUv1234[w * 2 + 1] = colorVertex[1];
                    }
                }

                //// Quad End Footer /////
                ///////////////////////////

                quad.texture(new TextureCol(pointCol));
                //add(quad);
                Polygon p = (Polygon) quad;
                if (p.getPoints().getData1d().size() > 3) {
                    add(new FaceWithUv(p, textureListUv1234));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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


    /**
     *
     */
    public Point3D findUvFace(double u, double v) {
        final Point3D[] p = {null};
        for (int i = 0; i < faces.size(); i++) {
            for (Representable representable : getListRepresentable()) {
                if (representable instanceof FaceWithUv faceWithUv) {
                    if (faceWithUv.textUv != null) {
                        double minU = Math.min(Math.min(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.min(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                        double maxU = Math.max(Math.max(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.max(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                        double minV = Math.min(Math.min(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.min(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                        double maxV = Math.max(Math.max(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.max(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                        if (minU <= u && u <= maxU && minV <= v && v <= maxV) {
                            return faceWithUv.calculerPoint3D((u - minU) / (maxU - minU), 1 - (v - minV) / (maxV - minV));
                        }
                    }
                }
            }
        }
        return p[0];
    }

    /***
     *
     * @param pos Point de l'espace
     * @return coordonnées (u,v) du point dy modèle le plus proche de point
     */
    public Point3D findUvForPoint3D(Point3D pos) {
        final Point3D[] p = {null};
        final double[] eps = {Double.MAX_VALUE};
        final FaceWithUv[] face = new FaceWithUv[1];
        for (int i = 0; i < faces.size(); i++) {
            for (Representable representable : getListRepresentable()) {
                if (representable instanceof FaceWithUv faceWithUv) {
                    if (faceWithUv.textUv != null) {
                        double minU = Math.min(Math.min(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.min(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                        double maxU = Math.max(Math.max(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.max(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                        double minV = Math.min(Math.min(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.min(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                        double maxV = Math.max(Math.max(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.max(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                        faceWithUv.getPolygon().getPoints().getData1d().forEach(new Consumer<Point3D>() {
                            @Override
                            public void accept(Point3D point3D) {
                                if (Point3D.distance(pos, point3D) < eps[0]) {
                                    eps[0] = Point3D.distance(pos, point3D);
                                    face[0] = faceWithUv;
                                }
                            }
                        });
                    }
                }
            }
        }

        Point3D p1 = p[0];
        FaceWithUv faceWithUv = face[0];
        double u = p1.getX();
        double v = p1.getY();
        Point3D goood;
        Point3D selectedUv = null;
        if (p[0] != null) {
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    double minU = Math.min(Math.min(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.min(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                    double maxU = Math.max(Math.max(faceWithUv.textUv[0], faceWithUv.textUv[2]), Math.max(faceWithUv.textUv[4], faceWithUv.textUv[6]));
                    double minV = Math.min(Math.min(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.min(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                    double maxV = Math.max(Math.max(faceWithUv.textUv[1], faceWithUv.textUv[3]), Math.max(faceWithUv.textUv[5], faceWithUv.textUv[7]));
                    u = minU + (maxU - minU) * i / 100;
                    v = minU + (maxV - minV) * i / 100;
                    if (minU <= u && u <= maxU && minV <= v && v <= maxV) {
                        goood = faceWithUv.calculerPoint3D((u - minU) / (maxU - minU), 1 - (v - minV) / (maxV - minV));
                        if (Point3D.distance(goood, faceWithUv.getPolygon().getIsocentre()) < eps[0]) {
                            selectedUv = goood;
                        }
                    }
                }
            }
        }

        if (selectedUv != null) {
            return selectedUv;
        }

        return null;
    }
}
