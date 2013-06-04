package org.darkstorm.darkbot.minecraftbot.world.block;

import org.darkstorm.darkbot.minecraftbot.nbt.NBTTagCompound;

public class SignTileEntity extends TileEntity {
	private final String[] text;

	public SignTileEntity(NBTTagCompound nbt) {
		super(nbt);
		text = new String[4];
		for(int i = 0; i < 4; i++)
			text[i] = nbt.getString("Text" + (i + 1));
	}

	public SignTileEntity(int x, int y, int z, String[] text) {
		super(x, y, z);
		this.text = text.clone();
	}

	public String[] getText() {
		return text.clone();
	}
}
