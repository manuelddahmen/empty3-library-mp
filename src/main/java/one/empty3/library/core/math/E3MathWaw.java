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

// Repère dans une courbe // associé à la courbe.
// Change en fonction de l'évolution de la courbe.
// 
package one.empty3.library.core.math;

import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.ParametricCurve;

import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * @author Se7en
 */
public class E3MathWaw {

    static double approxTangente = 0.000001;
    static double approxNormale = 0.000001;

    /*__
     * @param c            chemin à parcourir
     * @param emplacementT Valeur de la variable de la courbe paramétrique.
     * @return Les 3 segments de droites (vecteurs) qui composent le repère. 0:
     * l'origine du système d'axe dans le système où se trouve la courbe.
     * Fonction pour calculer le repère dans une courbe. Le repère est construit
     * avec la tangente et deux droites perpenducaires à la droite tangente.
     * (Normales)
     * <p>
     * Calcul par approximation simple au 1er degré. Une autre méthode permettra
     * d'autres approximations.
     * <p>
     * <p>
     * <p>
     * Choix des normales.
     */
    public Point3D[] calculRepere(ParametricCurve c, double emplacementT) {
        Point3D[] pts = new Point3D[4];

        pts[0] = calculerPointCourbe(c, emplacementT);

        pts[1] = tangente(c, emplacementT, 1).norme1();

        pts[2] = normale1(c, emplacementT, 1).norme1();

        pts[3] = pts[0].prodVect(pts[1]).norme1();

        /*__
         * Contrairement à ce que j'ai écrit sur Empty3, il semble que j'aie
         * fait une erreur de vocabulaire: une courbe ne possède pas deux
         * normale mais un plan normal en chaque point (pour une courbe 2 fois
         * continu- ment dérivable) et on veut que les 2 autres axes soient dans
         * ce plan. Camera FPS-Like.
         */
        pts[2] = normale1(c, emplacementT, 1);

        return pts;
    }

    public Point3D calculerPointCourbe(ParametricCurve c, double t) {
        return c.calculerPoint3D(t);
    }

    public Point3D tangente(ParametricCurve c, double t, int degre) {
        if (degre > 1) {
            Logger.getAnonymousLogger().log(Level.INFO, "Degré est supérieur à 1: pas d'implémentation actuelle");
        }
        Point3D p;
        /*try {*/

        // p = c.calculerVitesse3D(t);


        /*} catch (Exception ex) {*/
        Point3D p1 = c.calculerPoint3D(t);
        Point3D p2 = c.calculerPoint3D(t + approxTangente);

        p = (p2.moins(p1));
        /*}*/

        return p;
    }


    public Point3D normale1(ParametricCurve c, double t, int degre) {
        // Calcul de la direction générale de la courbe:
        /*for(double  a=0; a<Math.log((long)(1/approxTangente)); a++)
         {
         Point3D p = c.calculerPoint3D(t);
         }*/

        if (degre > 1) {
            Logger.getAnonymousLogger().log(Level.INFO, "Degré est supérieur à 1: pas d'implémentation actuelle, ici, maintenant");
        }
        Point3D p;
        try {

            p = c.calculerVitesse3D(t);

            return p;

        } catch (Exception ex) {
            Point3D p1 = tangente(c, t, degre);
            Point3D p2 = c.calculerPoint3D(t + approxNormale);

            p = (p2.moins(p1));
        }

        return p;

    }

    public class Repere {

        private final Matrix33 m33;
        private final Matrix33 m33T;
        Point3D[] axes;


        /*__
         * @param axes Axes avec le point d'origine en position 0 de tableau.
         */
        public Repere(Point3D[] axes) {

            Point3D[] axes2 = new Point3D[]{axes[1], axes[2], axes[3]};

            m33 = new Matrix33(axes2);

            m33T = m33.tild();

        }

        public Point3D transform(Point3D p0Axe0) {
            Matrix33 p = new Matrix33(new Double[]{p0Axe0.get(0), 0d, 0d, 0d, p0Axe0.get(1), 0d, 0d, 0d, p0Axe0.get(2)});

            Matrix33 res = (m33T.mult(p)).mult(m33);

            return new Point3D(res.get(0, 0), res.get(1, 1), res.get(2, 2));
        }
    }
}
