package postParser;

import java.util.Stack;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class Simplifier {

	public static String AlgebraicSimplification(String expr) {
		Stack<String> vars = new Stack<String>();
		Matcher m = Variable.pat.matcher(expr);
		while (m.find()) {
			if(vars.contains(m.group(0))) {
				vars.push(m.group(0));
			}
		}
		Matcher covarMatcher = Variable.coVarPat.matcher(expr);
		while(covarMatcher.find()){
			String full = covarMatcher.group(0);
			if(covarMatcher.group(26) != null) {
				String pow = covarMatcher.group(26);
				if(pow.matches("1(\\.0)?")) {
					expr = covarMatcher.replaceFirst(full.replace("^"+pow, ""));
				}else if(pow.matches("0(\\.0)?")) {
					expr = covarMatcher.replaceFirst(full.replace(covarMatcher.group(20)+covarMatcher.group(21), ""));
				}
			}
			expr = expr.replaceAll(full + "\\s?\\-\\s?"+full, "+ 0");
			expr = expr.replaceAll(full + "\\s?\\-\\s?"+full, "+ 0");
			if(covarMatcher.group(10) != null) {
				expr = expr.replaceAll(full + "\\s?\\+\\s?\\" + full.replace("-",""), "+ 0");
			}else{
				expr = expr.replaceAll(full + "\\s?\\+\\s?\\-\\s?" + full, "+ 0");
			}
			expr = expr.replaceAll(full + "\\s?\\/\\s?" + full, "+ 1");
			expr = expr.replaceAll(full + "\\s?\\*\\s?" + full, (covarMatcher.group(10) == null ? "+" : "-") + covarMatcher.group(11) + covarMatcher.group(20) + "^"+(covarMatcher.group(26) == null ? "2" : Double.toString(Double.parseDouble(covarMatcher.group(26))*2)));
			if(covarMatcher.group(11) != null) {
				String coeff = covarMatcher.group(11);
				if(coeff.matches("1(\\.0)?")) {
					expr = expr.replace(full, (covarMatcher.group(10) == null ? "+" : "-") + covarMatcher.group(20) + covarMatcher.group(21));
				}else if(coeff.matches("0(\\.0)?")) {
					expr = expr.replace(full, "+ 0");
				}
			}
		}
		expr = expr.replaceAll("^\\s?\\+\\s?(.*)$","$1");
		return expr;
	}
}
