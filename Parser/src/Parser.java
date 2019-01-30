import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Erik Hedberg on 4/18/2016.
 */
public class Parser {
    static boolean HasBeenInitialized = false;

    static String digitRegex = "(-)?\\d+(\\.\\d+)?";
    static String multRegex = "((-)?\\d+(\\.\\d+)?|\\p{L})\\s?\\*\\s?((-)?\\d+(\\.\\d+)?|\\p{L})";
    static String divRegex = "((-)?\\d+(\\.\\d+)?|\\p{L})\\s?\\/\\s?((-)?\\d+(\\.\\d+)?|\\p{L})";
    static String expRegex = "((-)?\\d+(\\.\\d+)?|\\p{L})\\s?\\^\\s?((-)?\\d+(\\.\\d+)?|\\p{L})";
    static String sqrtRegex = "2√(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])";
    static String nrtRegex = "((-)?\\d+(\\.\\d+)?|\\p{L})?√(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"; //1: base 5: argument
    static String varRegex = "(?=(\\p{L}))(?=([^e|π|τ]))";
    static String coVarRegex = "((((((-)?\\d+(\\.\\d+)?)(\\-|\\+|\\*|\\/|\\^)?|\\√)(-)?)*)(\\+|\\-))?((((-)?\\d+(\\.\\d+)?)(\\s{0}|\\*|\\/|\\√|\\^)((-)?\\d+(\\.\\d+)?)?)*)(\\p{L})(((\\+|\\*|\\/|\\^|\\√)?(-)?|(-?))((-)?\\d+(\\.\\d+)?)?)";
    //"((((-)?\\d+(\\.\\d+)?)(\\+|\\*|\\/|\\^)?|\\√)(-)?|(-?))("+varRegex+")(((\\+|\\*|\\/|\\^|\\√)?(-)?|(-?))((-)?\\d+(\\.\\d+)?)?)";
    static ConcurrentHashMap<String,Pattern> functionRegex = new ConcurrentHashMap<String, Pattern>();
    static String anyOperator;

    public static void init() {
        //capture group info in comments
        functionRegex.put("sin",Pattern.compile("(sin|asin|sinh|asinh)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (sin or asin or sinh) 3: argument
        functionRegex.put("cos",Pattern.compile("(cos|acos|cosh|acosh)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (cos or acos or cosh) 3: argument
        functionRegex.put("tan",Pattern.compile("(tan|atan|tanh|atanh)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (tan or atan or tanh) 3: argument
        functionRegex.put("sec",Pattern.compile("(sec|asec|sech|asech)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (sec or asec or sech) 3: argument
        functionRegex.put("csc",Pattern.compile("(csc|acsc|csch|acsch)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (csc or acsc or csch) 3: argument
        functionRegex.put("cot",Pattern.compile("(cot|acot|coth|acoth)(\\[|\\()((-)?\\d+(\\.\\d+)?|\\p{L})(\\]|\\))"));// 1: (cot or acot or coth) 3: argument
        functionRegex.put("log",Pattern.compile("log(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));// 2: argument
        functionRegex.put("ln",Pattern.compile("ln(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));// 2: argument
        functionRegex.put("logn",Pattern.compile("logn(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})\\,((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));// 2: base 5: argument
        functionRegex.put("mod",Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})\\s?mod\\s?((-)?\\d+(\\.\\d+)?|\\p{L})"));// 1: argument 4: divisor
        functionRegex.put("deg",Pattern.compile("deg(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));// 2: argument
        functionRegex.put("rad",Pattern.compile("rad(\\(|\\[)((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));// 2: argument
        functionRegex.put("per",Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})%"));// 1: argument
        functionRegex.put("sum",Pattern.compile("\\∑(\\(|\\[)(\\p{L})\\s?\\=\\s?(.)\\,(.)\\,(.*)(\\)|\\])"));// 2: index variable 3: initial value 4: terminating value 5: iteration function
        functionRegex.put("prod",Pattern.compile("\\∏(\\(|\\[)(\\p{L})\\s?\\=\\s?(.)\\,(.)\\,(.*)(\\)|\\])"));// same as above
        functionRegex.put("limit",Pattern.compile("lim(\\(|\\[)(\\+?|\\-?)\\p{L}\\,((-)?\\d+(\\.\\d+)?|\\p{L})(\\)|\\])"));
        functionRegex.put("fact",Pattern.compile("((-)?\\d+(\\.\\d+)?|\\p{L})!")); // 1: argument
        functionRegex.put("abs", Pattern.compile("\\|(.*)\\|"));// 1: argument

        StringBuilder s = new StringBuilder();
        s.append("|("+digitRegex+")");
        s.append("|("+multRegex+")");
        s.append("|("+divRegex+")");
        s.append("|("+expRegex+")");
        s.append("|("+sqrtRegex+")");
        s.append("|("+nrtRegex+")");
        s.append("|("+varRegex+")");
        s.append("|("+coVarRegex+")");
        for(Pattern val : functionRegex.values()) {
            s.append("|("+val+")");
        }
        s.deleteCharAt(0);
        anyOperator = s.toString();
        HasBeenInitialized = true;
    }

    public static boolean isAlgebraic(String expr) {
        return expr.matches(".*"+coVarRegex+".*");
    }

    public static Double simplify(String expr) {
    	if (expr == null || expr.isEmpty()) {
    		return 0d;
    	}else if(expr.equals("\u03C0")) {
            return Math.PI;
        }else if(expr.equals("τ")) {
            return Math.PI*2;
        }else if(expr.equals("e")) {
            return Math.E;
        }else if(expr.equals("k")) {
            throw new NumberFormatException(); //Gets run if user inputs 'k' improperly
        }else if(expr.matches(digitRegex)) {
            return Double.parseDouble(expr);
        }
        double exprd = 0;
        String[] parts;

        //First evaluate within parentheses
        Pattern par = Pattern.compile("\\(([^\\(\\)]+)\\)");
        Matcher m = par.matcher(expr);
        while(m.find()) {
            char bfrMatch = m.start()-1 >= 0 ? expr.charAt(m.start()-1) : ' ';
            exprd = simplify(m.group(1));
            if(!(bfrMatch == '(' || bfrMatch == '+' || bfrMatch == '-' || bfrMatch == '*' || bfrMatch == '/' || bfrMatch == '^' || bfrMatch == '√')) {
                expr = m.replaceFirst("*"+Double.toString(exprd));
            }else{
                expr = m.replaceFirst(Double.toString(exprd));
            }
            m = par.matcher(expr);
        }

        //Then evaluate within brackets (parenthetical alternative for function arguments to prevent adverse behavior)
        par = Pattern.compile("(\\[|\\()([^\\[\\(\\])]+)(\\]|\\))");
        m = par.matcher(expr);
        while(m.find()) {
            exprd = simplify(m.group(2));
            expr = m.replaceFirst("\\[" + Double.toString(exprd) + "\\]");
        }

        //Then evaluate functions
        for (int i = 0; i < 2; i++) {
            Iterator it = functionRegex.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Pattern> pair = (Map.Entry<String, Pattern>) it.next();
                Matcher matcher = pair.getValue().matcher(expr);
                while (matcher.find()) {
                    switch (pair.getKey()) {
                        case "sin":
                            String type = matcher.group(1);
                            exprd = type.equals("sin") ? Math.sin(simplify(matcher.group(3))) : (type.equals("asin") ? Math.asin(simplify(matcher.group(3))) : (type.equals("asinh") ? ExtendedMath.asinh(simplify(matcher.group(3))) : Math.sinh(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "cos":
                            type = matcher.group(1);
                            exprd = type.equals("cos") ? Math.cos(simplify(matcher.group(3))) : (type.equals("acos") ? Math.acos(simplify(matcher.group(3))) : (type.equals("acosh") ? ExtendedMath.acosh(simplify(matcher.group(3))) : Math.cosh(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "tan":
                            type = matcher.group(1);
                            exprd = type.equals("tan") ? Math.tan(simplify(matcher.group(3))) : (type.equals("atan") ? Math.atan(simplify(matcher.group(3))) : (type.equals("atanh") ? ExtendedMath.atanh(simplify(matcher.group(3))) : Math.tanh(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "cot":
                            type = matcher.group(1);
                            exprd = type.equals("cot") ? ExtendedMath.cot(simplify(matcher.group(3))) : (type.equals("acot") ? ExtendedMath.acot(simplify(matcher.group(3))) : (type.equals("acoth") ? ExtendedMath.acoth(simplify(matcher.group(3))) : ExtendedMath.coth(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "sec":
                            type = matcher.group(1);
                            exprd = type.equals("sec") ? ExtendedMath.sec(simplify(matcher.group(3))) : (type.equals("asec") ? ExtendedMath.asec(simplify(matcher.group(3))) : (type.equals("asech") ? ExtendedMath.asech(simplify(matcher.group(3))) : ExtendedMath.sech(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "csc":
                            type = matcher.group(1);
                            exprd = type.equals("csc") ? ExtendedMath.csc(simplify(matcher.group(3))) : (type.equals("acsc") ? ExtendedMath.acsc(simplify(matcher.group(3))) : (type.equals("acsch") ? ExtendedMath.acsch(simplify(matcher.group(3))) : ExtendedMath.csch(simplify(matcher.group(3)))));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "abs":
                            exprd = Math.abs(simplify(matcher.group(1)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "mod":
                            exprd = simplify(matcher.group(1)) % simplify(matcher.group(4));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "per":
                            exprd = simplify(matcher.group(1))/100;
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "deg":
                            exprd = Math.toDegrees(simplify(matcher.group(2)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "rad":
                            exprd = Math.toRadians(simplify(matcher.group(2)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "log":
                            exprd = Math.log10(Double.parseDouble(matcher.group(2)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "logn":
                            exprd = ExtendedMath.logn(simplify(matcher.group(2)), simplify(matcher.group(5)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "ln":
                            exprd = Math.log(simplify(matcher.group(2)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "fact":
                            exprd = ExtendedMath.factorial(simplify(matcher.group(1)));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "sum":
                            exprd = ExtendedMath.sum(simplify(matcher.group(3)), simplify(matcher.group(4)), matcher.group(5));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "prod":
                            exprd = ExtendedMath.prod(simplify(matcher.group(3)), simplify(matcher.group(4)), matcher.group(5));
                            expr = matcher.replaceFirst(Double.toString(exprd));
                            break;
                        case "derivative":
                            break;
                        case "prime":
                            break;
                        case "integral":
                            break;
                        case "defIntegral":
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //Then evaluate exponents and roots
        if(expr.indexOf("^") > expr.indexOf("√")) {
            Pattern pat = Pattern.compile(expRegex);
            Matcher mat = pat.matcher(expr);
            while(mat.find()) {
                exprd = Math.pow(simplify(mat.group(1)), simplify(mat.group(4)));
                expr = expr.replaceFirst(expRegex, Double.toString(exprd));
            }
            pat = Pattern.compile(nrtRegex);
            mat = pat.matcher(expr);
            while(mat.find()) {
                if(mat.group(1) == null || simplify(mat.group(1)) == 2) {
                    exprd = Math.sqrt(simplify(mat.group(5)));
                }else {
                    exprd = ExtendedMath.root(simplify(mat.group(1)), simplify(mat.group(5)));
                }
                expr = expr.replaceFirst(nrtRegex, Double.toString(exprd));
            }
        }else{
            Pattern pat = Pattern.compile(nrtRegex);
            Matcher mat = pat.matcher(expr);
            while(mat.find()) {
                if(mat.group(1) == null || simplify(mat.group(1)) == 2) {
                    exprd = Math.sqrt(simplify(mat.group(5)));
                }else {
                    exprd = ExtendedMath.root(simplify(mat.group(1)), simplify(mat.group(5)));
                }
                expr = expr.replaceFirst(nrtRegex, Double.toString(exprd));
            }
            pat = Pattern.compile(expRegex);
            mat = pat.matcher(expr);
            while(mat.find()) {
                exprd = Math.pow(simplify(mat.group(1)), simplify(mat.group(4)));
                expr = expr.replaceFirst(expRegex, Double.toString(exprd));
            }
        }

        //Then evaluate multiplication and division
        if(expr.indexOf("*") > expr.indexOf("/")) {
            parts = expr.split("\\s?\\*\\s?", 2);
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd *= simplify(parts[i]);
                }
                expr = expr.replace(multRegex,Double.toString(exprd));
            }
            parts = expr.split("\\s?\\/\\s?");
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd /= simplify(parts[i]);
                }
                expr = expr.replace(divRegex, Double.toString(exprd));
            }
        }else {
            parts = expr.split("\\s?\\/\\s?");
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd /= simplify(parts[i]);
                }
                expr = expr.replace(divRegex, Double.toString(exprd));
            }
            parts = expr.split("\\s?\\*\\s?", 2);
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd *= simplify(parts[i]);
                }
                expr = expr.replace(multRegex, Double.toString(exprd));
            }
        }

        //Then evaluate addition and subtraction
        if(expr.indexOf("+") < expr.indexOf("-")) {
            parts = expr.split("\\s?(?<!E)\\+\\s?", 2);
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd += simplify(parts[i]);
                }
                expr = Double.toString(exprd);
            }
            parts = expr.split("\\s?(?<!E)\\-\\s?");
            if (parts.length > 1 && !parts[1].isEmpty() && !parts[0].isEmpty()) {
                exprd = Double.parseDouble(parts[0]) - Double.parseDouble(parts[1]);
            }
        }else {
            parts = expr.split("\\s?(?<!E)\\-\\s?");
            if (parts.length > 1 && !parts[1].isEmpty() && !parts[0].isEmpty()) {
                exprd = Double.parseDouble(parts[0]) - simplify(parts[1]);
                expr = Double.toString(exprd);
            }
            parts = expr.split("\\s?(?<!E)\\+\\s?", 2);
            if (parts.length > 1) {
                exprd = simplify(parts[0]);
                for (int i = 1; i < parts.length; i++) {
                    exprd += simplify(parts[i]);
                }
            }
        }

        return exprd;
    }
    
    public static String Differentiate(String expr) {return "";}
    
    public static String simplifyMonovariant(String expr) {
    	StringBuilder string = new StringBuilder();
    	Matcher varPattern = Pattern.compile("((?<!"+varRegex+")"+varRegex+"(?!"+varRegex+"))").matcher(expr);
    	if (varPattern.find()) {
	    	String var = varPattern.group(1);
	    	
	    	Pattern par = Pattern.compile("\\(([^\\(\\)]+)\\)");
	        Matcher m = par.matcher(expr);
	        while(m.find()) {
	            char bfrMatch = m.start()-1 >= 0 ? expr.charAt(m.start()-1) : ' ';
	            if(!(bfrMatch == '(' || bfrMatch == '+' || bfrMatch == '-' || bfrMatch == '*' || bfrMatch == '/' || bfrMatch == '^' || bfrMatch == '√')) {
	                expr = m.replaceFirst("*"+simplifyMonovariant(m.group(1)));
	            }else{
	                expr = m.replaceFirst(simplifyMonovariant(m.group(1)));
	            }
	            m = par.matcher(expr);
	        }
	    	
	        String[] parts = expr.split("\\s?\\+\\s?");
	        
	        for(String str:parts) {
		        Matcher exp = Pattern.compile(var+"\\^(\\(([^\\(\\)]+)\\)|((-)?\\d+(\\.\\d+)?))/"+var).matcher(str);
		        while(exp.find()) {
			        if(Double.parseDouble(simplifyMonovariant(exp.group(1))) == 2.0d) {
			        	str = exp.replaceFirst(var);
			        }else if(Double.parseDouble(simplifyMonovariant(exp.group(1))) == 1.0d) {
			        	str = exp.replaceFirst("1");
			        }else {
			        	str = exp.replaceFirst(var+"?"+(Double.parseDouble(simplifyMonovariant(exp.group(1)))-1));
			        }
		        }
		        string.append(str.replaceAll("\\?", "^"));
		        string.append("+");
	        }
	        if(string.charAt(string.length()-1) == '+') {
	        	string.deleteCharAt(string.length()-1);
	        }
	        expr = string.toString();
	        return expr;
    	}else {
    		return Double.toString(simplify(expr));
    	}
    }
    
    public static String simplifyAlgebraic(String expr) {
        String[] parts = expr.split("((\\(\\p{L})){0}\\s?=\\s?");
        Pattern p = Pattern.compile(coVarRegex);
        double constant = 0;
        if(isAlgebraic(parts[0])) {
            StringBuilder s = new StringBuilder();
            if (expr.matches("^[^\\^]*$")) {
                double coeffY = 0;
                double coeffX = 0;
	            if(parts.length > 1) {
	                Matcher m = p.matcher(parts[0]);
	                while (m.find()) {
	                    String op = m.group(5);       
	                    constant -= simplify(m.group(2) + m.group(21));
	                    if (m.group(13) == null || m.group(13).isEmpty()) {
	                    	coeffY = (m.group(10) == null || m.group(10).isEmpty()) ? 1 : -1;
	                    }else if(m.group(17) == null || m.group(17).isEmpty()) {
	                    	coeffY = simplify(m.group(10)== null ? m.group(13) : "0"+m.group(10)+m.group(13));
	                    }else{
	                    	coeffY = (m.group(11) == null || m.group(11).isEmpty())? 1 : simplify("0"+m.group(10)+m.group(11));
	                    }
	                }
	                m = p.matcher(parts[1]);
	                while (m.find()) {
	                    String op = m.group(5);
	                    constant = simplify(m.group(2) + m.group(21));
	                    if (m.group(13) == null || m.group(13).isEmpty()) {
	                    	coeffX = (m.group(10) == null || m.group(10).isEmpty()) ? 1 : -1;
	                    }else if(m.group(17) == null || m.group(17).isEmpty()) {
	                    	coeffX = simplify("0"+m.group(10)+m.group(13));
	                    }else{
	                    	coeffX = (m.group(11) == null || m.group(11).isEmpty())? 1 : simplify("0"+m.group(10)+m.group(11));
	                    }
	                }
	            }else{
	                //Support for non definitive expressions ie: "2x + y"
	            }
	            return "-(" + constant + "/" + coeffY + ")/(" + coeffX + "/" + coeffY + ")";
	        }else {
	        	//Support for polynomials
	        	return expr;
	        }
        }else {
        	return expr;
        }
    }
}