package postParser;

import java.lang.reflect.InvocationTargetException;

public class Main {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Parser parser = new Parser();
		String expr = "sin(π)^2";
		parser.init(expr);
		Term result = parser.parse();
		System.out.println(result.type.toString());
		System.out.println(result.type == "Variable" ? ((Variable)result).toString() : ((Operand)result).value);
	}

}
