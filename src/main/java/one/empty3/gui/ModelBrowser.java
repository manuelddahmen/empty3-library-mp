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

/**
 * See also DataModel
 */
package one.empty3.gui;

import one.empty3.*;
import one.empty3.library.*;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by manue on 07-10-19.
 */
public class ModelBrowser {
    private final ZBufferImpl zimpl;
    private boolean clone = false;
    private Class classes = Representable.class;
    private List<Cell> objects = new ArrayList<>();
    private Class clazz = Representable.class;
    private int dim = -1;
    private boolean isRow;
    private int colNo;
    private int rowNo;

    public ModelBrowser(Representable representable, ZBufferImpl zimpl) {
        this.zimpl = zimpl;
        this.objects = new ArrayList<>();
        objects.add(new Cell(null, 0, 0, 0, representable, representable));
    }

    public ModelBrowser(List<Representable> representables, ZBufferImpl zimpl) {
        this.zimpl = zimpl;
        this.objects = new ArrayList<>();
        if (representables != null) {
            for (Representable representable : representables) {
                if (clazz.getClass().isAssignableFrom(representable.getClass()))
                    objects.add(new Cell(null, 0,
                            0, 0, representable, representable));
            }
        }
    }


    public List<Cell> getObjects() {
        return objects;
    }

    public List<Cell> getObjects(Class fromsClasses, int dim) {
        clazz = fromsClasses;
        throw new UnsupportedOperationException("");
    }

    public List<Cell> getObjects(Class fromsClasses, int dim, boolean isRow) {
        clazz = fromsClasses;
        this.dim = dim;
        this.isRow = isRow;

        throw new UnsupportedOperationException("");
    }

    public List<Cell> getObjects(Class fromsClasses, int dim, boolean isRow, int rowNo, int colNo) {
        clazz = fromsClasses;
        this.dim = dim;
        this.isRow = isRow;
        this.rowNo = rowNo;
        this.colNo = colNo;
        throw new UnsupportedOperationException("");
    }

    class Cell {
        public Point3D pRot;
        StructureMatrix structureMatrix;
        Object o;
        int dim;
        int row, col;
        public Representable ref;

        public Cell(StructureMatrix structureMatrix, int dim, int row, int col, Representable ref, Object o, Point3D oRot) {
            this.structureMatrix = structureMatrix;
            this.dim = dim;
            this.row = row;
            this.col = col;
            this.o = o;
            this.pRot = oRot;
        }

        public Cell(StructureMatrix structureMatrix, int dim, int row, int col, Representable ref, Object o) {
            this.structureMatrix = structureMatrix;
            this.dim = dim;
            this.row = row;
            this.col = col;
            this.o = o;
        }

        public Point3D getpRot() {
            return pRot;
        }

        public void setpRot(Point3D pRot) {
            this.pRot = pRot;
        }

        public StructureMatrix getStructureMatrix() {
            return structureMatrix;
        }

        public void setStructureMatrix(StructureMatrix structureMatrix) {
            this.structureMatrix = structureMatrix;
        }

        public Object getO() {
            return o;
        }

        public void setO(Object o) {
            this.o = o;
        }

        public int getDim() {
            return dim;
        }

        public void setDim(int dim) {
            this.dim = dim;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public Representable getRef() {
            return ref;
        }

        public void setRef(Representable ref) {
            this.ref = ref;
        }
    }


    /***
     *
     * @param impl
     * @param scene
     * @param classes
     */
    public ModelBrowser(ZBufferImpl impl, Scene scene, Class classes) {
        this.zimpl = impl;
        this.classes = classes;
        for (int i = 0; i < scene.getObjets().getData1d().size(); i++) {
            Representable representable = scene.getObjets().getData1d().get(i);
            browser(representable, scene);
        }
    }

    private void browser(Representable representable, Representable ref) {
        if (representable != null) {
            try {
                representable.declareProperties();
                representable.declarations().forEach((s, structureMatrix) -> {
                    switch (structureMatrix.getDim()) {
                        case 0:
                            browser(structureMatrix, structureMatrix.getDim(), -1, -1, structureMatrix.getElem(), representable);
                            break;
                        case 1:
                            int[] i = new int[]{0};
                            structureMatrix.data1d.forEach(new Consumer() {
                                @Override
                                public void accept(Object o) {
                                    browser(structureMatrix, structureMatrix.getDim(), i[0], -1, o, representable);
                                    i[0]++;
                                }
                            });
                            break;
                        case 2:
                            i = new int[]{0, 0};
                            structureMatrix.data2d.forEach(o -> {
                                for (Object o1 : ((List) o)) {
                                    browser(structureMatrix, structureMatrix.getDim(), i[0], i[1], o1, representable);
                                    i[1]++;
                                }
                                i[0]++;
                            });
                            break;

                    }
                });
            } catch (ConcurrentModificationException ex) {
                ex.printStackTrace();
                Logger.getAnonymousLogger().log(Level.INFO, "ModelBrowser.browse() Continue...");
            }
        }
    }

    private void browser(StructureMatrix structureMatrix, int dim, int row, int col, Object o, Representable ref) {
        Object e = o;


        if (e instanceof Integer) {
            if (classes.equals(Integer.class)) {
                objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
            }
        } else if (e instanceof Double) {
            if (classes.equals(Double.class)) {
                objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
            }

        } else if (e instanceof Boolean) {
            if (classes.equals(Boolean.class)) {
                objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
            }

        } else if (e instanceof String) {
            if (classes.equals(String.class)) {
                objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
            }

        } else if (e instanceof File) {
            if (classes.equals(File.class)) {
                objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
            }
        } else if (Representable.class.isAssignableFrom(e.getClass())) {
            if (classes.equals(e.getClass())) {
                if (e.getClass().equals(Point3D.class) && (ref != null) && (ref.getRotation() != null)) {
                    objects.add(new Cell(structureMatrix, dim, row, col, ref, e, zimpl.rotate((Point3D) e, ref)));
                    //objects.add(new Cell(structureMatrix, dim, row, col, ref, e));
                }
            }
            browser((Representable) e, ref);

        }
    }

    public void makeSelection(List<Cell> path) {

    }

    public void updateSelection() {

    }

    public void translateSelection(Point3D vect) {
        getObjects().forEach(cell -> {
            Representable r = cell.ref;
            if (clone && r != null && !(r instanceof Point3D)) {
                try {
                    r = (Representable) r.copy();
                    r.getRotation().getElem().getCentreRot().setElem(vect);
                } catch (CopyRepresentableError | IllegalAccessException |
                         InstantiationException copyRepresentableError) {
                    copyRepresentableError.printStackTrace();
                }
            }
            Point3D origin0 = new Point3D(((Point3D) cell.o));

            Point3D origin;
            if (cell.o instanceof Point3D) {
                origin = (Point3D) cell.o;
            } else if (cell.ref != null && cell.ref != null && !(cell.ref instanceof Point3D)) {
                origin = cell.ref.getRotation().getElem().getCentreRot().getElem();
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Error in translate selection");
                return;
            }
            if (vect != null && origin != null) {
                origin.changeTo(vect.plus(origin));
                Logger.getAnonymousLogger().log(Level.INFO, "Moves " + origin0 + "\n to \n" + cell.o);
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Error in translate selection");
            }
        });
    }

    public void rotateSelection(Rotation rotation) {
        getObjects().forEach(cell -> {
            Representable r = cell.ref;
            if (clone) {
                try {
                    r = (Representable) r.copy();

                } catch (CopyRepresentableError copyRepresentableError) {
                    copyRepresentableError.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
            r.getRotation().setElem(rotation);
        });
    }

    public boolean cloneSelection(boolean clone) {
        this.clone = clone;
        return clone;
    }
}
