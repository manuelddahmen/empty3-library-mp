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

package one.empty3.library.core.physics;


import one.empty3.*;
import one.empty3.library.*;

import one.empty3.libs.*;

import one.empty3.libs.Image;
import one.empty3.libs.Image;


public class Ball {


    public String nom;
    public Color color;
    public Point3D vitesse;
    public Point3D forceAttaction;
    public Point3D forceRepulsion;
    public double masse;
    public double energie;
    public Point3D position;
    public Image texture;
    public Matrix33 rotation;

    public Ball(String nom, Color color, Point3D vitesse, double masse,
                double energie, Point3D position, Image texture,
                Matrix33 rotation) {
        super();
        this.nom = nom;
        this.color = color;
        this.vitesse = vitesse;
        this.masse = masse;
        this.energie = energie;
        this.position = position;
        this.texture = texture;
        this.rotation = rotation;
    }
}
