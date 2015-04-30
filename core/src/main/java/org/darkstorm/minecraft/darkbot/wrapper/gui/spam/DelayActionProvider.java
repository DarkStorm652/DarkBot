package org.darkstorm.minecraft.darkbot.wrapper.gui.spam;

import java.awt.*;

import javax.swing.*;

public class DelayActionProvider extends ActionProvider {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	// JFormDesigner - End of variables declaration //GEN-END:variables

	public DelayActionProvider() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		// //GEN-END:initComponents
	}

	@Override
	public String getName() {
		return "Delay";
	}

	@Override
	public String getDescription() {
		return "Wait for a specified amount of time";
	}

	@Override
	public Action provideAction() {
		return new DelayAction();
	}

	@SuppressWarnings("serial")
	private class DelayAction extends JPanel implements Action {
		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JSpinner delaySpinner;

		// JFormDesigner - End of variables declaration //GEN-END:variables

		private DelayAction() {
			initComponents();
		}

		@Override
		public ActionProvider getProvider() {
			return DelayActionProvider.this;
		}

		@Override
		public JPanel getOptions() {
			return this;
		}

		@Override
		public void performAction(ActionManager manager) {
			try {
				Thread.sleep((Integer) delaySpinner.getValue());
			} catch(InterruptedException exception) {}
		}

		@Override
		public String getDescription() {
			return delaySpinner.getValue() + " ms";
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			JLabel delayLabel = new JLabel();
			delaySpinner = new JSpinner();

			// ======== this ========
			setLayout(new GridBagLayout());
			((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0, 0 };
			((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0 };
			((GridBagLayout) getLayout()).columnWeights = new double[] { 0.0,
					1.0, 1.0E-4 };
			((GridBagLayout) getLayout()).rowWeights = new double[] { 0.0,
					1.0E-4 };

			// ---- delayLabel ----
			delayLabel.setText("Delay (ms):");
			add(delayLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

			// ---- delaySpinner ----
			delaySpinner.setModel(new SpinnerNumberModel(0, 0, null, 100));
			add(delaySpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			// //GEN-END:initComponents
		}
	}
}
