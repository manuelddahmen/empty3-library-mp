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

package one.empty3.gui;

import one.empty3.library.MatrixPropertiesObject;

/**
 * Created by manue on 06-10-19.
 */
public class ClassesNotEqualException extends Exception {
    public ClassesNotEqualException(Class aClass1, Class aClass) {
        System.err.println(this.getClass().getName());
        System.err.println("Class A (element class attribute) : " + (aClass1==null?"NULL": aClass1.getCanonicalName()));
        System.err.println("Class B (MatrixPropertiesObject)  : " +( aClass==null?"NULL":aClass.getCanonicalName()));
    }
}
