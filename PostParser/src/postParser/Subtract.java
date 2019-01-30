package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Subtract extends Operator {
	public static Pattern pat = Pattern.compile("(-)?\\d+(\\.\\d+)?\\s?\\-\\s?(-)?\\d+(\\.\\d+)?");
	
	public Subtract(){
		super("Subtract","-","Operator",2,0);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Subtract(Stack<Term> ops) {
		super("Subtract","-","Operator",2,0,ops);
		this.operands = ops;
	}
	
	public Subtract(Term... ops) {
		super("Subtract","-","Operator",2,0,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute() {
		Double total = 0d;
		for(int i=0;i<reqOps;i++) {
			if(!operands.isEmpty()) {
				Term t = operands.pop();
				switch(t.type) {
					case "Operator":
						((Operator)t).operands = (Stack<Term>) this.operands.clone();
						if(i==0) {
							total -= ((Operator)t).execute().value;
						}else{
							total += ((Operator)t).execute().value;
						}
						this.operands = (Stack<Term>) ((Operator)t).operands.clone();
						break;
					case "Operand":
						if(t.name.equals("Number")){
							if(i==0) {
								total -= ((Number)t).value;
							}else{
								total += ((Number)t).value;
							}
						}
						break;
				}
			}
		}
		return new Number(total);
	}
}
