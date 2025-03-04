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
import one.empty3.libs.Image;
//import java.io.File;
//import java.util.function.Function;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class Run {
//    private static File dir = null;
//    public static int MAX_RES = 36;
//    private Neuron inputNeuron = new Neuron(MAX_RES);
//
//    public static void main(String[] args) {
//        String file = null;
//        if (args.length < 1) {
//            file = "C:\\Users\\manue\\OneDrive\\Bureau\\Dropbox\\Dropbox\\Chargements appareil photo\\IMG_20231202_223159.jpg";
//        } else
//            file = args[0];
//
//        int sqrt = (int) Math.sqrt(MAX_RES);
//        Logger.getAnonymousLogger().log(Level.INFO, "sqrt: " + sqrt);
//
//        dir = new File(file);
//        Run run = new Run();
//        run.inputNeuron = new Neuron(MAX_RES);
//
//        double[] x = new double[MAX_RES];
//        for (int i = 0; i < (MAX_RES); i++) {
//            run.inputNeuron.getW()[i] = Math.random() - 0.5;
//        }
//
//        Matrix matrixRandomInput = new Matrix(x, sqrt, sqrt);
//        run.loadImageInput(new one.empty3.libs.Image(new File(file)), sqrt, run.inputNeuron);
//        Matrix matrixNeuron = new Matrix(run.inputNeuron.getW(), sqrt, sqrt);
//        Matrix actual =
//                matrixNeuron.multiply(matrixRandomInput).softmax();
//        Matrix expectedMatrix = Util.createExpectedMatrix(sqrt, sqrt);
//        Matrix loss = LossFunction.crossEntropy(actual, expectedMatrix);
//
//        Matrix calculateError = actual.apply((index, value) -> value - expectedMatrix.get(index));
//
//        Logger.getAnonymousLogger().log(Level.INFO, "Actual result\n" + actual);
//        Logger.getAnonymousLogger().log(Level.INFO, "Matrix neuron image\n" + matrixNeuron);
//        Logger.getAnonymousLogger().log(Level.INFO, "Loss Matrix\n" + loss);
//        Logger.getAnonymousLogger().log(Level.INFO, "Calculate error Matrix\n" + calculateError);
//    }
//
//    public Matrix weightTransform(Matrix weights, Function<Matrix, Matrix> transform) {
//        final double INC = 0.0001;
//        Matrix loss1 = transform.apply(weights);
//        Matrix result = new Matrix(weights.getColumns(), weights.getLines(), i -> 0);
//        weights.forEach(((row, col, index, value) -> {
//            Matrix incremented = weights.addIncrement(row, col, INC);
//            Matrix loss2 = transform.apply(incremented);
//            double rate = (loss2.get(0) - loss1.get(0)) / INC;
//            result.set(row, col, rate);
//        }));
//        return result;
//    }
//
//    public Run() {
//    }
//
//    public void train(File inputFile, File outputFile) {
//        loadImageInput(new one.empty3.libs.Image(dir), MAX_RES, inputNeuron
//        );
//    }
//
//    private void loadImageInput(Image read, int maxRes, Neuron inputNeuron) {
//        PixM pixM = PixM.getPixM(read, maxRes);
//        for (int i = 0; i < pixM.getColumns(); i++) {
//            for (int j = 0; j < pixM.getLines(); j++) {
//                inputNeuron.getInput()[pixM.index(i, j) / 3] = pixM.luminance(i, j);
//            }
//        }
//    }
//}
