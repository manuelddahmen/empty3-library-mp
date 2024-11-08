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
 * 2013 2021 Manuel Dahmen
 */
package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.libs.*;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import one.empty3.tests.Animation;

public class Scene extends Representable implements Serializable {

    public static final String VERSION = "2021.6";
    /*__
     *
     */
    public String author;
    public String date;
    public String description;
    public StructureMatrix<Camera> cameraActive = new StructureMatrix<>(0, Camera.class);
    private StructureMatrix<Representable> objets = new StructureMatrix<>(1, Representable.class);
    private StructureMatrix<one.empty3.tests.Animation> animations = new StructureMatrix<>(1, Animation.class);
    private StructureMatrix<Camera> cameras = new StructureMatrix<>(1, Camera.class);
    private StructureMatrix<ITexture> colors = new StructureMatrix<>(1, ITexture.class);
    private StructureMatrix<Lumiere> lumieres = new StructureMatrix<>(1, Lumiere.class);
    private SceneCadre cadre = new SceneCadre();
    private GTime gt = new GTime();
    private Lumiere lumiereActive;
    private String DESCRIPTION;
    private Time time;
    private List<Move> moves;

    {

    }

    // FOR LOADER
    private Representable dernierAjout;

    public Scene() {

        super();
    }


    public boolean add(Representable add) {
        //objets.getData1d().add(add);
        return objets.getData1d().add(add);
    }

    public boolean add(Representable add, ZBuffer zBuffer) {
        add.setPainter(new Painter(zBuffer, this));
        return objets.getData1d().add(add);

    }

    @Deprecated
    public Camera camera() {
        return cameraActive();
    }

    @Deprecated
    public void camera(Camera c) {
        cameraActive(c);
    }

    public Camera cameraActive() {
        if (cameraActive != null) {
            return cameraActive.getElem();
        } else if (cameras.data1d.size() > 0) {
            return cameras.getElem(0);
        }
        return null;
    }

    public void cameraActive(Camera c) {
        this.cameraActive.setElem(c);
        cameras.setElem(c, 0);
    }

    public List<Camera> cameras() {
        return this.cameras.getData1d();
    }


    protected int colorAdd(int[] cs) {
        float[] compArray = new float[] {0,0,0,0};
        float[] compArray3 = new float[] {0,0,0,0};;

        int l = cs.length;
        for (int c = 0; c < l; c++) {
            for (int i = 0; i < 3; i++) {
                compArray3 = new Color(cs[i]).getRGBComponents(compArray);

                compArray3[i] += compArray[i] / l;
            }
        }
        int res = Lumiere.getInt(new double[] {(double)compArray3[0],(double) compArray3[1],(double) compArray3[2], compArray3[3]});

        return res;
    }

    public void dumpDATA() {
        this.setDESCRIPTION(toString());
    }

    public Representable find(String ido) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void flushImports() {
        dernierAjout = null;
    }

    public SceneCadre getCadre() {
        return cadre;
    }

    public void setCadre(SceneCadre cadre) {
        this.cadre = cadre;
    }

    public Representable getDernierAjout() {
        return dernierAjout;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
        DESCRIPTION = dESCRIPTION;
    }

    public GTime getGt() {
        return gt;
    }

    public Iterator<Representable> iterator() {
        return objets.getData1d().iterator();
    }

    public Object[] liste() {
        Iterator<Representable> ir = iterator();

        Object[] liste = new Object[size()];
        int i = 0;
        while (ir.hasNext()) {
            liste[i] = ir.next().toString();
            liste[i] = ((String) liste[i]).length() >= 100 ? ((String) liste[i]).substring(0, 100) : liste[i];
            i++;
        }
        return liste;
    }

    public Lumiere lumiereActive() {
        if (lumiereActive != null) {
            return lumiereActive;
        } else if (lumieres.getData1d().size() > 0) {
            return lumieres.getData1d().get(0);
        }
        return LumierePointSimple.PARDEFAUT;
    }

    public List<Lumiere> lumieres() {
        return lumieres.getData1d();
    }

    public int lumiereTotaleCouleur(int c, Point3D p, Point3D n) {
        if (n == null || Point3D.INFINI.equals(p) || lumieres.getData1d().isEmpty()) {
            return c;
        }

        double[] t = new double[]{0., 0., 0., 0.};

        int cpt = 0;

        for (Lumiere l : lumieres.getData1d()) {


            int cP = l.getCouleur(c, p, n);


            double[] tadd = Lumiere.getDoubles(cP);
            for (int j = 0; j < 3; j++)
                t[j] += tadd[j];

            cpt++;
        }

        for (int j = 0; j < 3; j++) {
            t[j] /= cpt;
        }

        return Lumiere.getInt(t);
    }

    public boolean remove(Representable rem) {
        return objets.getData1d().remove(rem);
    }

    public int size() {
        return objets.getData1d().size();
    }

    @Override
    public String toString() {
        String str = "scene \n(\n\n";

        Iterator<Representable> it = iterator();
        while (it.hasNext()) {
            Representable r = it.next();
            if (r instanceof Point3D) {
                str += ((Point3D) r).toLongString();
            } else {
                str += r.toString();
            }
        }
        str += "cameras (\n\t";
        if (cameras.getData1d().isEmpty() && cameraActive() != null) {
            str += "\n\t" + cameraActive().toString() + "\n";
        }
        Iterator<Camera> itC = cameras.getData1d().iterator();
        while (itC.hasNext()) {
            str += "\n\t" + itC.next().toString() + "\n";
        }
        str += "\n)";
        str += cameraActive != null ? cameraActive.toString() : "";
        str += "\n\n)\n";
        return str;
    }


    /*__
     * @return
     */
    public boolean updateTime() {
        return false;
    }

    public Time getTime() {
        return time;
    }

    public List<Move> getMoves() {
        return moves;
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("objets/Objets à peindre", objets);
        getDeclaredDataStructure().put("animations/Animation (pas implémenté maintenant jamais", animations);
        getDeclaredDataStructure().put("cameras/Caméras de la scène. cameraActive caméra en cours", cameras);
        getDeclaredDataStructure().put("lumieres/Lumières additionnelles", lumieres);
        getDeclaredDataStructure().put("cameraActive/cameraActive", this.cameraActive);
    }

    public static String getVERSION() {
        return VERSION;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Camera getCameraActive() {
        return cameraActive.getElem();
    }

    public void setCameraActive(Camera cameraActive) {
        this.cameraActive.setElem(cameraActive);
    }


    public void setCameraActive(StructureMatrix<Camera> cameraActive) {
        this.cameraActive = cameraActive;
    }

    public StructureMatrix<Representable> getObjets() {
        return objets;
    }

    public void setObjets(StructureMatrix<Representable> objets) {
        this.objets = objets;
    }

    public StructureMatrix<one.empty3.tests.Animation> getAnimations() {
        return animations;
    }

    public void setAnimations(StructureMatrix<one.empty3.tests.Animation> animations) {
        this.animations = animations;
    }

    public StructureMatrix<Camera> getCameras() {
        return cameras;
    }

    public void setCameras(StructureMatrix<Camera> cameras) {
        this.cameras = cameras;
    }

    public StructureMatrix<ITexture> getColors() {
        return colors;
    }

    public void setColors(StructureMatrix<ITexture> colors) {
        this.colors = colors;
    }

    public StructureMatrix<Lumiere> getLumieres() {
        return lumieres;
    }

    public void setLumieres(StructureMatrix<Lumiere> lumieres) {
        this.lumieres = lumieres;
    }

    public void setGt(GTime gt) {
        this.gt = gt;
    }

    public Lumiere getLumiereActive() {
        return lumiereActive;
    }

    public void setLumiereActive(Lumiere lumiereActive) {
        this.lumiereActive = lumiereActive;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public void setDernierAjout(Representable dernierAjout) {
        this.dernierAjout = dernierAjout;
    }

    public void clear() {
        objets.getData1d().clear();
    }
}
