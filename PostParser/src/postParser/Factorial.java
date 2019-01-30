package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Factorial extends Operator {
	public static Pattern pat = Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})!");
	
	public Factorial(){
		super("Factorial","!","Operator",1,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Factorial(Stack<Term> ops) {
		super("Factorial","!","Operator",1,3,ops);
		this.operands = ops;
	}
	
	public Factorial(Term... ops) {
		super("Factorial","!","Operator",1,3,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute() {
		Double total = 0d;
		for(int i=0;i<reqOps;i++) {
			Term t = operands.pop();
			switch(t.type) {
				case "Operator":
					((Operator)t).operands = (Stack<Term>) this.operands.clone();
					total = ((Operator)t).execute().value;
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						total = ((Number)t).value;
					}
					break;
			}
		}
		return new Number(ExtendedMath.factorial(total));
	}
}
