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

package one.empty3.gui;

import one.empty3.library.Polygon;
import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.*;
import one.empty3.library.core.nurbs.BSpline;
import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.TreeNodeEvalException;
import one.empty3.library.core.script.InterpreteException;
import one.empty3.library.core.script.InterpretePoint3D;
import one.empty3.library.core.tribase.Surface;
import one.empty3.library.core.tribase.TRIEllipsoide;
import one.empty3.library.core.tribase.Tubulaire3;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by manue on 27-06-19.
 */
public class RepresentableClassList {
    private static JTree scenes;
    private static boolean isInitScenes;
    static MyObservableList<ObjectDescription>
            listClasses = new MyObservableList<>();
    public static ArrayList containers;
    public static ArrayList surfaces;
    public static ArrayList points;
    public static ArrayList curves;

    public static void add(String name, Class clazz) {
        ObjectDescription objectDescription = new ObjectDescription();
        objectDescription.setName(name);
        objectDescription.setR(clazz);
        listClasses.add(objectDescription);
    }

    public static MyObservableList<ObjectDescription> myList(Object object) {
        return null;
    }

    public static MyObservableList<ObjectDescription> myList() {

        points = new ArrayList();
        curves = new ArrayList();
        surfaces = new ArrayList();
        containers = new ArrayList();
        listClasses = new MyObservableList<>();


        add("point", Point3D.class);
        points.add(Point3D.class);
        add("container (group)", RepresentableConteneur.class);
        containers.add(RepresentableConteneur.class);
        add("line", LineSegment.class);
        curves.add(LineSegment.class);
        add("bezier", CourbeParametriquePolynomialeBezier.class);
        curves.add(CourbeParametriquePolynomialeBezier.class);
        add("bezier2", SurfaceParametriquePolynomialeBezier.class);
        surfaces.add(SurfaceParametriquePolynomialeBezier.class);
        add("triangle", TRI.class);
        surfaces.add(TRI.class);
        add("polygon", Polygon.class);
        surfaces.add(Polygon.class);
        add("polyline", PolyLine.class);
        curves.add(PolyLine.class);
        add("sphere", Sphere.class);
        surfaces.add(Sphere.class);
        add("tube", Tubulaire3.class);
        surfaces.add(Tubulaire3.class);
        add("surface (P = f(u,v))", FunctionSurface.class);
        surfaces.add(FunctionSurface.class);
        add("curve   (P = f(u))", FunctionCurve.class);
        curves.add(FunctionCurve.class);
        add("ellipsoid", TRIEllipsoide.class);
        surfaces.add(TRIEllipsoide.class);
        add("fct y = f(x)", FctXY.class);
        curves.add(FctXY.class);
//        add("heightSurfaceXYZ", HeightMapSurfaceXYZ.class);
        add("B-Spline", BSpline.class);
        curves.add(BSpline.class);
        add("LumierePonctuelle", LumierePonctuelle.class);

//        add("extrusion", TRIExtrusionGeneralisee.class);
        add("circle", Circle.class);
        surfaces.add(Circle.class);
        add("extrusion2+", ExtrusionB1B1.class);///???
        surfaces.add(ExtrusionB1B1.class);
        surfaces.add(ExtrusionCurveCurve.class);
        //add("carte de niveaux, surface déformable", HeightMapSurface.class);///???
        //surfaces.add(HeightMapSurface.class);
        //add("tour de revolution", TourRevolution.class);
        //surfaces.add(TourRevolution.class);
        // courbe et surface par défaut à ajouter dans un objet.
        //add("move", Move.class);
        //add("paramCurve", ParametricCurve.class);
        //add("paramSurface", ParametricSurface.class);

        return listClasses;
    }

    public static Point3D pointParse(String x, String y, String z) throws AlgebraicFormulaSyntaxException, TreeNodeEvalException {
        Map<String, Double> hashMap = new HashMap<String, Double>();

        AlgebraicTree treeX = new AlgebraicTree(x, hashMap);
        treeX.construct();
        AlgebraicTree treeY = new AlgebraicTree(y, hashMap);
        treeY.construct();
        AlgebraicTree treeZ = new AlgebraicTree(z, hashMap);
        treeZ.construct();

        Point3D point3D = new Point3D((Double) treeX.eval().getElem(), (Double) treeY.eval().getElem(), (Double) treeZ.eval().getElem());

        return point3D;
    }


    public static Point3D pointParse(String toStringRepresentation) throws InterpreteException {
        InterpretePoint3D interpretePoint3D = new InterpretePoint3D();
        return (Point3D) interpretePoint3D.interprete(toStringRepresentation, 0);
    }

    /*
        public static Matrix33 matrixParse(String text) throws InterpreteException {
            InterpreteMatrix33 interpreteMatrix33 = new InterpreteMatrix33();
            return (Matrix33) interpreteMatrix33.interprete(text, 0);
        }
    */
    public static Matrix33 matrixParse(JTextField[] strings) throws AlgebraicFormulaSyntaxException, TreeNodeEvalException {
        Matrix33 matrix = new Matrix33();
        for (int i = 0; i < strings.length; i++) {
            AlgebraicTree treeI = new AlgebraicTree(strings[i].getText());
            treeI.construct();
            matrix.set(i / 3, i % 3, (double) treeI.eval().getElem());
            strings[i].setText("" + matrix.get(i / 3, i % 3));
        }
        return matrix;
    }

    public static void setObjectFields(Representable r, Point3D point3D, JTextArea textAreaPoint3D, JTextField[] jTextFields, Matrix33 matrix33, JTextArea textAreaMatrix33, JTextField[] jTextFields1, Point3D scale, JTextField textFieldScaleX, JTextField textFieldScaleY, JTextField textFieldScaleZ) {

        textAreaMatrix33.setText(matrix33.toString());
        textAreaPoint3D.setText(point3D.toString());

        for (int i = 0; i < jTextFields.length; i++)
            jTextFields[i].setText("" + String.valueOf(point3D.get(i)));
        for (int i = 0; i < jTextFields1.length; i++)
            jTextFields1[i].setText("" + matrix33.get(i / 3, i % 3));
        textFieldScaleX.setText("" + 1.0);
        textFieldScaleY.setText("" + 1.0);
        textFieldScaleZ.setText("" + 1.0);


    }

    public static void setObjectFieldsCamera(Camera camera, JTextField[] jTextFields) {
        for (int i = 0; i < 3; i++)
            jTextFields[i].setText("" + camera.getEye().get(i));
        for (int i = 3; i < 6; i++)
            jTextFields[i].setText("" + camera.getLookat().get(i - 3));
        // Verticale
        for (int i = 9; i < 18; i++) jTextFields[i].setText("" + camera.getMatrice().get((i - 9) / 3, (i - 9) % 3));
        jTextFields[18].setText("" + camera.getAngleX());
        jTextFields[19].setText("" + camera.getAngleY());
        jTextFields[20].setText("1.0");
    }

    public static void initTextValues(ITexture texture, JComboBox<String> comboBox1, JFileChooser fileChooser1, JTextField[] jTextFields) {
        if (texture != null) {
            if (texture instanceof TextureCol) {
                comboBox1.setSelectedIndex(0);
                float[] comps;
                Color color = new Color(((TextureCol) texture).color());
                jTextFields[0].setText("" + color.getRed());
                jTextFields[1].setText("" + color.getGreen());
                jTextFields[2].setText("" + color.getBlue());
                //jTextFields[3].setText(""+color.getAlpha());
            } else if (texture instanceof TextureMov) {

            } else if (texture instanceof TextureImg) {

            }
        }

    }
}
