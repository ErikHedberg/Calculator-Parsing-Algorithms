package postParser;

import java.util.regex.Pattern;

public class Number extends Operand {
	static Pattern pat = Pattern.compile("(-)?\\d+(\\.\\d+)?");
	
	Number() {
		super("Number","","Operand");
	}
	
	Number(String val) {
		super("Number","","Operand");
		this.value = Double.valueOf(val);
	}
	
	Number(Double val) {
		super("Number","","Operand");
		this.value = val;
	}
	
	@Override
	public String toString() {
		return Double.toString(this.value);
	}
	
}
