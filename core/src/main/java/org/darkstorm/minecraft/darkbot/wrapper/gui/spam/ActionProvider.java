package org.darkstorm.darkbot.mcwrapper.gui.spam;

import javax.swing.JPanel;

public abstract class ActionProvider {
	public abstract String getName();

	public abstract String getDescription();

	public abstract Action provideAction();

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return getName().hashCode();
	}

	public interface Action {
		public ActionProvider getProvider();

		public JPanel getOptions();

		public void performAction(ActionManager manager);

		public String getDescription();
	}
}
