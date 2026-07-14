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

import one.empty3.library.Point3D;

/*__
 * @author Se7en
 */
public class NeuronePoint extends Neurone {
    public static double dmax = -1.0;

    @Override
    public double fonction(Neurone source) {
        if (this.objet instanceof Point3D &&
                source.objet instanceof Point3D) {
            double dist = Point3D.distance(((Point3D) (source.objet)),
                    ((Point3D) (this.objet)));
            if (dist > dmax)
                dmax = dist;


            objet = ((Point3D) (this.objet))
                    .prodVect(((Point3D) (source.objet))).mult(poids);

            poids = 1.0 / (dist + 1);


        }


        return 0;
    }

}
