package com.egs.survivalsim.entities;

public class Zombie {

	//Basic info
	protected int id;
	protected int health;
	
	//Stats
	protected int strength;
	protected int speed;
	protected int intelligence;
	protected int threat;
	protected int luck;
	protected int causeOfDeath;
	
	//Variables
	protected boolean isDead;
	
	public Zombie(int id, int health, int strength, int speed, int intelligence, int threat, int luck){
		this.id = id;
		this.health = health;
		
		this.strength = strength;
		this.speed = speed;
		this.intelligence = intelligence;
		this.threat = threat;
		this.luck = luck;
		this.causeOfDeath = -1;
		
		this.isDead = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getIntelligence() {
		return intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getThreat() {
		return threat;
	}

	public void setThreat(int threat) {
		this.threat = threat;
	}

	public int getLuck() {
		return luck;
	}

	public void setLuck(int luck) {
		this.luck = luck;
	}

	public int getCauseOfDeath() {
		return causeOfDeath;
	}

	public void setCauseOfDeath(int causeOfDeath) {
		this.causeOfDeath = causeOfDeath;
	}
}
