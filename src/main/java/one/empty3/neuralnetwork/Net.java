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
import one.empty3.library.StructureMatrix;
import one.empty3.libs.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.function.Consumer;

public class Net<T extends Neuron> {
    private static double RESOLUTION = 14;
    private List<File> trainSet;
    private Layer<T> inputLayer;
    private List<Layer<T>> hiddenLayerList;
    private List<Layer<OutputNeuron>> outputLayerList;
    private PredictedResult<T> predictedResult;

    public Net() {
        //    inputLayer = new Layer<T>();
        outputLayerList = new ArrayList<>();
        hiddenLayerList = new ArrayList<>();
        trainSet = new ArrayList<>();
        RESOLUTION = 14;
    }

    public Layer<T> getInputLayer() {
        return inputLayer;
    }

    public void setInputLayer(Layer inputLayer) {
        this.inputLayer = inputLayer;
    }

    public List<Layer<T>> getHiddenLayerList() {
        return hiddenLayerList;
    }

    public void setHiddenLayerList(List<Layer<T>> hiddenNeuronList) {
        this.hiddenLayerList = hiddenNeuronList;
    }

    public List<Layer<OutputNeuron>> getOutputLayerList() {
        return outputLayerList;
    }

    public void setOutputLayerList(List<Layer<OutputNeuron>> outputNeuronList) {
        this.outputLayerList = outputNeuronList;
    }

    public List<File> getTrainSet() {
        return trainSet;
    }

    public void setTrainSet(List<File> trainSet) {
        this.trainSet = trainSet;
    }

    public PredictedResult<T> getPredictedResult() {
        return predictedResult;
    }

    public void setPredictedResult(PredictedResult<T> predictedResult) {
        this.predictedResult = predictedResult;
    }

    public static double getRESOLUTION() {
        return RESOLUTION;
    }

    public static void setRESOLUTION(double RESOLUTION) {
        Net.RESOLUTION = RESOLUTION;
    }

    public void loadModel(File model) {

    }


    public void train() throws IOException {
        int maxIterations = 1000;
        int t = 0;
        double errorGlobal = 0.0;

        while (t < maxIterations) {
            for (int n = 0; n < trainSet.size(); n++) {
                PixM pixM = PixM.getPixM((Image) new Image(1,1,1).getFromFile(trainSet.get(n)), RESOLUTION);
                inputLayer.getNeurons().data2d.forEach(new Consumer<List<T>>() {
                    @Override
                    public void accept(List<T> ts) {
                        ts.forEach(new Consumer<T>() {
                            @Override
                            public void accept(T t) {
                                t.setInputImage(pixM);
                            }
                        });
                    }
                });
                final double[] error = {0};

                final double[] function = {0};
                inputLayer.getNeurons().data2d.forEach(new Consumer<List<T>>() {
                    @Override
                    public void accept(List<T> ts) {
                        ts.forEach(new Consumer<T>() {
                            @Override
                            public void accept(T t) {
                                function[0] += t.function();
                                error[0] += t.error();
                                t.updateW();
                            }
                        });
                    }
                });
                // Compute Xs through network

            }
            t++;


        }
    }

    public double computeAll() {
        StructureMatrix<Double> structureMatrix = new StructureMatrix<Double>(inputLayer.getNeurons().getDim(), Double.class);
        switch (inputLayer.getNeurons().getDim()) {
            case 0:
                Neuron neuron = inputLayer.getNeurons().getElem();
                neuron.compute();
                structureMatrix.setElem(neuron.getOutput());
                break;
            case 1:
                inputLayer.getNeurons().getData1d().forEach(new Consumer<T>() {
                    @Override
                    public void accept(T t) {
                        t.compute();
                        structureMatrix.getData1d().add(t.getOutput());
                    }
                });
                break;
            case 2:
                inputLayer.getNeurons().getData2d().forEach(new Consumer<List<T>>() {
                    int i = 0;

                    @Override
                    public void accept(List<T> ts) {
                        final int[] j = {0};
                        ts.forEach(new Consumer<T>() {
                            @Override
                            public void accept(T t) {
                                t.compute();
                                structureMatrix.setElem(t.getOutput(), i, j[0]);
                                j[0]++;
                            }
                        });
                        i++;
                    }
                });
        }
        return 0;
    }
}
