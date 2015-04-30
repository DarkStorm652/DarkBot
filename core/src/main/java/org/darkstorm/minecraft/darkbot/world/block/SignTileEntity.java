package org.darkstorm.minecraft.darkbot.world.block;

import org.darkstorm.minecraft.darkbot.nbt.NBTTagCompound;

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

	public SignTileEntity(BlockLocation location, String[] text) {
		super(location);
		this.text = text.clone();
	}

	public String[] getText() {
		return text.clone();
	}
}
