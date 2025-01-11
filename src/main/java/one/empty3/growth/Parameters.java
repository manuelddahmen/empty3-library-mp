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

package one.empty3.growth;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class Parameters {
    private final HashMap<Integer, HashMap<String, Parameter>> map = new
            HashMap<>();
    private LSystem lSystem;

    public Parameters(LSystem lSystem) {
        this.lSystem = lSystem;
        //addParameter(0, new FunctionalParameter("t", 0., "t+1"));
    }

    public Parameter getParameter(int t, String name) {
        Parameter p;
        Map<String, Parameter> map2 = map.get(t);
        if (map2 != null) {
            p = map2.get(name);
            if (p != null) {
                p.lSystem = this.lSystem;

                return p;
            }
        }
        return null;
    }

    public HashMap<String, Parameter> getParameters(int t) {
        HashMap<String, Parameter> p;
        p = map.get(t);
        return p != null ? p : new HashMap<String, Parameter>();
    }

    public void addParameter(int t, Parameter f) {
        HashMap<String, Parameter> submap = this.map.get(t);
        if (submap == null) {
            this.map.put(t, new HashMap<>());

        }
        this.map.get(t).put(f.getName(), f);
    }

    public String toString() {
        String[] s = new String[]{"\nParameters class\n"};
        map.forEach((integer, stringParameterHashMap) -> stringParameterHashMap.forEach(new BiConsumer<String, Parameter>() {
            @Override
            public void accept(String s1, Parameter parameter) {
                s[0] += "t" + integer + parameter.toString();
            }
        }));
        return s[0];
    }
}
