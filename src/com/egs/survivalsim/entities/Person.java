package com.egs.survivalsim.entities;

public class Person {

	//Basic info
	protected int id;
	protected String firstName;
	protected String lastName;
	protected int health;
	protected int hunger;
	
	//Stats
	protected int strength;
	protected int speed;
	protected int intelligence;
	protected int threat;
	protected int luck;
	protected int causeOfDeath;
	
	//Variables
	protected boolean isDead;
	
	public Person(int id, String firstName, String lastName, int health, int hunger, int strength, int speed, int intelligence, int threat, int luck){
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.health = health;
		this.hunger = hunger;
		
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

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getHunger() {
		return hunger;
	}

	public void setHunger(int hunger) {
		this.hunger = hunger;
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
