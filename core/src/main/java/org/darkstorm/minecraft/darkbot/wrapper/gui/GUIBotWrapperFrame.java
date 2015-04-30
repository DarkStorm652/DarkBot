package org.darkstorm.minecraft.darkbot.wrapper.gui;

import java.util.ResourceBundle;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class GUIBotWrapperFrame extends JFrame {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem newMenuItem;
	private JMenuItem closeMenuItem;
	private JMenuItem exitMenuItem;
	private JPanel bottomPanel;
	private JPanel panel2;
	private JLabel statusLabel;
	private ClosableTabbedPane tabbedPane;
	private JLabel titleLabel;
	private JScrollPane whatsNewScrollPane;
	private JEditorPane whatsNewEditorPane;
	private JScrollPane aboutScrollPane;
	private JEditorPane aboutEditorPane;
	private JPanel newTabPanel;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private int lastSelectedIndex = 0;

	public GUIBotWrapperFrame() {
		initComponents();
		whatsNewScrollPane.setBorder(null);
		aboutScrollPane.setBorder(null);
		titleLabel.setText(titleLabel.getText() + " " + GUIBotWrapper.getVersion());
		statusLabel.setText(titleLabel.getText());
		setVisible(true);
	}

	private void tabbedPaneStateChanged(ChangeEvent e) {
		if(tabbedPane.getSelectedComponent() != null
				&& tabbedPane.getSelectedComponent().equals(newTabPanel)) {
			tabbedPane.setSelectedIndex(lastSelectedIndex);
			new NewBotDialog(this);
		}
		lastSelectedIndex = tabbedPane.getSelectedIndex();
		if(tabbedPane.getSelectedComponent() instanceof BotControlsUI) {
			String text = ((BotControlsUI) tabbedPane.getSelectedComponent())
					.getStatus();
			statusLabel.setText(text != null ? text : titleLabel.getText());
			closeMenuItem.setEnabled(true);
		} else {
			statusLabel.setText(titleLabel.getText());
			closeMenuItem.setEnabled(false);
		}
	}

	private void newMenuItemActionPerformed(ActionEvent e) {
		new NewBotDialog(this);
	}

	private void closeMenuItemActionPerformed(ActionEvent e) {
		if(tabbedPane.getSelectedComponent() instanceof BotControlsUI) {
			tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() - 1);
			tabbedPane.removeTabAt(tabbedPane.getSelectedIndex() + 1);
			((BotControlsUI) tabbedPane.getSelectedComponent()).onClose();
		}
	}

	private void exitMenuItemActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	public void addBotControls(final BotControlsUI controls) {
		int index = tabbedPane.getTabCount() - 1;
		controls.addPropertyChangeListener("TAB_REMOVE",
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(tabbedPane.indexOfComponent(controls) == -1) {
							controls.onClose();
							controls.removePropertyChangeListener(this);
						}
					}
				});
		tabbedPane.insertClosableTab(controls.getBotName(), null, controls,
				null, index);
		tabbedPane.setSelectedIndex(index);
	}

	public synchronized void updateStatus(final BotControlsUI controls) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(tabbedPane.getSelectedComponent().equals(controls)) {
					String text = controls.getStatus();
					statusLabel.setText(text != null ? text : titleLabel
							.getText());
				}
			}
		});
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle
				.getBundle("org.darkstorm.minecraft.darkbot.wrapper.gui.enUS");
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		newMenuItem = new JMenuItem();
		closeMenuItem = new JMenuItem();
		exitMenuItem = new JMenuItem();
		bottomPanel = new JPanel();
		JSeparator bottomSeparator = new JSeparator();
		panel2 = new JPanel();
		statusLabel = new JLabel();
		tabbedPane = new ClosableTabbedPane();
		JPanel welcomePanel = new JPanel();
		titleLabel = new JLabel();
		JPanel whatsNewPanel = new JPanel();
		whatsNewScrollPane = new JScrollPane();
		whatsNewEditorPane = new JEditorPane();
		JPanel aboutPanel = new JPanel();
		aboutScrollPane = new JScrollPane();
		aboutEditorPane = new JEditorPane();
		newTabPanel = new JPanel();

		// ======== this ========
		setTitle(bundle.getString("dbmc.this.title"));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== menuBar ========
		{

			// ======== fileMenu ========
			{
				fileMenu.setText(bundle.getString("dbmc.fileMenu.text"));

				// ---- newMenuItem ----
				newMenuItem.setText(bundle.getString("dbmc.newMenuItem.text"));
				newMenuItem.setIcon(new ImageIcon(getClass().getResource(
						"/icons/add.png")));
				newMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newMenuItemActionPerformed(e);
					}
				});
				fileMenu.add(newMenuItem);

				// ---- closeMenuItem ----
				closeMenuItem.setText(bundle
						.getString("dbmc.closeMenuItem.text"));
				closeMenuItem.setIcon(new ImageIcon(getClass().getResource(
						"/icons/cancel.png")));
				closeMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						closeMenuItemActionPerformed(e);
					}
				});
				fileMenu.add(closeMenuItem);
				fileMenu.addSeparator();

				// ---- exitMenuItem ----
				exitMenuItem
						.setText(bundle.getString("dbmc.exitMenuItem.text"));
				exitMenuItem.setIcon(new ImageIcon(getClass().getResource(
						"/icons/exit.png")));
				exitMenuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						exitMenuItemActionPerformed(e);
					}
				});
				fileMenu.add(exitMenuItem);
			}
			menuBar.add(fileMenu);
		}
		setJMenuBar(menuBar);

		// ======== bottomPanel ========
		{
			bottomPanel.setLayout(new BorderLayout());
			bottomPanel.add(bottomSeparator, BorderLayout.NORTH);

			// ======== panel2 ========
			{
				panel2.setBorder(new EmptyBorder(0, 5, 2, 5));
				panel2.setLayout(new BorderLayout());

				// ---- statusLabel ----
				statusLabel.setText(" ");
				statusLabel.setFont(statusLabel.getFont().deriveFont(
						statusLabel.getFont().getStyle() & ~Font.BOLD));
				panel2.add(statusLabel, BorderLayout.CENTER);
			}
			bottomPanel.add(panel2, BorderLayout.CENTER);
		}
		contentPane.add(bottomPanel, BorderLayout.SOUTH);

		// ======== tabbedPane ========
		{
			tabbedPane.setBorder(new EmptyBorder(3, 0, 0, 0));
			tabbedPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					tabbedPaneStateChanged(e);
				}
			});

			// ======== welcomePanel ========
			{
				welcomePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				welcomePanel.setLayout(new GridBagLayout());
				((GridBagLayout) welcomePanel.getLayout()).columnWidths = new int[] {
						0, 0 };
				((GridBagLayout) welcomePanel.getLayout()).rowHeights = new int[] {
						0, 0, 0, 0 };
				((GridBagLayout) welcomePanel.getLayout()).columnWeights = new double[] {
						1.0, 1.0E-4 };
				((GridBagLayout) welcomePanel.getLayout()).rowWeights = new double[] {
						0.0, 1.0, 1.0, 1.0E-4 };

				// ---- titleLabel ----
				titleLabel.setText("DarkBot");
				titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
				titleLabel.setFont(new Font("Dialog", Font.BOLD, 26));
				welcomePanel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== whatsNewPanel ========
				{
					whatsNewPanel.setBorder(new TitledBorder(bundle
							.getString("dbmc.whatsNewPanel.border")));
					whatsNewPanel.setLayout(new BorderLayout());

					// ======== whatsNewScrollPane ========
					{

						// ---- whatsNewEditorPane ----
						whatsNewEditorPane.setEditable(false);
						whatsNewEditorPane.setContentType("text/html");
						whatsNewEditorPane.setBackground(UIManager
								.getColor("Panel.background"));
						whatsNewScrollPane.setViewportView(whatsNewEditorPane);
					}
					whatsNewPanel.add(whatsNewScrollPane, BorderLayout.CENTER);
				}
				welcomePanel.add(whatsNewPanel, new GridBagConstraints(0, 1, 1,
						1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== aboutPanel ========
				{
					aboutPanel.setBorder(new TitledBorder(bundle
							.getString("dbmc.aboutPanel.border")));
					aboutPanel.setLayout(new BorderLayout());

					// ======== aboutScrollPane ========
					{

						// ---- aboutEditorPane ----
						aboutEditorPane.setContentType("text/html");
						aboutEditorPane.setBackground(UIManager
								.getColor("Panel.background"));
						aboutEditorPane.setEditable(false);
						aboutScrollPane.setViewportView(aboutEditorPane);
					}
					aboutPanel.add(aboutScrollPane, BorderLayout.CENTER);
				}
				welcomePanel.add(aboutPanel, new GridBagConstraints(0, 2, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			tabbedPane.addTab(bundle.getString("dbmc.welcomePanel.tab.title"),
					welcomePanel);

			// ======== newTabPanel ========
			{
				newTabPanel.setLayout(new BorderLayout());
			}
			tabbedPane.addTab(bundle.getString("dbmc.newTabPanel.tab.title"),
					new ImageIcon(getClass().getResource("/icons/add.png")),
					newTabPanel);

		}
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		setSize(500, 650);
		setLocationRelativeTo(getOwner());
		// //GEN-END:initComponents
	}
}
