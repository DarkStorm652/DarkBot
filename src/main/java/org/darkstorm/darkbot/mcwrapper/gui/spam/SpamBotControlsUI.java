package org.darkstorm.darkbot.mcwrapper.gui.spam;

import java.util.*;
import java.util.List;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import org.darkstorm.darkbot.mcwrapper.gui.*;
import org.darkstorm.darkbot.mcwrapper.gui.spam.SpamBot.SpamBotData;

@SuppressWarnings({ "serial", "rawtypes" })
public class SpamBotControlsUI extends BotControlsUI {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JProgressBar progressBar;
	private JToggleButton loginButton;
	private JToggleButton connectButton;
	private JToggleButton spamButton;
	private JButton joinActionsAddButton;
	private JButton joinActionsRemoveButton;
	private JList joinActionsList;
	private JButton spamActionsAddButton;
	private JButton spamActionsRemoveButton;
	private JList spamActionsList;
	private JTable botTable;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private final List<ActionProvider> actionProviders;

	private final String server;
	private final int botAmount, loginDelay;
	private final List<String> accounts, proxies;

	private final List<String> usedAccounts = new ArrayList<String>();
	private final Random random = new Random();

	private String status = "!!!!!!!!!!!!!!!!!!";
	private BotTask task = null;

	public SpamBotControlsUI(String server, int botAmount, int loginDelay,
			List<String> accounts, List<String> proxies) {
		this.server = server;
		this.botAmount = botAmount;
		this.loginDelay = loginDelay;
		this.accounts = Collections.unmodifiableList(accounts);
		this.proxies = Collections.unmodifiableList(proxies);

		actionProviders = Collections.unmodifiableList(loadActionProviders());

		initComponents();

		joinActionsList.setModel(new DefaultListModel());
		spamActionsList.setModel(new DefaultListModel());
	}

	private List<ActionProvider> loadActionProviders() {
		List<ActionProvider> actionProviders = new ArrayList<ActionProvider>();
		actionProviders.add(new SayActionProvider());
		actionProviders.add(new DelayActionProvider());
		actionProviders.add(new RepeatActionProvider());
		return actionProviders;
	}

	@Override
	public String getBotName() {
		return "Spambot[" + server + "]";
	}

	@Override
	public String getStatus() {
		return status;
	}

	private void loginButtonItemStateChanged(ItemEvent e) {
		if(loginButton.isSelected()) {
			connectButton.setEnabled(true);
			task = BotTask.LOGIN;
			for(int i = 0; i < botAmount; i++) {
				SpamBotData data = new SpamBotData();
				synchronized(usedAccounts) {
					if(usedAccounts.size() >= accounts.size())
						break;
					String account;
					do {
						account = accounts.get(random.nextInt(accounts.size()));
					} while(usedAccounts.contains(account));
					data.setUsername(account);
				}
			}
		} else {
			spamButton.setEnabled(false);
			spamButton.setSelected(false);
			connectButton.setEnabled(false);
			connectButton.setSelected(false);
			task = null;
		}
	}

	@Override
	public void onClose() {
	}

	private void connectButtonItemStateChanged(ItemEvent e) {
		if(connectButton.isSelected()) {
			spamButton.setEnabled(true);
			task = BotTask.CONNECT;
		} else {
			spamButton.setEnabled(false);
			spamButton.setSelected(false);
			task = BotTask.LOGIN;
		}
	}

	private void spamButtonItemStateChanged(ItemEvent e) {
		if(loginButton.isSelected()) {
			task = BotTask.SPAM;
		} else
			task = BotTask.CONNECT;
	}

	private void joinActionsAddButtonActionPerformed(ActionEvent e) {
		ActionDialog dialog = new ActionDialog();
		ActionProvider actionProvider = dialog.getResult();
		if(actionProvider == null)
			return;
		ActionListValue value = new ActionListValue();
		value.provider = actionProvider;
		value.action = dialog.actions.get(actionProvider);
		((DefaultListModel) joinActionsList.getModel()).addElement(value);
	}

	private void joinActionsRemoveButtonActionPerformed(ActionEvent e) {
		if(joinActionsList.getSelectedIndex() == -1)
			return;
		((DefaultListModel) joinActionsList.getModel()).remove(joinActionsList
				.getSelectedIndex());
	}

	private void joinActionsListValueChanged(ListSelectionEvent e) {
		joinActionsRemoveButton
				.setEnabled(joinActionsList.getSelectedIndex() != -1);
	}

	private void spamActionsAddButtonActionPerformed(ActionEvent e) {
		ActionDialog dialog = new ActionDialog();
		ActionProvider actionProvider = dialog.getResult();
		if(actionProvider == null)
			return;
		ActionListValue value = new ActionListValue();
		value.provider = actionProvider;
		value.action = dialog.actions.get(actionProvider);
		((DefaultListModel) spamActionsList.getModel()).addElement(value);
	}

	private void spamActionsRemoveButtonActionPerformed(ActionEvent e) {
		if(spamActionsList.getSelectedIndex() == -1)
			return;
		((DefaultListModel) spamActionsList.getModel()).remove(spamActionsList
				.getSelectedIndex());
	}

	private void spamActionsListValueChanged(ListSelectionEvent e) {
		spamActionsRemoveButton
				.setEnabled(spamActionsList.getSelectedIndex() != -1);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle
				.getBundle("org.darkstorm.darkbot.mcwrapper.gui.enUS");
		JPanel mainControlPanel = new JPanel();
		progressBar = new JProgressBar();
		loginButton = new JToggleButton();
		connectButton = new JToggleButton();
		spamButton = new JToggleButton();
		JPanel actionsPanel = new JPanel();
		JPanel joinActionsPanel = new JPanel();
		JPanel joinActionsControlPanel = new JPanel();
		joinActionsAddButton = new JButton();
		joinActionsRemoveButton = new JButton();
		JScrollPane joinActionsScrollPane = new JScrollPane();
		joinActionsList = new JList();
		JPanel spamActionsPanel = new JPanel();
		JPanel spamActionsControlPanel = new JPanel();
		spamActionsAddButton = new JButton();
		spamActionsRemoveButton = new JButton();
		JScrollPane spamActionsScrollPane = new JScrollPane();
		spamActionsList = new JList();
		JScrollPane botScrollPane = new JScrollPane();
		botTable = new JTable();

		// ======== this ========
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0 };
		((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 155, 0, 0 };
		((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0,
				1.0E-4 };
		((GridBagLayout) getLayout()).rowWeights = new double[] { 0.0, 0.0,
				1.0, 1.0E-4 };

		// ======== mainControlPanel ========
		{
			mainControlPanel.setLayout(new GridBagLayout());
			((GridBagLayout) mainControlPanel.getLayout()).columnWidths = new int[] {
					0, 0, 0, 0, 0 };
			((GridBagLayout) mainControlPanel.getLayout()).rowHeights = new int[] {
					0, 0 };
			((GridBagLayout) mainControlPanel.getLayout()).columnWeights = new double[] {
					1.0, 0.0, 0.0, 0.0, 1.0E-4 };
			((GridBagLayout) mainControlPanel.getLayout()).rowWeights = new double[] {
					0.0, 1.0E-4 };
			mainControlPanel.add(progressBar, new GridBagConstraints(0, 0, 1,
					1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- loginButton ----
			loginButton.setText(bundle
					.getString("spambotcontrols.loginButton.text"));
			loginButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					loginButtonItemStateChanged(e);
				}
			});
			mainControlPanel.add(loginButton, new GridBagConstraints(1, 0, 1,
					1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- connectButton ----
			connectButton.setText(bundle
					.getString("spambotcontrols.connectButton.text"));
			connectButton.setEnabled(false);
			connectButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					connectButtonItemStateChanged(e);
				}
			});
			mainControlPanel.add(connectButton, new GridBagConstraints(2, 0, 1,
					1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- spamButton ----
			spamButton.setText(bundle
					.getString("spambotcontrols.spamButton.text"));
			spamButton.setEnabled(false);
			spamButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					spamButtonItemStateChanged(e);
				}
			});
			mainControlPanel.add(spamButton, new GridBagConstraints(3, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		add(mainControlPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ======== actionsPanel ========
		{
			actionsPanel.setLayout(new GridLayout(1, 2));

			// ======== joinActionsPanel ========
			{
				joinActionsPanel.setBorder(new TitledBorder("Join Actions"));
				joinActionsPanel.setLayout(new BorderLayout());

				// ======== joinActionsControlPanel ========
				{
					joinActionsControlPanel.setLayout(new GridBagLayout());
					((GridBagLayout) joinActionsControlPanel.getLayout()).columnWidths = new int[] {
							0, 0, 0, 0 };
					((GridBagLayout) joinActionsControlPanel.getLayout()).rowHeights = new int[] {
							0, 0 };
					((GridBagLayout) joinActionsControlPanel.getLayout()).columnWeights = new double[] {
							1.0, 0.0, 0.0, 1.0E-4 };
					((GridBagLayout) joinActionsControlPanel.getLayout()).rowWeights = new double[] {
							1.0, 1.0E-4 };

					// ---- joinActionsAddButton ----
					joinActionsAddButton.setIcon(new ImageIcon(getClass()
							.getResource("/icons/add.png")));
					joinActionsAddButton
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									joinActionsAddButtonActionPerformed(e);
								}
							});
					joinActionsControlPanel.add(joinActionsAddButton,
							new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

					// ---- joinActionsRemoveButton ----
					joinActionsRemoveButton.setIcon(new ImageIcon(getClass()
							.getResource("/icons/remove.png")));
					joinActionsRemoveButton.setEnabled(false);
					joinActionsRemoveButton
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									joinActionsRemoveButtonActionPerformed(e);
								}
							});
					joinActionsControlPanel.add(joinActionsRemoveButton,
							new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));
				}
				joinActionsPanel.add(joinActionsControlPanel,
						BorderLayout.SOUTH);

				// ======== joinActionsScrollPane ========
				{

					// ---- joinActionsList ----
					joinActionsList
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					joinActionsList
							.addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent e) {
									joinActionsListValueChanged(e);
								}
							});
					joinActionsScrollPane.setViewportView(joinActionsList);
				}
				joinActionsPanel
						.add(joinActionsScrollPane, BorderLayout.CENTER);
			}
			actionsPanel.add(joinActionsPanel);

			// ======== spamActionsPanel ========
			{
				spamActionsPanel.setBorder(new TitledBorder("Spam Actions"));
				spamActionsPanel.setLayout(new BorderLayout());

				// ======== spamActionsControlPanel ========
				{
					spamActionsControlPanel.setLayout(new GridBagLayout());
					((GridBagLayout) spamActionsControlPanel.getLayout()).columnWidths = new int[] {
							0, 0, 0, 0 };
					((GridBagLayout) spamActionsControlPanel.getLayout()).rowHeights = new int[] {
							0, 0 };
					((GridBagLayout) spamActionsControlPanel.getLayout()).columnWeights = new double[] {
							1.0, 0.0, 0.0, 1.0E-4 };
					((GridBagLayout) spamActionsControlPanel.getLayout()).rowWeights = new double[] {
							1.0, 1.0E-4 };

					// ---- spamActionsAddButton ----
					spamActionsAddButton.setIcon(new ImageIcon(getClass()
							.getResource("/icons/add.png")));
					spamActionsAddButton
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									spamActionsAddButtonActionPerformed(e);
								}
							});
					spamActionsControlPanel.add(spamActionsAddButton,
							new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

					// ---- spamActionsRemoveButton ----
					spamActionsRemoveButton.setIcon(new ImageIcon(getClass()
							.getResource("/icons/remove.png")));
					spamActionsRemoveButton.setEnabled(false);
					spamActionsRemoveButton
							.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									spamActionsRemoveButtonActionPerformed(e);
								}
							});
					spamActionsControlPanel.add(spamActionsRemoveButton,
							new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));
				}
				spamActionsPanel.add(spamActionsControlPanel,
						BorderLayout.SOUTH);

				// ======== spamActionsScrollPane ========
				{

					// ---- spamActionsList ----
					spamActionsList
							.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					spamActionsList
							.addListSelectionListener(new ListSelectionListener() {
								public void valueChanged(ListSelectionEvent e) {
									spamActionsListValueChanged(e);
								}
							});
					spamActionsScrollPane.setViewportView(spamActionsList);
				}
				spamActionsPanel
						.add(spamActionsScrollPane, BorderLayout.CENTER);
			}
			actionsPanel.add(spamActionsPanel);
		}
		add(actionsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ======== botScrollPane ========
		{

			// ---- botTable ----
			botTable.setModel(new DefaultTableModel(new Object[][] {},
					new String[] { "Username", "Status" }) {
				Class[] columnTypes = new Class[] { String.class, Object.class };
				boolean[] columnEditable = new boolean[] { false, false };

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			{
				TableColumnModel cm = botTable.getColumnModel();
				cm.getColumn(0).setMinWidth(120);
				cm.getColumn(0).setPreferredWidth(120);
				cm.getColumn(1).setPreferredWidth(250);
			}
			botScrollPane.setViewportView(botTable);
		}
		add(botScrollPane, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		// //GEN-END:initComponents
	}

	private enum BotTask {
		LOGIN,
		CONNECT,
		SPAM
	}

	private class ActionDialog extends JDialog {
		private ActionProvider actionProvider;
		private Map<ActionProvider, ActionProvider.Action> actions = new HashMap<ActionProvider, ActionProvider.Action>();

		private ActionDialog() {
			super(GUIBotWrapper.getInstance().getUI());
			setVisible(false);
			initComponents();
			actionComboBox.setModel(new DefaultComboBoxModel(actionProviders
					.toArray()));
			for(ActionProvider actionProvider : actionProviders) {
				ActionProvider.Action action = actionProvider.provideAction();
				actions.put(actionProvider, action);
				actionOptionsPanel.add(action.getOptions(),
						actionProvider.getName());
			}
		}

		public ActionProvider getResult() {
			setVisible(true);
			setVisible(false);
			return actionProvider;
		}

		private void okButtonActionPerformed(ActionEvent e) {
			actionProvider = (ActionProvider) ((DefaultComboBoxModel) actionComboBox
					.getModel()).getSelectedItem();
			setVisible(false);
		}

		private void cancelButtonActionPerformed(ActionEvent e) {
			setVisible(false);
		}

		private void actionComboBoxItemStateChanged(ItemEvent e) {
			((CardLayout) actionOptionsPanel.getLayout()).show(
					actionOptionsPanel, actionComboBox.getSelectedItem()
							.toString());
		}

		private void actionDialogWindowClosed(WindowEvent e) {
			setVisible(false);
		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			ResourceBundle bundle = ResourceBundle
					.getBundle("org.darkstorm.darkbot.mcwrapper.gui.enUS");
			JPanel containerPanel = new JPanel();
			JPanel bottomPanel = new JPanel();
			JPanel buttonPanel = new JPanel();
			okButton = new JButton();
			cancelButton = new JButton();
			actionComboBox = new JComboBox();
			descriptionLabel = new JLabel();
			actionOptionsPanel = new JPanel();

			// ======== this ========
			setTitle(bundle.getString("spambotcontrols.actionDialog.title"));
			setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
			setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					actionDialogWindowClosed(e);
				}
			});
			Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout());

			// ======== containerPanel ========
			{
				containerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
				containerPanel.setLayout(new GridBagLayout());
				((GridBagLayout) containerPanel.getLayout()).columnWidths = new int[] {
						0, 0 };
				((GridBagLayout) containerPanel.getLayout()).rowHeights = new int[] {
						0, 0, 0, 0, 0 };
				((GridBagLayout) containerPanel.getLayout()).columnWeights = new double[] {
						1.0, 1.0E-4 };
				((GridBagLayout) containerPanel.getLayout()).rowWeights = new double[] {
						0.0, 0.0, 1.0, 0.0, 1.0E-4 };

				// ======== bottomPanel ========
				{
					bottomPanel.setLayout(new BorderLayout());

					// ======== buttonPanel ========
					{
						buttonPanel.setLayout(new GridLayout());

						// ---- okButton ----
						okButton.setText(bundle
								.getString("spambotcontrols.okButton.text"));
						okButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								okButtonActionPerformed(e);
							}
						});
						buttonPanel.add(okButton);

						// ---- cancelButton ----
						cancelButton
								.setText(bundle
										.getString("spambotcontrols.cancelButton.text"));
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								cancelButtonActionPerformed(e);
							}
						});
						buttonPanel.add(cancelButton);
					}
					bottomPanel.add(buttonPanel, BorderLayout.EAST);
				}
				containerPanel.add(bottomPanel, new GridBagConstraints(0, 3, 1,
						1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

				// ---- actionComboBox ----
				actionComboBox.addItemListener(new ItemListener() {
					public void itemStateChanged(ItemEvent e) {
						actionComboBoxItemStateChanged(e);
					}
				});
				containerPanel.add(actionComboBox, new GridBagConstraints(0, 0,
						1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ---- descriptionLabel ----
				descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(
						descriptionLabel.getFont().getStyle() | Font.BOLD,
						descriptionLabel.getFont().getSize() + 2f));
				containerPanel.add(descriptionLabel, new GridBagConstraints(0,
						1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

				// ======== actionOptionsPanel ========
				{
					actionOptionsPanel.setLayout(new CardLayout());
				}
				containerPanel.add(actionOptionsPanel, new GridBagConstraints(
						0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
			}
			contentPane.add(containerPanel, BorderLayout.CENTER);
			setSize(295, 230);
			setLocationRelativeTo(getOwner());
			// //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JButton okButton;
		private JButton cancelButton;
		private JComboBox actionComboBox;
		private JLabel descriptionLabel;
		private JPanel actionOptionsPanel;
		// JFormDesigner - End of variables declaration //GEN-END:variables
	}

	public void setStatus(SpamBot spamBot, String status2) {
	}

	public void setProgress(SpamBot spamBot, boolean indeterminate) {
	}

	public void setProgress(SpamBot spamBot, int percentage,
			boolean indeterminate) {
	}

	public void setProgress(SpamBot spamBot, int percentage) {
	}

	private class ActionListValue {
		private ActionProvider provider;
		private ActionProvider.Action action;

		@Override
		public String toString() {
			return "<html><body>" + provider.toString()
					+ " <font color=\"gray\" size=\"2\">"
					+ action.getDescription() + "</font></body></html>";
		}
	}

	private class ColorfulCellRenderer extends JLabel implements
			ListCellRenderer {

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setText(value.toString());
			return this;
		}

	}
}
