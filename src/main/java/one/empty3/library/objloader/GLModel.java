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

package one.empty3.library.objloader;

import com.jogamp.opengl.GL2;
import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.Polygon;

import one.empty3.libs.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GLModel {

    private static final int GL_TRIANGLES = 1;
    private static final int GL_QUADS = 2;
    private static final int GL_POLYGON = 4;
    private ArrayList vertexsets;
    private ArrayList vertexsetsnorms;
    private ArrayList vertexsetstexs;
    private ArrayList faces;
    private ArrayList facestexs;
    private ArrayList facesnorms;
    private ArrayList mattimings;
    private MtlLoader materials;
    private int objectlist;
    private int numpolys;
    public float toppoint;
    public float bottompoint;
    public float leftpoint;
    public float rightpoint;
    public float farpoint;
    public float nearpoint;
    private String mtl_path;

    //THIS CLASS LOADS THE MODELS
    public GLModel(BufferedReader ref, boolean centerit, String path, GL2 gl) {

        mtl_path = path;
        vertexsets = new ArrayList();
        vertexsetsnorms = new ArrayList();
        vertexsetstexs = new ArrayList();
        faces = new ArrayList();
        facestexs = new ArrayList();
        facesnorms = new ArrayList();
        mattimings = new ArrayList();
        numpolys = 0;
        toppoint = 0.0F;
        bottompoint = 0.0F;
        leftpoint = 0.0F;
        rightpoint = 0.0F;
        farpoint = 0.0F;
        nearpoint = 0.0F;
        loadobject(ref);
        if (centerit)
            centerit();
        //opengldrawtolist(gl);
        numpolys = faces.size();
        Logger.getAnonymousLogger().log(Level.INFO, "parsed poly count = " + numpolys);
        //cleanup();
        //Logger.getAnonymousLogger().log(Level.INFO, "parsed poly count after cleanup = " + numpolys);
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

                    //LOADS VERTEX COORDINATES
                    if (newline.startsWith("v ")) {
                        float coords[] = new float[4];
                        String coordstext[] = new String[4];
                        newline = newline.substring(2, newline.length());
                        StringTokenizer st = new StringTokenizer(newline, " ");
                        for (int i = 0; st.hasMoreTokens(); i++)
                            coords[i] = Float.parseFloat(st.nextToken());

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
                    } else

                        //LOADS VERTEX TEXTURE COORDINATES
                        if (newline.startsWith("vt")) {
                            float coords[] = new float[4];
                            String coordstext[] = new String[4];
                            newline = newline.substring(3, newline.length());
                            StringTokenizer st = new StringTokenizer(newline, " ");
                            for (int i = 0; st.hasMoreTokens(); i++)
                                coords[i] = Float.parseFloat(st.nextToken());

                            vertexsetstexs.add(coords);
                        } else

                            //LOADS VERTEX NORMALS COORDINATES
                            if (newline.startsWith("vn")) {
                                float coords[] = new float[4];
                                String coordstext[] = new String[4];
                                newline = newline.substring(3, newline.length());
                                StringTokenizer st = new StringTokenizer(newline, " ");
                                for (int i = 0; st.hasMoreTokens(); i++)
                                    coords[i] = Float.parseFloat(st.nextToken());

                                vertexsetsnorms.add(coords);
                            } else

                                //LOADS FACES COORDINATES
                                if (newline.startsWith("f ")) {
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
                                } else

                                    //LOADS MATERIALS
                                    if (newline.charAt(0) == 'm' && newline.charAt(1) == 't' && newline.charAt(2) == 'l' && newline.charAt(3) == 'l' && newline.charAt(4) == 'i' && newline.charAt(5) == 'b') {
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
                                        }
                }
            }
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
        float xshift = (rightpoint - leftpoint) / 2.0F;
        float yshift = (toppoint - bottompoint) / 2.0F;
        float zshift = (nearpoint - farpoint) / 2.0F;
        for (int i = 0; i < vertexsets.size(); i++) {
            float coords[] = new float[4];
            coords[0] = ((float[]) vertexsets.get(i))[0] - leftpoint - xshift;
            coords[1] = ((float[]) vertexsets.get(i))[1] - bottompoint - yshift;
            coords[2] = ((float[]) vertexsets.get(i))[2] - farpoint - zshift;
            vertexsets.set(i, coords);
        }

    }

    public float getXWidth() {
        float returnval = 0.0F;
        returnval = rightpoint - leftpoint;
        return returnval;
    }

    public float getYHeight() {
        float returnval = 0.0F;
        returnval = toppoint - bottompoint;
        return returnval;
    }

    public float getZDepth() {
        float returnval = 0.0F;
        returnval = nearpoint - farpoint;
        return returnval;
    }

    public int numpolygons() {
        return numpolys;
    }

    public void opengldrawtolist(GL2 gl) {
        ////////////////////////////////////////
        /// With Materials if available ////////
        ////////////////////////////////////////
        this.objectlist = gl.glGenLists(1);

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

        gl.glNewList(objectlist, GL2.GL_COMPILE);
        for (int i = 0; i < faces.size(); i++) {
            if (i == nextmat) {
                gl.glEnable(GL2.GL_COLOR_MATERIAL);
                gl.glColor4f((materials.getKd(nextmatname))[0], (materials.getKd(nextmatname))[1], (materials.getKd(nextmatname))[2], (materials.getd(nextmatname)));
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
            int polytype;
            if (tempfaces.length == 3) {
                polytype = gl.GL_TRIANGLES;
            } else if (tempfaces.length == 4) {
                polytype = gl.GL_QUADS;
            } else {
                polytype = gl.GL_POLYGON;
            }
            gl.glBegin(polytype);
            ////////////////////////////

            for (int w = 0; w < tempfaces.length; w++) {
                if (tempfacesnorms[w] != 0) {
                    float normtempx = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[0];
                    float normtempy = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[1];
                    float normtempz = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[2];
                    gl.glNormal3f(normtempx, normtempy, normtempz);
                }

                if (tempfacestexs[w] != 0) {
                    float textempx = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[0];
                    float textempy = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[1];
                    float textempz = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[2];
                    gl.glTexCoord3f(textempx, 1f - textempy, textempz);
                }

                float tempx = ((float[]) vertexsets.get(tempfaces[w] - 1))[0];
                float tempy = ((float[]) vertexsets.get(tempfaces[w] - 1))[1];
                float tempz = ((float[]) vertexsets.get(tempfaces[w] - 1))[2];
                gl.glVertex3f(tempx, tempy, tempz);
            }


            //// Quad End Footer /////
            gl.glEnd();
            ///////////////////////////


        }
        gl.glEndList();
    }

    public void addToRepresentable(RepresentableConteneur representableConteneur) {
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


        int rCount = 0;
        for (int i = 0; i < faces.size(); i++) {

            int faceLength = ((int[]) (faces.get(i))).length;
            Color color = null;
            if (i == nextmat) {
                color = new Color(Lumiere.getIntFromFloats((materials.getKd(nextmatname))[0], (materials.getKd(nextmatname))[1], (materials.getKd(nextmatname))[2], (materials.getd(nextmatname))));
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
            int polytype;
            if (tempfaces.length == 3) {
                polytype = GL_TRIANGLES;
            } else if (tempfaces.length == 4) {
                polytype = GL_QUADS;
            } else {
                polytype = GL_POLYGON;
            }
            ////////////////////////////

            Point3D normal;
            Point3D[] face = new Point3D[faceLength];
            for (int w = 0; w < faceLength; w++) {
                if (tempfacesnorms[w] != 0) {
                    float normtempx = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[0];
                    float normtempy = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[1];
                    float normtempz = ((float[]) vertexsetsnorms.get(tempfacesnorms[w] - 1))[2];
                    normal = new Point3D(1.0d * normtempx, 1.0d * normtempy, 1.0d * normtempz);
                }

                Point3D textCoord;
                if (tempfacestexs[w] != 0) {
                    float textempx = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[0];
                    float textempy = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[1];
                    float textempz = ((float[]) vertexsetstexs.get(tempfacestexs[w] - 1))[2];
                    textCoord = new Point3D(0d + textempx, 1d - textempy, 0d + textempz);
                }

                float tempx = ((float[]) vertexsets.get(tempfaces[w] - 1))[0];
                float tempy = ((float[]) vertexsets.get(tempfaces[w] - 1))[1];
                float tempz = ((float[]) vertexsets.get(tempfaces[w] - 1))[2];
                face[w] = new Point3D(1.0 * tempx, 1.0 * tempy, 1.0 * tempz);
                face[w].texture(new ColorTexture((color != null) ? color : new Color(Color.WHITE.getRGB())));
            }

            if (color == null)
                color = new Color(Color.WHITE);
            Representable r = null;
            if (faceLength >= 4) {
                r = new Polygon(face, color);
            } else if (faceLength == 3) {
                r = new TRI(face[0], face[1], face[2], color);
            } else if (faceLength == 2) {
                r = new LineSegment(face[0], face[1], new ColorTexture(color));
            } else if (faceLength == 1) {
                r = face[0];
            }

            //// Quad End Footer /////

            if (r != null) {
                representableConteneur.add(r);
                rCount++;
            }
            ///////////////////////////


        }
        Logger.getAnonymousLogger().log(Level.INFO, "Num of poly = " + rCount);
    }

    public void opengldraw(GL2 gl) {
        gl.glCallList(objectlist);
        gl.glDisable(GL2.GL_COLOR_MATERIAL);
    }
}
