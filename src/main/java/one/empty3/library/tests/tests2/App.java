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

package one.empty3.library.tests.tests2;

import one.empty3.library.core.testing.TestObjetSub;

/*__
 * Manuel Dahmen
 */
public abstract class App {
    public abstract void initialize(TestObjetSub testObjetSub);

    public static void main(String[] args) {

        if (args.length >= 2) {
            String classname = args[0];
            String appClassname = args[1];
            try {
                Class aClass = Class.forName(classname);
                Object instance = aClass.newInstance();
                TestObjetSub testObjetSub = ((TestObjetSub) instance);
                Class appClass = Class.forName(appClassname);
                Object appInstance = appClass.newInstance();
                ((App) appInstance).initialize(testObjetSub);
                testObjetSub.run();
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
