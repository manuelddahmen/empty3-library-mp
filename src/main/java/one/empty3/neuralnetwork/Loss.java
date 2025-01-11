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

public class Loss {
    /***
     * Fonction de perte Cross Entropy
     * Permet de calculer l'erreur (la différence entre la sortie du réseau et la sortie que serait
     * celle d'un item identifié (train je pense).
     * @param sumX Taille en X de la donnée
     * @param sumY Taille en Y de la donnée (0= vecteur)
     * @param actual Sortie du reseau constatée
     * @param expected Sortie prédite
     * @return Mesure 'CrossEntropy' de l'erreur
     */
    public static double[] crossentropy(int sumX, int sumY, double [] actual, double [] expected) {
        double[] result = new double[expected.length];
        for (int i = 0; i < expected.length; i++) {
                result[i] =-expected[i]*Math.log(actual[i]);

        }
        double[] res2 = new double[result.length / sumX];
        for(int j=0; j<sumY; j++) {
            for (int i = 0; i < sumX; i++) {
                res2[j]+= result[res2.length/sumY+i];
            }
        }
        return res2;
    }

}
