package postParser;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

import postParser.Number;

/**
 * Created by Erik Hedberg on 8/9/2017
*/

public class Parser {
	Stack<Term> terms;
	String expr;
	String allOperators;
	
	ArrayList<String> opSymbols = new ArrayList<String>();
	
	public Parser() {
		populateOpSymbols();
	}
	
	public void populateOpSymbols() {
		
		//Basic Operators
		opSymbols.add("+");
		opSymbols.add("-");
		opSymbols.add("*");
		opSymbols.add("/");
		opSymbols.add("^");
		opSymbols.add("√");
		
		//Basic Functions
		opSymbols.add("abs");
		opSymbols.add("!");
		opSymbols.add("mod");
		opSymbols.add("<"); //Negate placeholder symbol ex: -2 ==> <2
		
		//Logarithmic Functions
		opSymbols.add("logn");
		opSymbols.add("log");
		opSymbols.add("ln");
				
		//Trigonometry Functions
		opSymbols.add("asinh");
		opSymbols.add("sinh");
		opSymbols.add("sin");
		opSymbols.add("asin");
		opSymbols.add("acosh");
		opSymbols.add("cosh");
		opSymbols.add("acos");
		opSymbols.add("cos");
		opSymbols.add("atanh");
		opSymbols.add("tanh");
		opSymbols.add("atan");
		opSymbols.add("tan");
		
		//Advanced Functions
		opSymbols.add("∑");
				
		StringBuilder str = new StringBuilder();
		opSymbols.forEach(s->str.append((s.matches("\\p{L}.*") ? "" : "\\")+s+"|"));
		str.deleteCharAt(str.length()-1);
		this.allOperators = str.toString(); 
	}
	
	public boolean isOperator(String s) {
		for(String o : opSymbols) {
			if(o.equals(s)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isVariable(String s) {
		return Variable.pat.matcher(s).matches() | Variable.coVarPat.matcher(s).matches();
	}
	
	public int priority(String op) {
		switch(op) {
			case "+":
			case "-":
				return 0;
			case "*":
			case "/":
				return 1;
			case "pow":
			case "^":
			case "nrt":
			case "√":
				return 2;
			case "abs":
			case "!":
			case "mod":
			case "<":
				return 3;
			default:
				return 3;
			
		}
	}
	
	public Class<?> getOpClass(String op) {
		switch(op) {
			case "abs":
				return Abs.class;
			case "+":
				return Add.class;
			case "-":
				return Subtract.class;
			case "*":
				return Multiply.class;
			case "/":
				return Divide.class;
			case "^":
			case "pow":
				return Exponent.class;
			case "√":
			case "nrt":
				return Root.class;
			case "!":
				return Factorial.class;
			case "mod":
				return Modulo.class;
			case "<":
				return Negate.class;
			case "log":
				return Log.class;
			case "logn":
				return Logn.class;
			case "ln":
				return Ln.class;
			case "sum":
			case "∑":
				return Sum.class;
			//case "prod":
			case "sin":
			case "asin":
			case "sinh":
			case "asinh":
				return Sin.class;
			case "cos":
			case "acos":
			case "cosh":
			case "acosh":
				return Cos.class;
			case "tan":
			case "atan":
			case "tanh":
			case "atanh":
				return Tan.class;
			default:
				return Operator.class;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public Class getOperandClass(String op) {
		if(Number.pat.matcher(op).matches()) {
			return Number.class;
		}else if(Constant.pat.matcher(op).matches()) {
			return Constant.class;
		}else{
			return Operand.class;
		}
	}
	
	public void testOp(String s, Stack<String> out, Stack<String> ops) {
		int sPriority = priority(s);
		int oPriority = ops.isEmpty() ? -1 : priority(ops.peek());
		if(sPriority > oPriority) {
			ops.push(s);
		}else if(sPriority == oPriority){
			out.push(ops.pop());
			ops.push(s);
		}else{
			out.push(ops.pop());
			testOp(s,out,ops);
		}
	}
	
	public void init(String expression) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		expr = expression;
		terms = new Stack<Term>();
		
		Stack<String> out = new Stack<String>();
		Stack<String> ops = new Stack<String>();
		expr = expr.replaceAll("\\(", "( ");
		expr = expr.replaceAll("\\)"," )");
		expr = expr.replaceAll("\\,", " ");
		expr = expr.replaceAll("sum", "∑");
		expr = expr.replaceAll("("+allOperators+"\\s|^)\\√", "$1 2 √ ");
		expr = expr.replaceAll("("+allOperators+"\\s|^)\\-", "$1< ");
		expr = expr.replaceAll("("+allOperators+")((?<!\\s\\S)|(\\S))", " $1 ");
		expr = expr.replaceAll("\\|(.*)\\|", "abs ( $1 )");
		String[] asList = expr.split("\\s");
		ArrayList<String> intermediate = new ArrayList<String>(Arrays.asList(asList));
		intermediate.removeIf(e-> e.isEmpty());
		for(String s : intermediate) {
			if(isOperator(s)) {
				if(ops.isEmpty() || ops.peek().equals("(")) {
					ops.push(s);
				}else{
					testOp(s,out,ops);
				}
			}else if(s.equals("(")) {
				ops.push(s);
			}else if(s.equals(")")) {
				String op = ops.pop();
				while(!(op.equals("(") && (ops.indexOf("(") != ops.lastIndexOf("(")))) {
					if(!op.equals("(")) {
						out.push(op);
					}
					if(ops.isEmpty()) {
						break;
					}else{
						op = ops.pop();
					}
				}
			}else{
				out.push(s);
			}
		}
		if(!ops.isEmpty()) {
			Collections.reverse(ops);
			ops.forEach((e->out.push(e)));
		}
		ops.clear();
		ops.addAll(out);
		for(String s : ops) {
			if(isOperator(s)) {
				Operator op = (Operator) getOpClass(s).getConstructor(new Class[] {Stack.class}).newInstance(terms.clone());
				if(op.type.equals("Trig")) {
					String flag = s.replaceAll("sin|cos|tan", "");
					((Trig)op).flag = flag;
				}
				terms.push(op);
			}else if(isVariable(s)) {
				Matcher m = Variable.coVarPat.matcher(s); //Capture group info: 10: sign 11: coefficient 20: variable 26: power
				m.matches();
				Double c = 1d;
				if(m.group(10) != null && !m.group(10).isEmpty()) {
					String sign = m.group(10);
					sign = sign.replaceAll("<", "-");
					sign = sign.replaceAll(" ", "");
					if(m.group(11) != null && !m.group(11).isEmpty()) {
						c = Double.valueOf(sign+m.group(11));
					}else{
						c = Double.valueOf(sign+"1");
					}
				}else if(m.group(11) != null && !m.group(11).isEmpty()) {
					c = Double.valueOf(m.group(11));
				}
				String v = m.group(20);
				Double p = m.group(26) == null || m.group(26).isEmpty() ? 1d : Double.valueOf(m.group(26));
				if(p==0) {
					terms.push(new Number(c)); //push c in cv^p as cv^p = c*1
				}else if(c==0) {
					terms.push(new Number(0d)); //push 0 as 0*v^p = 0
				}else{
					Variable t = new Variable(c,v,p);
					terms.push(t);
				}
			}else if (Sum.varPat.matcher(s).matches()) {
				Matcher matcher = Sum.varPat.matcher(s);
				matcher.matches();
				MatchResult match = matcher.toMatchResult();
				Operand var = (Operand) new Variable(match.group(1));
				Operand lowerBound = (Operand) getOperandClass(match.group(2)).newInstance();
				lowerBound.value = Double.valueOf(match.group(2));
				terms.push(var);
				terms.push(lowerBound);
			}else{
				Operand t = (Operand) getOperandClass(s).newInstance();
				t.value = Double.valueOf(s);
				terms.push(t);
			}
		}
	}
	
	public Term parse() {
		if(terms.isEmpty()){
			return new Number(0d);
		}else if(terms.peek().type.equals("Operand")) {
			return terms.pop();
		}else if(terms.peek().type.equals("Variable")) {
			return terms.pop();
		}else{
			Operator ops = (Operator) terms.pop();
			return ops.execute();
		}
	}
}
