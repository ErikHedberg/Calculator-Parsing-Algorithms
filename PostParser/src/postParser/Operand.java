package postParser;

import java.util.regex.Pattern;

public class Operand extends Term {
	public static Pattern pat;
	String type = "Operand";
	String name = "Operand";
	
	Double value;
	
	public Operand() {
		super("","Operand","Operand");
	}

	public Operand(String n, String r, String t) {
		super(n,r,t);
		this.name = n;
	}
	
	@Override
	public String toString() {
		return Double.toString(this.value);
	}
}
