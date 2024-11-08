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

package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.library.core.nurbs.Fct1D_1D;

/*__
 * Created by manue on 05-01-20.
 */
public class T {
    protected double t0;
    protected double t1;
    protected double t;
    protected double fps;
    protected double noFrames;
    protected Fct1D_1D curveT;


    public T() {

    }


    public void set(double fps, double t1, double t0, double tInit, double noFrames) {
        this.fps = fps;
        this.t0 = t0;
        this.t1 = t1;
        this.noFrames = noFrames;
        t = tInit;
    }

    public double incrT() {
        return curveT.result((t1) * fps / noFrames) - curveT.result((t0) * fps / noFrames);
    }

    public double tPP() {
        this.t = curveT.result(t) + curveT.result((1 - t0) * fps / noFrames);
        return t;
    }

    public double noFrame() {
        return t / incrT();
    }


    public double getT0() {
        return t0;
    }

    public void setT0(double t0) {
        this.t0 = t0;
    }

    public double getT1() {
        return t1;
    }

    public void setT1(double t1) {
        this.t1 = t1;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
    }

    public double getNoFrames() {
        return noFrames;
    }

    public void setNoFrames(double noFrames) {
        this.noFrames = noFrames;
    }


    /*__*
     * Set function for the object X
     * @param curveT curveT = curve t=f(t0)
     */
    public void setCurve(Fct1D_1D curveT) {
        this.curveT = curveT;
    }
}
