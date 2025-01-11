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

package one.empty3.growth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SymbolSequence {
    private List<SymbolSequence> symbols = new ArrayList<SymbolSequence>();
    /***
     * ssUp here up is the sense from trunk to branches
     */
    SymbolSequence
            ssUp;
    /***
     * ssDown here up is the inverse sense from trunk to branches,
     * So the sense from branches to trunk
     * */
    SymbolSequence
            ssDown;


    public SymbolSequence() {

    }

    public SymbolSequence getSsUp() {
        return ssUp;
    }

    public void setSsUp(SymbolSequence ssUp) {
        this.ssUp = ssUp;
    }

    public SymbolSequence getSsDown() {
        return ssDown;
    }

    public void setSsDown(SymbolSequence ssDown) {
        this.ssDown = ssDown;
    }

    public List<SymbolSequence> upTo(int n) {
        return null;
    }

    public SymbolSequence(SymbolSequence ss) {
        symbols.add(ss);
    }

    public SymbolSequence(Symbol s) {
        symbols.add(s);
    }

    public SymbolSequence(List<Symbol> listOfSymbolsRes) {
        for (int i = 0; i < listOfSymbolsRes.size(); i++)
            symbols.add(listOfSymbolsRes.get(i));
    }

    public void add(Symbol s) {
        symbols.add(s);
    }

    public void add(SymbolSequence ss) {
        symbols.add(ss);
    }

    public List<SymbolSequence> getSymbols() {
        return symbols;
    }

    public SymbolSequence parts() {
        SymbolSequence res = new SymbolSequence();
        for (int index = 0; index < getSymbols().size(); index++) {
            SymbolSequence symbolSequence = getSymbols().get(index);
            if (symbolSequence instanceof Symbol) {
                res.add((Symbol) symbolSequence);
            } else {
                res.addAll(symbolSequence);
            }
            {

            }
        }
        return res;
    }

    public List<Symbol> each() {
        List<Symbol> res = new ArrayList<>();
        for (int index = 0; index < getSymbols().size(); index++) {
            SymbolSequence symbolSequence = getSymbols().get(index);
            if (symbolSequence instanceof Symbol) {
                res.add((Symbol) symbolSequence);
            } else {
                res.addAll(symbolSequence.each());
            }
            {

            }
        }
        return res;

    }

    private Collection<? extends Symbol> toCollection() {
        List<Symbol> list = new ArrayList<>();
        for (SymbolSequence ss : symbols) {
            if (ss instanceof Symbol) {
                list.add((Symbol) ss);

            } else {
                list.addAll(ss.toCollection());
            }
        }
        return list;
    }

    public String toString() {
        String s = "seq_:";
        for (SymbolSequence ss : symbols) {
            s += ss.toString() + " &&";
        }
        return s;
    }

    public int size() {
        return symbols.size();
    }

    public SymbolSequence get(int i) {
        return symbols.get(i);
    }

    public void addAll(SymbolSequence ss) {
        symbols.addAll(ss.symbols);
    }

    public void addAll(int i, SymbolSequence ss) {
        symbols.addAll(i, ss.symbols);
    }

    public boolean equals(Object to) {
        if (to instanceof SymbolSequence) {

            SymbolSequence a = this.parts();
            SymbolSequence b = ((SymbolSequence) to).parts();

            if (a.symbols.size() == b.symbols.size()) {
                int ok = 0;
                for (int i = 0; i < a.size(); i++) {
                    if (a.get(i).equals(b.get(i))) {
                        ok++;
                    }
                }
                if (ok == a.size()) {
                    return true;
                }
            }
        }
        return false;
    }

}