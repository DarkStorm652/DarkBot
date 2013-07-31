package org.darkstorm.darkbot.minecraftbot.protocol;

import java.lang.reflect.InvocationTargetException;

import org.darkstorm.darkbot.minecraftbot.world.item.ItemStack;

public final class PacketContainer {
	private final Packet packet;
	private final Class<?> packetClass;

	public PacketContainer(Packet packet) {
		if(packet == null)
			throw new NullPointerException();
		this.packet = packet;
		packetClass = packet.getClass();
	}

	public int getId() {
		return packet.getId();
	}

	public Packet getPacket() {
		return packet;
	}

	public Object getField(String name) {
		try {
			return packetClass.getDeclaredField(name).get(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public String getFieldString(String name) {
		return (String) getField(name);
	}

	public ItemStack getFieldItem(String name) {
		return (ItemStack) getField(name);
	}

	public boolean getFieldBoolean(String name) {
		try {
			return packetClass.getDeclaredField(name).getBoolean(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public byte getFieldByte(String name) {
		try {
			return packetClass.getDeclaredField(name).getByte(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public char getFieldChar(String name) {
		try {
			return packetClass.getDeclaredField(name).getChar(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public short getFieldShort(String name) {
		try {
			return packetClass.getDeclaredField(name).getShort(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public int getFieldInt(String name) {
		try {
			return packetClass.getDeclaredField(name).getInt(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public long getFieldLong(String name) {
		try {
			return packetClass.getDeclaredField(name).getLong(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public float getFieldFloat(String name) {
		try {
			return packetClass.getDeclaredField(name).getFloat(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public double getFieldDouble(String name) {
		try {
			return packetClass.getDeclaredField(name).getDouble(packet);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setField(String name, Object value) {
		try {
			packetClass.getDeclaredField(name).set(packet, value);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldBoolean(String name, boolean z) {
		try {
			packetClass.getDeclaredField(name).setBoolean(packet, z);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldByte(String name, byte b) {
		try {
			packetClass.getDeclaredField(name).setByte(packet, b);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldChar(String name, char c) {
		try {
			packetClass.getDeclaredField(name).setChar(packet, c);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldShort(String name, short s) {
		try {
			packetClass.getDeclaredField(name).setShort(packet, s);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldInt(String name, int i) {
		try {
			packetClass.getDeclaredField(name).setInt(packet, i);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldLong(String name, long l) {
		try {
			packetClass.getDeclaredField(name).setLong(packet, l);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldFloat(String name, float f) {
		try {
			packetClass.getDeclaredField(name).setFloat(packet, f);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public void setFieldDouble(String name, double d) {
		try {
			packetClass.getDeclaredField(name).setDouble(packet, d);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible field", exception);
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public Class<?> getFieldType(String name) {
		try {
			return packetClass.getField(name).getType();
		} catch(NoSuchFieldException exception) {
			throw new IllegalArgumentException("No such field", exception);
		}
	}

	public boolean hasField(String name) {
		try {
			return packetClass.getField(name) != null;
		} catch(NoSuchFieldException exception) {
			return false;
		}
	}

	public Object getMethod(String name, Class<?>[] argumentTypes, Object... arguments) {
		try {
			return packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public boolean invokeMethodBoolean(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Boolean) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public byte invokeMethodByte(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Byte) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public char invokeMethodChar(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Character) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public short invokeMethodShort(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Short) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public int invokeMethodInt(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Integer) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public long invokeMethodLong(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Long) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public float invokeMethodFloat(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Float) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public double invokeMethodDouble(String name, Class<?> argumentTypes, Object... arguments) {
		try {
			return (Double) packetClass.getDeclaredMethod(name).invoke(packet, arguments);
		} catch(IllegalAccessException exception) {
			throw new IllegalArgumentException("Inaccessible method", exception);
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		} catch(InvocationTargetException exception) {
			throw new RuntimeException("Error in target method", exception);
		}
	}

	public Class<?> getMethodReturnType(String name) {
		try {
			return packetClass.getMethod(name).getReturnType();
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		}
	}

	public Class<?>[] getMethodArgumentTypes(String name) {
		try {
			return packetClass.getMethod(name).getParameterTypes();
		} catch(NoSuchMethodException exception) {
			throw new IllegalArgumentException("No such method", exception);
		}
	}

	public boolean hasMethod(String name) {
		try {
			return packetClass.getMethod(name) != null;
		} catch(NoSuchMethodException exception) {
			return false;
		}
	}
}
