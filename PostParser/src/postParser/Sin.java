package postParser;

import java.util.Stack;

public class Sin extends Trig {

	public Sin(){
		super("Sin","sin","Trig",1,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Sin(Stack<Term> ops) {
		super("Sin","sin","Trig",1,3,ops);
		this.operands = ops;
	}
	
	public Sin(Term... ops) {
		super("Sin","sin","Trig",1,3,ops);
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
			return new Number(Math.sin(total));
		}else{
			switch(flag) {
				case "a":
					return new Number(Math.asin(total));
				case "h":
					return new Number(Math.sinh(total));
				case "ah":
					return new Number(ExtendedMath.asinh(total));
				default:
					return new Number(Math.asin(total));
			}
		}
	}
}
