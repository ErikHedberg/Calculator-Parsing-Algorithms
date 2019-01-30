package postParser;

import java.util.Stack;

public class Ln extends Operator {

	public Ln(){
		super("Ln","ln","Operator",1,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Ln(Stack<Term> ops) {
		super("Ln","ln","Operator",1,3,ops);
		this.operands = ops;
	}
	
	public Ln(Term... ops) {
		super("Ln","ln","Operator",1,3,ops);
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
		return new Number(Math.log(total));
	}
}
