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

public class PredictedResult<T extends Neuron> {
    private double outputValues;
    private double sigmaError;
    private String inputFilenameId;
    private String inputFileDescription;

    public double getOutputValues() {
        return outputValues;
    }

    public void setOutputValues(double outputValues) {
        this.outputValues = outputValues;
    }

    public double getSigmaError() {
        return sigmaError;
    }

    public void setSigmaError(double sigmaError) {
        this.sigmaError = sigmaError;
    }

    public String getInputFilenameId() {
        return inputFilenameId;
    }

    public void setInputFilenameId(String inputFilenameId) {
        this.inputFilenameId = inputFilenameId;
    }

    public String getInputFileDescription() {
        return inputFileDescription;
    }

    public void setInputFileDescription(String inputFileDescription) {
        this.inputFileDescription = inputFileDescription;
    }
}