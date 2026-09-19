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

package one.empty3.library.tests.tests2.neurone;

/*__
 * @author Se7en
 */
public abstract class Neurone {
    public double poids = 1;
    public Object objet;

    public abstract double fonction(Neurone source);

    public static double moyenne;

    public static double sortie(Neurone[] neurones) {
        int n = 0;
        double total = 0;
        for (int i = 0; i < neurones.length; i++)
            for (int j = 0; j < i; j++) {
                if (i != j)
                    total += neurones[i].fonction(neurones[j]);

                n++;

            }

        moyenne = total / n;
        return moyenne;
    }
}
