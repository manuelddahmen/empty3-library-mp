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


import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Created by manue on 26-06-19.
 */
public class FunctionView {
    private static final int RENDERER_E3 = 0;
    private static final int RENDERER_GL = 1;
    private DataModel dataModel;
    private double zoom = 1.0;
    //private Camera camera;
    private String xFormula ="0";
    private String yFormula="0";
    private String zFormula="0";
    private double uMin =0;
    private double uMax=1;
    private double vMin=0;
    private double vMax=1;
    private int rendererType = RENDERER_E3;
    private boolean ok;
    private Camera camera =new Camera(new Point3D(0.0, 0.0, -100.0), Point3D.O0);
    private int zDiplayType = ZBufferImpl.SURFACE_DISPLAY_LINES;
    private ITexture texture = new TextureCol(Colors.random());
    private boolean refresh = true;
    private Scene scene = new Scene();
    private MeshEditorBean meshEditorBean = new MeshEditorBean();
    public FunctionView()
    {
        camera.calculerMatrice(Point3D.Y);
        dataModel = new DataModel();
        scene = dataModel.getScene();
    }

    public static int getRendererE3() {
        return RENDERER_E3;
    }

    public static int getRendererGl() {
        return RENDERER_GL;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        changeSupport.firePropertyChange("zoom", (Double) this.zoom, (Double)zoom);
        this.zoom = zoom;
    }


    public double getuMin() {
        return uMin;
    }

    public void setuMin(double uMin) {
        changeSupport.firePropertyChange("uMin", (Double) this.uMin, (Double)uMin);
        this.uMin = uMin;
    }

    public double getuMax() {
        return uMax;
    }

    public void setuMax(double uMax) {
        changeSupport.firePropertyChange("uMax", (Double) this.uMax, (Double)uMax);
        this.uMax = uMax;
    }

    public double getvMin() {
        return vMin;
    }

    public void setvMin(double vMin) {
        changeSupport.firePropertyChange("vMin", (Double) this.vMin, (Double)vMin);
        this.vMin = vMin;
    }

    public double getvMax() {
        return vMax;
    }

    public void setvMax(double vMax) {
        changeSupport.firePropertyChange("vMax", (Double) this.vMax, (Double)vMax);
        this.vMax = vMax;
    }

    public int getRendererType() {
        return rendererType;
    }

    public void setRendererType(int rendererType) {
        changeSupport.firePropertyChange("rendererType", (Integer) this.rendererType, (Integer)rendererType);
        this.rendererType = rendererType;
    }

    public String getxFormula() {
        return xFormula;
    }

    public void setxFormula(String xFormula) {
        changeSupport.firePropertyChange("xFormula", this.xFormula, xFormula);
        this.xFormula = xFormula;
    }

    public String getyFormula() {
        return yFormula;
    }

    public void setyFormula(String yFormula) {
        changeSupport.firePropertyChange("yFormula", this.yFormula, yFormula);
        this.yFormula = yFormula;
    }

    public String getzFormula() {
        return zFormula;
    }

    public void setzFormula(String zFormula) {
        changeSupport.firePropertyChange("zFormula", this.zFormula, zFormula);
        this.zFormula = zFormula;
    }

    public void setOk(int dummy)
    {
        changeSupport.firePropertyChange("ok", this.ok, true);
        ok = true;

    }

    public void setCancel(int dummy)
    {
        changeSupport.firePropertyChange("ok", this.ok, false);
        ok = false;

    }
    public void setRefresh(int dummy)
    {
        changeSupport.firePropertyChange("ok", this.ok, false);
        changeSupport.firePropertyChange("ok", this.ok, true);
        ok = true;

    }

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        changeSupport.firePropertyChange("camera", this.camera, camera);
        this.camera = camera;
    }

    public void setzDiplayType(int zDiplayType) {
        changeSupport.firePropertyChange("zDiplayType", this.zDiplayType, zDiplayType);
        this.zDiplayType = zDiplayType;
    }

    public int getzDiplayType() {
        return zDiplayType;
    }

    public ITexture getTexture() {
        changeSupport.firePropertyChange("texture", this.texture, texture);
        return texture;
    }

    public void setTexture(ITexture texture) {
        changeSupport.firePropertyChange("texture", this.texture, texture);
        this.texture = texture;
    }

    public void setScene(Scene scene)
    {
        changeSupport.firePropertyChange("scene", this.scene, scene);
        this.scene = scene;
    }

    public Scene getScene() {
        return scene;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        changeSupport.firePropertyChange("ok", this.ok, ok);
        this.ok = ok;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        changeSupport.firePropertyChange("zbuffer", this.refresh, refresh);
        this.refresh = refresh;
    }

    public DataModel getDataModel() {
        return dataModel;
    }
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    public MeshEditorBean getMeshEditorBean() {
        return meshEditorBean;
    }

    public void setMeshEditorBean(MeshEditorBean meshEditorBean) {
        this.meshEditorBean = meshEditorBean;
    }
}
