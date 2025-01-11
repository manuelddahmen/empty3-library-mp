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
package one.empty3.library.core.script;

import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.tribase.Plan3D;

import java.util.ArrayList;

/*__
 * @author DAHMEN Manuel
 *         <p>
 *         dev
 * @date 22-mars-2012
 */
public class InterpretePlan3D implements Interprete {

    private int position;
    private String repertoire;

    public InterpreteConstants constant() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPosition() {
        return position;
    }

    public Object interprete(String text, int pos) throws InterpreteException {
        Plan3D plan = new Plan3D();
        InterpretesBase ib = null;
        ArrayList<Integer> pattern;
        InterpretePoint3D pp;
        InterpreteTColor is;
        ib = new InterpretesBase();
        pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.LEFTPARENTHESIS);
        pattern.add(ib.BLANK);
        ib.compile(pattern);
        ib.read(text, pos);
        pos = ib.getPosition();
        pp = new InterpretePoint3D();
        plan.pointOrigine((Point3D) pp.interprete(text, pos));
        pos = pp.getPosition();
        pp = new InterpretePoint3D();
        plan.pointXExtremite((Point3D) pp.interprete(text, pos));
        pos = pp.getPosition();
        pp = new InterpretePoint3D();
        plan.pointYExtremite((Point3D) pp.interprete(text, pos));
        pos = pp.getPosition();
        is = new InterpreteTColor();
        is.setRepertoire(repertoire);

        TextureCol tc = (TextureCol) is.interprete(text, pos);
        plan.texture(tc);
        pos = is.getPosition();

        ib = new InterpretesBase();
        pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.RIGHTPARENTHESIS);
        pattern.add(ib.BLANK);
        ib.compile(pattern);
        ib.read(text, pos);
        pos = ib.getPosition();

        this.position = pos;
        return plan;
    }

    public void setConstant(InterpreteConstants c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRepertoire(String r) {
        this.repertoire = r;
    }
}
