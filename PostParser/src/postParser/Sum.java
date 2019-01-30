package postParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.regex.Pattern;

public class Sum extends Operator {
	public static Pattern pat = Pattern.compile("(?>sum|\\∑)(\\(|\\[)(\\p{L})\\s?\\=\\s?(.*)\\,(.*)\\,(.*)(\\)|\\])"); //2: index variable 3: lower bound 4: upper bound 5: iterating function 
	public static Pattern varPat = Pattern.compile("(\\p{L})\\s?\\=\\s?(.*)\\s?");
	
	public Sum(){
		super("Sum","∑","Operator",4,2);
		operands = new Stack<Term>();
		super.operands = new Stack<Term>();
	}
	
	public Sum(Stack<Term> ops) {
		super("Sum","∑","Operator",4,2,ops);
		this.operands = ops;
	}
	
	public Sum(Term... ops) {
		super("Sum","∑","Operator",4,2,ops);
		for(Term op : ops) {
			this.operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute() {
		Function function;
		Term f = operands.pop();
		if(f.name=="Function") {
			function = (Function)f;
		}else{
			StringBuilder func = new StringBuilder();
			Term t = operands.pop();
			int i = 0;
			while(operands.size() > 2) {
				func.insert(0,t.toString());
				t = operands.pop();
				i = t.toString().length();
			}
			func.insert(func.length()-i, f.toString());
			operands.push(t);
			function = new Function(func.toString());
		}
		Number lower = (Number)operands.pop();
		Number upper = (Number)operands.pop();
		String varName = ((Variable)operands.pop()).toString();
		try {
			return new Number(ExtendedMath.sum(upper.value, lower.value, function));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Number("0");
		}
	}
}
