package postParser;

import java.util.Stack;

public class Negate extends Operator {

	public Negate(){
		super("Negate","<","Operator",1,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Negate(Stack<Term> ops) {
		super("Negate","<","Operator",1,3,ops);
		this.operands = ops;
	}
	
	public Negate(Term... ops) {
		super("Negate","<","Operator",1,3,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute() {
		Double total = 0d;
		Term t = operands.pop();
		switch(t.type) {
			case "Operator":
				((Operator)t).operands = (Stack<Term>) this.operands.clone();
				total -= ((Operator)t).execute().value;
				this.operands = (Stack<Term>) ((Operator)t).operands.clone();
				break;
			case "Operand":
				if(t.name.equals("Number")){
					total -= ((Number)t).value;
				}
				break;
		}
		return new Number(total);
	}
}
