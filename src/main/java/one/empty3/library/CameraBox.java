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

/*

 Vous êtes libre de :

 */
package one.empty3.library;


/*__
 * @author Manuel DAHMEN
 * @date
 */
public class CameraBox extends Representable {

    public static final int PERSPECTIVE_ISOMETRIQUE = 1;
    public static final int PERSPECTIVE_POINTDEFUITE = 1;
    protected StructureMatrix<Double> angleX = new StructureMatrix<>(0, Double.class);
    protected StructureMatrix<Double> angleY = new StructureMatrix<>(0, Double.class);
    private int type = PERSPECTIVE_ISOMETRIQUE;

    public CameraBox() {
        //System.err.println("New camera box");
        angleX.setElem(Math.PI / 4);
        angleY.setElem(Math.PI / 4);
    }

    public Double getAngleX() {
        return angleX.getElem();
    }

    public void setAngleX(Double angleX) {
        this.angleX.setElem(angleX);
    }

    @Deprecated
    public void angleXr(double angleX, double ratioXY) {
        this.angleX.setElem(angleX);
        this.angleY.setElem(angleX / ratioXY);
    }

    public void angleXY(int width, int height, double angleRadians, Axis refAxis) {
        this.angleX.setElem((refAxis.equals(Axis.X)) ? angleRadians : angleRadians * width / (double) height);
        this.angleY.setElem(refAxis.equals(Axis.Y) ? angleRadians : angleRadians * height / (double) width);
    }

    public Double getAngleY() {
        return angleY.getElem();
    }

    public void setAngleY(Double angleY) {
        this.angleY.setElem(angleY);
    }

    public void setAngleYr(double angleY, double ratioXY) {
        this.angleY .setElem( angleY);
        this.angleX .setElem( angleY * ratioXY);
    }

    public void perspectiveIsometrique() {
        this.type = PERSPECTIVE_ISOMETRIQUE;
    }

    public void perspectivePointDeFuite() {
        this.type = PERSPECTIVE_POINTDEFUITE;
    }

    public int type() {
        return type;
    }

    public void viserObjet(Representable r) {
        throw new UnsupportedOperationException("Non supportée");
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("angleX/angle horizontal caméra", angleX);
        getDeclaredDataStructure().put("angleY/angle vertical caméra", angleY);

    }
    public void ratioHorizontalAngle(int dimx, int dimy) {
        this.angleX.setElem(1.0*dimx/dimy*angleY.getElem());
    }
}