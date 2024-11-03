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

import java.util.Objects;

/*__
 * @author Se7en
 */
public class Resolution extends one.empty3.library.core.testing.Resolution {
    public static final Resolution K4RESOLUTION = new Resolution(1920 * 2, 1080 * 2).makeFinal();
    public static final Resolution HD720RESOLUTION = new Resolution(1600, 720).makeFinal();
    public static final Resolution HD1080RESOLUTION = new Resolution(1920, 1080).makeFinal();
    public static final Resolution XVGARESOLUTION = new Resolution(640, 480).makeFinal();
    private int nbits = 32;
    private boolean aFinal = false;

    public Resolution(int xv, int yv) {
        super(xv, yv);
        this.x = xv;
        this.y = yv;

    }

    public void x(int v) {
        if (!isFinal())
            this.x = v;
    }

    public void y(int v) {
        if (!isFinal())
            this.y = v;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public boolean isFinal() {
        return aFinal;
    }

    protected Resolution makeFinal() {
        aFinal = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resolution that = (Resolution) o;
        if (x() != that.x() || y() != that.y())
            return false;
        return nbits == that.nbits && aFinal == that.aFinal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nbits, aFinal);
    }
}
