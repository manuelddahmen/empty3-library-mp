
package one.empty3.library1.tree;

public class Instruction extends InstructionBlock {

    private String type;

    private String name;
    protected ListInstructions.Instruction expression;

    public Instruction(ListInstructions.Instruction expression) {
        super();
        this.expression = expression;
    }

    public Instruction() {
        super();
        this.expression = new ListInstructions.Instruction(0, null, null, "");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String name) {
        this.type = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public ListInstructions.Instruction getExpression() {
        return expression;
    }

    public void setExpression(ListInstructions.Instruction expression) {
        this.instructionList.set(0, new Instruction(expression));
    }

    @Override
    public String toString() {
        return "Instruction{" +
                "type='" + type + '\'' +
                "', name='" + name + '\'' +
                "', expression='" + this.expression.toString() +
                "'}";
    }

    @Override
    public String toLangStringJava(boolean debug) {
        if (expression != null && expression.tokenExpression2 != null && expression instanceof ListInstructions.Instruction) {
            return expression.tokenExpression2.toString();
            //TODO Restore this line. return new TokenExpression2toString().toString(expression.getExpressionTokenString());
        } else {
            return super.toLangStringJava(debug);
        }
    }
}