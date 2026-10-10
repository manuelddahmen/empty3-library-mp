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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.gui;

import one.empty3.library.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Created by manue on 22-07-19.
 */
public class DataModel implements PropertyChangeListener {
    private ArrayList<ITexture> textures = new ArrayList();
    private String fileModel;
    private File newImageFile;
    private String sceneDirectory;

    public DataModel() {
        getDirectory(true);
        Scene scene = new Scene();
        setScene(scene);
        scene.cameraActive(new Camera());
        scene.cameraActive().calculerMatrice(null);
    }

    public DataModel(File inputEcXml) {
        if (!inputEcXml.isDirectory() && inputEcXml.exists()) {
            sceneDirectory = inputEcXml.getParent();
            try {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputEcXml);
                doc.getDocumentElement().normalize();
                Element rootElement = doc.getDocumentElement();

                browser(rootElement, new Scene());
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        }
        Logger.getAnonymousLogger().log(Level.INFO, "" + this);
        Logger.getAnonymousLogger().log(Level.INFO, "Model loaded");
    }

    private List<Element> getChildElements(Element element) {
        List<Element> list = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                list.add((Element) node);
            }
        }
        return list;
    }

    private List<Element> getChildElements(Element element, String tagName) {
        List<Element> list = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && (tagName == null || node.getNodeName().equals(tagName))) {
                list.add((Element) node);
            }
        }
        return list;
    }

    private Element getFirstChildElement(Element element, String tagName) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && (tagName == null || node.getNodeName().equals(tagName))) {
                return (Element) node;
            }
        }
        return null;
    }

    private String getAttributeValue(Element element, String name) {
        if (element.hasAttribute(name)) {
            String val = element.getAttribute(name);
            return val.isEmpty() ? null : val;
        }
        return null;
    }

    private String getLocalName(Element element) {
        String localName = element.getLocalName();
        return localName != null ? localName : element.getNodeName();
    }

    private void browser(Element element, MatrixPropertiesObject representable) {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        String classVal = getAttributeValue(element, "class");
        try {
            Class<?> aClass1 = systemClassLoader.loadClass(classVal);
            if (aClass1.equals(Scene.class)) {
                this.scene = new Scene();
                representable = scene;
            }
            {
                if (representable instanceof Representable)
                    ((Representable) representable).declareProperties();
                // TODO TEST EQUALITY CLASSES
            }
            List<Element> childElements = getChildElements(element);
            for (int i = 0; i < childElements.size(); i++) {
                Element element1 = childElements.get(i);
                String name = getAttributeValue(element1, "name");
                if (name != null) {
                    if (name.contains("/"))
                        name = name.split("/")[0];
                StructureMatrix declaredProperty = representable.getDeclaredProperty(name);
                if (declaredProperty == null)
                    System.err.println("Element: " + element.toString() + "Element class: " + getAttributeValue(element, "class") + "\nStructureMatrix : null \nelement1: name=" + name + "\nElement1= " + element1.toString());
                else
                    browser(element1, (StructureMatrix) declaredProperty);
                }

            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ClassesNotEqualException e) {
            throw new RuntimeException(e);
        }

    }

    private void browser(Element structureElement, StructureMatrix sm) throws ClassNotFoundException, ClassesNotEqualException {
        if (structureElement == null || sm == null)
            try {
                throw new ClassesNotEqualException(structureElement == null ? null : structureElement.getClass(), sm == null ? null : sm.getClass());
            } catch (ClassesNotEqualException e) {
                e.printStackTrace();
            }
        if (!getAttributeValue(structureElement, "class").equals(StructureMatrix.class.getName()))
            throw new ClassesNotEqualException(Class.forName(getAttributeValue(structureElement, "class")), StructureMatrix.class);

        String smClassName = getAttributeValue(structureElement, "typeClass");
        try {
            Class<?> aClass = Class.forName(smClassName);
            Element data = getFirstChildElement(structureElement, "Data");
            int dim = Integer.parseInt(getAttributeValue(structureElement, "dim"));
            sm.init(dim, aClass);
            List<Element> cellElements = getChildElements(data, "Cell");
            int size = cellElements.size();
            for (int i = 0; i < size; i++) {
                int l, c;
                Element cell = cellElements.get(i);
                l = Integer.parseInt(getAttributeValue(cell, "l"));
                c = Integer.parseInt(getAttributeValue(cell, "c"));
                List<Element> cellChildren = getChildElements(cell);
                if (cellChildren.size() > 0) {

                    Object valueOf = null;
                    valueOf = valueOf(cellChildren.get(0));
                    Object elemLc = null;
                    if (valueOf == null)
                        elemLc = browser(cellChildren.get(0), sm, dim, l, c);
                    if (valueOf != null || elemLc != null) {

                        if (valueOf instanceof Representable && size > 0 && size == 3) {
                            Logger.getAnonymousLogger().log(Level.INFO, valueOf.toString());
                        }


                        switch (dim) {
                            case 0:
                                if (valueOf != null) {
                                    sm.setElem(valueOf);

                                } else {
                                    sm.setElem(elemLc);
                                }
                                break;
                            case 1:
                                if (valueOf != null) {
                                    sm.setElem(valueOf, c);

                                } else {
                                    sm.setElem(elemLc, c);
                                }
                                break;
                            case 2:
                                if (valueOf != null) {
                                    sm.setElem(valueOf, l, c);

                                } else {
                                    sm.setElem(elemLc, l, c);
                                }
                                break;
                        }
                    }
                }

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private Object valueOf(Element simple) {
        String localName = getLocalName(simple);
        String value = simple.getTextContent();
        switch (localName) {
            case "Double":
                return Double.parseDouble(value);
            case "Integer":
                return Integer.parseInt(value);
            case "String:":
                return value;
            case "Boolean":
                return Boolean.parseBoolean(value);
            case "File":
                return new File(value);
        }
        return null;

    }

    private Object browser(Element matrixContainedObjet, StructureMatrix sm, int dim, int l, int c) {

        try {

            Class<?> rClass = Class.forName(getAttributeValue(matrixContainedObjet, "class"));
            Object o = rClass.newInstance();
            if (rClass.getMethod("getDeclaredProperty", String.class) != null) {
                if (o instanceof Representable) {
                    ((Representable) o).declareProperties();
                }
                switch (dim) {
                    case 0:
                        sm.setElem(o);
                        browser(matrixContainedObjet, (MatrixPropertiesObject) o);
                        break;
                    case 1:
                        sm.setElem(o, c);
                        browser(matrixContainedObjet, (MatrixPropertiesObject) o);
                        break;
                    case 2:
                        sm.setElem(o, l, c);
                        browser(matrixContainedObjet, (MatrixPropertiesObject) o);
                        break;
                }
            } else {
                throw new ClassesNotEqualException(rClass, (Class<? extends MatrixPropertiesObject>) o.getClass());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassesNotEqualException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
            switch (localName)
            {
                case "StructureMatrix":
                    break;
                case "Representable":
                    break;
                case "Data":
                    break;
                case "Double":
                    break;
                case "Integer":
                    break;
                case "String:" :
                    break;
                case "Boolean":
                    break;
                case "File":
                    break;
            }

     */
    private Scene scene;

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {

        this.scene = scene;
    }

    public void saveAs(File file) throws IOException {
        this.fileModel = file.getAbsolutePath();
        save(fileModel);

    }

    public String getDirectory(boolean isNew) {
        if (isNew) {
            this.sceneDirectory = new Config().getMap().get("folderoutput")
                    + "/storage/scene-" + System.nanoTime();
            new File(sceneDirectory).mkdirs();
        }
        return sceneDirectory;
    }

    public String getDefaultFilename() {
        return getDirectory(false) + "/" + "scene-" + System.nanoTime() + "";
    }


    public void save(String fileModel) throws IOException {
        Logger.getAnonymousLogger().info("Save Data Model");


        try {
            Scene scene = getScene();
            StringBuilder stringBuilder = new StringBuilder();
            scene.xmlRepresentation(getDirectory(false), stringBuilder, (Representable) scene);

            File out = new File((fileModel != null ? fileModel : getDefaultFilename()));
            System.out.println(out.getAbsolutePath());
            String xml = stringBuilder.toString();
            PrintWriter pw = new PrintWriter(out);
            pw.print(xml);
            pw.close();
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }

    }

    public void copy(File a, File b)
            throws IOException {

        Path copied = Paths.get(b.getCanonicalPath());
        Path originalPath = a.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    /*
        public static DataModel load(File file) throws FileNotFoundException {
            XStream stream = new XStream();
            DataModel dataModel = new DataModel();
            dataModel.setScene((Scene) stream.fromXML(new FileInputStream(file)));

            return dataModel;

        }
    */
    private void addFile(ZipOutputStream zipOut, FileInputStream fis, ZipEntry zipEntry) throws IOException {
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
    }

    static ZipEntry getEntry(ZipFile zipIn, String zipEntry) throws IOException {
        return zipIn.getEntry(zipEntry);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {

        }
    }

    public void addTexture(ITexture sel) {
        this.textures.add(sel);
    }


    public String getNewImageFile() {
        return getDefaultFilename() + ".jpg";
    }

    public String getFileModel() {
        return getDefaultFilename() + ".moodz";
    }

    public String getNewStlFile() {

        return getDefaultFilename() + ".stl";
    }

    public String getNewObjFile() {
        return getDefaultFilename() + ".obj";
    }


    class MoodModelCell {
        private String idContainer;
        private String propertyName;
        private String propertyDescription;
        private String FullObjectClassName;
        private boolean isListType;
        private String ListFullObjectClassName;
        private boolean isArrayType;
        private String ArrayFullObjectClassName;
        private String propertyDim;
        private String propertyIndices;
        private Object propertyValue;

        public String getIdContainer() {
            return idContainer;
        }

        public void setIdContainer(String idContainer) {
            this.idContainer = idContainer;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getPropertyDescription() {
            return propertyDescription;
        }

        public void setPropertyDescription(String propertyDescription) {
            this.propertyDescription = propertyDescription;
        }

        public String getFullObjectClassName() {
            return FullObjectClassName;
        }

        public void setFullObjectClassName(String fullObjectClassName) {
            FullObjectClassName = fullObjectClassName;
        }

        public boolean isListType() {
            return isListType;
        }

        public void setListType(boolean listType) {
            isListType = listType;
        }

        public String getListFullObjectClassName() {
            return ListFullObjectClassName;
        }

        public void setListFullObjectClassName(String listFullObjectClassName) {
            ListFullObjectClassName = listFullObjectClassName;
        }

        public boolean isArrayType() {
            return isArrayType;
        }

        public void setArrayType(boolean arrayType) {
            isArrayType = arrayType;
        }

        public String getArrayFullObjectClassName() {
            return ArrayFullObjectClassName;
        }

        public void setArrayFullObjectClassName(String arrayFullObjectClassName) {
            ArrayFullObjectClassName = arrayFullObjectClassName;
        }

        public String getPropertyDim() {
            return propertyDim;
        }

        public void setPropertyDim(String propertyDim) {
            this.propertyDim = propertyDim;
        }

        public String getPropertyIndices() {
            return propertyIndices;
        }

        public void setPropertyIndices(String propertyIndices) {
            this.propertyIndices = propertyIndices;
        }


        public void setPropertyValue(Object propertyValue) {
            this.propertyValue = propertyValue;
        }

        public Object getPropertyValue() {
            return propertyValue;
        }
    }


    @Override
    public String toString() {
        Scene scene = getScene();
        StringBuilder stringBuilder = new StringBuilder();
        scene.xmlRepresentation(getDirectory(false), stringBuilder, (Representable) scene);
        String xml = stringBuilder.toString();
        return xml;
    }
}
