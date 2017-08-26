package org.darkstorm.minecraft.darkbot.world.block;

import com.github.steveice10.opennbt.tag.builtin.CompoundTag;

public class SignTileEntity extends TileEntity {
	private final String[] text;

	public SignTileEntity(CompoundTag nbt) {
		super(nbt);
		text = new String[4];
		for(int i = 0; i < 4; i++)
			text[i] = (String)nbt.get("Text" + (i + 1)).getValue();
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
