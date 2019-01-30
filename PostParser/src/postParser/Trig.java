package postParser;

import java.util.Stack;

public class Trig extends Operator {
	public String flag;
	
	public Trig(String n, String r, String t, int o, int p) {
		super(n, r, t, o, p);
		flag = "";
	}

	public Trig(String n, String r, String t, int o, int p, Stack<Term> ops) {
		super(n, r, t, o, p, ops);
		flag = "";
	}

	public Trig(String n, String r, String t, int o, int p, Term... ops) {
		super(n, r, t, o, p, ops);
		flag = "";
	}

}
