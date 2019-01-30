package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Modulo extends Operator {
	public static Pattern pat = Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})\\s?mod\\s?((-)?\\d+(\\.\\d+)?|\\p{L})");
	
	public Modulo(){
		super("Modulo","mod","Operator",2,3);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Modulo(Stack<Term> ops) {
		super("Modulo","mod","Operator",2,3,ops);
		this.operands = ops;
	}
	
	public Modulo(Term... ops) {
		super("Modulo","mod","Operator",2,3,ops);
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
						total = ((Operator)t).execute().value % total;
					}
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						if(i==0) {
							total = ((Number)t).value;
						}else{
							total = ((Number)t).value % total;
						}
					}
					break;
			}
		}
		return new Number(total);
	}
}
