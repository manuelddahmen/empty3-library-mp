
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

package one.empty3.library.core.export;

import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.exporters.obj.Exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ObjExport extends Exporter {

    public class Vertex {
        private double x, y, z;
    }

    public class VertexTexture {
        private double u, v;
    }

    public class VertexNormal {
        private double vx, vy, vz;
    }

    public class Face {
        private Vertex v;
        private VertexTexture vt;
        private VertexNormal vn;

    }


    public static void save(File file, Scene scene, boolean override)
            throws IOException {
        if (!file.exists() || (file.exists() && override)) {
            file.createNewFile();
            PrintWriter pw = new PrintWriter(new FileOutputStream(file));

            pw.println("o scene_" + scene.getDescription() + "");

            Iterator<Representable> it = scene.iterator();

            while (it.hasNext()) {
                Representable r = it.next();


                traite(r, pw);
            }

            pw.close();
        }
    }

    private static void traite(Polygon r, PrintWriter pw) {
        for (int s = 0; s < r.getPoints().getData1d().size(); s++) {
            print("v ", pw);
            for (int i = 0; i < 3; i++) {
                double A = r.getPoints().getData1d().get(s).get(i);
                if (Double.isNaN(A)) {
                    A = 0;
                }
                print(A + " ", pw);
            }
            traite(r.getIsocentre(), pw);


        }
        int size = r.getPoints().getData1d().size();
        for (int t = 0; t < size; t++) {
            print("f " + (t % size) + " " +
                    ((t + 1) % size) + " " + size + "\n", pw);
        }
    }

    private static void traite(Representable r, PrintWriter pw) {
        print("", pw);

        if (r instanceof RepresentableConteneur) {
            for (Representable representable : ((RepresentableConteneur) r).getListRepresentable())
                traite(representable, pw);
        }
        if (r instanceof Polygon) {
            traite((Polygon) r, pw);
        }
        if (r instanceof ParametricSurface) {
            traite((ParametricSurface) r, pw);
        }
        if (r instanceof TRIObject) {
            traite((TRIObject) r, pw);
        }
        if (r instanceof TRIGenerable) {
            traite((TRIGenerable) r, pw);
        }
        if (r instanceof Polygon) {
            traite((Polygon) r, pw);
        }
        if (r instanceof TRI) {
            traite((TRI) r, pw);
        }
//        }
        if (r instanceof TRIConteneur) {
            traite((TRIConteneur) r, pw);
        }
    }

    private static void traite(ParametricSurface r, PrintWriter pw) {
        print("", pw);
        int countU = (int) ((r.getEndU() - r.getStartU()) / r.getIncrU());
        int countV = (int) ((r.getEndV() - r.getStartV()) / r.getIncrV());
        for (int i = 0; i < countU; i++) {
            for (int j = 0; j < countV; j++) {
                double u = 1.0 * (1.0 * i / countU) * (r.getEndU() - r.getStartU()) + r.getStartU();
                double v = 1.0 * (1.0 * i / countV) * (r.getEndV() - r.getStartV()) + r.getStartV();
                traite(r.getElementSurface(u,
                        r.getIncrU(),
                        v, r.getIncrV()), pw);
            }

        }
    }

    private static void traite(RepresentableConteneur r, PrintWriter pw) {
        print("", pw);
        Iterator<Representable> it = r.iterator();
        while (it.hasNext()) {
            Representable next = it.next();
            traite(next, pw);
        }
    }

    private static void traite(Point3D r, PrintWriter pw) {
        print("v " + r.get(0) + " " + r.get(1) + " " + r.get(2) + "\n", pw);
    }

    private static void traite(TRI r, PrintWriter pw) {
        for (int i = 0; i < 3; i++) {
            traite(r.getSommet().getElem(i), pw);
        }
        for (int s = 0; s < 3; s++) {
            print("f ", pw);
            for (int c = 0; c < 3; c++) {
                double A = r.getSommet().getElem(s).get(c);
                if (Double.isNaN(A)) {
                    A = 0;
                }
                print(A + " ", pw);
            }
            print("\n", pw);
        }


        print("f 1/1/2", pw);
        print(" 2/2/2", pw);
        print(" 3/3/3\n", pw);
    }

    public static void traite(TRIConteneur TC, PrintWriter pw) {
        print("", pw);

        Iterator<TRI> it = TC.iterable().iterator();

        while (it.hasNext()) {
            TRI t = it.next();

            traite(t, pw);
        }
    }

    private static void traite(TRIGenerable r, PrintWriter pw) {
        r.generate();
    }

    private static void traite(TRIObject r, PrintWriter pw) {
        String s = "";
        Iterator<TRI> it = r.getTriangles().iterator();
        while (it.hasNext()) {

            traite(it.next(), pw);
        }
    }

//    private static void traite(TRIObjetGenerateur r, PrintWriter pw) {
//        String s = "";
//        int x = r.getMaxX();
//        int y = r.getMaxY();
//        TRI[] tris = new TRI[2];
//        for (int i = 0; i < x; i++) {
//            for (int j = 0; j < y; j++) {
//                r.getTris(i, j, tris);
//                traite(tris[0], pw);
//                traite(tris[1], pw);
//
//            }
//        }
//    }

    public static void print(String flowElement, PrintWriter pw) {
        pw.print(flowElement);
    }
}
