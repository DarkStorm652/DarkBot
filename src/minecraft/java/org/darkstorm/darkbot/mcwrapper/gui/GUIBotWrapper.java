package org.darkstorm.darkbot.mcwrapper.gui;

import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.darkstorm.darkbot.DarkBot;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

public final class GUIBotWrapper {
	private static GUIBotWrapper instance;
	private static final String version;

	private final DarkBot darkBot;
	private final GUIBotWrapperFrame ui;

	static {
		ResourceBundle resources = ResourceBundle.getBundle("org.darkstorm.darkbot.mcwrapper.gui.bot");
		version = resources.getString("version");
	}

	private GUIBotWrapper() {
		if(instance != null)
			throw new IllegalStateException();
		instance = this;
		try {
			UIManager.setLookAndFeel(new AcrylLookAndFeel());
		} catch(Exception exception) {}
		darkBot = new DarkBot();
		ui = new GUIBotWrapperFrame();
	}

	public DarkBot getDarkBot() {
		return darkBot;
	}

	public GUIBotWrapperFrame getUI() {
		return ui;
	}

	public static GUIBotWrapper getInstance() {
		return instance;
	}

	public static String getVersion() {
		return version;
	}

	public static void main(String[] args) {
		if(args.length != 1 || !args[0].equals("--force")) {
			System.err.println("The GUI is not complete and thus is not recommended.");
			System.err.println("If you wish to proceed, run with `--force`.");
			System.exit(1);
			return;
		}
		new GUIBotWrapper();
	}
}