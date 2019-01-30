package postParser;

import java.lang.reflect.InvocationTargetException;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function extends Term{
	int reqOps;
	String func;
	String abbr;
	Stack<String> vars = new Stack<String>();
	String expression;
	
	Parser parser;
	
	Pattern variables = Variable.pat;
	
	public Function(String f) {
		this.name = "Function";
		parser = new Parser();
		func = f;
		Matcher m = variables.matcher(f);
		int count = 0;
		while (m.find())
			if(vars.contains(m.group(0))){
				vars.push(m.group(0));
				count++;
			}
		reqOps = count;
		expression = f;
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute(Term... ops) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Stack<Term> operands = new Stack<Term>();
		for(Term op : ops) {
			operands.push(op);
		}
		String e = expression;
		if(ops.length == reqOps) {
			for(int i=0;i<reqOps;i++) {
				Term t = operands.pop();
				String var = vars.pop();
				switch(t.type) {
				case "Operator":
					((Operator)t).operands = (Stack<Term>) operands.clone();
					e = e.replaceAll(var,((Operator)t).execute().value.toString());
					operands = (Stack<Term>) ((Operator)t).operands.clone();
					break;
				case "Operand":
					if(t.name.equals("Number")){
						e = e.replaceAll(var,((Number)t).value.toString());
					}
					break;
				}
			}
		}
		parser.init(e);
		return (Operand)parser.parse();
	}

}
