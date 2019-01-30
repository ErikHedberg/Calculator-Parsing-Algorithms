package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Divide extends Operator {
	public static Pattern pat = Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})\\s?\\/\\s?((-)?\\d+(\\.\\d+)?|\\p{L})");
	
	public Divide(){
		super("Divide","/","Operator",2,1);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Divide(Stack<Term> ops) {
		super("Divide","/","Operator",2,1,ops);
		this.operands = ops;
	}
	
	public Divide(Term... ops) {
		super("Divide","/","Operator",2,1,ops);
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
					if(i==0) {
						total /= ((Operator)t).execute().value;
					}else{
						total *= ((Operator)t).execute().value;
					}
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						if(i==0) {
							total /= ((Number)t).value;
						}else{
							total *= ((Number)t).value;
						}
					}
					break;
			}
		}
		return new Number(total);
	}
}
