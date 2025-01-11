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

package one.empty3.library1.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ControlledInstructions extends Instruction {
    public String controlExpression;

    public ControlledInstructions(String controlExpression) {
        super(null);
        this.controlExpression = controlExpression;
    }

    public String getControlExpression() {
        return controlExpression;
    }

    public void setControlExpression(String controlExpression) {
        this.controlExpression = controlExpression;
    }

    public List<InstructionBlock> getInstructionsList() {
        return instructionList;
    }

    public void setInstructionsList(ArrayList<InstructionBlock> instructions) {
        this.instructionList = instructions;
    }

    public static class If extends ControlledInstructions {
        public InstructionBlock instructionsIf = new InstructionBlock();
        public InstructionBlock instructionsElse = new InstructionBlock();

        public If(String controlExpression) {
            super(controlExpression);
        }

        @Override
        public String toLangStringJava(boolean debug) {
            return super.toLangStringJava(debug);
        }
    }

    public static class For extends ControlledInstructions {
        public boolean forEachType = false;
        private Instruction loopInstruction = new Instruction();
        private Instruction firstForInstruction = new Instruction();

        public For(String controlExpression) {
            super(controlExpression);
            //this.firstForInstruction.add(initInstruction);
            //this.loopInstruction.add(loopInstruction);
            forEachType = false;
        }

        /***
         * For (Type variableName : expression)
         *     ==================  ===========
         *     loopInstruction     controlExpression
         * @param firstForInstruction
         * @param controlExpression
         */
        public For(Instruction firstForInstruction, String controlExpression) {
            super(controlExpression);
            this.firstForInstruction = firstForInstruction;
            this.controlExpression = controlExpression;
            forEachType = true;
        }

        public boolean isForEachType() {
            return forEachType;
        }

        public void setForEachType(boolean forEachType) {
            this.forEachType = forEachType;
        }

        public Instruction getLoopInstruction() {
            return loopInstruction == null ? new Instruction() : loopInstruction;
        }

        public void setLoopInstruction(Instruction loopInstruction) {
            this.loopInstruction = loopInstruction;
        }

        public Instruction getFirstForInstruction() {
            return firstForInstruction == null ? new Instruction() : firstForInstruction;
        }

        public void setFirstForInstruction(Instruction firstForInstruction) {
            this.firstForInstruction = firstForInstruction;
        }

        @Override
        public String toLangStringJava(boolean debug) {
            return super.toLangStringJava(debug);
        }
    }

    public static class While extends ControlledInstructions {
        public While(String controlExpression) {
            super(controlExpression);
        }

        @Override
        public String toLangStringJava(boolean debug) {
            return super.toLangStringJava(debug);
        }
    }

    public static class DoWhile extends ControlledInstructions {
        public DoWhile(String controlExpression) {
            super(controlExpression);
        }

        @Override
        public String toLangStringJava(boolean debug) {
            return super.toLangStringJava(debug);
        }

    }

    @Override
    public String toLangStringJava(boolean debug) {
        return super.toLangStringJava(debug);
    }

}
