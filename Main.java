package calculator;

import java.math.BigInteger;
import java.sql.SQLOutput;
import java.util.*;

public class Main {

	private static boolean isExit = false;

	private static String line;

	private static String invalidExpression = "Invalid expression";

	private static Map<String, String> variables = new HashMap<>();

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		while (!isExit) {
			line = scanner.nextLine().trim();
			if ("/exit".equals(line)) {
				isExit = true;
				System.out.println("Bye!");
				break;
			} else if ("/help".equals(line)) {
				System.out.println("The program calculates the sum and subtractions of numbers. It support both unary" +
						" and binary minus operators. It interpret two adjacent minus signs as a plus.");
				continue;
			}

			if ("".equals(line)) {
				System.out.print("");
			} else if (line.matches("/.+")) {
				System.out.println("Unknown command");
			} else if (line.matches("\\d+[-+]") || line.matches(
					"\\d+\\s+\\d+") || line.contains("\\*{2,}|\\/{2,}")) {
				System.out.println("Invalid expression");
			} else {
				simplifyInput();
				if (line.contains("=")) {
					saveVariable();
				} else {
					try {
						simplifyInput();
						System.out.println(evaluatePostfix());
					} catch (Exception e) {

						System.out.println(invalidExpression);
					}
				}
			}

		}
	}

	private static void simplifyInput() {

		//replace extra spaces
		line = line.replaceAll("[^\\S\\r\\n]+", "").trim();
		line = line.replaceAll("-{2}", "+");
		line = line.replaceAll("\\+{2,}", "+");
		line = line.replaceAll("-\\+|\\+-", "-");
	}



	private static String transformToPostfix() {

		StringBuilder expression = new StringBuilder();
		Deque<Character> stack = new ArrayDeque<>();

		for (Character c : line.toCharArray()) {
			if (Character.isLetterOrDigit(c)) {
				expression.append(c);
			} else if (c == '(') {
				stack.push(c);
			} else if (c == ')') {
				while (!stack.isEmpty() && stack.peek() != '(') {
					expression.append(" ");
					expression.append(stack.pop());
					expression.append(" ");
					if(stack.isEmpty()){
						return invalidExpression;
					}
				}
				if (!stack.isEmpty() && stack.peek() != '(') {
					return invalidExpression;
				} else {
					stack.pop();
				}

			} else {
				expression.append(" ");
				while (!stack.isEmpty() && precedence(c) <= precedence(stack.peek())) {
					if (stack.peek() == '(') {
						return invalidExpression;
					}

					expression.append(stack.pop());
					expression.append(" ");
				}
				stack.push(c);
			}
		}
		while (!stack.isEmpty()) {
			if (stack.peek() == '(')
				return invalidExpression;
			expression.append(" ");
			expression.append(stack.pop());
			expression.append(" ");
		}

		return expression.append(" ").toString().replaceAll("[^\\S\\r\\n]+", " ");
	}

	private  static String evaluatePostfix() {

		String postfix = transformToPostfix();

		if (postfix.equals(invalidExpression)) {
			return postfix;
		}

		Deque<BigInteger> stack = new ArrayDeque<>();
		StringBuilder parsedInteger = new StringBuilder();

		for (int i = 0; i < postfix.length(); i++) {
			char c = postfix.charAt(i);
			if (Character.isLetterOrDigit(c)) {
				parsedInteger.append(c);
			} else if (c == ' ' ) {
				if(!parsedInteger.toString().equals("")){

					if (parsedInteger.toString().matches("[A-Za-z]+")) {

						if (variables.containsKey(parsedInteger.toString())) {
							String temp =
									parsedInteger.toString();
							parsedInteger = new StringBuilder();
							parsedInteger.append(variables.get(temp));
							//	System.out.println(parsedInteger);
						} else {
							return "Unknown variable";
						}
					}

					stack.push(new BigInteger(parsedInteger.toString()));
					parsedInteger = new StringBuilder();
				}
			} else {
				BigInteger a = stack.pop();
				BigInteger b = stack.pop();
				stack.push(applyOperator(c, a, b));
			}
		}

		return String.valueOf(stack.pop());
	}




	private	static int precedence(char op) {

		int result = -1;

		switch (op) {
			case '+':
			case '-':
				result = 1;
				break;

			case '*':
			case '/':
				result = 2;
				break;

			case '^':
				result = 3;
				break;
		}
		return result;
	}

	public static BigInteger applyOperator(char operator, BigInteger b, BigInteger a) {

		switch (operator) {
			case '+':
				return a.add(b);
			case '-':
				return a.subtract(b);
			case '*':
				return a.multiply(b);
			case '/':
				if (b.equals(BigInteger.ZERO))
					throw new
							UnsupportedOperationException("Cannot divide by zero");
				return a.divide(b);
			case '^':
				return a.pow(b.intValue());
		}
		return BigInteger.ZERO;
	}

	private static void saveVariable() {

		String[] variable = line.split("=");
		if (variable.length > 2) {
			System.out.println("Invalid assignment");
			return;
		}

		if (variable[0].matches(".*\\d.*")) {

			System.out.println("Invalid identifier");
			return;
		}

		if (variable[1].matches(".*\\D.*")) {
			if (variables.containsKey(variable[1])) {
				putVariableToMap(variable[0], variables.get(variable[1]));

			} else {
				System.out.println("Invalid assignment");
			}
			return;
		}
		putVariableToMap(variable[0], variable[1]);

	}

	private static void putVariableToMap(String key, String value) {

		if (variables.containsKey(key)) {
			variables.replace(key, value);

		} else {
			variables.put(key, value);
		}

//	System.out.println(variables);
	}
}
