package org.darkstorm.minecraft.darkbot.wrapper.gui;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class BotOptionsUI extends JPanel {
	public abstract boolean areOptionsValid();

	public abstract BotControlsUI createBot();
}
