package org.darkstorm.darkbot.darkbotmc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.Timer;

public final class Util {
	private static final Map<Component, Timer> components;
	private static final Font minecraftFont;

	static {
		components = new HashMap<Component, Timer>();
		Font font;
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, Util.class
					.getResourceAsStream("/resources/minecraft_font.ttf"));
		} catch(Exception exception) {
			font = Font.getFont("Monospace");
		}
		minecraftFont = font;
	}

	private Util() {
	}

	public static void flashRed(final Component component) {
		synchronized(components) {
			Timer oldTimer = components.get(component);
			if(oldTimer != null && oldTimer.isRunning())
				oldTimer.stop();
			final Timer timer = new FadeTimer(component);
			timer.start();
			components.put(component, timer);
		}
	}

	@SuppressWarnings("serial")
	private static class FadeTimer extends Timer {
		private Component component;
		private Color originalBG;

		public FadeTimer(final Component component) {
			super(50, null);
			this.component = component;
			originalBG = component.getBackground();
			component.setBackground(new Color(255, 100, 100));
			ActionListener actionListener = new ActionListener() {
				private int r = 255, g = 100, b = 100;

				@Override
				public void actionPerformed(ActionEvent e) {
					Color color = new Color(r, g, b);
					component.setBackground(color);
					component.repaint();
					if(originalBG.equals(color))
						stop();
					updateColors(20);
				}

				private void updateColors(int rate) {
					r -= Math.min(rate, r - originalBG.getRed());
					g -= originalBG.getGreen() < g ? Math.min(rate, g
							- originalBG.getGreen()) : Math.max(-rate, g
							- originalBG.getGreen());
					b -= originalBG.getBlue() < b ? Math.min(rate, b
							- originalBG.getBlue()) : Math.max(-rate, b
							- originalBG.getBlue());
				}
			};
			addActionListener(actionListener);
			setRepeats(true);
			setInitialDelay(1500);
		}

		@Override
		public void stop() {
			super.stop();
			component.setBackground(originalBG);
		}
	}

	public static Font get1MinecraftFont() {
		// return new Font("Monospaced", Font.PLAIN, 12);
		return minecraftFont;
	}
}
