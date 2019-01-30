package postParser;

import java.util.regex.Pattern;

public class Variable extends Operand {
	public static Pattern pat = Pattern.compile("(\\p{L}(?![eπτ])){1}");
	public static Pattern coVarPat = Pattern.compile("((((((-)?\\d+(\\.\\d+)?)(\\-|\\+|\\*|\\/|\\^)?|\\√)(-)?)*)(\\+|\\-))?((((-)?\\d+(\\.\\d+)?)(\\s{0}|\\*|\\/|\\√|\\^)((-)?\\d+(\\.\\d+)?)?)*)(\\p{L})(((\\+|\\*|\\/|\\^|\\√)?(-)?|(-?))((-)?\\d+(\\.\\d+)?)?)");
	
	String var = "";
	Double coeff = 1d;
	Double power = 1d;
	
	Variable() {
		super("Variable","","Variable");
	}
	
	Variable(String v) {
		super("Variable","","Variable");
		this.var = v;
	}
	
	Variable(Double c,String v) {
		super("Variable","","Variable");
		this.var = v;
		this.coeff = c;
	}
	
	Variable(Double c,String v,Double p) {
		super("Variable","","Variable");
		this.var = v;
		this.coeff = c;
		this.power = p;
	}
	
	@Override
	public String toString() {
		return coeff.toString()+var+(power != 1d ? power.toString() : "");
	}
}
