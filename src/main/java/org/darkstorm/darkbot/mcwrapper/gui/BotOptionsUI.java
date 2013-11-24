package org.darkstorm.darkbot.mcwrapper.gui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class BotOptionsUI extends JPanel {
	public abstract boolean areOptionsValid();

	public abstract BotControlsUI createBot();
}
