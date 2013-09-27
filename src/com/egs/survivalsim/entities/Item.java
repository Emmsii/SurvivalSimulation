package com.egs.survivalsim.entities;

public class Item {

	protected int id;
	protected int type;
	protected int value;
	
	public Item(int id, int type, int value){
		this.id = id;
		this.type = type;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}
