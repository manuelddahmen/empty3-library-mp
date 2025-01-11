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

package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.feature.QuadTransform2D;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.nurbs.Point2Point;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;
import one.empty3.library1.shader.Vec;

public class VecHeightMap extends ParametricSurface {
    private final StructureMatrix<Vec> vec = new StructureMatrix<>(1, Vec.class);
    private StructureMatrix<SurfaceParametriquePolynomialeBezier> surfaceParametriquePolynomialeBezier
            = new StructureMatrix<>(0, SurfaceParametriquePolynomialeBezier.class);
    private StructureMatrix<Integer>  columnsCount = new StructureMatrix<>(0, Integer.class);
    private StructureMatrix<ParametricSurface> parametricSurface = new StructureMatrix<>(0, ParametricSurface.class);

    public VecHeightMap(ParametricSurface parametricSurface, Vec map, int columnsCount) {
        terminalU.setElem(parametricSurface.getTerminalU().getElem());
        terminalV.setElem(parametricSurface.getTerminalV().getElem());
        this.vec.setElem(map);
        this.columnsCount.setElem(columnsCount);
        this.parametricSurface.setElem(parametricSurface);

        init();

    }

    private void init() {
        Point3D[][] ps = new Point3D[columnsCount.getElem()][vec.getElem().size() / columnsCount.getElem()];
        try {
            for (int i = 0; i < vec.getElem().size(); i++) {
                ps[i % columnsCount.getElem()][i / columnsCount.getElem()] = new Point3D(vec.getElem().get(i), 0.0, 0.0);
            }
            surfaceParametriquePolynomialeBezier.setElem(new SurfaceParametriquePolynomialeBezier(ps));
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        int i = (int) (u * columnsCount.getElem())+vec.getElem().size()*2;
        int j = (int) (v * ((vec.getElem().size() / columnsCount.getElem())))+vec.getElem().size()*2;

        i = Math.round(i);
        j = Math.round(j);

        i = i % (columnsCount.getElem());
        j = j % (vec.getElem().size()/columnsCount.getElem());


        if(surfaceParametriquePolynomialeBezier.getElem()!=null) {
            return parametricSurface.getElem().calculerPoint3D(u, v)
                    .plus(parametricSurface.getElem().calculerNormale3D(u, v)
                            .mult(surfaceParametriquePolynomialeBezier.getElem().calculerPoint3D(u, v)));

        } else {
            Double round = getControlAt(i, j);
            if (round != null) {
                return parametricSurface.getElem().calculerPoint3D(u, v)
                        .plus(parametricSurface.getElem().calculerNormale3D(u, v)
                                .mult(round));
            }
            return null;
        }
    }

    public Double getControlAt(int x, int y) {
        Double v = null;
        if(y*columnsCount.getElem()+x<vec.getElem().size()&&y*columnsCount.getElem()+x>=0
                &&x>=0&&y>=0&&x<vec.getElem().size()&&y<vec.getElem().size()) {
            v = vec.getElem().get(y * columnsCount.getElem() + x);
        }
        return v;
    }

    public Vec getVec() {
        return vec.getElem();
    }

    public int getColumnsCount() {
        return columnsCount.getElem();
    }

    public void setColumnsCount(int columnsCount) {
        this.columnsCount.setElem(columnsCount);
    }

    public ParametricSurface getParametricSurface() {
        return parametricSurface.getElem();
    }

    @Override
    public Point3D getVectX() {
        return  parametricSurface.getElem().getVectX();
    }

    @Override
    public Point3D getVectY() {
        return parametricSurface.getElem().getVectY();
    }

    @Override
    public Point3D getVectZ() {
        return parametricSurface.getElem().getVectZ();
    }

    @Override
    public Point3D getOrig() {
        return parametricSurface.getElem().getOrig();
    }

    @Override
    public void setVectX(Point3D vectX) {
        parametricSurface.getElem().setVectX(vectX);
    }

    @Override
    public void setVectY(Point3D vectY) {
        parametricSurface.getElem().setVectY(vectY);
    }

    @Override
    public void setVectZ(Point3D vectZ) {
        parametricSurface.getElem().setVectZ(vectZ);
    }

    @Override
    public void setOrig(Point3D orig) {
        parametricSurface.getElem().setOrig(orig);
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("parametricSurface/surface à monter en surface de Béziers", parametricSurface);
        getDeclaredDataStructure().put("vec", vec);
        getDeclaredDataStructure().put("columnsCount", columnsCount);
    }

    @Override
    public void texture(ITexture tc) {
        super.texture(tc);
        parametricSurface.getElem().texture(tc);
    }
}
