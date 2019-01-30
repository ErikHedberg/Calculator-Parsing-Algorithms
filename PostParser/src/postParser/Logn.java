package postParser;

import java.util.Stack;

public class Logn extends Operator {
	
	public Logn(){
		super("Logn","logn","Operator",2,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Logn(Stack<Term> ops) {
		super("Logn","logn","Operator",2,3,ops);
		this.operands = ops;
	}
	
	public Logn(Term... ops) {
		super("Logn","logn","Operator",2,3,ops);
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
					if(i==0) {
						total = ((Operator)t).execute().value;
					}else{
						total = ExtendedMath.logn(((Operator)t).execute().value, total);
					}
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						if(i==0) {
							total = ((Number)t).value;
						
						}else{
							total = ExtendedMath.logn(((Number)t).value,total);
						}
					}
					break;
			}
		}
		return new Number(total);
	}
}
