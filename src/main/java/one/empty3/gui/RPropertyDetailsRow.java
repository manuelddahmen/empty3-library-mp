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

//import com.sun.org.apache.regexp.internal.RE;

import one.empty3.*;
import one.empty3.library.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by manue on 15-07-19.
 */
public class RPropertyDetailsRow implements TableModel {


    java.util.List<Object> objectList = new ArrayList<>();

    public List<ObjectDetailDescription> objectDetailDescriptions = new ArrayList<>();
    public static final int ARRAYTYPE_SINGLE = 0;
    public static final int ARRAYTYPE_1D = 1;
    public static final int ARRAYTYPE_2D = 2;
    public static final int TYPE_REPRESENTABLE = 0;
    public static final int TYPE_DOUBLE = 1;
    public static final int TYPE_TEXTURE = 2;
    private MatrixPropertiesObject representable;
    String[] columnNames = {"Shape's Name", "Description", "Dim", "Indices", "ObjectType", "Data"};
    Class<?>[] columnClass = {String.class, String.class, String.class, String.class, String.class, Object.class};
    private TableModelListener listener;
    private Main main;

    public RPropertyDetailsRow(MatrixPropertiesObject representable) {
        this.representable = representable;
        if (representable instanceof Representable) {
            ((Representable) representable).declareProperties();
        }
        emptyTable();
        initTable();
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public RPropertyDetailsRow(RPropertyDetailsRow rPropertyDetailsRow) {
        this(rPropertyDetailsRow.getRepresentable());
    }


    private void emptyTable() {
        index = 0;
        objectList.clear();
        objectDetailDescriptions.clear();
    }

    public void refresh() {
        emptyTable();
        initTable();
    }


    int index;


    protected String[] split(String entryKey) {
        return entryKey.split("/");
    }

    public void initTable() {

        objectList.clear();
        objectDetailDescriptions.clear();

        index = 0;

        if (representable.declarations().size() > 0) {
            representable.declarations().forEach((s, structureMatrix) -> {
                String name = s;
                String[] propName = name.split("/");

                if (structureMatrix == null)
                    return;

                int i = 0;
                String[] split = split(name);
                if (structureMatrix.getDim() == 0) {
                    Object elem = structureMatrix.getElem();
                    objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                            0, "0", elem == null ? null : elem.getClass(), structureMatrix.getElem()));
                    objectList.add(structureMatrix.getElem());
                    index++;
                }
                if (structureMatrix.getDim() == 1) {

                    if (structureMatrix.getClassType().isAssignableFrom(Lumiere.class)) {
                        for (int ind = 0; ind < structureMatrix.getData1d().size(); ind++) {
                            objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                                    0, "" + ind, structureMatrix.getElem(ind).getClass(), structureMatrix.getElem(ind)));
                            objectList.add(structureMatrix.getElem(ind));
                            //main.getDataModel().getScene().lumieres().add((Lumiere) data.getElem(ind));//++
                            index++;
                        }
                    } else {
                        if (structureMatrix.getData1d().size() == 0) {
                            objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                                    0, "" + -1, structureMatrix.getClassType(), null));
                            objectList.add(null);
                            index++;
                        } else {
                            for (int ind = 0; ind < structureMatrix.getData1d().size(); ind++) {
                                objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                                        0, "" + ind, structureMatrix.getElem(ind).getClass(), structureMatrix.getElem(ind)));
                                objectList.add(structureMatrix.getElem(ind));
                                index++;
                            }
                        }
                    }
                }
                if (structureMatrix.getDim() == 2) {
                    if (structureMatrix.getData2d().size() == 0) {
                        for (int ind = 0; ind < 1; ind++) {
                            for (int ind1 = 0; ind1 < 1; ind1++) {

                                objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                                        0, "" + ind + "," + ind1, structureMatrix.getClassType(), null));
                                objectList.add(null);
                                index++;
                            }
                        }
                    } else {
                        for (int ind = 0; ind < structureMatrix.getData2d().size(); ind++) {
                            for (int ind1 = 0; ind1 < ((List) ((List) structureMatrix.getData2d()).get(ind)).size(); ind1++) {

                                objectDetailDescriptions.add(new ObjectDetailDescription(propName[0], propName[1],
                                        0, "" + ind + "," + ind1, structureMatrix.getElem(ind, ind1).getClass(), structureMatrix.getElem(ind, ind1)));
                                objectList.add(structureMatrix.getElem(ind, ind1));
                                index++;
                            }
                        }
                    }

                }

            });

        }
        if (representable instanceof Scene || representable instanceof RepresentableConteneur) {
            MyObservableList<ObjectDescription> objectDescriptions = RepresentableClassList.myList();
            objectDescriptions.forEach(objectDescription -> {
                Representable value = null;
                try {
                    Class<? extends Representable> r = objectDescription.getR();
                    if (r != null)
                        value = r.getConstructor().newInstance();
                    else
                        Logger.getAnonymousLogger().log(Level.INFO, "Class R is null");
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                         InvocationTargetException e) {
                    //e.printStackTrace();
                    Logger.getAnonymousLogger().log(Level.INFO, "Cannot instantiate: " + objectDescription.getR());

                }
                if (value != null) {
                    objectDetailDescriptions.add(new ObjectDetailDescription(
                            objectDescription.getName(), "NEW", 1, "" + index, value.getClass(), null));
                    objectList.add(value);
                    index++;
                } else {
                    objectDetailDescriptions.add(new ObjectDetailDescription(
                            objectDescription.getName(), "ERROR", 1, "" + index, String.class, null));
                    objectList.add("ERROR on Scene, RepresentableConteneur");
                    index++;

                }
            });


        }
        if (!(objectDetailDescriptions.size() == index && objectList.size() == index))
            System.exit(1);
    }

    @Override
    public int getRowCount() {
        return objectDetailDescriptions.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }


    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 5;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < objectDetailDescriptions.size() && rowIndex >= 0)
            return objectDetailDescriptions.get(rowIndex).get(columnIndex);
        return null;
    }


    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (rowIndex < objectDetailDescriptions.size() && rowIndex >= 0) {
            if (representable instanceof StructureMatrix)
                return;
            Representable representable = (Representable) this.representable;
            ObjectDetailDescription descr = objectDetailDescriptions.get(rowIndex);
            Object property = null;
            Class propertyClass = null;
            try {
                property = representable.getProperty(descr.getName());
                propertyClass = property.getClass();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            //Logger.getAnonymousLogger().log(Level.INFO, "property : " + propertyClass.toString());
            try {
                Class propertyType = null;
                try {
                    propertyType = representable.getPropertyType(descr.getName());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                String propertyName = descr.getName();


                if (propertyClass.equals(StructureMatrix.class)) {
                    StructureMatrix property1 = (StructureMatrix) property;


                    switch (property1.getDim()) {
                        case 0:
                            propertyClass = property1.getElem().getClass();
                            break;
                        case 1:
                            propertyClass = property1.getElem(0).getClass();
                            break;
                        case 2:
                            propertyClass = property1.getElem(0, 0).getClass();
                            break;

                    }

                    Logger.getAnonymousLogger().log(Level.INFO, propertyClass.toString());
                    Object avalue = "Error";
                    switch (propertyClass.toString()) {
                        case "class java.lang.Double":
                            avalue = Double.parseDouble((String) aValue);
                            break;
                        case "class java.lang.Integer":
                            avalue = Integer.parseInt((String) aValue);
                            break;
                        case "class java.lang.String":
                            avalue = aValue.toString();
                            break;
                        case "class java.lang.Boolean":
                            avalue = Boolean.parseBoolean((String) aValue);
                            break;
                        default:
                            System.exit(1);
                            break;

                    }
                    //Logger.getAnonymousLogger().log(Level.INFO, avalue.getClass());
                    switch (property1.getDim()) {
                        case 0:
                            property1.setElem(avalue);
                            break;
                        case 1:
                            property1.setElem(avalue, Integer.parseInt(descr.getIndexes()));
                            break;
                        case 2:
                            String indexes = descr.getIndexes();
                            String[] split = indexes.split(",");
                            property1.setElem(avalue, Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                            break;
                    }
                } else if ((propertyType.equals(Double.class) || propertyType.equals(Integer.class) || propertyType.equals(String.class)) && aValue.getClass().equals(String.class)) {
                    if (propertyType.equals(Double.class))
                        aValue = Double.parseDouble((String) aValue);
                    if (propertyType.equals(Integer.class))
                        aValue = Integer.parseInt((String) aValue);
                    Logger.getAnonymousLogger().log(Level.INFO, "Property type : " + propertyType.getName() + " Property name " + aValue);
                    ((Representable) representable).setProperty(descr.getName(),
                            aValue);
                    refresh();
                    descr.set(columnIndex, aValue);
                    System.out.print("save");
                    listener.tableChanged(new TableModelEvent(this, rowIndex, rowIndex, columnIndex));
                }

                int dim = descr.getDim();
                String indices = descr.getIndexes();
                if (dim > 0 && dim == 1 && propertyType.equals(Double[].class)) {
                    aValue = Double.parseDouble((String) aValue);
                    int rowArray = Integer.parseInt(indices);
                    ((Double[]) property)[rowArray] = Double.parseDouble((String) aValue);

                } else if (dim > 0 && dim <= 2 && propertyType.equals(Double[][].class)) {
                    aValue = Double.parseDouble((String) aValue);
                    if (dim == 2) {
                        String[] split = indices.split(",");
                        int rowArray = Integer.parseInt(split[0]);
                        int columnArray = Integer.parseInt(split[1]);

                        if (property.getClass().equals(Double[][].class)) {
                            ((Double[][]) property)[rowArray][columnArray] = Double.parseDouble((String) aValue);
                        }

                    } else {
                        int rowArray = Integer.parseInt(indices);
                        if (property.getClass().equals(Double[].class)) {
                            ((Double[]) property)[rowArray] = Double.parseDouble((String) aValue);
                        }
                    }
                    Logger.getAnonymousLogger().log(Level.INFO, "Property type : " + propertyType.getName() + " Property name " + descr.getName());
                    ((Representable) representable).setProperty(descr.getName(),
                            aValue);
                    refresh();
                    descr.set(columnIndex, aValue);
                    System.out.print("save");

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        refresh();
    }


    @Override
    public void addTableModelListener(TableModelListener l) {
        listener = l;
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listener = l;

    }

    public Object getItemList(int current) {
        return objectList.get(current);
    }

    public MatrixPropertiesObject getRepresentable() {
        return representable;
    }

    public List<Object> getObjectList() {
        return objectList;
    }

    public List<ObjectDetailDescription> getObjectDetailDescriptions() {
        return objectDetailDescriptions;
    }
}
