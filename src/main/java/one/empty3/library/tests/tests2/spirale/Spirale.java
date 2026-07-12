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

/*__
 Global license :

 Microsoft Public Licence

 author Manuel Dahmen <manuel.dahmen@gmx.com>
 ***/


package one.empty3.library.tests.tests2.spirale;

import one.empty3.library.core.testing.TestObjetSub;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class Spirale extends TestObjetSub {
    public static void main(String[] args) {
        Spirale s = new Spirale();

        s.setResx(2000);
        s.setResy(1500);

        s.setMaxFrames(1500);

        s.setGenerate(GENERATE_IMAGE);

        new Thread(s).start();

    }

    @Override
    public void ginit() {
    }

    @Override
    public void testScene() throws Exception {
    }

}
