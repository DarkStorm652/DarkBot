package org.darkstorm.minecraft.darkbot.wrapper.gui.spam;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;

import org.darkstorm.minecraft.darkbot.wrapper.gui.*;

@SuppressWarnings("serial")
public class SpamBotOptionsUI extends BotOptionsUI {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JTextField serverField;
	private JSpinner botAmountSpinner;
	private JSpinner loginDelaySpinner;
	private JPanel panel1;
	private JPanel accountsPanel;
	private JTextField accountField;
	private JButton accountLoadButton;
	private JButton accountAddButton;
	private JButton accountRemoveButton;
	private JScrollPane accountScrollPane;
	private JList accountList;
	private JPanel proxiesPanel;
	private JTextField proxyField;
	private JButton proxyLoadButton;
	private JButton proxyAddButton;
	private JButton proxyRemoveButton;
	private JScrollPane proxyScrollPane;
	private JList proxyList;
	private JCheckBox accountsCheckBox;
	private JCheckBox proxiesCheckBox;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private final Pattern accountPattern = Pattern
			.compile("[\\w]{1,16}:[\\w]{1,16}"),
			proxyPattern = Pattern
					.compile("[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}");
	private final JFileChooser fileChooser;

	public SpamBotOptionsUI() {
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "Text files";
			}

			@Override
			public boolean accept(File f) {
				String[] parts = f.getName().split("\\.");
				return f.isDirectory()
						|| (parts.length > 1 && parts[parts.length - 1]
								.equalsIgnoreCase("txt"));
			}
		});

		initComponents();
		accountsPanel.setBorder(new ComponentTitledBorder(accountsCheckBox,
				accountsPanel, accountsPanel.getBorder()));
		proxiesPanel.setBorder(new ComponentTitledBorder(proxiesCheckBox,
				proxiesPanel, proxiesPanel.getBorder()));

		accountList.setModel(new DefaultListModel());
		proxyList.setModel(new DefaultListModel());
	}

	@Override
	public boolean areOptionsValid() {
		boolean valid = true;
		if(serverField.getText().length() == 0) {
			Util.flashRed(serverField);
			valid = false;
		}
		if(accountsCheckBox.isSelected()) {
			if(accountList.getModel().getSize() == 0) {
				Util.flashRed(accountList);
			} else if(accountList.getModel().getSize() < (Integer) botAmountSpinner
					.getValue()) {
				Util.flashRed(((JSpinner.DefaultEditor) botAmountSpinner
						.getEditor()).getTextField());
				Util.flashRed(accountList);
				valid = false;
			}
		}
		return valid;
	}

	@Override
	public BotControlsUI createBot() {
		String server = serverField.getText();
		int botAmount = (Integer) botAmountSpinner.getValue();
		int loginDelay = (Integer) loginDelaySpinner.getValue();
		List<String> accounts = new ArrayList<String>();
		List<String> proxies = new ArrayList<String>();

		DefaultListModel model = ((DefaultListModel) accountList.getModel());
		for(int i = 0; i < model.getSize(); i++) {
			String account = (String) model.get(i);
			if(accountPattern.matcher(account).matches())
				accounts.add(account);
		}
		model = ((DefaultListModel) proxyList.getModel());
		for(int i = 0; i < model.getSize(); i++) {
			String proxy = (String) model.get(i);
			if(proxyPattern.matcher(proxy).matches())
				proxies.add(proxy);
		}

		return new SpamBotControlsUI(server, botAmount, loginDelay, accounts,
				proxies);
	}

	private void accountFieldCaretUpdate(CaretEvent e) {
		accountAddButton.setEnabled(accountField.getText().length() > 0
				&& accountPattern.matcher(accountField.getText()).matches());
	}

	private void accountAddButtonActionPerformed(ActionEvent e) {
		if(accountField.getText().length() == 0
				|| !accountField.getText().contains(":"))
			return;
		((DefaultListModel) accountList.getModel()).addElement(accountField
				.getText());
		accountField.setText("");
	}

	private void accountRemoveButtonActionPerformed(ActionEvent e) {
		if(accountList.getSelectedIndex() == -1)
			return;
		((DefaultListModel) accountList.getModel()).remove(accountList
				.getSelectedIndex());
	}

	private void accountListValueChanged(ListSelectionEvent e) {
		accountRemoveButton.setEnabled(accountList.getSelectedIndex() != -1);
	}

	private void proxyFieldCaretUpdate(CaretEvent e) {
		proxyAddButton.setEnabled(proxyField.getText().length() > 0
				&& proxyPattern.matcher(proxyField.getText()).matches());
	}

	private void proxyAddButtonActionPerformed(ActionEvent e) {
		if(proxyField.getText().length() == 0
				|| !proxyField.getText().contains(":"))
			return;
		((DefaultListModel) proxyList.getModel()).addElement(proxyField
				.getText());
		proxyField.setText("");
	}

	private void proxyRemoveButtonActionPerformed(ActionEvent e) {
		if(proxyList.getSelectedIndex() == -1)
			return;
		((DefaultListModel) proxyList.getModel()).remove(proxyList
				.getSelectedIndex());
	}

	private void proxyListValueChanged(ListSelectionEvent e) {
		proxyRemoveButton.setEnabled(proxyList.getSelectedIndex() != -1);
	}

	private void accountsCheckBoxItemStateChanged(ItemEvent e) {
		boolean value = accountsCheckBox.isSelected();
		toggleComponentStates(accountsPanel, value);
		if(value) {
			accountAddButton.setEnabled(accountField.getText().length() > 0
					&& accountField.getText().contains(":"));
			accountRemoveButton
					.setEnabled(accountList.getSelectedIndex() != -1);
		}
		accountsCheckBox.setEnabled(true);
	}

	private void proxiesCheckBoxItemStateChanged(ItemEvent e) {
		boolean value = proxiesCheckBox.isSelected();
		toggleComponentStates(proxiesPanel, value);
		if(value) {
			proxyAddButton.setEnabled(proxyField.getText().length() > 0
					&& proxyField.getText().contains(":"));
			proxyRemoveButton.setEnabled(proxyList.getSelectedIndex() != -1);
		}
		proxiesCheckBox.setEnabled(true);
	}

	private void toggleComponentStates(Container container, boolean value) {
		container.setEnabled(value);
		for(Component component : container.getComponents()) {
			component.setEnabled(value);
			if(component instanceof Container)
				toggleComponentStates((Container) component, value);
		}
	}

	private void accountLoadButtonActionPerformed(ActionEvent e) {
		int returnValue = fileChooser.showOpenDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			List<String> accounts;
			try {
				accounts = loadLines(file, accountPattern);
			} catch(IOException exception) {
				JOptionPane.showMessageDialog(this,
						"Error loading accounts from " + file.getName());
				return;
			}
			if(accounts.size() == 0) {
				JOptionPane.showMessageDialog(this, "No accounts found in "
						+ file.getName());
				return;
			}
			DefaultListModel model = (DefaultListModel) accountList.getModel();
			for(String account : accounts)
				model.addElement(account);
			JOptionPane.showMessageDialog(this, "Loaded " + accounts.size()
					+ " accounts from " + file.getName());
		} else
			return;
	}

	private void proxyLoadButtonActionPerformed(ActionEvent e) {
		int returnValue = fileChooser.showOpenDialog(this);
		if(returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			List<String> proxies;
			try {
				proxies = loadLines(file, proxyPattern);
			} catch(IOException exception) {
				JOptionPane.showMessageDialog(this,
						"Error loading proxies from " + file.getName());
				return;
			}
			if(proxies.size() == 0) {
				JOptionPane.showMessageDialog(this, "No proxies found in "
						+ file.getName());
				return;
			}
			DefaultListModel model = (DefaultListModel) proxyList.getModel();
			for(String proxy : proxies)
				model.addElement(proxy);
			JOptionPane.showMessageDialog(this, "Loaded " + proxies.size()
					+ " proxies from " + file.getName());
		} else
			return;
	}

	private List<String> loadLines(File file, Pattern pattern)
			throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> matches = new ArrayList<String>();
		String line;
		while((line = reader.readLine()) != null) {
			Matcher matcher = pattern.matcher(line);
			if(matcher.find())
				matches.add(matcher.group());
		}
		return matches;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("org.darkstorm.minecraft.darkbot.wrapper.gui.enUS");
		JLabel serverLabel = new JLabel();
		serverField = new JTextField();
		JLabel botAmountLabel = new JLabel();
		botAmountSpinner = new JSpinner();
		JLabel loginDelayLabel = new JLabel();
		loginDelaySpinner = new JSpinner();
		panel1 = new JPanel();
		accountsPanel = new JPanel();
		JPanel accountControlPanel = new JPanel();
		accountField = new JTextField();
		JPanel accountButtonPanel = new JPanel();
		accountLoadButton = new JButton();
		accountAddButton = new JButton();
		accountRemoveButton = new JButton();
		accountScrollPane = new JScrollPane();
		accountList = new JList();
		proxiesPanel = new JPanel();
		JPanel proxyControlPanel = new JPanel();
		proxyField = new JTextField();
		JPanel proxyButtonPanel = new JPanel();
		proxyLoadButton = new JButton();
		proxyAddButton = new JButton();
		proxyRemoveButton = new JButton();
		proxyScrollPane = new JScrollPane();
		proxyList = new JList();
		accountsCheckBox = new JCheckBox();
		proxiesCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new GridBagLayout());
		((GridBagLayout)getLayout()).columnWidths = new int[] {0, 0, 0};
		((GridBagLayout)getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
		((GridBagLayout)getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
		((GridBagLayout)getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

		//---- serverLabel ----
		serverLabel.setText(bundle.getString("spambotoptions.serverLabel.text"));
		add(serverLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- serverField ----
		serverField.setToolTipText(bundle.getString("spambotoptions.serverField.toolTipText"));
		add(serverField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- botAmountLabel ----
		botAmountLabel.setText(bundle.getString("spambotoptions.botAmountLabel.text"));
		add(botAmountLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- botAmountSpinner ----
		botAmountSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
		botAmountSpinner.setToolTipText(bundle.getString("spambotoptions.botAmountSpinner.toolTipText"));
		add(botAmountSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//---- loginDelayLabel ----
		loginDelayLabel.setText(bundle.getString("spambotoptions.loginDelayLabel.text"));
		add(loginDelayLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 5), 0, 0));

		//---- loginDelaySpinner ----
		loginDelaySpinner.setModel(new SpinnerNumberModel(0, 0, null, 50));
		loginDelaySpinner.setToolTipText(bundle.getString("spambotoptions.loginDelaySpinner.toolTipText"));
		add(loginDelaySpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 5, 0), 0, 0));

		//======== panel1 ========
		{
			panel1.setLayout(new GridLayout(2, 0));

			//======== accountsPanel ========
			{
				accountsPanel.setBorder(new TitledBorder(""));
				accountsPanel.setLayout(new BorderLayout());

				//======== accountControlPanel ========
				{
					accountControlPanel.setLayout(new BorderLayout());

					//---- accountField ----
					accountField.setToolTipText(bundle.getString("spambotoptions.accountField.toolTipText"));
					accountField.addCaretListener(new CaretListener() {
						public void caretUpdate(CaretEvent e) {
							accountFieldCaretUpdate(e);
						}
					});
					accountControlPanel.add(accountField, BorderLayout.CENTER);

					//======== accountButtonPanel ========
					{
						accountButtonPanel.setLayout(new GridLayout());

						//---- accountLoadButton ----
						accountLoadButton.setText(bundle.getString("spambotoptions.accountLoadButton.text"));
						accountLoadButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								accountLoadButtonActionPerformed(e);
							}
						});
						accountButtonPanel.add(accountLoadButton);

						//---- accountAddButton ----
						accountAddButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png")));
						accountAddButton.setEnabled(false);
						accountAddButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								accountAddButtonActionPerformed(e);
							}
						});
						accountButtonPanel.add(accountAddButton);

						//---- accountRemoveButton ----
						accountRemoveButton.setIcon(new ImageIcon(getClass().getResource("/icons/remove.png")));
						accountRemoveButton.setEnabled(false);
						accountRemoveButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								accountRemoveButtonActionPerformed(e);
							}
						});
						accountButtonPanel.add(accountRemoveButton);
					}
					accountControlPanel.add(accountButtonPanel, BorderLayout.EAST);
				}
				accountsPanel.add(accountControlPanel, BorderLayout.NORTH);

				//======== accountScrollPane ========
				{

					//---- accountList ----
					accountList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					accountList.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent e) {
							accountListValueChanged(e);
						}
					});
					accountScrollPane.setViewportView(accountList);
				}
				accountsPanel.add(accountScrollPane, BorderLayout.CENTER);
			}
			panel1.add(accountsPanel);

			//======== proxiesPanel ========
			{
				proxiesPanel.setBorder(new TitledBorder(""));
				proxiesPanel.setLayout(new BorderLayout());

				//======== proxyControlPanel ========
				{
					proxyControlPanel.setLayout(new BorderLayout());

					//---- proxyField ----
					proxyField.setToolTipText(bundle.getString("spambotoptions.proxyField.toolTipText"));
					proxyField.addCaretListener(new CaretListener() {
						public void caretUpdate(CaretEvent e) {
							proxyFieldCaretUpdate(e);
						}
					});
					proxyControlPanel.add(proxyField, BorderLayout.CENTER);

					//======== proxyButtonPanel ========
					{
						proxyButtonPanel.setLayout(new GridLayout());

						//---- proxyLoadButton ----
						proxyLoadButton.setText(bundle.getString("spambotoptions.proxyLoadButton.text"));
						proxyLoadButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								proxyLoadButtonActionPerformed(e);
							}
						});
						proxyButtonPanel.add(proxyLoadButton);

						//---- proxyAddButton ----
						proxyAddButton.setIcon(new ImageIcon(getClass().getResource("/icons/add.png")));
						proxyAddButton.setEnabled(false);
						proxyAddButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								proxyAddButtonActionPerformed(e);
							}
						});
						proxyButtonPanel.add(proxyAddButton);

						//---- proxyRemoveButton ----
						proxyRemoveButton.setIcon(new ImageIcon(getClass().getResource("/icons/remove.png")));
						proxyRemoveButton.setEnabled(false);
						proxyRemoveButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								proxyRemoveButtonActionPerformed(e);
							}
						});
						proxyButtonPanel.add(proxyRemoveButton);
					}
					proxyControlPanel.add(proxyButtonPanel, BorderLayout.EAST);
				}
				proxiesPanel.add(proxyControlPanel, BorderLayout.NORTH);

				//======== proxyScrollPane ========
				{

					//---- proxyList ----
					proxyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					proxyList.addListSelectionListener(new ListSelectionListener() {
						public void valueChanged(ListSelectionEvent e) {
							proxyListValueChanged(e);
						}
					});
					proxyScrollPane.setViewportView(proxyList);
				}
				proxiesPanel.add(proxyScrollPane, BorderLayout.CENTER);
			}
			panel1.add(proxiesPanel);
		}
		add(panel1, new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
			GridBagConstraints.CENTER, GridBagConstraints.BOTH,
			new Insets(0, 0, 0, 0), 0, 0));

		//---- accountsCheckBox ----
		accountsCheckBox.setText(bundle.getString("spambotoptions.accountsCheckBox.text"));
		accountsCheckBox.setSelected(true);
		accountsCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				accountsCheckBoxItemStateChanged(e);
			}
		});

		//---- proxiesCheckBox ----
		proxiesCheckBox.setText(bundle.getString("spambotoptions.proxiesCheckBox.text"));
		proxiesCheckBox.setSelected(true);
		proxiesCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				proxiesCheckBoxItemStateChanged(e);
			}
		});
		// //GEN-END:initComponents
	}
}
