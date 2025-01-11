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

/*
 * 2013-2020 Manuel Dahmen
 */
package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.library.core.TemporalComputedObject3D;
import one.empty3.library.core.lighting.Colors;
import one.empty3.tests.Path;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Representable class represents a generic object that can be rendered and manipulated in a 3D scene.
 * This class provides various methods to control the position, orientation, and appearance of the object.
 */
public class Representable implements Serializable, Comparable, XmlRepresentable, MatrixPropertiesObject, TemporalComputedObject3D {

    /**
     * This variable represents a structure matrix of Point3D vectors.
     * The structure matrix is used to store and manipulate a collection of Point3D vectors.
     * The vectors can be accessed using various methods provided by the StructureMatrix class.
     */
    protected StructureMatrix<Point3D> vectors;
    /**
     * Represents the display option for rendering all elements.
     * The value of this variable is 0.
     */
    public static final int DISPLAY_ALL = 0;
    /***
     * Represents the display type for rendering text quads on a surface.
     * This variable is a constant with a value of 1.
     * It is used as a parameter to control the display of text quads on a surface.
     *
     * @see Representable
     * @see Representable#getDisplayType()
     * @see Representable#setDisplayType(int)
     */
    public static final int SURFACE_DISPLAY_TEXT_QUADS = 1;
    /**
     * The variable SURFACE_DISPLAY_COL_QUADS represents the number of quads used for surface display in a graphical application.
     * It is a static final int variable with a value of 3.
     * <p>
     * This variable is defined in the class Representable, which is a superclass for various graphical representations.
     */
    public static final int SURFACE_DISPLAY_COL_QUADS = 3;
    /**
     * Constant integer variable representing the display type of colored triangles on the surface.
     * The value is set to 4.
     */
    public static final int SURFACE_DISPLAY_COL_TRI = 4;
    /**
     * The number of lines used for displaying a surface.
     */
    public static final int SURFACE_DISPLAY_LINES = 5;
    /**
     * The SURFACE_DISPLAY_POINTS variable represents the number of surface points to be displayed.
     * It is a constant integer value that determines the level of detail for rendering surfaces.
     * The higher the value, the more points will be displayed on the surface, resulting in a more detailed representation.
     * The default value is 6.
     */
    public static final int SURFACE_DISPLAY_POINTS = 6;
    public static final ITexture DEFAULT_TEXTURE = new TextureCol(Colors.random());
    private static final int SURFACE_DISPLAY_TEXT_TRI = 2;
    private static final String[] displayTypes = {"All", "Textured Quad", "SURFACE_DISPLAY_TEXT_TRI", "SURFACE_DISPLAY_COL_QUADS", "SURFACE_DISPLAY_COL_TRI", "SURFACE_DISPLAY_LINES", "SURFACE_DISPLAY_POINTS"};
    public static Point3D SCALE1;
    protected static ArrayList<Painter> classPainters = new ArrayList<Painter>();
    protected static HashMap<String, StructureMatrix> defaultHashMapData = new HashMap<String, StructureMatrix>();
    public static final int PATH_ELEM_STRUCTURE_MATRIX = 1;
    public static int PATH_ELEM_DOUBLE_VALUES = 2;
    public static int PATH_ELEM_REPRESENTABLE = 4;
    public final StructureMatrix<Rotation> rotation = new StructureMatrix<Rotation>(0, Rotation.class);
    protected double NFAST = 100;
    protected ITexture CFAST = DEFAULT_TEXTURE;
    // protected Barycentre bc = new Barycentre();
    protected Representable parent;
    /**
     * Represents a scene object.
     *
     * <p>
     * A scene is a container that holds various objects and is used for rendering purposes.
     * It provides methods to set and retrieve the objects within the scene.
     * </p>
     *
     * <p>
     * This variable is declared as protected, which means it can only be accessed
     * within the class or its subclasses.
     * </p>
     *
     * @see Representable
     */
    protected Scene scene;
    /**
     * Represents a texture
     */
    protected ITexture texture = DEFAULT_TEXTURE;
    protected Render render; //= Render.getInstance(0, -1);
    protected StructureMatrix<T> T; // = new StructureMatrix<T>(0, one.empty3.library.T.class);
    /**
     * The display type of an object.
     */
    private int displayType = SURFACE_DISPLAY_TEXT_QUADS;
    private String id;
    private Painter painter = null;
    private int RENDERING_DEFAULT = 0;
    private Map<String, StructureMatrix> declaredDataStructure;// = Collections.synchronizedMap(new HashMap());
    private Map<String, StructureMatrix> declaredLists;//= new HashMap<>();

    /**
     * Represents an object that can be represented in three-dimensional space.
     * This class provides a constructor that initializes the rotation and vectors
     * of the object based on its type.
     * <p>
     * The rotation is set to an empty rotation object if the object is not an instance of
     * Matrix33, Point3D, or Camera. The vectors are set to a default StructureMatrix
     * with Point3D objects representing the X, Y, Z, and origin vectors.
     * </p>
     */
    public Representable() {
        if (!(this instanceof Matrix33 || this instanceof Point3D || this instanceof Camera)) {
            //rotation.setElem(new Rotation(vU, getPosition(), a));
            //scale = new Point3D(1d, 1d, 1d);
            //texture = new TextureCol(Colors.random());
            vectors = new StructureMatrix<>(1, Point3D.class);
            vectors.setElem(Point3D.X, 0);
            vectors.setElem(Point3D.Y, 1);
            vectors.setElem(Point3D.Z, 2);
            vectors.setElem(Point3D.O0, 3);

        }
    }

    public Point3D getOrientedPoint(Point3D a) {
        Point3D oriented = vectors.getElem(3)
                .plus(vectors.getElem(0).mult(a.get(0)))
                .plus(vectors.getElem(1).mult(a.get(1)))
                .plus(vectors.getElem(2).mult(a.get(2)));
        oriented.texture(a.texture());
        return oriented;
    }

    public static void setPaintingActForClass(ZBuffer z, Scene s, PaintingAct pa) {
        Painter p = null;
        classPainters().add(new Painter(z, s, Representable.class));
        p.addAction(pa);
    }

    private static ArrayList<Painter> classPainters() {
        return classPainters;
    }

    public static String[] getDisplayTypes() {
        return displayTypes;
    }

    public StructureMatrix<Rotation> getRotation() {
        return rotation;
    }

    public void setRotation(StructureMatrix<Rotation> rotation) {
        if (rotation != null && rotation.getElem() != null) {
            this.rotation.setElem(rotation.getElem());
        }
    }

    public Point3D rotate(Point3D p0, Representable ref) {
        if (p0 == null) {
            return Point3D.O0;
        } else if (ref != null && ref.getRotation() != null && ref.getRotation().getElem() != null)
            return ref.getRotation().getElem().rotation(p0);
        else
            return p0;
    }

    public String id() {
        return id;
    }
/*
    public void scene(Scene scene) {
        this.scene = scene;

    }
*/

    public void id(String id) {
        this.id = id;
    }

    public void informer(Representable parent) {
        this.parent = parent;
    }

    public void replace(String moo) {
        throw new UnsupportedOperationException("Operation non supportee");
    }

    public boolean supporteTexture() {
        return false;
    }

    public ITexture texture() {
        return this.texture;
    }

    public void texture(ITexture tc) {
        this.texture = tc;
    }


    public Point3D refPoint(Point3D x) {
        if (!(this instanceof Point3D) && !(this instanceof Matrix33))
            return getOrientedPoint(x);
        else
            return x;
    }

    public void setAxes(Point3D o, Point3D vx, Point3D vy, Point3D vz) {
        setOrig(o);
        setVectX(vx);
        setVectY(vy);
        setVectZ(vz);
    }

    /*__
     * DOn't call ZBuffer dessiine methods here: it would loop.
     *
     * @param z ZBuffer use plot or dessine(P) or tracerTriangle(TRI, Itexture)
     */
    public void drawStructureDrawFast(ZBuffer z) {
        throw new UnsupportedOperationException("No genral method for drawing objects");
    }

    public boolean ISdrawStructureDrawFastIMPLEMENTED(ZBuffer z) {
        return false;
    }

    /*__
     * When correctly initialized, PaintingAct action method is called while
     * the shape is rendered.
     *
     * @param z  the actual ZBuffer in which the action should occurs
     * @param s  the scene in which the actions can access to other objects properties.
     *           Optional parameter
     * @param pa The "painting act" (term referring to history of arts).
     */
    public void setPaintingAct(ZBuffer z, Scene s, PaintingAct pa) {
        this.painter = new Painter(z, s, this);
        pa.setObjet(this);
        pa.setScene(s);
        pa.setZBuffer(z);
        painter.addAction(pa);
    }

    public Painter getPainter() {
        return painter;
    }

    public void setPainter(Painter painter) {
        this.painter = painter;
    }

    public void paint() {
        if (getPainter() != null) {
            getPainter().getPaintingAct().paint();
        }
    }

    public Representable intersects(Representable r2) {
        throw new UnsupportedOperationException("Pas implémenté  en cours" +
                "");
    }

    public void become(Representable r) {
        if (this.getClass().equals(r.getClass())) {
            set(r);
        }
    }

    private void set(Representable r) {
    }

    /*__
     *
     * @param o
     * @return ???
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Representable))
            return 0;
        else
            return 1;

    }

    public void draw(ZBufferImpl zBuffer) {
    }

    /**
     * Retrieves the value of the declared property with the given name.
     *
     * @param name the name of the property to retrieve
     * @return the value of the declared property, or null if the property was not found
     */
    @Override
    public StructureMatrix getDeclaredProperty(String name) {
        declareProperties();
        for (String s : getDeclaredDataStructure().keySet()) {
            if (s.startsWith(name)) {
                return getDeclaredDataStructure().get(s);
            }
        }
        return null;
    }

    /**
     * Retrieves the declared data structure of the Representable class.
     *
     * @return the declared data structure, which is a map with string keys and StructureMatrix values
     */
    public Map<String, StructureMatrix> getDeclaredDataStructure() {
        if ((!(this instanceof Point3D)) && (declaredDataStructure == null))
            declaredDataStructure = Collections.synchronizedMap(new HashMap());
        else declaredDataStructure = defaultHashMapData;

        return declaredDataStructure;
    }

    public Map<String, StructureMatrix> getDeclaredLists() {
        return declaredLists;
    }

    public ITexture getTexture() {
        return texture;
    }

    public void setTexture(ITexture texture) {
        this.texture = texture;
    }

    /***
     * Retrieves the type of the property with the given name.
     *
     * @param propertyName the name of the property
     * @return the type of the property
     * @throws NoSuchMethodException if the property getter method is not found
     */
    public Class getPropertyType(String propertyName) throws NoSuchMethodException {
        Method propertyGetter = null;
        propertyGetter = this.getClass().getMethod("get" + ("" + propertyName.charAt(0)).toUpperCase() + (propertyName.length() > 1 ? propertyName.substring(1) : ""));
        return propertyGetter.getReturnType();
    }

    /**
     * Sets the value of the property with the given name.
     *
     * @param propertyName the name of the property
     * @param value        the value to be set
     * @throws InvocationTargetException if the invoked method throws an exception
     * @throws IllegalAccessException    if the invoked method is not accessible
     * @throws NoSuchMethodException     if the property setter method is not found
     */
    public void setProperty(String propertyName, Object value) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method propertySetter = null;

        propertySetter = this.getClass().getMethod("set" + ("" + propertyName.charAt(0)).toUpperCase() + (propertyName.substring(1)), value == null ? null : value.getClass());
        propertySetter.invoke(this, value);
        Logger.getAnonymousLogger().log(Level.INFO, "RType : " + this.getClass().getName() + " Property: " + propertyName + " New Value set " + getProperty(propertyName));
    }

    /**
     * Retrieves the value of the property with the given name.
     *
     * @param propertyName the name of the property to retrieve
     * @return the value of the declared property, or null if the property was not found
     * @throws InvocationTargetException if the invoked method throws an exception
     * @throws IllegalAccessException    if the invoked method is not accessible
     * @throws NoSuchMethodException     if the property getter method is not found
     */
    public Object getProperty(String propertyName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method propertySetter = null;
        propertySetter = this.getClass().getMethod("get" + ("" + propertyName.charAt(0)).toUpperCase() + propertyName.substring(1));
        return propertySetter.invoke(this);
    }

    public String toString() {
        return "Representable()";
    }

    /**
     * Declares the properties of the Representable class.
     * This method clears the declaredDataStructure map and adds specific properties to it based on the object's rotation and type.
     * If the object's rotation is not null and the object is not an instance of Point3D, a "rotation/Rotation" property is added.
     * The "vectors/vectors" property is always added to the map.
     */
    public void declareProperties() {
        getDeclaredDataStructure().clear();
        if (getRotation() != null
                && getRotation().getElem() != null && !(this instanceof Point3D)) {
            getDeclaredDataStructure().put("rotation/Rotation", rotation);
        }
        getDeclaredDataStructure().put("vectors/vectors", vectors);
    }

    public Map<String, StructureMatrix> declarations() {
        declareProperties();
        Map<String, StructureMatrix> dec = Collections.synchronizedMap(new HashMap<>());
        getDeclaredDataStructure().forEach((s, structureMatrix) -> dec.put(s, structureMatrix));
        return dec;
    }

    public ITexture getCFAST() {
        return CFAST;
    }

    public void setCFAST(ITexture CFAST) {
        this.CFAST = CFAST;
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, Double o) {
        stringBuilder.append("<Double class=\"" + o.getClass() + "\">" + o + "</Double>");
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, Boolean o) {
        stringBuilder.append("<Boolean class=\"" + o.getClass() + "\">" + o + "</Boolean>");
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, Integer o) {
        stringBuilder.append("<Integer class=\"" + o.getClass() + "\">" + o + "</Integer>");
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, String o) {
        stringBuilder.append("<String class=\"" + o.getClass() + "\"> <![CDATA[ " + o + " ]]> </String>");
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, File o) {
        stringBuilder.append("<String filename=\"" + o.getName() + "\"class=\"" + o.getClass() + "\"> <![CDATA[ " + o.getName() + " ]]> </String>");
        try {
            FileInputStream fileInputStream = new FileInputStream(o);
            File copy = new File(filesPath + "/" + o.getName());
            copy.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(copy);
            int read = -1;
            byte[] bytes = new byte[4096];
            while (read != 0) {

                read = fileInputStream.read(bytes);
                fileOutputStream.write(bytes, 0, read);
            }

        } catch (Exception ex) {
        }


    }

    public void xmlRepresentation(String filesPath, MatrixPropertiesObject parent, StringBuilder stringBuilder, ArrayList o) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, Object o) {
        if (o == null) return;
        if (o instanceof StructureMatrix) {
            xmlRepresentation(filesPath, stringBuilder, (StructureMatrix) o);
        } else if (o instanceof Representable) {
            xmlRepresentation(filesPath, stringBuilder, ((Representable) o));
        } else {
            switch (o.getClass().getName()) {
                case "java.lang.Boolean":
                    xmlRepresentation(filesPath, stringBuilder, ((Boolean) o));
                    break;
                case "java.lang.Double":
                    xmlRepresentation(filesPath, stringBuilder, ((Double) o));
                    break;
                case "java.lang.Integer":
                    xmlRepresentation(filesPath, stringBuilder, ((Integer) o));
                    break;
                case "java.lang.String":
                    xmlRepresentation(filesPath, stringBuilder, ((String) o));
                    break;
                case "java.util.ArrayList":
                    //xmlRepresentation(filesPath, (XmlRepresentable)parent, stringBuilder, ((ArrayList) o));
                    break;
                case "java.io.File":
                    xmlRepresentation(filesPath, stringBuilder, ((File) o));
                    break;
            }

        }
    }

    public void xmlRepresentation(String filesPath, StringBuilder stringBuilder, Representable is) {
        if (stringBuilder.toString().length() == 0) {
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        }
        stringBuilder.append("<Representable class=\"" + is.getClass().getName() + "\">\n");
        is.declareProperties();
        is.declarations().forEach((s, o) -> {
            if (o instanceof StructureMatrix) {
                StructureMatrix b = (StructureMatrix) o;
                xmlRepresentation(filesPath, s, stringBuilder, o);
            }

        });
        stringBuilder.append("</Representable>\n");
    }

    public void xmlRepresentation(String filesPath, MatrixPropertiesObject parent, StringBuilder stringBuilder) {
        xmlRepresentation(filesPath, stringBuilder, (Representable) parent);
    }

    public void xmlRepresentation(String filesPath, String name, StringBuilder stringBuilder, StructureMatrix is) {
        stringBuilder.append("<StructureMatrix name=\"" + name + "\" dim=\"" + is.getDim() + "\" class=\"" + is.getClass().getName() + "\" typeClass=\"" + is.getClassType().getName() + "\">");
        switch (is.getDim()) {
            case 0:
                stringBuilder.append("<Data>");
                stringBuilder.append("<Cell l=\"0\" c=\"0\">");
                xmlRepresentation(filesPath, stringBuilder, is.getElem());
                stringBuilder.append("</Cell>");
                stringBuilder.append("</Data>");
                break;
            case 1:
                stringBuilder.append("<Data>");
                int[] i1 = new int[]{0, 0};
                is.data1d.forEach(new Consumer() {
                    @Override
                    public void accept(Object o) {
                        stringBuilder.append("<Cell l=\"" + i1[0] + "\" c=\"" + i1[1] + "\">");
                        xmlRepresentation(filesPath, stringBuilder, o);
                        stringBuilder.append("</Cell>");
                        i1[1]++;

                    }

                });
                stringBuilder.append("</Data>");
                break;
            case 2:
                stringBuilder.append("<Data>");
                int[] i2 = new int[]{0, 0};
                is.data2d.forEach(new Consumer<List>() {
                    @Override
                    public void accept(List ts) {
                        i2[1] = 0;
                        ts.forEach(new Consumer() {
                            @Override
                            public void accept(Object o) {
                                stringBuilder.append("<Cell l=\"" + i2[0] + "\" c=\"" + i2[1] + "\">");
                                xmlRepresentation(filesPath, stringBuilder, o);
                                i2[1]++;
                                stringBuilder.append("</Cell>");
                            }
                        });

                        i2[0]++;
                    }
                });
                stringBuilder.append("</Data>");
                break;


        }
        stringBuilder.append("</StructureMatrix>");
    }

    /*
        Map<String, Double> data = new HashMap<>();
        public void addData(String key, double number) {
            data.put(key, number);
        }
        public double getData(String key)
        {
            return data.get(key);
        }
    */
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        Class<? extends Representable> aClass = this.getClass();
        try {
            Representable representable = aClass.newInstance();
            representable.declareProperties();
            declarations().forEach((s, structureMatrix) -> {
                try {
                    try {
                        representable.setProperty(s.split("/")[0], structureMatrix.copy());
                    } catch (CopyRepresentableError copyRepresentableError) {
                        copyRepresentableError.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            });
            return representable;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return null;
    }

    public int getDisplayType() {
        return displayType;
    }

    public void setDisplayType(int displayType) {
        this.displayType = displayType;
    }

    @Override
    public double T(T t) {
        this.T.setElem(t);
        return t.getT();
    }

    @Override
    public Point3D calculerPointT(double t) {
        return null;
    }

    @Override
    public Point3D calculerCurveT(double u, double t) {
        return null;
    }

    @Override
    public Point3D calculerSurfaceT(double u, double v, double t) {
        return null;
    }

    public void setPosition(Point3D calcCposition) {
        //getRotation().getElem().getCentreRot().setElem(calcCposition);
    }

    /***
     * Evaluates property of current object or of a subproperty
     * @param property propertyName:arrayIndex1:arrayIndex2,subpropertyName:i2:j2,subpro...
     * @return atomic property
     */
    public Path getPath(String property) {
        String[] split = property.split(",");

        Object o;

        Representable representable1 = this;
        Object value = null;
        StructureMatrix declaredProperty = null;
        int i = -1;
        int j = -1;
        for (int k = 0; k < split.length; k++) {
            String split0 = split[k].split("/")[0];
            if (value != null) {
                if (value instanceof Representable) {
                    representable1 = ((Representable) value);
                }

            }
            String[] split1 = split0.split(":");
            if (split1.length > 1) {
                i = Integer.parseInt(split1[1]);
            }
            if (split1.length > 2) {
                j = Integer.parseInt(split1[2]);
            }

            declaredProperty = (StructureMatrix) representable1.getDeclaredProperty(split1[0]);

            if (declaredProperty == null)
                return null;
            else {
                if (declaredProperty instanceof StructureMatrix) {

                    StructureMatrix sm = (StructureMatrix) declaredProperty;
                    switch (sm.getDim()) {
                        case 0:
                            Object data0d = sm.getData0d();
                            value = sm.getElem();
                            break;
                        case 1:
                            List data1d = sm.getData1d();
                            value = sm.getElem(i);
                            break;
                        case 2:
                            List data2d = sm.getData2d();
                            value = sm.getElem(i, j);
                            break;
                    }
                }
            }
        }
        return new Path(declaredProperty, value, property, Representable.PATH_ELEM_STRUCTURE_MATRIX, i, j);


    }

    public Point3D computeSpherical(Point3D pxy, double scale) {
        for (Point3D point3D : vectors.data1d) {
            if (point3D == null)
                return pxy;
        }
        double cos = Math.cos(-Math.PI / 2 + Math.PI * pxy.get(1));
        return getOrig().plus(
                getVectX().mult(
                                Math.cos(2.0 * Math.PI * pxy.get(0)) * cos).plus(
                                getVectY().mult(
                                        Math.sin(2.0 * Math.PI * pxy.get(1)) * cos))
                        .plus(getVectZ().mult(Math.sin(-Math.PI / 2 + Math.PI * pxy.get(1)))
                        ).norme1().mult(scale));
    }

    public Point3D computeCubic(Point3D pxy, double scale) {
        for (Point3D point3D : vectors.data1d) {
            if (point3D == null)
                return pxy;
        }
        return getOrig().plus(getVectX().mult(pxy.get(0)).plus(getVectY().mult(pxy.get(1)).plus(getVectZ().mult(pxy.get(2)))).norme1().mult(scale));
    }

    public Point3D getVectX() {
        Point3D elem = vectors.getElem(0);
        return elem == null ? Point3D.X : elem;
    }

    public void setVectX(Point3D vectX) {
        this.vectors.setElem(vectX, 0);
    }

    public Point3D getVectY() {
        Point3D elem = vectors.getElem(1);
        return elem == null ? Point3D.Y : elem;
    }

    public void setVectY(Point3D vectY) {
        this.vectors.setElem(vectY, 1);
    }

    public Point3D getVectZ() {
        Point3D elem = vectors.getElem(2);
        return elem == null ? Point3D.Y : elem;
    }

    public void setVectZ(Point3D vectZ) {
        this.vectors.setElem(vectZ, 2);
    }

    public void setOrig(Point3D orig) {

    }

    public Point3D getOrig() {
        Point3D elem = vectors.getElem(3);
        return elem == null ? Point3D.O0 : elem;
    }

    public StructureMatrix<Point3D> getVectors() {
        return vectors;
    }
}


