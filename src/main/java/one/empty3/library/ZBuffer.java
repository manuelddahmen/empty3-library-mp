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
 * *
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library;


import one.empty3.ECImage;

import one.empty3.ECImage;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.nurbs.ParametricVolume;

import java.awt.*;
import java.io.File;

/*__
 * *
 * Rendu graphique
 *
 * @author Manuel Dahmen
 */
public interface ZBuffer {

    /*__
     * Retourne la caméra de la scène virtuelle
     *
     * @return camera used for display
     */
    Camera camera();

    void couleurDeFond(ITexture couleurFond);

    public void copyResourceFiles(File destDirectory);

    /*__
     * Dessine la scène complète
     */
    void draw();

    /*__
     * Ajoute un objet à l'image... (le dessine si tout est bien initialisé
     *
     * @param r         Objet à peindre
     */
    void draw(Representable r);

    int getColorAt(Point p);

    /*__
     * *
     * Instancie un zbuffer. Si l'instance demandée (coordArr, y) existe déjà, elle est
     * retournée.
     *
     * @param x largeur (resx)
     * @param y hauteur (resy)
     * @return instance
     */
    ZBuffer getInstance(int x, int y);


    /*__
     * Retourne l'image, après dessin par draw
     *
     * @return image
     */
    ECImage image();

    ECImage imageInvX();

    /*__
     * Verrou
     *
     * @return Verrou?
     */
    boolean isLocked();

    void isobox(boolean isBox);


    /*__
     * @param p1 premier point
     * @param p2 second point
     * @param t  couleur de la line
     */
    void line(Point3D p1, Point3D p2, ITexture t);

    /*__
     * Verouille le zbuffer pendant les calculs.
     *
     * @return false si le zbuffer a été préalablement verrouillé. true si
     * verrouillage par appel de cette méthode.
     */
    boolean lock();


    /*__
     * Dessine un point
     *
     * @param p point
     * @param c couleur
     */
    void plotPoint(Point3D p, Color c);

    /*__
     * *
     * Résolution X
     *
     * @return résolution coordArr
     */
    int resX();

    /*__
     * Résolution Y
     *
     * @return résolution y
     */
    int resY();

    /*__
     * Retourne la scène en cours de traitement
     *
     * @return scene
     */
    Scene scene();

    /*__
     * Assigne une nouvelle scène
     *
     * @param s scene
     */
    void scene(Scene s);

    /*__
     * Passe une nouvelle image
     */
    void next();

    /*__
     * Teste le point p
     *
     * @param point3D point
     */
    boolean testDeep(Point3D point3D);

    /*__
     * Dessine un point
     *
     * @param p point
     * @param c couleur
     */
    boolean testDeep(Point3D p, Color c);

    boolean testDeep(Point3D p, int c);

    void tracerLumineux();

    /*__
     * Déverrouille le zbuffer
     *
     * @return true si déverrouillage. False si non-verrouillé
     */
    boolean unlock();

    /*__
     * Ajuste le facteur de zoom (cadre) en 3D isométrique
     *
     * @param z
     */
    void zoom(float z);

    public ITexture backgroundTexture();

    public void backgroundTexture(ITexture iTexture);

    int largeur();

    int hauteur();

    public boolean checkScreen(Point p1);

    public void setDimension(int width, int height);

    public Point3D clickAt(double x, double y);

    public void idzpp();

    public int idz();

    public void drawElementVolume(Representable representable, ParametricVolume volume);


    int getDisplayType();

    double maxDistance(Point... points);

    boolean testDeep(Point3D pFinal, ITexture texture, double u, double v, ParametricSurface n);

    public int la();

    public int ha();

    public ZBufferImpl.Box2D getCube();

}
