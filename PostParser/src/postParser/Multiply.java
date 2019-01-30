package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Multiply extends Operator {
	public static Pattern pat = Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})\\s?\\*\\s?((-)?\\d+(\\.\\d+)?|\\p{L})");
	
	public Multiply(){
		super("Multiply","*","Operator",2,1);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Multiply(Stack<Term> ops) {
		super("Multiply","*","Operator",2,1,ops);
		this.operands = ops;
	}
	
	public Multiply(Term... ops) {
		super("Multiply","*","Operator",2,1,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute() {
		Double total = 1d;
		for(int i=0;i<reqOps;i++) {
			Term t = operands.pop();
			switch(t.type) {
				case "Operator":
					((Operator)t).operands = (Stack<Term>) this.operands.clone();
					total *= ((Operator)t).execute().value;
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
					total *= ((Number)t).value;
					}
					break;
			}
		}
		return new Number(total);
	}
}
