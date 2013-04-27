package org.darkstorm.darkbot.darkbotmc;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class BotControlsUI extends JPanel {
	public abstract String getBotName();

	public abstract String getStatus();

	public abstract void onClose();
}
