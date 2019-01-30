package postParser;

import java.util.regex.Pattern;

public class Constant extends Operand {
	public static Pattern pat = Pattern.compile("e|π|τ");
	
	Constant() {
		super("Constant","","Operand");
	}
	
	Constant(String val) {
		super("Constant","","Operand");
		switch(val) {
			case "e":
				this.value = Math.E;
			case "pi":
			case "π":
				this.value = Math.PI;
			case "τ":
				this.value = 2*Math.PI;
			default:
				this.value = Double.valueOf(val);
		}
	}
	
	Constant(Double val) {
		super("Constant","","Operand");
		this.value = val;
	}
}
