import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Stack;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class CalcView implements ActionListener {

	public static final String[][][] BUTTON_TEXT_NAMES = {
			{ { "7", "n7" }, { "8", "n8" }, { "9", "n9" }, { "+", "add" } },
			{ { "4", "n4" }, { "5", "n5" }, { "6", "n6" }, { "-", "rem" } },
			{ { "1", "n1" }, { "2", "n2" }, { "3", "n3" }, { "*", "mul" } },
			{ { "0", "n0" }, { "CLR", "clr" }, { "/", "div" }, { "=", "eq" } }, { { "BCKSPC", "BCKSPC" } } };

	public static final Font BTN_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 17);
	private JLabel operationLabel, resultLabel;

	/**
	 * Stos przechowujący wprowadzone operatory i liczby.
	 */
	private Stack<Character> inputStack = new Stack<>();
	private Calc c;

	public CalcView(Calc c) {

		this.c = c;

		// Wygląd
		JPanel labelContainer = new JPanel(new BorderLayout());
		operationLabel = new JLabel("", SwingConstants.RIGHT);
		operationLabel.setName("op");
		operationLabel.setPreferredSize(new Dimension(15, 20));

		resultLabel = new JLabel("", SwingConstants.RIGHT);
		resultLabel.setName("res");
		resultLabel.setFont(BTN_FONT);
		resultLabel.setPreferredSize(new Dimension(25, 20));

		labelContainer.add(operationLabel, BorderLayout.PAGE_START);
		labelContainer.add(resultLabel, BorderLayout.SOUTH);

		operationLabel.setFont(BTN_FONT.deriveFont(Font.PLAIN));
		JPanel btnPanel = new JPanel(new GridLayout(BUTTON_TEXT_NAMES.length, BUTTON_TEXT_NAMES[0].length));

		for (int i = 0; i < BUTTON_TEXT_NAMES.length; i++) {
			for (int j = 0; j < BUTTON_TEXT_NAMES[i].length; j++) {
				// Ustawienie napisów na przyciskach
				JButton btn = new JButton(BUTTON_TEXT_NAMES[i][j][0]);
				// Ustawienie nazwy elementu
				btn.setName(BUTTON_TEXT_NAMES[i][j][1]);
				btn.setFont(BTN_FONT);
				btnPanel.add(btn);

				btn.addActionListener(this);
			}
		}

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(labelContainer, BorderLayout.PAGE_START);
		mainPanel.add(btnPanel, BorderLayout.CENTER);

		JFrame frame = new JFrame("Prosty kalkulator GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(mainPanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		String pressedBtn = e.getActionCommand();

		switch (pressedBtn) {
		case "CLR":
			operationLabel.setText("");
			resultLabel.setText("");

			// Wyczyść cały stos
			inputStack.clear();

			break;

		case "BCKSPC":

			try {

				/*
				 * Jeśli na stosie leży operator arytmetyczny,usuń trzy ostatnie
				 * znaki z labelki. Jesli liczba, jeden znak.
				 */
				if (!inputStack.isEmpty() && !Character.isDigit(inputStack.peek())) {
					operationLabel
							.setText(operationLabel.getText().substring(0, operationLabel.getText().length() - 3));

					// Usuń znak ze stosu
					inputStack.pop();
				} else {
					operationLabel
							.setText(operationLabel.getText().substring(0, operationLabel.getText().length() - 1));

					// Usuń znak ze stosu
					inputStack.pop();
				}
			} catch (Exception e2) {
//				System.out.println(e2.getMessage());
			}

			break;

		case "+":
		case "-":
		case "/":
		case "*":

			// Podmiana operatora arytmetycznego
			if (!inputStack.isEmpty() && !Character.isDigit(inputStack.peek())) {
				String operationWithoutLastOperator = operationLabel.getText()
						.substring(0, operationLabel.getText().length() - 2).trim();
				operationLabel.setText(operationWithoutLastOperator + " " + pressedBtn + " ");

				// Zamień ostatni operator na stosie
				inputStack.pop();
				inputStack.push(pressedBtn.charAt(0));

				break;
			}

			/*
			 * Nie pozwól na wprowadzenie operatora gdy nie wprowadzono jeszcze
			 * pierwszej liczby
			 */
			if (operationLabel.getText().length() > 0) {

				inputStack.push(pressedBtn.charAt(0));
				// Wyświetl operator na labelce.
				operationLabel.setText(operationLabel.getText() + " " + pressedBtn + " ");
			}
			break;

		case "=":
			Double result;
			DecimalFormat format = new DecimalFormat("0.##");

			// Jeśli już raz wykonano działanie równania, przerwij.
			String inputEquation = operationLabel.getText();

			String postfix = Calc.infixToPostfix(inputEquation);

			try {
				result = Calc.evalRPN(postfix);
				
				resultLabel.setText("" + format.format(result));
			} catch (Exception e1) {
				break;
			}
			

			break;

		default:
			// Wprowadzenie liczb na labelke
			operationLabel.setText(operationLabel.getText() + pressedBtn);

			// Wrzuć liczbę na stos
			inputStack.push(pressedBtn.charAt(0));

			break;
		}
	}
}