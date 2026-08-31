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

package one.empty3.library.tests;

import one.empty3.library.ColorTexture;
import one.empty3.library.Matrix33;
import one.empty3.library.Representable;
import one.empty3.library.core.lighting.Colors;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

public class Membre {
    private final Representable representable;

    public Membre(Representable representable) {
        this.representable = representable;
        representable.texture(new ColorTexture(one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random())));


    }

    public void addMembre(Membre membre, Matrix33 rotationMin, Matrix33 rotationMax) {

    }

    public void addObjectContraint(RealObject realObject, Matrix33 matrixRelative) {

    }

    public Representable getRepresentable() {
        return representable;
    }
}
