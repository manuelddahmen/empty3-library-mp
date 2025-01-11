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

import one.empty3.library.Lumiere;
import one.empty3.libs.*;
import java.util.ArrayList;

public class InterpreteCouleur implements Interprete {

    private String repertoire;
    private int pos;

    @Override
    public InterpreteConstants constant() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPosition() {
        return pos;
    }

    public Object interprete(String text, int pos) throws InterpreteException {
        InterpretesBase ib = new InterpretesBase();
        ArrayList<Integer> pattern = new ArrayList<Integer>();
        pattern.add(ib.BLANK);
        pattern.add(ib.LEFTPARENTHESIS);
        pattern.add(ib.BLANK);
        pattern.add(ib.INTEGER);
        pattern.add(ib.BLANK);
        pattern.add(ib.COMA);
        pattern.add(ib.BLANK);
        pattern.add(ib.INTEGER);
        pattern.add(ib.BLANK);
        pattern.add(ib.COMA);
        pattern.add(ib.BLANK);
        pattern.add(ib.INTEGER);
        pattern.add(ib.BLANK);
        pattern.add(ib.RIGHTPARENTHESIS);
        pattern.add(ib.BLANK);

        ib.compile(pattern);
        ArrayList<Object> os = ib.read(text, pos);
        this.pos = ib.getPosition();

        return new Color(Lumiere.getIntFromFloats((Integer) os.get(3), (Integer) os.get(7),
                (Integer) os.get(11)));
    }

    @Override
    public void setConstant(InterpreteConstants c) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setRepertoire(String r) {
        this.repertoire = r;
    }

}
