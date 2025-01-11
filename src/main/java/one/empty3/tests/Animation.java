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

package one.empty3.tests;
import one.empty3.library.Point3D;
import one.empty3.library.Representable;
import one.empty3.library.StructureMatrix;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
public class Animation extends Representable {
    private final HashMap<Class, MoveCollection> animations = new HashMap<>();

    public Animation(Class<? extends Representable> anime, MoveCollection moveCollection) {
        this.animations.put(anime, moveCollection);
    }

    public Representable anime(Representable item, double tAnim) {
        MoveCollection moves = animations.get(item.getClass());
        if(moves!=null)
            for (Move move : moves.getMoves()) {
                if (move.getO() instanceof Representable) {
                    Path path = ((Representable) move.getO()).getPath(move.getProperty());
                    if(path!=null ) {
                        Point3D plus = Point3D.O0;
                        if (path.getPathElemType() == Representable.PATH_ELEM_STRUCTURE_MATRIX) {
                            if(path.getDeclaredProperty()==null ) {
                                Logger.getAnonymousLogger().log(Level.INFO, "Error structureMatrix==null");
                                continue;
                            }
                            if (path.getDeclaredProperty().getDim()==0) {
                                if(move.getMoved() instanceof Point3D)  {
                                    plus = ((Point3D) (move.getMoved())).plus( // MOVE TYPE
                                            (Point3D) ((StructureMatrix<Object>) path.getDeclaredProperty()).getElem()
                                    );
                                }
                                Point3D moved = (Point3D) (move.getMoved());
                                ((StructureMatrix<Object>) path.getDeclaredProperty()).setElem(plus);
                                plus.texture(moved.texture());
                            }
                            if (path.getDeclaredProperty().getDim()==1) {
                                if(move.getMoved() instanceof Point3D) {
                                    plus = ((Point3D) (move.getMoved())).plus( // MOVE TYPE
                                            (Point3D) ((StructureMatrix<Object>) path.getDeclaredProperty()).getElem(path.getIndexI())
                                    );
                                    ((StructureMatrix<Object>) path.getDeclaredProperty()).setElem(plus);
                                    Point3D moved = (Point3D) (move.getMoved());
                                    plus.texture(moved.texture());
                                }

                                ((StructureMatrix<Object>) path.getDeclaredProperty()).setElem(plus, path.getIndexI());
                            }
                            if (path.getDeclaredProperty().getDim()==2) {
                                if(move.getMoved() instanceof Point3D) {
                                    plus = ((Point3D) (move.getMoved())).plus( // MOVE TYPE
                                            (Point3D) ((StructureMatrix<Object>) path.getDeclaredProperty()).getElem(path.getIndexI(), path.getIndexJ())
                                    );
                                    Point3D moved = (Point3D) (move.getMoved());
                                    plus.texture(moved.texture());
                                }
                                ((StructureMatrix<Object>) path.getDeclaredProperty()).setElem(plus, path.getIndexI()
                                        , path.getIndexJ());
                            }
                        }
                        if (path.getPathElemType() == Representable.PATH_ELEM_DOUBLE_VALUES) {

                        }
                        if (path.getPathElemType() == Representable.PATH_ELEM_REPRESENTABLE) {

                        }
                    } else {
                        Logger.getAnonymousLogger().log(Level.INFO, "catched error... Cannot invoke \"one.empty3.tests.Path.getPathElemType()\" because \"path\" is null");
                    }
                }
            }
        else
            Logger.getAnonymousLogger().log(Level.INFO, "Animation anime error moves == null");
        return item;
    }
}

