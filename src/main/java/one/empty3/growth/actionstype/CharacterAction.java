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

package one.empty3.growth.actionstype;

import one.empty3.growth.Action;
import one.empty3.growth.Symbol;
import one.empty3.growth.SymbolSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class CharacterAction extends Action {

    private SymbolSequence replaceman = new SymbolSequence();

    public CharacterAction(Character c) {
        this.replaceman = new Symbol(c);
    }

    public CharacterAction(String cs) {
        for (Byte c : cs.getBytes()) {
            replaceman.add(new Symbol((char) c.shortValue()));
        }
    }

    public void replace(SymbolSequence pattern, SymbolSequence toReplacePattern) {
        List<Symbol> stringOfSymbols = new ArrayList<Symbol>();
        SymbolSequence patternParts = pattern.parts();
        SymbolSequence tokenParts = toReplacePattern.parts();
        for (Symbol ss : patternParts.each()) {
            if (ss instanceof Symbol) {
                stringOfSymbols.add((Symbol) ss);

            }

        }
        int id = 0;
        for (int i = 0; i < stringOfSymbols.size(); i++) {
            for (int j = 0; j < tokenParts.size(); j++) {
                if (i + j >= stringOfSymbols.size())

                    break;

                if (stringOfSymbols.get(i + j).equals(tokenParts.get(j)))

                    id++;


            }

            if (id == tokenParts.size()) {

            }
        }
    }
}
