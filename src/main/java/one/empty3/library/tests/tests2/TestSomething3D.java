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

import one.empty3.library.Representable;
import one.empty3.library.core.testing.TestObjet;

/*__
 * Created by Win on 29-08-18.
 */
public abstract class TestSomething3D<T extends Representable> extends TestObjet {


    @Override
    public void afterRenderFrame() {

    }

    @Override
    public abstract void finit();

    @Override
    public void ginit() {

    }

    @Override
    public void afterRender() {
        System.gc();
    }

    @Override
    public void testScene() throws Exception {

    }
}
