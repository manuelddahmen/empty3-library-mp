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

import one.empty3.library.StructureMatrix;


public class Layer <T extends Neuron>{
    private int size;
    private int lengthX;
    private StructureMatrix<T> neurons;
    private int dimensions;

    public Layer(int lengthX, int size, Class<T> t) {
        dimensions = 1;
        this.size = size;
        this.lengthX = lengthX;
        neurons = new StructureMatrix<>(dimensions, t);
        for (int i = 0; i < size; i++) {
            try {
                neurons.setElem(t.newInstance(), i);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public Layer(int lengthX, Class<T> t) {
        this(lengthX, 0, t);
    }

    public Layer(StructureMatrix<T> neurons) {
        this.neurons = neurons;
    }

    public StructureMatrix<T> getNeurons() {
        return neurons;
    }

    public void setNeurons(StructureMatrix<T> neurons) {
        this.neurons = neurons;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getLengthX() {
        return lengthX;
    }

    public void setLengthX(int lengthX) {
        this.lengthX = lengthX;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
}
