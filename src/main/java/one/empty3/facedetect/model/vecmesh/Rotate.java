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

package one.empty3.facedetect.model.vecmesh;

import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;
import one.empty3.library.Representable;
import one.empty3.library.ZBufferImpl;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Rotate {
    private Representable representable;
    private final JPanel panel;
    private Matrix33 matrixObject;
    private boolean mouseDown = false;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private Matrix33 newRotationMatrix;
    private ZBufferImpl zBuffer;


    public Matrix33 getRotationMatrix() {
        this.matrixObject = new Matrix33(new Point3D[]{representable.getVectX(), representable.getVectY(),
                representable.getVectZ()});
        return matrixObject;
    }

    public void setRotationMatrix(Matrix33 rotationMatrix) {
        this.matrixObject = rotationMatrix;
        updateRepresentableCoordinates();
    }


    public Rotate(Representable r, JPanel jPanel) {
        this.matrixObject = new Matrix33(new Point3D[]{r.getVectX(), r.getVectY(), r.getVectZ()});
        this.representable = r;
        this.panel = jPanel;
        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                handleMouseDown(e);
                handleMouseMove(e);
                //zBuffer.setDisplayType(ZBufferImpl.SURFACE_DISPLAY_LINES);

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseUp(e);
                updateRepresentableCoordinates();

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
        MouseMotionListener mouseMotionListener = new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseMove(e);
                Point3D[] multiply = newRotationMatrix.mult(matrixObject).getColVectors();
                representable.setVectX(multiply[0]);
                representable.setVectY(multiply[1]);
                representable.setVectZ(multiply[2]);
                matrixObject = new Matrix33(multiply);
                System.out.print("=>");
                updateRepresentableCoordinates();
            }

            @Override
            public void mouseMoved(MouseEvent e) {


            }
        };
        jPanel.addMouseListener(mouseListener);
        jPanel.addMouseMotionListener(mouseMotionListener);
    }

    void handleMouseDown(MouseEvent event) {
        mouseDown = true;
        lastMouseX = event.getX();
        lastMouseY = event.getY();
    }

    void handleMouseUp(MouseEvent event) {
        mouseDown = false;
    }

    void handleMouseMove(MouseEvent event) {
        if (!mouseDown) {
            return;
        }
        var newX = event.getX();
        var newY = event.getY();

        var deltaX = newX - lastMouseX;
        newRotationMatrix = new Matrix33();
        Matrix33 newRotationMatrixY = Matrix33.rotationY(2 * Math.PI * deltaX / 360);

        var deltaY = newY - lastMouseY;
        Matrix33 newRotationMatrixX = Matrix33.rotationX(2 * Math.PI * deltaY / 360);

        newRotationMatrix = newRotationMatrixX.mult(newRotationMatrixY);

        lastMouseX = newX;
        lastMouseY = newY;
    }

    public JPanel getPanel() {
        return panel;
    }

    public Representable getRepresentable() {
        return representable;
    }

    public void setRepresentable(Representable parametricSurface) {
        this.representable = parametricSurface;
    }

    public void updateRepresentableCoordinates() {
        Point3D[] rowVectors = matrixObject.getRowVectors();
        representable.setVectX(rowVectors[0]);
        representable.setVectY(rowVectors[1]);
        representable.setVectZ(rowVectors[2]);
    }

    public void setZBuffer(ZBufferImpl zBuffer) {
        this.zBuffer = zBuffer;
    }
}
