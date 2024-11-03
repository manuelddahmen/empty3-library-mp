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


import one.empty3.ECImage;



/*__
 * Created by manue on 07-03-19.
 */
public class Render {
    public static final int TYPE_POINT = 0;
    public static final int TYPE_CURVE = 1;
    public static final int TYPE_SURFACE = 2;
    public static final int POINTS = 4;
    public static final int LINES = 8;
    public static final int FILL = 16;
    protected int objectType = -1;
    protected int renderingType = -1;

    public Render(int objectType, int renderingType) {
        this.objectType = objectType;
        this.renderingType = renderingType;
    }


    public static Render getInstance(int objectType, int renderingType) {
        return new Render (objectType, renderingType);
    }

    public int getObjectType() {
        return objectType;
    }

    public void setObjectType(int objectType) {
        this.objectType = objectType;
    }

    public int getRenderingType() {
        return renderingType;
    }

    public void setRenderingType(int renderingType) {
        this.renderingType = renderingType;
    }
}
