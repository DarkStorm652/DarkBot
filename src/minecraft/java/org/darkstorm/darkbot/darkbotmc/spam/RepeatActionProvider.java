package org.darkstorm.darkbot.darkbotmc.spam;

import java.awt.CardLayout;

import javax.swing.JPanel;

public class RepeatActionProvider extends ActionProvider {
	public RepeatActionProvider() {
	}

	@Override
	public String getName() {
		return "Repeat";
	}

	@Override
	public String getDescription() {
		return "Repeat all the tasks.";
	}

	@Override
	public Action provideAction() {
		return new RepeatAction();
	}

	@SuppressWarnings("serial")
	private class RepeatAction extends JPanel implements Action {
		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		// JFormDesigner - End of variables declaration //GEN-END:variables

		private RepeatAction() {
			initComponents();
		}

		@Override
		public ActionProvider getProvider() {
			return RepeatActionProvider.this;
		}

		@Override
		public JPanel getOptions() {
			return this;
		}

		@Override
		public void performAction(ActionManager manager) {
			manager.restart();
		}

		@Override
		public String getDescription() {
			return "";
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents

			// ======== this ========
			setLayout(new CardLayout());
			// //GEN-END:initComponents
		}
	}
}
