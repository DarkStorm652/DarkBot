package org.darkstorm.darkbot.darkbotmc;

import java.util.ResourceBundle;

import javax.swing.UIManager;

import org.darkstorm.darkbot.DarkBot;

import com.jtattoo.plaf.acryl.AcrylLookAndFeel;

public final class DarkBotMC {
	private static DarkBotMC instance;
	private static final String version;

	private final DarkBot darkBot;
	private final DarkBotMCUI ui;

	static {
		ResourceBundle resources = ResourceBundle.getBundle("org.darkstorm.darkbot.darkbotmc.dbmc");
		version = resources.getString("version");
	}

	private DarkBotMC() {
		if(instance != null)
			throw new IllegalStateException();
		instance = this;
		try {
			UIManager.setLookAndFeel(new AcrylLookAndFeel());
		} catch(Exception exception) {}
		darkBot = new DarkBot();
		ui = new DarkBotMCUI();
	}

	public DarkBot getDarkBot() {
		return darkBot;
	}

	public DarkBotMCUI getUI() {
		return ui;
	}

	public static DarkBotMC getInstance() {
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
		new DarkBotMC();
	}
}