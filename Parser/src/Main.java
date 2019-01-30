
public class Main {

	public static void main(String[] args) {
		Parser parser = new Parser();
		parser.init();
		System.out.println(parser.simplify("(sin(0))^2)"));
		System.out.println(parser.simplifyMonovariant("2y = x"));
	}
}
