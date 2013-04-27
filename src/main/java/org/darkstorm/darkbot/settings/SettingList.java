package org.darkstorm.darkbot.settings;

import java.util.*;

@SuppressWarnings("serial")
public class SettingList extends Vector<Setting> {
	@Override
	public synchronized boolean add(Setting setting) {
		if(setting == null)
			throw new NullPointerException();
		return super.add(setting);
	}

	@Override
	public void add(int index, Setting setting) {
		if(setting == null)
			throw new NullPointerException();
		super.add(index, setting);
	}

	@Override
	public synchronized boolean addAll(Collection<? extends Setting> c) {
		for(Setting setting : c)
			if(setting == null)
				throw new IllegalArgumentException("param 0 "
						+ "(type Collection<? extends Setting>) "
						+ "contains a null element (type Setting)");
		return super.addAll(c);
	}

	@Override
	public synchronized boolean addAll(int index,
			Collection<? extends Setting> c) {
		for(Setting setting : c)
			if(setting == null)
				throw new IllegalArgumentException("param 1 "
						+ "(type Collection<? extends Setting>) "
						+ "contains a null element (type Setting)");
		return super.addAll(index, c);
	}

	@Override
	public synchronized void insertElementAt(Setting obj, int index) {
		if(obj == null)
			throw new NullPointerException();
		super.insertElementAt(obj, index);
	}

	@Override
	public synchronized Setting set(int index, Setting setting) {
		if(setting == null)
			throw new NullPointerException();
		return super.set(index, setting);
	}

	@Override
	public synchronized void setElementAt(Setting obj, int index) {
		if(obj == null)
			throw new NullPointerException();
		super.setElementAt(obj, index);
	}

	public synchronized void addSetting(String key, String value) {
		add(new Setting(key, value));
	}

	public synchronized boolean removeSetting(String key) {
		Setting setting = getSetting(key);
		if(setting == null)
			return false;
		return remove(setting);
	}

	public synchronized String getSettingValue(String key) {
		Setting setting = getSetting(key);
		if(setting == null)
			return null;
		return setting.getValue();
	}

	public synchronized boolean setSettingValue(String key, String newValue) {
		Setting setting = getSetting(key);
		if(setting == null)
			return false;
		setting.setValue(newValue);
		return true;
	}

	public synchronized Setting getSetting(String key) {
		if(key == null)
			throw new NullPointerException();
		for(Object element : elementData) {
			if(element instanceof Setting) {
				Setting setting = (Setting) element;
				String settingKey = setting.getKey();
				if(settingKey.equals(key)) {
					return setting;
				}
			}
		}
		return null;
	}

	public synchronized String getSettingValue(int index) {
		Setting setting = get(index);
		if(setting == null)
			return null;
		return setting.getValue();
	}
}
