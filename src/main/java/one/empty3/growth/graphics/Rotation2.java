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

package one.empty3.growth.graphics;

import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;

/*
 *  Rotation d'un angle quelconque autour d'un axe
 Bonjour,

 Je galère pour trouver l'agorithme correct pour la "Rotation3D"

 J'ai trouvé ceci ce matin
 https://www.developpez.net/forums/d8...t-3d-distance/

 Ce qui donne les coordonnées du projeté P du point sur l'axe. J'ai essayé ça fonctionne.

 Maintenant pour la rotation je crois, et si on veut faire tourner beaucoup de points il faut calculer la matrice de rotation en fonction de XP (droite perpendiculaire à l'axe AB.

 Puis faire M.X => X' (point "rotationné")

 Voilà pour calculer la matrice comment faire?

 On a le vecteur directeur de l'axe disons V ou U1
 On a P P0 si on le divise par la norme |PP0| on obtient un deuxième vecteur unitaire. U2
 Pour le troisième on fait U1^U2 = U3.

 Mais là comment les mettre sous forme matricielle, en lignes, en colonnes?

 A l fin M*X = X'
 Puis finalement repositionner le point dans l'espace réel: P+X'

 Non je crois que la fin du raisonnement est fausse. mais à partir de P, U2, et U3, on peut faire une matrice de rotation non? Rotation d'un angle quelconque autour d'un axe
 Bonjour,

 Je galère pour trouver l'agorithme correct pour la "Rotation3D"

 J'ai trouvé ceci ce matin
 https://www.developpez.net/forums/d8...t-3d-distance/

 Ce qui donne les coordonnées du projeté P du point sur l'axe. J'ai essayé ça fonctionne.

 Maintenant pour la rotation je crois, et si on veut faire tourner beaucoup de points il faut calculer la matrice de rotation en fonction de XP (droite perpendiculaire à l'axe AB.

 Puis faire M.X => X' (point "rotationné")

 Voilà pour calculer la matrice comment faire?

 On a le vecteur directeur de l'axe disons V ou U1
 On a P P0 si on le divise par la norme |PP0| on obtient un deuxième vecteur unitaire. U2
 Pour le troisième on fait U1^U2 = U3.

 Mais là comment les mettre sous forme matricielle, en lignes, en colonnes?

 A l fin M*X = X'
 Puis finalement repositionner le point dans l'espace réel: P+X'

 Non je crois que la fin du raisonnement est fausse. mais à partir de P, U2, et U3, on peut faire une matrice de rotation non?
 */
public class Rotation2 {
    private static double Epsilon = 0.000001;

    /**
     * Methode qui calcule la projection orthogonale du point P sur une droite D representée par un point X et un vecteur V (P = X + kV).
     * ATTENTION : cette methode renvoit le coefficient k.
     *
     * @param X Un point de la droite D.
     * @param V Le vecteur directeur de la droite D.
     * @param P Le point dont on souhaite connaître le projeté sur la droite D.
     * @return Le coefficient de k de P = X + kV.
     */
    public Point3D projection(Point3D X, Point3D V, Point3D P) {
        int Size = 3;
        double num = 0.0, den = 0.0;

        for (int i = 0; i < Size; i++) {
            num += V.get(i) * (P.get(i) - X.get(i));
            den += Math.pow(V.get(i), 2.0);
        }

        if (Math.abs(den) < Epsilon)
            throw new ArithmeticException("Denominator equal to zero => Vector V is a vector null.");
        Point3D projete = X.plus(V.mult(num / den));

        for (int i = 0; i < 3; i++) {
            if (projete.get(i) == Double.NaN) {
                projete = P;
            }
        }

        return projete;
    }


    /*
     *
     * @param X Point à faire tourner
     * @param A Point de la droite d (premier)
     * @param B Point de la droite d (deuxième)
     * @param angle Angle de rotation
     * @return résultat
     */
    public Point3D rotation(Point3D X, Point3D A, Point3D B, double angle) {
        Point3D V = B.moins(A);
        Point3D P = projection(A, V, X);
        Point3D u1 = V.norme1();
        Point3D u2 = X.moins(P).norme1();
        Point3D u3 = u1.prodVect(u2);

        double distance = P.moins(X).norme();

        Matrix33 rotationPlanPperdAB = new Matrix33(new double[]
                {
                        Math.cos(angle), Math.sin(angle), 0d,
                        -Math.sin(angle), Math.cos(angle), 0d,
                        0d, 0d, 1d
                });
        Point3D pU2U3 = rotationPlanPperdAB.mult(new Point3D(distance, 0., 0d));

        Point3D res = u2.mult(pU2U3.getX()).plus(u3.mult(pU2U3.getY()));


        return P.plus(res);
    }

    public void ifIsNan(Point3D atester, Point3D defautValue) {

    }

}
