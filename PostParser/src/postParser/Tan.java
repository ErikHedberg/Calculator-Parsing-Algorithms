package postParser;

import java.util.Stack;

public class Tan extends Trig {

	public Tan(){
		super("Tan","tan","Operator",1,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Tan(Stack<Term> ops) {
		super("Tan","tan","Operator",1,3,ops);
		this.operands = ops;
	}
	
	public Tan(Term... ops) {
		super("Tan","tan","Operator",1,3,ops);
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
		if(flag.isEmpty()) { 
			return new Number(Math.tan(total));
		}else{
			switch(flag) {
				case "a":
					return new Number(Math.atan(total));
				case "h":
					return new Number(Math.tanh(total));
				case "ah":
					return new Number(ExtendedMath.atanh(total));
				default:
					return new Number(Math.atan(total));
			}
		}
	}
}
