package postParser;

import java.util.Stack;
import java.util.regex.Pattern;

public class Operator extends Term {
	int reqOps = 1; //Required operands
	public Pattern pat = Pattern.compile("");
	int priority = 0; //Operator priority
	
	Stack<Term> operands;
	
	Operator(String n,String r,String t,int o,int p){
		super(n,r,t);
		reqOps = o;
		priority = p;
	}
	
	Operator(String n,String r,String t,int o,int p,Stack<Term> ops) {
		super(n,r,t);
		reqOps = o;
		priority = p;
		operands = ops;
	}
	
	Operator(String n,String r,String t,int o,int p,Term... ops) {
		super(n,r,t);
		reqOps = o;
		priority = p;
		operands = new Stack<Term>();
		for(Term op : ops) {
			operands.push(op);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Operand execute(){
		Number ret = null;
		for(int i=0;i<reqOps;) {
			Term t = operands.pop();
			if(t.getClass() == Operator.class) {
				((Operator)t).operands = (Stack<Term>) this.operands.clone();
				t = ((Operator)t).execute();
			}
			ret = (Number) t;
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return this.rep;
	}
}
