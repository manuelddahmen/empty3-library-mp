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

package one.empty3.io;

import one.empty3.feature.PixM;
import one.empty3.feature.ProcessBean;
import one.empty3.feature.process.InProcessCode;
import one.empty3.libs.Image;
import one.empty3.library.core.script.Code;

import javax.imageio.ImageIO;

import one.empty3.libs.Image;
import one.empty3.libs.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Properties;

public class ProcessNFiles {
    private InProcessCode code;
    public List<ProcessNFiles> processNFiles = new ArrayList<>();
    protected ObjectWithProperties properties;
    public ProcessBean bean;
    protected int maxRes = 400;
    private Properties property;
    private File outputDirectory = null;
    private List<File> imagesStack = new ArrayList<>();
    public boolean shouldOverwrite = false;


    public ProcessNFiles() {
        initProperties(this);
    }

    public void initProperties(ProcessNFiles processFile) {
        if (properties == null) {
            properties = new ObjectWithProperties(processFile);
        }
        getProperties().addProperty("maxRes", ObjectWithProperties.ClassTypes.AtomicInt, this.maxRes);
        this.processNFiles.add(this);

    }

    public void onInstanceInit() {

    }

    protected static boolean isImage(File in) {
        return in != null && (in.getAbsolutePath().toLowerCase().endsWith(".jpg")
                || in.getAbsolutePath().toLowerCase().endsWith(".png"));
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public PixM getSource(String s) {
        try {
            Properties p = getProperty();
            String property = p.getProperty(s);
            File file = new File(property);
            Image read = null;
            read = new Image((BufferedImage) new Image(1,1,1).getFromFile(file));
            return (new PixM(read));
        } catch (Exception ex) {
        }

        return null;
    }

    private Properties getProperty() {
        return property;
    }

    public void setProperty(Properties property) {
        this.property = property;
    }

    public boolean processMem(PixM in, PixM out) {
        return in != null && out != null;
    }

    public void setMaxRes(int maxRes) {
        this.maxRes = maxRes;
    }

    @Deprecated
    public File getStackItem(int index) {
        System.out.printf("STACK %d : %s", index, imagesStack.get(index));
        return imagesStack.get(index);
    }

    @Deprecated
    public void setStack(List<File> files1) {
        this.imagesStack = files1;
    }

    public void addSource(File fo) {
        imagesStack.add(fo);
    }

    public boolean processFiles(File out, File... ins) {
        if (this instanceof ProcessFile) {
            return ((ProcessFile) this).process(ins[0], out);
        }
        return false;
    }

    public void addFilter(ProcessNFiles stackItem) {
        this.processNFiles.add(stackItem);
    }

    public List<ProcessNFiles> getProcessNFiles() {
        return processNFiles;
    }

    public void setProcessNFiles(List<ProcessNFiles> processNFiles) {
        this.processNFiles = processNFiles;
    }

    public ObjectWithProperties getProperties() {
        if (properties == null) {
            properties = new ObjectWithProperties(this);
        }
        return properties;
    }

    public void setProperties(ObjectWithProperties properties) {
        this.properties = properties;
    }

    public InProcessCode getCode() {
        return code;
    }

    public void setCode(InProcessCode inProcessCode) {
        this.code = inProcessCode;
    }
}
