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

package one.empty3.library.tests.tests2.tihange_reac_cendar;

import one.empty3.library.ITexture;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.TextureCol;
import one.empty3.library.core.nurbs.ParametricSurface;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * Created by Win on 22-12-18.
 */
public class Tihange extends RepresentableConteneur {

    public Tihange() {

        clear();

        ITexture texture1 = new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f));
        ITexture texture2 = new TextureCol(one.empty3.libs.Color.newCol(0.0f,1.0f,0f));
        ITexture texture3 = new TextureCol(one.empty3.libs.Color.newCol(0,0,1));
        ITexture texture4 = new TextureCol(Color.newCol(1f, 0f, 1f));
        ITexture texture5 = new TextureCol(Color.newCol(1f, 1f, 0f));
        ITexture texture6 = new TextureCol(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1f));


        // Paroi extérieure haut
        ParametricSurface surface = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);
                texture(texture1);
            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                double largeur = 8 + (v - 0.5) * (v - 0.5) * 4;
                double z = (v - 0.5) * 2;
                double coordLarge = 2 * Math.PI * u;
                return new Point3D(Math.cos(coordLarge) * largeur, Math.sin(coordLarge) * largeur,
                        z);
            }
        };
        // Paroi intérieur haut
        ParametricSurface surface1 = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);

                texture(texture2);
            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                double largeur = 6;
                double z = (v - 0.5) * 2;
                double coordLarge = 2 * Math.PI * u;
                return new Point3D(Math.cos(coordLarge) * largeur, Math.sin(coordLarge) * largeur,
                        z);
            }
        };
        // Surface sommet haut
        ParametricSurface surface2 = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);

                texture(texture3);
            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                double coordLarge = 2 * Math.PI * u;
                double largeur = v + 6;
                double hauteur = 1.0;
                return new Point3D(largeur * Math.cos(coordLarge),
                        largeur * Math.sin(coordLarge), hauteur);
            }
        };
        // Surface intérieur bas horizontale
        ParametricSurface surface3 = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);

                texture(texture4);
            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                double largeur = v * 9;
                double coordLarge = 2 * Math.PI * u;
                return new Point3D(Math.cos(coordLarge) * largeur,
                        largeur * Math.sin(coordLarge), -1d);
            }
        };
        // Paroi extérieure bas horizontale
        ParametricSurface surface4 = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);

                texture(texture5);
            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                double coordLarge = 2 * Math.PI * u;
                double largeur = 9 * v;
                double z = -1.2;
                return new Point3D(Math.cos(coordLarge) *
                        largeur, Math.sin(coordLarge) * largeur, z);
            }
        };

        // Paroi extérieure bas méridionale
        ParametricSurface surface5 = new ParametricSurface() {
            {
                setIncrU(0.01);
                setIncrV(0.01);

            }

            @Override
            public Point3D calculerPoint3D(double u, double v) {
                texture(texture6);
                double largeur = 9;
                double coordLarge = 2 * Math.PI * u;
                double z = -v / 5 - 1.0;
                return new Point3D(
                        largeur * Math.cos(coordLarge),
                        largeur * Math.sin(coordLarge), z);
            }
        };
        add(surface);
        add(surface1);
        add(surface2);
        add(surface3);
        add(surface4);
        add(surface5);
    }

}
