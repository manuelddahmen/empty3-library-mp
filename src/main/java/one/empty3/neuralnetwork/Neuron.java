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

import one.empty3.feature.PixM;

import java.io.*;

public class Neuron implements Comparable {
    private int length;
    private Net<? extends Neuron> network;
    private Layer<? extends Neuron> layer;
    private double[] w;
    protected double[] input;
    protected double output;
    protected double bias;
    private ActivationFunction activationFunction;
    private ActivationMethod activationMethod = ActivationMethod.ReLU;

    public Neuron(int length) {
        this.length = length;
        w = new double[length];
        input = new double[length];
        initConstant(0.0);
    }

    public void compute() {
        double dot = dot(getW(), getInput());
        double function = function();
        output = function;
    }

    public double[] getW() {
        return w;
    }

    public void setW(double[] w) {
        this.w = w;
    }

    public double[] getInput() {
        return input;
    }

    public void setInput(double[] input) {
        this.input = input;
    }

    public double getOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }


    public double function() {
        return dot(input, w) + bias;
    }

    private double dot(double[] inputs, double[] w) {
        // Check colors components
        double res = 0.0;
        for (int i = 0; i < w.length; i++) {
            res += inputs[i] * w[i];
        }
        return res;
    }

    /***
     * Not implemented yet
     * @return error double
     */
    public double error() {
        double e = 0.0;
        for (int i = 0; i < w.length; i++) {
            e = e + Math.pow(output - input[i] * w[i], 2);

        }
        return e;
    }

    /***
     * Not implemented yet
     * @return error double
     */
    public double error(double[] w) {
        double e = 0.0;
        for (int i = 0; i < w.length; i++) {
            e = e + Math.pow(output - input[i] * w[i], 2);

        }
        return e;
    }

    /***
     * Not implemented yet
     */
    public void updateW() {
        double e = error();
        double w1 = 0.0, w2 = 0.0;
        for (int i = 0; i < w.length; i++) {
            w[i] = w[i] + e * (function() - network.getPredictedResult().getOutputValues()) * input[i];
        }
        return;
    }

    public double sigmoid(double[] x, double[] w) {
        double z = dot(x, w);
        return 1. / (1. + Math.exp(-z));
    }

    @Override
    public int compareTo(Object o) {
        if (o instanceof Neuron && o.getClass().equals(getClass())) {
            Neuron o1 = (Neuron) o;
            if (o1.w.equals(w))
                return 0;
        }
        return -1;
    }

    public void initRandom(double random) {
        for (int i = 0; i < w.length; i++) {
            w[i] = (Math.random() - 0.5) * 2;
        }
    }

    public void initConstant(double random) {
        for (int i = 0; i < w.length; i++) {
            w[i] = random;
        }
    }

    public double activation() {
        switch (activationMethod) {
            case None:
                return activationFunction != null ?
                        activationFunction.activation(this) :
                        getOutput();
            case ReLU:
                //# rectified linear function
                return Math.max(0.0, getOutput());
            case Identity:
            case Linear:
                return getOutput();
            case Sigmoid:
                break;
            case MinMax:
                double min = Math.min(1.0, Math.max(0.0, function()));
                return min < 1.0 ? 0.0 : 1.0;
            case Logisitic:
                return 1.0 / (1.0 + Math.exp(-getOutput()));
            case MinMax01:
                return Math.min(1.0, Math.max(0.0, function()));
        }
        return function() + bias > 0 ? 1 : 0;
    }
/*
    public int ordPix(int x, int y, int comp) {
        return comp * sizeX * sizeY + sizeX * y + x;
    }
*/
    /*
    public Point3D getPixelColorComponents(int i, int j) {
        return new Point3D(input[ordPix(i, j, 0)],
                input[ordPix(i, j, 1)], input[ordPix(i, j, 2)]);
    }
*/

    public void setBias(double bias) {
        bias = bias;
    }

    public double getBias() {
        return bias;
    }

    public void setInputImage(PixM pixM) {
        for (int x = 0; x < Config.RES; x++)
            for (int y = 0; y < Config.RES; y++)
                for (int c = 0; c < pixM.getCompCount(); c++) {
                    pixM.setCompNo(c);
                    input[(x + y * pixM.getColumns() * pixM.compCount) + c] = pixM.get(x, y);
                }
    }

    public int getLength() {
        return length;
    }

    public void setNetwork(Net<? extends Neuron> network) {
        this.network = network;
    }

    public Layer<? extends Neuron> getLayer() {
        return layer;
    }

    public void setLayer(Layer<? extends Neuron> layer) {
        this.layer = layer;
    }

    public Net<? extends Neuron> getNetwork() {
        return network;
    }

    public ActivationFunction getActivationFunction() {
        return activationFunction;
    }

    public void setActivationFunction(ActivationFunction activationFunction) {
        this.activationFunction = activationFunction;
    }

    public ActivationMethod getActivationMethod() {
        return activationMethod;
    }

    public void setActivationMethod(ActivationMethod activationMethod) {
        this.activationMethod = activationMethod;
    }

    public double[] softMax(double[] x, double[] w) {
        double[] res = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            res[i] = 0;
        }
        double sum = 0.0;
        for (int i = 0; i < w.length; i++) {
            res[i] = Math.exp(w[i]);
            sum += res[i];
        }
        for (int i = 0; i < w.length; i++) {
            res[i] = res[i] /= sum;
        }
        return res;
    }

    public boolean save(DataOutputStream outputStream) {
        double[] byteString = new double[w.length];
        for (int i = 0; i < w.length; i++) {
            byteString[i] = w[i];
        }
        String lengthStr = "" + w.length;
        try {
            outputStream.writeInt(w.length);
            for (int i = 0; i < byteString.length; i++) {
                outputStream.writeDouble(byteString[i]);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean read(DataInputStream inputStream) {
        String lengthStr = "" + w.length;
        try {

            int size = inputStream.readInt();
            double[] byteString = new double[size];

            for (int i = 0; i < byteString.length; i++) {
                byteString[i] = inputStream.readDouble();
            }
            setW(byteString);
            length = byteString.length;
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public class Connection {
    }

    public void addNext(Connection connection, Neuron neuron) {

    }

    public double[] multiply(double[] input) {
        int sqrt = (int) Math.sqrt(w.length + 1);
        double[] res = new double[sqrt];
        for (int j = 0; j < sqrt; j++) {
            for (int i = 0; i < sqrt; i++) {
                res[j] += w[i * sqrt + j] * input[j];
            }
        }
        return res;
    }
}