package com.egs.survivalsim.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Building {

	Random random;
	
	protected int id;
	protected int size;
	
	protected boolean isEmpty;
	
	protected List<Item> itemArray = new ArrayList<Item>();
	
	public Building(int id, int size){
		this.id = id;
		this.size = size;
		
		this.isEmpty = false;
		
		random = new Random();
		populateBuilding();
	}
	
	public void populateBuilding(){
		for(int i = 0; i < size; i++){
			int roll = random.nextInt(2);
			//Random chance of giving item
			if(roll == 1){
				int type = random.nextInt(2);
				int value = 1;
				
				if(type == 0){
					//Food
					value = random.nextInt(5) + 2;
				}else if(type == 1){
					//Medpack
					value = random.nextInt(3) + 10;
				}
				
				itemArray.add(new Item(id, type, value));
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public List<Item> getItemArray() {
		return itemArray;
	}

	public void setItemArray(List<Item> itemArray) {
		this.itemArray = itemArray;
	}
}
