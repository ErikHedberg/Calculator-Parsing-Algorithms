import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Monovariant {
	char variable;
	double constant;
	String expr;

	public Monovariant(String expr) {
		Matcher varPattern = Pattern.compile("((?<!\\p{L})\\p{L}(?!\\p{L}))").matcher(expr);
		if(varPattern.find()) {
			variable = varPattern.group(1).charAt(0);
			this.expr = expr;
			ComputeConstant();
		}
	}
	
	public void ComputeConstant() {
		Pattern p = Pattern.compile(Parser.coVarRegex);
		Matcher m = p.matcher(expr);
		double constant = 0;
        while (m.find()) {
            String op = m.group(5);
            constant = Parser.simplify(m.group(2) + m.group(21));
        }
	}
}
