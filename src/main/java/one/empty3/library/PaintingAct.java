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

package one.empty3.library;


import one.empty3.libs.Image;

/*__
 * Created by manue on 08-10-15.
 */
public abstract class PaintingAct {
    private ZBuffer ZBuffer;
    private Scene scene;
    private Representable objet;

    protected ZBuffer z() {

        return ZBuffer;
    }

    protected Scene s() {
        return scene;
    }

    protected Representable objet() {
        return objet;
    }

    public abstract void paint();

    protected Representable getObjet() {
        return objet;
    }

    public void setObjet(Representable objet) {
        this.objet = objet;
    }

    protected Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    protected ZBuffer getZBuffer() {
        return ZBuffer;
    }

    public void setZBuffer(ZBuffer zBuffer) {
        this.ZBuffer = zBuffer;
    }

}
