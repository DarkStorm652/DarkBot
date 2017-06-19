package org.darkstorm.minecraft.darkbot.wrapper.gui.spam;

import java.awt.*;

import javax.swing.*;

public class SayActionProvider extends ActionProvider {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public SayActionProvider() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// //GEN-END:initComponents
	}

	@Override
	public String getName() {
		return "Say";
	}

	@Override
	public String getDescription() {
		return "Send a message";
	}

	@Override
	public Action provideAction() {
		return new SayAction();
	}

	@SuppressWarnings("serial")
	private class SayAction extends JPanel implements Action {
		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JScrollPane scrollPane1;
		private JTextArea textArea;
		// JFormDesigner - End of variables declaration //GEN-END:variables

		private int line;

		private SayAction() {
			initComponents();
		}

		@Override
		public ActionProvider getProvider() {
			return SayActionProvider.this;
		}

		@Override
		public JPanel getOptions() {
			return this;
		}

		@Override
		public void performAction(ActionManager manager) {
			String[] lines = textArea.getText().replace("\r", "").split("\n");
			if(line > lines.length - 1)
				line = 0;
			manager.getBot().sendChat(lines[line++]);
		}

		@Override
		public String getDescription() {
			return textArea.getText().replace("\r", "").split("\n")[0];
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			scrollPane1 = new JScrollPane();
			textArea = new JTextArea();

			// ======== this ========
			setLayout(new GridBagLayout());
			((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0 };
			((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0 };
			((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0,
					1.0E-4 };
			((GridBagLayout) getLayout()).rowWeights = new double[] { 1.0,
					1.0E-4 };

			// ======== scrollPane1 ========
			{

				// ---- textArea ----
				textArea.setWrapStyleWord(true);
				scrollPane1.setViewportView(textArea);
			}
			add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			// //GEN-END:initComponents
		}
	}
}
