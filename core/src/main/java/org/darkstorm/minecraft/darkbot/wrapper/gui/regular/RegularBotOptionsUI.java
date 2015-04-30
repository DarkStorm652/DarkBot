package org.darkstorm.minecraft.darkbot.wrapper.gui.regular;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import org.darkstorm.minecraft.darkbot.ai.Task;
import org.darkstorm.minecraft.darkbot.wrapper.gui.*;
import org.darkstorm.minecraft.darkbot.wrapper.gui.regular.RegularBot.RegularBotData;

@SuppressWarnings("serial")
public class RegularBotOptionsUI extends BotOptionsUI {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JTextField usernameField;
	private JCheckBox passwordCheckBox;
	private JPasswordField passwordField;
	private JTextField serverField;
	private JCheckBox proxyCheckBox;
	private JTextField proxyField;
	private JPanel tasksPanel;
	private JList availableTasksList;
	private JButton addButton;
	private JList selectedTasksList;
	private JButton removeButton;
	private JButton addAllButton;
	private JButton removeAllButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private Map<String, Class<? extends Task>> tasks;

	public RegularBotOptionsUI() {
		initComponents();
		tasks = new HashMap<String, Class<? extends Task>>();
		try {
			for(Class<?> c : org.darkstorm.minecraft.darkbot.Util
					.getClassesInPackage(Task.class.getPackage().getName())) {
				try {
					if(Task.class.isAssignableFrom(c) && !Task.class.equals(c))
						tasks.put(c.getSimpleName(), c.asSubclass(Task.class));
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		} catch(IOException exception) {
			exception.printStackTrace();
		}
		// tasks.put("Mob Defence", DefendTask.class);
		// tasks.put("Chop Trees", ChopTreesTask.class);
		// tasks.put("Eat", EatTask.class);
		// tasks.put("Farming", FarmingTask.class);
		// tasks.put("Fishing", FishingTask.class);
		// tasks.put("Follow", FollowTask.class);
		// tasks.put("Attack", AttackTask.class);
		// tasks.put("Hostile", HostileTask.class);
		// tasks.put("Mining", MiningTask.class);
		DefaultListModel model = new DefaultListModel();
		for(String taskName : tasks.keySet())
			model.addElement(taskName);
		availableTasksList.setModel(model);
		selectedTasksList.setModel(new DefaultListModel());
	}

	@Override
	public boolean areOptionsValid() {
		boolean valid = true;
		if(usernameField.getText().length() == 0) {
			Util.flashRed(usernameField);
			valid = false;
		}
		if(passwordCheckBox.isSelected()
				&& passwordField.getPassword().length == 0) {
			Util.flashRed(passwordField);
			valid = false;
		}
		if(serverField.getText().length() == 0) {
			Util.flashRed(serverField);
			valid = false;
		}
		if(proxyCheckBox.isSelected()
				&& (proxyField.getText().length() == 0 || !proxyField.getText()
						.contains(":"))) {
			Util.flashRed(proxyField);
			valid = false;
		}
		return valid;
	}

	@Override
	public BotControlsUI createBot() {
		RegularBotData data = new RegularBotData();
		data.setUsername(usernameField.getText());
		data.setPassword(passwordCheckBox.isSelected() ? new String(
				passwordField.getPassword()) : null);
		data.setServer(serverField.getText());
		data.setProxy(proxyCheckBox.isSelected() ? proxyField.getText() : null);
		List<Class<? extends Task>> tasks = new ArrayList<Class<? extends Task>>();
		for(int i = 0; i < selectedTasksList.getModel().getSize(); i++)
			tasks.add(this.tasks.get(selectedTasksList.getModel().getElementAt(
					i)));
		data.setTasks(tasks);
		return new RegularBotControlsUI(data);
	}

	private void passwordCheckBoxItemStateChanged(ItemEvent e) {
		passwordField.setEnabled(passwordCheckBox.isSelected());

	}

	private void proxyCheckBoxItemStateChanged(ItemEvent e) {
		proxyField.setEnabled(proxyCheckBox.isSelected());
	}

	private void addButtonActionPerformed(ActionEvent e) {
		String selectedAvailableTask = (String) availableTasksList
				.getSelectedValue();
		if(selectedAvailableTask == null)
			return;
		int selectedIndex = availableTasksList.getSelectedIndex();
		((DefaultListModel) availableTasksList.getModel())
				.removeElement(selectedAvailableTask);
		((DefaultListModel) selectedTasksList.getModel())
				.addElement(selectedAvailableTask);
		availableTasksList.setSelectedIndex(Math.min(selectedIndex,
				availableTasksList.getModel().getSize() - 1));
	}

	private void removeButtonActionPerformed(ActionEvent e) {
		String selectedAvailableTask = (String) selectedTasksList
				.getSelectedValue();
		if(selectedAvailableTask == null)
			return;
		int selectedIndex = selectedTasksList.getSelectedIndex();
		((DefaultListModel) selectedTasksList.getModel())
				.removeElement(selectedAvailableTask);
		((DefaultListModel) availableTasksList.getModel())
				.addElement(selectedAvailableTask);
		selectedTasksList.setSelectedIndex(Math.min(selectedIndex,
				selectedTasksList.getModel().getSize() - 1));
	}

	private void addAllButtonActionPerformed(ActionEvent e) {
		int size = availableTasksList.getModel().getSize();
		for(int i = 0; i < size; i++) {
			((DefaultListModel) selectedTasksList.getModel())
					.addElement(((DefaultListModel) availableTasksList
							.getModel()).remove(0));
		}
	}

	private void removeAllButtonActionPerformed(ActionEvent e) {
		int size = selectedTasksList.getModel().getSize();
		for(int i = 0; i < size; i++) {
			((DefaultListModel) availableTasksList.getModel())
					.addElement(((DefaultListModel) selectedTasksList
							.getModel()).remove(0));
		}
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle
				.getBundle("org.darkstorm.minecraft.darkbot.wrapper.gui.enUS");
		JLabel usernameLabel = new JLabel();
		usernameField = new JTextField();
		passwordCheckBox = new JCheckBox();
		passwordField = new JPasswordField();
		JLabel serverLabel = new JLabel();
		serverField = new JTextField();
		proxyCheckBox = new JCheckBox();
		proxyField = new JTextField();
		tasksPanel = new JPanel();
		JLabel availableLabel = new JLabel();
		JLabel selectedLabel = new JLabel();
		JScrollPane availableTasksScrollPane = new JScrollPane();
		availableTasksList = new JList();
		addButton = new JButton();
		JScrollPane selectedTasksScrollPane = new JScrollPane();
		selectedTasksList = new JList();
		removeButton = new JButton();
		addAllButton = new JButton();
		removeAllButton = new JButton();

		// ======== this ========
		setLayout(new GridBagLayout());
		((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 0, 0 };
		((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		((GridBagLayout) getLayout()).columnWeights = new double[] { 0.0, 1.0,
				1.0E-4 };
		((GridBagLayout) getLayout()).rowWeights = new double[] { 0.0, 0.0,
				0.0, 0.0, 1.0, 1.0E-4 };

		// ---- usernameLabel ----
		usernameLabel.setText(bundle
				.getString("regularbotoptions.usernameLabel.text"));
		add(usernameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 5), 0, 0));
		add(usernameField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ---- passwordCheckBox ----
		passwordCheckBox.setText(bundle
				.getString("regularbotoptions.passwordCheckBox.text"));
		passwordCheckBox.setSelected(true);
		passwordCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				passwordCheckBoxItemStateChanged(e);
			}
		});
		add(passwordCheckBox, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 5), 0, 0));
		add(passwordField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ---- serverLabel ----
		serverLabel.setText(bundle
				.getString("regularbotoptions.serverLabel.text"));
		add(serverLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 5), 0, 0));
		add(serverField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ---- proxyCheckBox ----
		proxyCheckBox.setText(bundle
				.getString("regularbotoptions.proxyCheckBox.text"));
		proxyCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				proxyCheckBoxItemStateChanged(e);
			}
		});
		add(proxyCheckBox, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 5), 0, 0));

		// ---- proxyField ----
		proxyField.setEnabled(false);
		add(proxyField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 5, 0), 0, 0));

		// ======== tasksPanel ========
		{
			tasksPanel.setBorder(new TitledBorder(bundle
					.getString("regularbotoptions.tasksPanel.border")));
			tasksPanel.setLayout(new GridBagLayout());
			((GridBagLayout) tasksPanel.getLayout()).columnWeights = new double[] {
					1.0, 0.0, 1.0 };
			((GridBagLayout) tasksPanel.getLayout()).rowWeights = new double[] {
					0.0, 1.0, 1.0, 1.0, 1.0 };

			// ---- availableLabel ----
			availableLabel.setText(bundle
					.getString("regularbotoptions.availableLabel.text"));
			availableLabel.setFont(availableLabel.getFont().deriveFont(
					availableLabel.getFont().getStyle() & ~Font.BOLD));
			tasksPanel.add(availableLabel, new GridBagConstraints(0, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- selectedLabel ----
			selectedLabel.setText(bundle
					.getString("regularbotoptions.selectedLabel.text"));
			selectedLabel.setFont(selectedLabel.getFont().deriveFont(
					selectedLabel.getFont().getStyle() & ~Font.BOLD));
			tasksPanel.add(selectedLabel, new GridBagConstraints(2, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ======== availableTasksScrollPane ========
			{

				// ---- availableTasksList ----
				availableTasksList
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				availableTasksScrollPane.setViewportView(availableTasksList);
			}
			tasksPanel.add(availableTasksScrollPane, new GridBagConstraints(0,
					1, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- addButton ----
			addButton.setIcon(new ImageIcon(getClass().getResource(
					"/icons/forward.png")));
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addButtonActionPerformed(e);
				}
			});
			tasksPanel.add(addButton, new GridBagConstraints(1, 1, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));

			// ======== selectedTasksScrollPane ========
			{

				// ---- selectedTasksList ----
				selectedTasksList
						.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				selectedTasksScrollPane.setViewportView(selectedTasksList);
			}
			tasksPanel.add(selectedTasksScrollPane, new GridBagConstraints(2,
					1, 1, 4, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- removeButton ----
			removeButton.setIcon(new ImageIcon(getClass().getResource(
					"/icons/backward.png")));
			removeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeButtonActionPerformed(e);
				}
			});
			tasksPanel.add(removeButton, new GridBagConstraints(1, 2, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- addAllButton ----
			addAllButton.setIcon(new ImageIcon(getClass().getResource(
					"/icons/next.png")));
			addAllButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					addAllButtonActionPerformed(e);
				}
			});
			tasksPanel.add(addAllButton, new GridBagConstraints(1, 3, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			// ---- removeAllButton ----
			removeAllButton.setIcon(new ImageIcon(getClass().getResource(
					"/icons/previous.png")));
			removeAllButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeAllButtonActionPerformed(e);
				}
			});
			tasksPanel.add(removeAllButton, new GridBagConstraints(1, 4, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
		add(tasksPanel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		// //GEN-END:initComponents
	}
}
