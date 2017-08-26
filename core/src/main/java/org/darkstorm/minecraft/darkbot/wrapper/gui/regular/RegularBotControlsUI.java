package org.darkstorm.minecraft.darkbot.wrapper.gui.regular;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.regex.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.darkstorm.minecraft.darkbot.MinecraftBot;
import org.darkstorm.minecraft.darkbot.world.entity.MainPlayerEntity;
import org.darkstorm.minecraft.darkbot.wrapper.gui.*;
import org.darkstorm.minecraft.darkbot.wrapper.gui.regular.RegularBot.RegularBotData;

@SuppressWarnings("serial")
public class RegularBotControlsUI extends BotControlsUI {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JPanel statusPanel;
	private JPanel healthPanel;
	private JPanel hungerPanel;
	private JPanel experiencePanel;
	private JScrollPane logScrollPane;
	private JEditorPane logTextArea;
	private JTextField commandField;
	private JButton sendButton;
	private JProgressBar progressBar;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private static final int MAX_LOG_SIZE = 150;

	private final List<String> log;

	private String status;
	private RegularBot bot;

	private Canvas healthBar, hungerBar, expBar;

	public RegularBotControlsUI(RegularBotData data) {
		initComponents();
		initStatus();
		log = new Vector<String>(MAX_LOG_SIZE);
		Color color = logTextArea.getForeground();
		logTextArea.setForeground(logTextArea.getBackground());
		logTextArea.setBackground(color);
		logTextArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
				Boolean.TRUE);
		// System.out.println(logTextArea.getFont().getFontName());
		logTextArea.getDocument().addDocumentListener(
				new ScrollingDocumentListener());
		logTextArea.setText("<html><body></body></html>");

		bot = new RegularBot(this, data);
	}

	private void initStatus() {
		final BufferedImage image;
		try {
			image = ImageIO.read(getClass().getResourceAsStream(
					"/icons/icons.png"));
		} catch(Exception exception) {
			exception.printStackTrace();
			return;
		}
		healthBar = new Canvas() {
			private Image full = image.getSubimage(52, 0, 9, 9)
					.getScaledInstance(18, 18, 0), half = image.getSubimage(61,
					0, 9, 9).getScaledInstance(18, 18, 0), empty = image
					.getSubimage(16, 0, 9, 9).getScaledInstance(18, 18, 0);

			@Override
			public void paint(Graphics g) {
				update(g);
			}

			@Override
			public void update(Graphics g) {
				g.clearRect(0, 0, getWidth(), getHeight());
				MinecraftBot mcbot = bot.getBot();
				if(mcbot == null)
					return;
				MainPlayerEntity player = mcbot.getPlayer();
				if(player == null)
					return;
				float health = player.getHealth();
				int width = ((empty.getWidth(null) + 2) * 10) - 2, height = empty
						.getHeight(null);
				int x = (getWidth() / 2) - (width / 2), y = (getHeight() / 2)
						- (height / 2);
				for(int i = 0; i < 10; i++) {
					g.drawImage(empty, x, y, null);
					if(health > (i * 2) + 1)
						g.drawImage(full, x, y, null);
					else if(health > i * 2)
						g.drawImage(half, x, y, null);
					x += empty.getWidth(null) + 2;
				}
			}
		};
		hungerBar = new Canvas() {
			private Image full = image.getSubimage(52, 27, 9, 9)
					.getScaledInstance(18, 18, 0), half = image.getSubimage(61,
					27, 9, 9).getScaledInstance(18, 18, 0), empty = image
					.getSubimage(16, 27, 9, 9).getScaledInstance(18, 18, 0);

			@Override
			public void paint(Graphics g) {
				update(g);
			}

			@Override
			public void update(Graphics g) {
				g.clearRect(0, 0, getWidth(), getHeight());
				MinecraftBot mcbot = bot.getBot();
				if(mcbot == null)
					return;
				MainPlayerEntity player = mcbot.getPlayer();
				if(player == null)
					return;
				int hunger = player.getHunger();
				int width = ((empty.getWidth(null) + 2) * 10) - 2, height = empty
						.getHeight(null);
				int x = (getWidth() / 2) - (width / 2), y = (getHeight() / 2)
						- (height / 2);
				for(int i = 0; i < 10; i++) {
					g.drawImage(empty, x, y, null);
					if(hunger > (i * 2) + 1)
						g.drawImage(full, x, y, null);
					else if(hunger > i * 2)
						g.drawImage(half, x, y, null);
					x += empty.getWidth(null) + 2;
				}
			}
		};

		expBar = new Canvas() {
			private Image start = image.getSubimage(0, 64, 10, 5)
					.getScaledInstance(20, 10, 0), middle = image.getSubimage(
					11, 64, 10, 5).getScaledInstance(20, 10, 0), end = image
					.getSubimage(172, 64, 10, 5).getScaledInstance(20, 10, 0);
			private Image startFull = image.getSubimage(0, 69, 10, 5)
					.getScaledInstance(20, 10, 0), middleFull = image
					.getSubimage(11, 69, 10, 5).getScaledInstance(20, 10, 0),
					endFull = image.getSubimage(172, 69, 10, 5)
							.getScaledInstance(20, 10, 0);

			@Override
			public void paint(Graphics g) {
				update(g);
			}

			@Override
			public void update(Graphics g) {
				g.clearRect(0, 0, getWidth(), getHeight());
				MinecraftBot mcbot = bot.getBot();
				if(mcbot == null)
					return;
				MainPlayerEntity player = mcbot.getPlayer();
				if(player == null)
					return;
				int exp = player.getExperience();
				int bars = (int) (((double) exp)
						/ (player.getExperienceForLevel(player
								.getExperienceLevel() + 1) - player
								.getExperienceForLevel(player
										.getExperienceLevel())) * 18);

				int width = 18 * middle.getWidth(null), height = middle
						.getHeight(null);
				int x = (getWidth() / 2) - (width / 2), y = (getHeight() / 2)
						- (height / 2);
				for(int i = 0; i < 18; i++) {
					if(i == 0)
						g.drawImage(i <= bars ? startFull : start, x, y, null);
					else if(i == 18 - 1)
						g.drawImage(i <= bars ? endFull : end, x, y, null);
					else
						g.drawImage(i <= bars ? middleFull : middle, x, y, null);
					x += middle.getWidth(null);
				}
			}
		};
		healthPanel.add(healthBar);
		hungerPanel.add(hungerBar);
		experiencePanel.add(expBar);
	}

	public void updateStatus() {
		healthBar.repaint();
		hungerBar.repaint();
		expBar.repaint();
	}

	@Override
	public void onClose() {
		bot.disconnect();
	}

	@Override
	public String getBotName() {
		return "Bot[" + bot.getData().getUsername() + "]";
	}

	@Override
	public String getStatus() {
		return status;
	}

	public synchronized void setStatus(final String status) {
		this.status = status;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				progressBar.setString(status);
			}
		});
		GUIBotWrapper.getInstance().getUI().updateStatus(this);
	}

	public synchronized void setProgress(int percentage) {
		setProgress(percentage, false);
	}

	public synchronized void setProgress(boolean indeterminate) {
		setProgress(0, indeterminate);
	}

	public synchronized void setProgress(final int percentage,
			final boolean indeterminate) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(percentage != progressBar.getValue())
					progressBar.setValue(percentage);
				if(indeterminate != progressBar.isIndeterminate())
					progressBar.setIndeterminate(indeterminate);
			}
		});
	}

	public synchronized void clearLog() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				logTextArea.setText("");
				synchronized(log) {
					log.clear();
				}
			}
		});
	}

	public synchronized void log(final String message) {
		if(message == null)
			return;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				synchronized(log) {
					if(log.size() > MAX_LOG_SIZE)
						log.remove(0);
					log.add(convertToHTMLColors(StringEscapeUtils.escapeHtml4(
							message).replace("&sect;", "§")));
					String text = "<html><body>";
					for(String line : log)
						text += "<font face=\"Monospaced\" color=\"#ffffff\">"
								+ line + "<br /></font>";
					text += "</body></html>";
					logTextArea.setText(text);
				}
			}
		});
	}

	private String convertToHTMLColors(String message) {
		Pattern pattern = Pattern.compile("(?i)§[0-9a-fk-or]");
		Matcher matcher = pattern.matcher(message);
		boolean first = true;
		while(matcher.find()) {
			String color = convertMCColor(matcher.group());
			if(color.isEmpty())
				continue;
			message = message.replace(matcher.group(), (first ? "" : "</font>")
					+ "<font color=\"#" + color + "\">");
			first = false;
		}
		if(!first)
			message += "</font>";
		return message;
	}

	private String convertMCColor(String color) {
		char value = color.charAt(1);
		switch(value) {
		case '0':
			return "FFFFFF";
		case '1':
			return "0000BE";
		case '2':
			return "00BE00";
		case '3':
			return "00BEBE";
		case '4':
			return "BE0000";
		case '5':
			return "BE00BE";
		case '6':
			return "D9A334";
		case '7':
			return "BEBEBE";
		case '8':
			return "3F3F3F";
		case '9':
			return "3F3FFE";
		case 'a':
			return "3FFE3F";
		case 'b':
			return "3FFEFE";
		case 'c':
			return "FE3F3F";
		case 'd':
			return "FE3FFE";
		case 'e':
			return "FEFE3F";
		case 'f':
			return "FFFFFF";
		case 'k':
			return "▒";
		case 'l':
			return "";
		case 'm':
			return "";
		case 'n':
			return "";
		case 'o':
			return "";
		case 'r':
			return "FFFFFF";
		default:
			return "";
		}
	}

	private void sendButtonActionPerformed(ActionEvent e) {
		String text = commandField.getText();
		if(text.isEmpty() || bot == null || bot.getBot() == null)
			return;
		commandField.setText("");
		bot.executeCommand(text);
	}

	private void commandFieldKeyPressed(KeyEvent e) {
		if(e.getModifiers() == 0 && e.getKeyCode() == KeyEvent.VK_ENTER) {
			String text = commandField.getText();
			if(text.isEmpty() || bot == null || bot.getBot() == null)
				return;
			commandField.setText("");
			bot.executeCommand(text);
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle
				.getBundle("org.darkstorm.minecraft.darkbot.wrapper.gui.enUS");
		statusPanel = new JPanel();
		healthPanel = new JPanel();
		hungerPanel = new JPanel();
		experiencePanel = new JPanel();
		logScrollPane = new JScrollPane();
		logTextArea = new JEditorPane();
		JPanel commandPanel = new JPanel();
		commandField = new JTextField();
		sendButton = new JButton();
		progressBar = new JProgressBar();

		// ======== this ========
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0 };
		((GridBagLayout) getLayout()).rowHeights = new int[] { 85, 0, 0, 0, 0 };
		((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0,
				1.0E-4 };
		((GridBagLayout) getLayout()).rowWeights = new double[] { 0.0, 1.0,
				0.0, 0.0, 1.0E-4 };

		// ======== statusPanel ========
		{
			statusPanel.setBorder(new TitledBorder(bundle
					.getString("regularbotcontrols.statusPanel.border")));
			statusPanel.setLayout(new GridBagLayout());
			((GridBagLayout) statusPanel.getLayout()).columnWeights = new double[] {
					1.0, 1.0 };
			((GridBagLayout) statusPanel.getLayout()).rowWeights = new double[] {
					1.0, 1.0 };

			// ======== healthPanel ========
			{
				healthPanel.setLayout(new BorderLayout());
			}
			statusPanel.add(healthPanel, new GridBagConstraints(0, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

			// ======== hungerPanel ========
			{
				hungerPanel.setLayout(new BorderLayout());
			}
			statusPanel.add(hungerPanel, new GridBagConstraints(1, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

			// ======== experiencePanel ========
			{
				experiencePanel.setLayout(new BorderLayout());
			}
			statusPanel.add(experiencePanel, new GridBagConstraints(0, 1, 2, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		add(statusPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ======== logScrollPane ========
		{

			// ---- logTextArea ----
			logTextArea.setEditable(false);
			logTextArea.setContentType("text/html");
			logScrollPane.setViewportView(logTextArea);
		}
		add(logScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ======== commandPanel ========
		{
			commandPanel.setLayout(new GridBagLayout());
			((GridBagLayout) commandPanel.getLayout()).columnWidths = new int[] {
					0, 0, 0 };
			((GridBagLayout) commandPanel.getLayout()).rowHeights = new int[] {
					0, 0 };
			((GridBagLayout) commandPanel.getLayout()).columnWeights = new double[] {
					1.0, 0.0, 1.0E-4 };
			((GridBagLayout) commandPanel.getLayout()).rowWeights = new double[] {
					1.0, 1.0E-4 };

			// ---- commandField ----
			commandField.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					commandFieldKeyPressed(e);
				}
			});
			commandPanel.add(commandField, new GridBagConstraints(0, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- sendButton ----
			sendButton.setText(bundle
					.getString("regularbotcontrols.sendButton.text"));
			sendButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					sendButtonActionPerformed(e);
				}
			});
			commandPanel.add(sendButton, new GridBagConstraints(1, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		add(commandPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ---- progressBar ----
		progressBar.setStringPainted(true);
		add(progressBar, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		// //GEN-END:initComponents
	}

	class ScrollingDocumentListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			maybeScrollToBottom();
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			maybeScrollToBottom();
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			maybeScrollToBottom();
		}

		private void maybeScrollToBottom() {
			JScrollBar scrollBar = logScrollPane.getVerticalScrollBar();
			boolean scrollBarAtBottom = isScrollBarFullyExtended(scrollBar);
			if(scrollBarAtBottom) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						EventQueue.invokeLater(new Runnable() {
							@Override
							public void run() {
								scrollToBottom(logTextArea);
							}
						});
					}
				});
			}
		}

		private boolean isScrollBarFullyExtended(JScrollBar vScrollBar) {
			BoundedRangeModel model = vScrollBar.getModel();
			return (model.getExtent() + model.getValue()) == model.getMaximum();
		}

		private void scrollToBottom(JComponent component) {
			Rectangle visibleRect = component.getVisibleRect();
			visibleRect.y = component.getHeight() - visibleRect.height;
			component.scrollRectToVisible(visibleRect);
		}
	}
}
