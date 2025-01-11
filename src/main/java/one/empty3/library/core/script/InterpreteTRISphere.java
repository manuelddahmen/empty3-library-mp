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

package one.empty3.library.core.script;

import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.tribase.TRISphere;

import java.util.ArrayList;

/*__
 * @author DAHMEN Manuel
 *         <p>
 *         dev
 * @date 23-mars-2012
 */
public class InterpreteTRISphere implements Interprete {

    private String repertoire;

    private int pos;

    public InterpreteConstants constant() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getPosition() {
        return pos;
    }

    public Object interprete(String text, int pos) throws InterpreteException {
        Point3D ps = null;

        InterpretesBase ib = new InterpretesBase();
        ArrayList<Integer> pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.LEFTPARENTHESIS);
        pattern.add(ib.BLANK);
        ib.compile(pattern);
        ib.read(text, pos);
        pos = ib.getPosition();

        InterpretePoint3D pp = new InterpretePoint3D();
        ps = (Point3D) pp.interprete(text, pos);
        pos = pp.getPosition();

        pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        ib = new InterpretesBase();
        ib.compile(pattern);
        ib.read(text, pos);
        pos = ib.getPosition();

        pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.DECIMAL);
        pattern.add(ib.BLANK);
        ib = new InterpretesBase();
        ib.compile(pattern);
        Double r = (Double) ib.read(text, pos).get(1);
        pos = ib.getPosition();

        TextureCol tc = null;

        InterpreteTColor tci = new InterpreteTColor();

        tci.setRepertoire(repertoire);

        tc = (TextureCol) tci.interprete(text, pos);

        pos = tci.getPosition();

        pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.RIGHTPARENTHESIS);
        pattern.add(ib.BLANK);
        ib = new InterpretesBase();
        ib.compile(pattern);
        ib.read(text, pos);
        pos = ib.getPosition();

        this.pos = pos;

        TRISphere s = new TRISphere(ps, r);

        s.texture(tc);

        return s;
    }

    public void setConstant(InterpreteConstants c) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setRepertoire(String r) {
        this.repertoire = r;
    }

}
