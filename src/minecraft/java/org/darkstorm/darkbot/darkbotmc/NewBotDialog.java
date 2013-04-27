package org.darkstorm.darkbot.darkbotmc;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.darkstorm.darkbot.darkbotmc.regular.RegularBotOptionsUI;
import org.darkstorm.darkbot.darkbotmc.spam.SpamBotOptionsUI;

@SuppressWarnings("serial")
public class NewBotDialog extends JDialog {
	private final DarkBotMCUI ui;
	private final Map<String, BotOptionsUI> optionsUIs;

	public NewBotDialog(DarkBotMCUI ui) {
		super(ui);
		this.ui = ui;
		optionsUIs = new HashMap<String, BotOptionsUI>();
		optionsUIs.put("Regular", new RegularBotOptionsUI());
		optionsUIs.put("Spambot", new SpamBotOptionsUI());
		initComponents();
		updateSelectedBotType();
		typeComboBox.setModel(new DefaultComboBoxModel(optionsUIs.keySet()
				.toArray()));
		setVisible(true);
	}

	private void typeComboBoxItemStateChanged(ItemEvent e) {
		updateSelectedBotType();
	}

	private void okButtonActionPerformed(ActionEvent e) {
		BotOptionsUI optionsUI = (BotOptionsUI) optionsPanel.getComponent(0);
		if(!optionsUI.areOptionsValid())
			return;
		setVisible(false);
		BotControlsUI controlsUI = optionsUI.createBot();
		if(controlsUI != null)
			ui.addBotControls(controlsUI);
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		setVisible(false);
	}

	private void updateSelectedBotType() {
		String selectedItem = (String) typeComboBox.getSelectedItem();
		if(selectedItem == null) {
			typeComboBox.setSelectedIndex(0);
			updateSelectedBotType();
		}
		BotOptionsUI optionsUI = optionsUIs.get(selectedItem);
		if(optionsUI == null)
			return;
		optionsPanel.removeAll();
		optionsPanel.add(optionsUI, BorderLayout.CENTER);
		validate();
		optionsPanel.repaint();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle
				.getBundle("org.darkstorm.darkbot.darkbotmc.enUS");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		JLabel typeLabel = new JLabel();
		typeComboBox = new JComboBox();
		separator1 = new JSeparator();
		optionsPanel = new JPanel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		// ======== this ========
		setTitle(bundle.getString("newbotdialog.this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setLayout(new GridBagLayout());
				((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {
						0, 0, 0 };
				((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {
						0, 0, 0, 0 };
				((GridBagLayout) contentPanel.getLayout()).columnWeights = new double[] {
						0.0, 1.0, 1.0E-4 };
				((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {
						0.0, 0.0, 1.0, 1.0E-4 };

				// ---- typeLabel ----
				typeLabel.setText(bundle
						.getString("newbotdialog.typeLabel.text"));
				contentPanel.add(typeLabel, new GridBagConstraints(0, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

				// ---- typeComboBox ----
				typeComboBox.setModel(new DefaultComboBoxModel(new String[] {
						"Regular", "Spambot" }));
				typeComboBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						typeComboBoxItemStateChanged(e);
					}
				});
				contentPanel.add(typeComboBox, new GridBagConstraints(1, 0, 1,
						1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
				contentPanel.add(separator1, new GridBagConstraints(0, 1, 2, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== optionsPanel ========
				{
					optionsPanel.setLayout(new BorderLayout());
				}
				contentPanel.add(optionsPanel, new GridBagConstraints(0, 2, 2,
						1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			// ======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {
						0, 85, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {
						1.0, 0.0, 0.0 };

				// ---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
						0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

				// ---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(400, 550);
		setLocationRelativeTo(getOwner());
		// //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JComboBox typeComboBox;
	private JSeparator separator1;
	private JPanel optionsPanel;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables
}
