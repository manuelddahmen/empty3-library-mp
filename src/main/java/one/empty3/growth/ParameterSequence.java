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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ParameterSequence {
    List<Parameter> parametersLst = new ArrayList<>();

    public ParameterSequence() {

    }

    public List<Parameter> getParametersLst() {
        return parametersLst;
    }

    public void setParametersLst(List<Parameter> parametersLst) {
        this.parametersLst = parametersLst;
    }

    public Iterator<Parameter> parameterIterator() {
        return parametersLst.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterSequence)) return false;

        ParameterSequence that = (ParameterSequence) o;

        return getParametersLst().equals(that.getParametersLst());
    }

    @Override
    public int hashCode() {
        return getParametersLst().hashCode();
    }
}
