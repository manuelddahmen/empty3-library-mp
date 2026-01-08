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

package one.empty3.apps.sculpt;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.tribase.Tubulaire3;
import one.empty3.libs.Image;

/***
 * @author manuelddahmen
 * @see one.empty3.library.core.tribase.Tubulaire3
 * @see one.empty3.library.HeightMapSurface
 *
 * Tube (cylindre à âme courbe et sculptée par une image en niveauc de gris (0-255)
 */
public class T3D extends Tubulaire3 {
    protected StructureMatrix<HeightMapSurface> surfaceUV = new StructureMatrix<HeightMapSurface>(0, HeightMapSurface.class);
    private ITexture texture2;

    /***
     * Constructor by default.
     *
     */
    public T3D() {
        super();
        soulCurve = new StructureMatrix<>(0, ParametricCurve.class);
        surfaceUV = new StructureMatrix<>(0, HeightMapSurface.class);
        surfaceUV.setElem(new HeightMapSurface() {
            @Override
            public Point3D height(double u, double v) {
                return super.height(u, v);
            }
        });
    }

    /**
     * Calculates 3D point using curve, diameter, and surface
     */
    @Override
    public Point3D calculerPoint3D(double u, double v) {
        if (level == 0 && quad_not_computed > 0) {
            super.calculerPoint3D(v, u);
        }
        Point3D[] vectPerp = vectPerp(v, u);

        // Computes point offset by cosine scaled by height
        return soulCurve.getElem().calculerPoint3D(u).plus(
                vectPerp[1].mult(diameterFunction.getElem().result(u) * Math.cos(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                )).plus(
                vectPerp[2].mult(diameterFunction.getElem().result(u) * Math.sin(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                ));
    }
    /*
        @Override
        public void texture(ITexture tc) {
            super.texture(tc);
            this.texture2 = new ITexture() {
                @Override
                public int getColorAt(double u, double v) {
                    return texture.getColorAt(v , u);
                }
                @Override
                public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
                    return null;
                }
            };
            return;
        }

        @Override
        public ITexture texture() {
            return texture2;
        }
    */
    public StructureMatrix<HeightMapSurface> getSurfaceUV() {
        return surfaceUV;
    }

    public void setSurfaceUV(StructureMatrix<HeightMapSurface> surfaceUV) {
        this.surfaceUV = surfaceUV;
    }
}
