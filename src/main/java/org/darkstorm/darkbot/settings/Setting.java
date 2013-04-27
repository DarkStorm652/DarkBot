package org.darkstorm.darkbot.settings;


public class Setting {
	private SettingList subSettings;
	private String key, value;

	public Setting(String key, String value) {
		this(key, value, null);
	}

	public Setting(String key, String value, SettingList subSettings) {
		if(key == null)
			throw new NullPointerException();
		if(value == null)
			throw new NullPointerException();
		if(subSettings == null)
			this.subSettings = new SettingList();
		else
			this.subSettings = subSettings;
		this.key = key;
		this.value = value;

	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public SettingList getSubSettings() {
		return subSettings;
	}

	@Override
	public String toString() {
		int subSettingCount = subSettings.size();
		String stringValue = key + " = " + value;
		if(subSettingCount > 0)
			stringValue += " (" + subSettingCount + " subsettings)";
		return stringValue;
	}

}
