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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

/*
package one.empty3.library.core.physics;

import one.empty3.library.Point3D;

import one.empty3.libs.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ListingTableau {
    private Move f;

    public static void main(String[] args) {
        OutputStream out = null;
        try {
            out = new FileOutputStream("listing-positions-1.txt");
        } catch (FileNotFoundException e) {
            System.exit(1);
        }

        Move f = new Move();


        ArrayList billes = billeConfig();

        f.configurer(billes);

        int frameMax = Integer.parseInt(args[0]);

        int frame = 1;
        while (frame < frameMax) {
            for (int i = 0; i < billes.size(); i++) {
                try {
                    out.write((
                            "" + frame
                                    + "\t" + frameMax +
                                    "\t" + i + "\t"
                                    + billes[i].position.get(0) + "\t"
                                    + billes[i].position.get(1) + "\t"
                                    + billes[i].position.get(2) + "\t"
                                    + "\n"
                    ).getBytes());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            f.calculer();

            Logger.getAnonymousLogger().log(Level.INFO, frame);

            frame++;

        }
    }

    private static ArrayList<Bille> billeConfig() {
        int X, Y, Z;
        X = Y = Z = 5;
        ArrayList<Bille> billes = new ArrayList<Bille>(X * Y * Z);
        for(int i=0; i<X*Y*Z; i++)
            billes.add(new Bille());
        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                for (int k = 0; k < Z; k++) {

                    billes.get(k * Y * X + j * X + i).position = new Point3D(
                            (i - X / 2) / 1f, (j - Y / 2) / 1f,
                            (k - Z / 2) / 1f);
                    billes.get(k * Y * X + j * X + i).vitesse = new Point3D(
                            (i - X / 2) / 1f, (j - Y / 2) / 1f,
                            (k - Z / 2) / 1f);
                    billes.get(k * Y * X + j * X + i).color = new Color(1.0f * i
                            / X, 1.0f * j / Y, 1.0f * k / Z);
                    billes.get(k * Y * X + j * X + i).masse = 1;
                    billes.get(k * Y * X + j * X + i).attraction = 1;
                    billes.get(k * Y * X + j * X + i).repulsion = 0.1;
                    billes.get(k * Y * X + j * X + i).amortissement = 0.2;
                }
            }
        }
        return billes;
    }

    public Move getForce() {
        return this.f;
    }

    public void setForce(Move f) {
        this.f = f;
    }
}*/
