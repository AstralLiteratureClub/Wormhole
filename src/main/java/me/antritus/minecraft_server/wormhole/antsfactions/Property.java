package me.antritus.minecraft_server.wormhole.antsfactions;

public class Property<Z, T> {
	private T value;
	private final Z name;

	public Property(Z name, T value) {
		this.value = value;
		this.name = name;
	}

	public void setValue(T value) {
		this.value = value;
	}
	@SuppressWarnings("unchecked")
	public void setValueObj(Object value){
		if (value != null){
			this.value = ((T) value);
		}
	}


	public Z getName() {
		return name;
	}

	public T getValue() {
		return value;
	}


	public static SimpleProperty<?> simple(String name, Object value){
		return new SimpleProperty<>(name, value);
	}
}
