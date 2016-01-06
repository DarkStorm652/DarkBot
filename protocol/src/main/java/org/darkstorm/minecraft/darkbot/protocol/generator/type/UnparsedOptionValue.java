package org.darkstorm.minecraft.darkbot.protocol.generator.type;

import java.util.*;

public abstract class UnparsedOptionValue {
	public static final class Singular extends UnparsedOptionValue {
		private final String value;
		
		private Singular(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return value;
		}
	}
	public static final class Composite extends UnparsedOptionValue {
		private final Collection<UnparsedOptionValue> values;
		
		private Composite(Collection<UnparsedOptionValue> values) {
			this.values = Collections.unmodifiableCollection(new ArrayList<>(values));
		}
		
		public Collection<UnparsedOptionValue> getValues() {
			return values;
		}
	}
	private UnparsedOptionValue() {}
	
	public static UnparsedOptionValue.Singular singular(String value) {
		return new Singular(value);
	}
	public static UnparsedOptionValue.Composite composite(UnparsedOptionValue... values) {
		return new Composite(Arrays.asList(values));
	}
	public static UnparsedOptionValue.Composite composite(Collection<UnparsedOptionValue> values){
		return new Composite(values);
	}
}
