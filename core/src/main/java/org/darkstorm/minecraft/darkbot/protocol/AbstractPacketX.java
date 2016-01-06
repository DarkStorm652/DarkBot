package org.darkstorm.minecraft.darkbot.protocol;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;

import org.darkstorm.minecraft.darkbot.nbt.*;
import org.darkstorm.minecraft.darkbot.protocol.ProtocolX.State;
import org.darkstorm.minecraft.darkbot.util.IntHashMap;
import org.darkstorm.minecraft.darkbot.world.block.BlockLocation;
import org.darkstorm.minecraft.darkbot.world.entity.WatchableObject;
import org.darkstorm.minecraft.darkbot.world.item.*;

public abstract class AbstractPacketX implements PacketX {
	private static final Charset UTF8 = Charset.forName("UTF-8");
	private final int id;
	private final State state;
	private final Direction direction;

	protected AbstractPacketX(int id, State state, Direction direction) {
		this.id = id;
		this.state = state;
		this.direction = direction;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public State getState() {
		return state;
	}

	@Override
	public Direction getDirection() {
		return direction;
	}

	public static String readString(DataInputStream in) throws IOException {
		int length = readVarInt(in);
		byte[] data = new byte[length];
		in.readFully(data);
		return new String(data, UTF8);
	}

	public static void writeString(String string, DataOutputStream out) throws IOException {
		writeVarInt(string.length(), out);
		out.write(string.getBytes(UTF8));
	}

	public static int varIntLength(int varInt) {
		int size = 0;
		while(true) {
			size++;
			if((varInt & 0xFFFFFF80) == 0)
				return size;
			varInt >>>= 7;
		}
	}

	public static int readVarInt(DataInputStream in) throws IOException {
		int i = 0;
		int j = 0;
		while(true) {
			int k = in.read();
			if(k == -1)
				throw new IOException("End of stream");

			i |= (k & 0x7F) << j++ * 7;

			if(j > 5)
				throw new IOException("VarInt too big");

			if((k & 0x80) != 128)
				break;
		}

		return i;
		/*int varInt = 0;
		for(int i = 0; i < 5; i++) {
			int b = in.read();
			varInt |= (b & (i != 4 ? 0x7F : 0x0F)) << (i * 7);

			if(i == 4 && (((b & 0x80) == 0x80) || ((b & 0x70) != 0)))
				throw new IOException("VarInt too big");
			if((b & 0x80) != 0x80)
				break;
		}
		return varInt;*/
	}

	public static long readVarInt64(DataInputStream in) throws IOException {
		long varInt = 0;
		for(int i = 0; i < 10; i++) {
			byte b = in.readByte();
			varInt |= ((long) (b & (i != 9 ? 0x7F : 0x01))) << (i * 7);

			if(i == 9 && (((b & 0x80) == 0x80) || ((b & 0x7E) != 0)))
				throw new IOException("VarInt too big");
			if((b & 0x80) != 0x80)
				break;
		}
		return varInt;
	}

	public static void writeVarInt(int varInt, DataOutputStream out) throws IOException {
		while(true) {
			if((varInt & 0xFFFFFF80) == 0) {
				out.write(varInt);
				return;
			}

			out.write(varInt & 0x7F | 0x80);
			varInt >>>= 7;
		}
		/*int length = 5;
		for(int i = 4; i >= 0; i--)
			if(((varInt >> (i * 7)) & (i != 4 ? 0x7F : 0x0F)) == 0)
				length--;
		for(int i = 0; i < length; i++)
			out.write((i == length - 1 ? 0x00 : 0x80) | ((varInt >> (i * 7)) & (i != 4 ? 0x7F : 0x0F)));*/
	}

	public static void writeVarInt64(long varInt, DataOutputStream out) throws IOException {
		int length = 10;
		for(int i = 9; i >= 0; i--)
			if(((varInt >> (i * 7)) & (i != 9 ? 0x7F : 0x01)) == 0)
				length--;
		for(int i = 0; i < length; i++)
			out.writeByte((int) ((i == length - 1 ? 0x00 : 0x80) | ((varInt >> (i * 7)) & (i != 9 ? 0x7F : 0x01))));
	}

	public static byte[] readByteArray(DataInputStream in) throws IOException {
		short length = in.readShort();
		if(length < 0)
			throw new IOException("Invalid array length");
		byte[] data = new byte[length];
		in.readFully(data);
		return data;
	}

	public static void writeByteArray(byte[] data, DataOutputStream out) throws IOException {
		out.writeShort(data.length);
		out.write(data);
	}

	public static ItemStack readItemStack(DataInputStream in) throws IOException {
		ItemStack item = null;
		short id = in.readShort();

		if(id >= 0) {
			byte stackSize = in.readByte();
			short damage = in.readShort();
			item = new BasicItemStack(id, stackSize, damage);
			item.setStackTagCompound(readNBTTagCompound(in));
		}

		return item;
	}

	public static void writeItemStack(ItemStack item, DataOutputStream out) throws IOException {
		if(item != null) {
			out.writeShort(item.getId());
			out.writeByte(item.getStackSize());
			out.writeShort(item.getDamage());

			writeNBTTagCompound(item.getStackTagCompound(), out);
		} else
			out.writeShort(-1);
	}

	public static NBTTagCompound readNBTTagCompound(DataInputStream in) throws IOException {
		short length = in.readShort();

		if(length >= 0) {
			byte[] data = new byte[length];
			in.readFully(data);
			return CompressedStreamTools.decompress(data);
		} else
			return null;
	}

	public static void writeNBTTagCompound(NBTTagCompound compound, DataOutputStream out) throws IOException {
		if(compound != null) {
			byte[] data = CompressedStreamTools.compress(compound);
			out.writeShort((short) data.length);
			out.write(data);
		} else
			out.writeShort(-1);
	}

	public static void writeWatchableObjects(List<WatchableObject> objects, DataOutputStream out) throws IOException {
		for(WatchableObject object : objects)
			writeWatchableObject(out, object);
		out.writeByte(127);
	}

	public static void writeWatchableObjects(IntHashMap<WatchableObject> objects, DataOutputStream out) throws IOException {
		for(WatchableObject object : objects.values())
			writeWatchableObject(out, object);
		out.writeByte(127);
	}

	private static void writeWatchableObject(DataOutputStream out, WatchableObject watchableObject) throws IOException {
		int i = (watchableObject.getObjectType() << 5 | watchableObject.getDataValueId() & 0x1f) & 0xff;
		out.writeByte(i);

		switch(watchableObject.getObjectType()) {
		case 0:
			out.writeByte(((Byte) watchableObject.getObject()).byteValue());
			break;
		case 1:
			out.writeShort(((Short) watchableObject.getObject()).shortValue());
			break;
		case 2:
			out.writeInt(((Integer) watchableObject.getObject()).intValue());
			break;
		case 3:
			out.writeFloat(((Float) watchableObject.getObject()).floatValue());
			break;
		case 4:
			writeString((String) watchableObject.getObject(), out);
			break;
		case 5:
			ItemStack itemstack = (ItemStack) watchableObject.getObject();
			writeItemStack(itemstack, out);
			break;
		case 6:
			BlockLocation chunkcoordinates = (BlockLocation) watchableObject.getObject();
			out.writeInt(chunkcoordinates.getX());
			out.writeInt(chunkcoordinates.getY());
			out.writeInt(chunkcoordinates.getZ());
			break;
		}
	}

	public static IntHashMap<WatchableObject> readWatchableObjects(DataInputStream in) throws IOException {
		IntHashMap<WatchableObject> map = new IntHashMap<WatchableObject>();
		for(byte b = in.readByte(); b != 127; b = in.readByte()) {
			int i = (b & 0xe0) >> 5;
			int j = b & 0x1f;
			WatchableObject watchableobject = null;
			switch(i) {
			case 0:
				watchableobject = new WatchableObject(i, j, Byte.valueOf(in.readByte()));
				break;
			case 1:
				watchableobject = new WatchableObject(i, j, Short.valueOf(in.readShort()));
				break;
			case 2:
				watchableobject = new WatchableObject(i, j, Integer.valueOf(in.readInt()));
				break;
			case 3:
				watchableobject = new WatchableObject(i, j, Float.valueOf(in.readFloat()));
				break;
			case 4:
				watchableobject = new WatchableObject(i, j, readString(in));
				break;
			case 5:
				watchableobject = new WatchableObject(i, j, readItemStack(in));
				break;
			case 6:
				int k = in.readInt();
				int l = in.readInt();
				int i1 = in.readInt();
				watchableobject = new WatchableObject(i, j, new BlockLocation(k, l, i1));
				break;
			}
			map.put(i, watchableobject);
		}
		return map;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}