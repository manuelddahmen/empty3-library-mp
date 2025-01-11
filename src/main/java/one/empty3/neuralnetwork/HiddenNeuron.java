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

package one.empty3.neuralnetwork;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HiddenNeuron extends Neuron {
    private Dimension dimensionZ;

    public HiddenNeuron(int length) {
        super(length);
    }


    /***
     * Brut calculus
     * @param dw
     * @return
     */
    public double[] gradient(double dw) {
        double h = error();
        double[] g = new double[getW().length];
        for (int i = 0; i < getW().length; i++) {
            double[] wa = getW().clone();
            wa[i] = getW()[i] + dw;
            double a = error(wa);
            g[i] = (a - h) / dw;
        }
        return g;
    }

    public double[] updateDescend(double e, double dw) {
        double[] wa = getW().clone();
        double[] g = gradient(dw);
        for (int i = 0; i < getW().length; i++) {
            wa[i] = getW()[i] - e * g[i];
        }
        return wa;
    }

    public double[] learn(double dw, double e, double n) {
        for (int i = 0; i < n; i++) {
            setW(updateDescend(e, dw));
            Logger.getAnonymousLogger().log(Level.INFO,""+ error());
        }
        return getW();
    }
}
