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

package one.empty3.library.core.lighting;
import java.util.*;
import java.awt.Color;
public class ColorDist {
        public Color color;
        public double dist;
        /*
        public int compareTo(Object o) {
            if (o instanceof ColorDist) {
                ColorDist cd = (ColorDist) o;
                return dist<cd.dist?-1:(dist==cd.dist?0:1);
           } else 
                return 0;//throw??
        }
        */
        public static void sort(ColorDist[] cd) {
            Arrays.sort(cd, new SortbyDist());
        }
    }
class SortbyDist implements Comparator<ColorDist> 
{ 

    // Used for sorting in ascending order of 

    // roll number 

    public int compare(ColorDist a, ColorDist b) 

    {

        int compare1 = Double.compare(a.dist, b.dist);
        int compareR = Double.compare(b.dist, a.dist);
        if(Math.signum(compare1)==-Math.signum(compareR))
            return (int) Math.signum(compare1);
        else
            throw new NullPointerException("Error comparator ColorDist c!=-c");



    } 
} 
