package org.darkstorm.darkbot.mcwrapper.commands;

import java.util.*;

import org.darkstorm.darkbot.mcwrapper.MinecraftBotWrapper;
import org.darkstorm.darkbot.minecraftbot.util.Util;

public class CalcCommand extends AbstractCommand {

	public CalcCommand(MinecraftBotWrapper bot) {
		super(bot, "calc", "Calculator", "<script>", ".*");
	}

	@Override
	public void execute(String[] args) {
		String text = Util.join(args, " ");
		try {
			bot.say("Result: " + eval(text));
		} catch(Exception exception) {
			bot.say("Error: " + exception.getMessage());
		}
	}

	private double eval(String expression) {
		expression = expression.replace(" ", "").replace("\n", "").replace("\t", "");
		Queue<StackItem> exprQueue = new ArrayDeque<StackItem>();
		StringBuffer buffer = new StringBuffer();
		for(char c : expression.toCharArray()) {
			String string = buffer.toString();
			if(Character.isDigit(c) || (c == '.' && !string.contains("."))) {
				if(!string.isEmpty()) {
					try {
						Double.parseDouble(string.endsWith(".") ? string.substring(0, string.length() - 1) : string);
					} catch(NumberFormatException exception) {
						Operator operator = Operator.byValue(string);
						buffer = new StringBuffer();
						if(operator == null)
							throw new RuntimeException("Invalid operation: " + string);
						exprQueue.add(operator);
					}
				}
			} else if(c == '(' || c == ')') {
				if(!string.isEmpty()) {
					try {
						double d = Double.parseDouble(string.endsWith(".") ? string.substring(0, string.length() - 1) : string);
						exprQueue.add(new Variable(d));
					} catch(NumberFormatException exception) {
						Operator operator = Operator.byValue(string);
						if(operator == null)
							throw new RuntimeException("Invalid operation: " + string);
						exprQueue.add(operator);
					}
					buffer = new StringBuffer();
				}
				Parenthesis parenthesis = Parenthesis.byValue(c);
				if(parenthesis == null)
					throw new RuntimeException("Parenthesis not found: should not happen.");
				exprQueue.add(parenthesis);
				continue;
			} else if(!string.isEmpty()) {
				try {
					double d = Double.parseDouble(string.endsWith(".") ? string.substring(0, string.length() - 1) : string);
					buffer = new StringBuffer();
					exprQueue.add(new Variable(d));
				} catch(NumberFormatException exception) {
					throw new RuntimeException("Invalid number: " + string);
				}
			}
			buffer.append(c);
		}
		String string = buffer.toString();
		if(!string.isEmpty()) {
			try {
				double d = Double.parseDouble(string.endsWith(".") ? string.substring(0, string.length() - 1) : string);
				exprQueue.add(new Variable(d));
			} catch(NumberFormatException exception) {
				Operator operator = Operator.byValue(string);
				if(operator == null)
					throw new RuntimeException("Invalid operation: " + string);
				exprQueue.add(operator);
			}
		}
		Queue<StackItem> reversePolishExpression = toReversePolishNotation(exprQueue);
		return compute(reversePolishExpression);
	}

	private Queue<StackItem> toReversePolishNotation(Queue<StackItem> expression) {
		Queue<StackItem> queue = new ArrayDeque<StackItem>();
		Stack<Operator> operandStack = new Stack<Operator>();
		while(expression.size() > 0) {
			StackItem stack = expression.poll();
			if(stack instanceof Operator) {
				Operator operator = (Operator) stack;
				boolean dump = false;
				for(Operator op : operandStack)
					if(op.getPriority() >= operator.getPriority())
						dump = true;
				if(dump)
					while(operandStack.size() > 0)
						queue.offer(operandStack.pop());
				operandStack.push(operator);
			} else if(stack == Parenthesis.OPEN) {
				Queue<StackItem> subExpr = new ArrayDeque<StackItem>();
				int depth = 1;
				while(expression.size() > 0) {
					StackItem item = expression.poll();
					if(item == Parenthesis.OPEN)
						depth++;
					if(item == Parenthesis.CLOSE) {
						depth--;
						if(depth == 0)
							break;
					}
					subExpr.add(item);
				}
				if(depth != 0)
					throw new RuntimeException("Invalid syntax");
				queue.offer(new Variable(compute(toReversePolishNotation(subExpr))));
			} else if(stack == Parenthesis.CLOSE)
				throw new RuntimeException("Invalid syntax");
			else
				queue.offer(stack);
		}
		while(operandStack.size() > 0)
			queue.offer(operandStack.pop());
		return queue;
	}

	private double compute(Queue<StackItem> reversePolishExpression) {
		Stack<Double> stack = new Stack<Double>();
		while(reversePolishExpression.size() > 0) {
			StackItem item = reversePolishExpression.poll();
			if(item instanceof Operator) {
				double var1 = stack.pop();
				double var2 = stack.pop();
				stack.push(((Operator) item).perform(var2, var1));
			} else
				stack.push(Double.valueOf(((Variable) item).getValue()));
		}
		return stack.pop();
	}

	private static interface StackItem {
	}

	private static enum Parenthesis implements StackItem {
		OPEN('('),
		CLOSE(')');

		private final char value;

		private Parenthesis(char value) {
			this.value = value;
		}

		public char getValue() {
			return value;
		}

		@Override
		public String toString() {
			return Character.toString(value);
		}

		public static Parenthesis byValue(char value) {
			for(Parenthesis parenthesis : Parenthesis.values())
				if(value == parenthesis.getValue())
					return parenthesis;
			return null;
		}
	}

	private static enum Operator implements StackItem {
		ADD("+", 0) {
			@Override
			public double perform(double var1, double var2) {
				return var1 + var2;
			}
		},
		SUBTRACT("-", 0) {
			@Override
			public double perform(double var1, double var2) {
				return var1 - var2;
			}
		},
		MULTIPLY("*", 1) {
			@Override
			public double perform(double var1, double var2) {
				return var1 * var2;
			}
		},
		DIVIDE("/", 1) {
			@Override
			public double perform(double var1, double var2) {
				return var1 / var2;
			}
		},
		;

		private final String value;
		private final int priority;

		private Operator(String value, int priority) {
			this.value = value;
			this.priority = priority;
		}

		public String getValue() {
			return value;
		}

		public int getPriority() {
			return priority;
		}

		@Override
		public String toString() {
			return value;
		}

		public abstract double perform(double var1, double var2);

		public static Operator byValue(String value) {
			for(Operator operator : Operator.values())
				if(value.equals(operator.getValue()))
					return operator;
			return null;
		}
	}

	private static class Variable implements StackItem {
		private final double value;

		public Variable(double value) {
			this.value = value;
		}

		public double getValue() {
			return value;
		}

		@Override
		public String toString() {
			return Double.toString(value);
		}
	}
}
