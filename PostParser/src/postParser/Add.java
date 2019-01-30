package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Add extends Operator {
	public static Pattern pat = Pattern.compile("(-)?\\d+(\\.\\d+)?\\s?\\+\\s?(-)?\\d+(\\.\\d+)?");
	
	public Add(){
		super("Add","+","Operator",2,0);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Add(Stack<Term> ops) {
		super("Add","+","Operator",2,0,ops);
		this.operands = ops;
	}
	
	public Add(Term... ops) {
		super("Add","+","Operator",2,0,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute(){
		Double total = 0d;
		for(int i=0;i<reqOps;i++) {
			Term t = operands.pop();
			switch(t.type) {
				case "Operator":
					((Operator)t).operands = (Stack<Term>) this.operands.clone();
					total += ((Operator)t).execute().value;
					this.operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						total += ((Number)t).value;
					}
					break;
			}
		}
		return new Number(total);
	}
}
