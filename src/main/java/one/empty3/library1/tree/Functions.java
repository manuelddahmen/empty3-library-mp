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

package one.empty3.library1.tree;

import java.util.ArrayList;
import java.util.List;

public class Functions {
    private static List<String> listOfFunctions = new ArrayList<>();
    private static boolean isInitialized = false;

    public static void init() {
        listOfFunctions.add("sum");
        listOfFunctions.add("product");
        listOfFunctions.add("avg");
        listOfFunctions.add("median");
        listOfFunctions.add("count");
    }


    public static List<String> getListOfFunctions() {
        if (listOfFunctions.size() == 0) {
            init();
            isInitialized = true;
        }
        return listOfFunctions;
    }

    public static double search(String call, Double[] vec) {
        if (!isInitialized) {
            init();
            isInitialized = true;
        }
        return switch (call) {
            case "sum" -> sum(vec);
            case "product" -> product(vec);
            case "avg" -> avg(vec);
            case "median" -> median(vec);
            case "count" -> count(vec);
            default -> 0;
        };
    }

    public static double sum(Double... args) {
        double sum = 0;
        for (int i = 0; i < args.length; i++) {
            sum += args[i];
        }

        return sum;
    }

    public static double product(Double... args) {
        double sum = 1.0;
        for (int i = 0; i < args.length; i++) {
            sum *= args[i];
        }
        return sum;
    }

    public static double avg(Double... args) {
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < args.length; i++) {
            sum += args[i];
            count++;
        }
        return sum / count;
    }

    public static double median(Double... args) {
        double avg = avg(args);
        double sum = 0.0;
        int count = 0;
        for (int i = 0; i < args.length; i++) {
            sum += (args[i] - avg) * (args[i] - avg);
            count++;
        }
        return sum;
    }

    public static double count(Double... args) {
        int count = args.length;
        return count;
    }
}
