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

public class Symbol extends SymbolSequence{
    private Object value;
    private Class oType;

    private ParameterSequence parameters = new ParameterSequence();

    public Symbol(Character c) {
        super();
        value = c;
        oType = Character.class;

    }
    public Object getValue() {
        return value;
    }

    public Class getoType() {
        return oType;
    }

    public Symbol(Integer i) {
        super();
        value = i;
        oType = Integer.class;
    }

    public Symbol(Double i) {
        super();
        value = i;
        oType = Double.class;
    }

    public ParameterSequence getParameters() {
        return parameters;
    }

    public void setParameters(ParameterSequence parameters) {
        this.parameters = parameters;
    }

    public String toString() {
        return "[" + value + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol)) return false;
        if (!super.equals(o)) return false;

        Symbol symbol = (Symbol) o;

        if (!getValue().equals(symbol.getValue())) return false;
        if (!getoType().equals(symbol.getoType())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getValue().hashCode();
        result = 31 * result + getoType().hashCode();
        result = 31 * result + getParameters().hashCode();
        return result;
    }

}
