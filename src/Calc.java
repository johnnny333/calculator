import java.util.LinkedList;
import java.util.Stack;

public class Calc {
	
	/**
	 * Parsing/Shunting-yard algorithm
	 * 
	 * @param infix
	 * @see https://rosettacode.org/wiki/Parsing/Shunting-yard_algorithm
	 * @return
	 */
	public static String infixToPostfix(String infix) {
		final String ops = "-+/*^";
		StringBuilder sb = new StringBuilder();
		Stack<Integer> s = new Stack<>();

		for (String token : infix.split("\\s")) {
			if (token.isEmpty())
				continue;
			char c = token.charAt(0);
			int idx = ops.indexOf(c);

			// check for operator
			if (idx != -1) {
				if (s.isEmpty())
					s.push(idx);

				else {
					while (!s.isEmpty()) {
						int prec2 = s.peek() / 2;
						int prec1 = idx / 2;
						if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
							sb.append(ops.charAt(s.pop())).append(' ');
						else
							break;
					}
					s.push(idx);
				}
			} else if (c == '(') {
				s.push(-2); // -2 stands for '('
			} else if (c == ')') {
				// until '(' on stack, pop operators.
				while (s.peek() != -2)
					sb.append(ops.charAt(s.pop())).append(' ');
				s.pop();
			} else {
				sb.append(token).append(' ');
			}
		}
		while (!s.isEmpty())
			sb.append(ops.charAt(s.pop())).append(' ');
		return sb.toString();
	}
	
	/**
	 * Parsing/RPN calculator algorithm
	 * 
	 * @param expr
	 * @see https://rosettacode.org/wiki/Parsing/RPN_calculator_algorithm
	 * @return
	 */
	public static Double evalRPN(String expr) {
		String cleanExpr = cleanExpr(expr);
		LinkedList<Double> stack = new LinkedList<Double>();
		// System.out.println("Input\tOperation\tStack after");
		for (String token : cleanExpr.split("\\s")) {
			// System.out.print(token+"\t");
			Double tokenNum = null;
			try {
				tokenNum = Double.parseDouble(token);
			} catch (NumberFormatException e) {
			}
			if (tokenNum != null) {
				// System.out.print("Push\t\t");
				stack.push(Double.parseDouble(token + ""));
			} else if (token.equals("*")) {
				// System.out.print("Operate\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand * secondOperand);
			} else if (token.equals("/")) {
				// System.out.print("Operate\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand / secondOperand);
			} else if (token.equals("-")) {
				// System.out.print("Operate\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand - secondOperand);
			} else if (token.equals("+")) {
				// System.out.print("Operate\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(firstOperand + secondOperand);
			} else if (token.equals("^")) {
				// System.out.print("Operate\t\t");
				double secondOperand = stack.pop();
				double firstOperand = stack.pop();
				stack.push(Math.pow(firstOperand, secondOperand));
			} else {// just in case
				System.out.println("Error");
				return null;
			}
			// System.out.println(stack);
		}
		// System.out.println("Final answer: " + stack.pop());
		return stack.pop();
	}

	private static String cleanExpr(String expr) {
		// remove all non-operators, non-whitespace, and non digit chars
		return expr.replaceAll("[^\\^\\*\\+\\-\\d/\\s]", "");
	}
}