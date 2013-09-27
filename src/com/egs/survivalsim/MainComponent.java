package com.egs.survivalsim;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.egs.survivalsim.entities.Building;
import com.egs.survivalsim.entities.Person;
import com.egs.survivalsim.entities.Zombie;
import com.egs.survivalsim.util.FileUtil;

public class MainComponent extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;
	private static int width = 250;
	private static int height = 300;
	
	private Thread thread;
	private static Random random;
	private static FileUtil fileUtil;
	private JFrame frame;
	private boolean running = false;
	private static String ups = "60"; //TODO Change updates per second here
	private int updates;
	private int frames;
	
	//DEBUG
	public int turn = 0;
	public int oldTurn = 0;
	public int encounters = 0;
	public int encountersToday = 1;
	public boolean canCheckOldTurn = true;
	public boolean firstTurn = true;
	public boolean gameOver = false;
	
	//STARTING VALUES
	public int startingPeople = 0;
	public int startingZombies = 0;
	public int startingBuildings = 0;
	public int startingItems = 0;
	
	//GAME STORAGE
	public List<Person> personArray = new ArrayList<Person>();
	public List<Zombie> zombieArray = new ArrayList<Zombie>();
	public List<Building> buildingArray = new ArrayList<Building>();
	
	public List<String> firstNames = new ArrayList<String>();
	public List<String> lastNames = new ArrayList<String>();
	
	//GAME VALUES
	public int day;
	public int year = 1;
	public int currentPeople;
	public int currentZombies;
	public int currentBuildings;
	public int currentItems;
	public int peopleDeaths;
	public int zombieDeaths;
	
	public boolean peopleWin = false;
	public boolean zombiesWin = false;
	
	public MainComponent(){
		Dimension size = new Dimension(width, height);
		setPreferredSize(size);
		frame = new JFrame();
	}

	public static void main(String[] args){
		MainComponent main = new MainComponent();
		random = new Random();
		fileUtil = new FileUtil();
		//ups = JOptionPane.showInputDialog("How many updates per second? Default is 60.", "60");
		main.frame.setResizable(false);
		main.frame.add(main);
		main.frame.pack();
		main.frame.setTitle("Survival Sim");
		main.frame.setLocationRelativeTo(null);
		main.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.frame.setVisible(true);
		main.start();
	}

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / Integer.parseInt(ups);
		double delta = 0;
		updates = 0;
		frames = 0;
		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while(delta >= 1){
				//UPDATE
				update();
				updates++;
				delta--;
			}
			//RENDER
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000){
				timer += 1000;
				System.out.println(updates +" ups " + frames + "fps");
				//SHOW STATS
				updates = 0;
				frames = 0;
			}
		}
		stop();
	}
	
	public synchronized void start(){
		running = true;
		thread = new Thread(this, "maincomp");
		thread.start();
	}
	
	public synchronized void stop(){
		try{
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public String intString(int number){
		return Integer.toString(number);
	}
	
	public int stringInt(String value){
		return Integer.parseInt(value);
	}
	
	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);
			return;
		}

		Graphics g = bs.getDrawGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		
		g.drawString("Survival Sim", (width / 2) - 35, 12);
		
		g.drawString("Day: " + intString(day) + " Year: " + intString(year) + " (Turn: " + intString(turn) + ")", 8, 26);
		
		g.setColor(Color.GREEN);
		g.drawString("Survivors", 8, 40);
		g.setColor(Color.WHITE);
		g.drawString("Population: " + intString(currentPeople), 8, 51);
		g.drawString("Deaths: " + intString(peopleDeaths), 8, 62);
		
		g.setColor(Color.RED);
		g.drawString("Zombies", 150, 40);
		g.setColor(Color.WHITE);
		g.drawString("Population: " + intString(currentZombies), 150, 51);
		g.drawString("Deaths: " + intString(zombieDeaths), 150, 62);
		
		g.drawString("Encounters: " + intString(encounters),  8, 150);
		g.drawString("Buildings: " + intString(currentBuildings), 8, 161);
		g.drawString("Items: " + intString(currentItems), 8, 172);
		
		g.dispose();
		bs.show();
	}
	
	public void update(){	
		if(gameOver){
			int resetOption = JOptionPane.showConfirmDialog(null, "Go again?", "Simualtion End", JOptionPane.YES_NO_OPTION);
			if(resetOption == 0){
				gameOver = false;
				reset();
				firstTurn = true;
			}else{
				System.exit(0);
			}
			return;
		}
		if(firstTurn){
			try {
				fileUtil.createLog();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		turn++;
		if(!firstTurn){
			day++;
		}
		
		if(peopleWin){
			try {
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("GAME OVER");
				fileUtil.addLog("Survivors Won!");
				fileUtil.addLog("Zombies lasted for " + intString(day) + " days and " + intString(year) + " years. (" + intString(turn) + " turns)");
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("FINAL STATS");
			} catch (IOException e) {
				e.printStackTrace();
			}
			gameOver = true;
			return;
		}
		
		if(zombiesWin){
			try {
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("GAME OVER");
				fileUtil.addLog("Zombies Won!");
				fileUtil.addLog("Survivors lasted for " + intString(day) + " days and " + intString(year) + " years. (" + intString(turn) + " turns)");
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("FINAL STATS");
			} catch (IOException e) {
				e.printStackTrace();
			}
			gameOver = true;
			return;
		}
		
		if(!firstTurn){
			try {
				fileUtil.addLog("DAY " + intString(day) + " YEAR " + intString(year) + " BEGINS...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		checks();
		simulateTurn();
		if(!firstTurn){
			try {
				fileUtil.addLog("THE DAY ENDS...");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		encountersToday = 0;
	}	
	
	public void reset(){
		turn = 0;
		oldTurn = 0;
		encounters = 0;
		encountersToday = 1;
		canCheckOldTurn = true;
		firstTurn = true;
		gameOver = false;
		
		startingPeople = 0;
		startingZombies = 0;
		startingBuildings = 0;
		startingItems = 0;
		
		//GAME STORAGE
		personArray = new ArrayList<Person>();
		zombieArray = new ArrayList<Zombie>();
		buildingArray = new ArrayList<Building>();
		
		firstNames = new ArrayList<String>();
		lastNames = new ArrayList<String>();
		
		//GAME VALUES
		day = 1;
		year = 1;
		currentPeople = 0;
		currentZombies = 0;
		currentBuildings = 0;
		currentItems = 0;
		peopleDeaths = 0;
		zombieDeaths = 0;
		
		peopleWin = false;
		zombiesWin = false;
	}
	
	public void checks(){
		if(firstTurn){
			loadNames();
			createWorld();
			try {
				fileUtil.addLog("START OF LOG");
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("STARTING CONDITIONS");
				fileUtil.addLog("Survivors Starting: " + intString(startingPeople));
				fileUtil.addLog("Zombies Starting: " + intString(startingZombies));
				fileUtil.addLog("Buildings: " + intString(startingBuildings));
				fileUtil.addLog("Starting Items: " + intString(startingItems));
				fileUtil.addLog("--------------------------------------------------");
				fileUtil.addLog("BEGIN WORLD");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			firstTurn = false;
		}
		
		//Calculate Days and Years
		if(canCheckOldTurn){
			oldTurn = turn;
			canCheckOldTurn = false;
		}
		
		
		if(turn == (oldTurn + 365)){
			canCheckOldTurn = true;
			year++;
			day = 1;
		}
		
		if(currentPeople <= 0){
			currentPeople = 0;
			zombiesWin = true;
			//GAME END - ZOMBIES WIN
		}
		
		if(currentZombies <= 0){
			currentZombies = 0;
			peopleWin = true;
			//GAME END - SURVIVORS WIN
		}
		
		if(encountersToday == 0){
			try {
				fileUtil.addLog("There were no encounters today.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		calculateItems();
		buildingCheck();
	}
	
	public void loadNames(){
		firstNames.add("Fred");
		firstNames.add("Bob");
		firstNames.add("Jeff");
		firstNames.add("Steve");
		firstNames.add("George");
		firstNames.add("Kevin");
		firstNames.add("Joan");
		firstNames.add("Mary");
		firstNames.add("Elizabeth");
		firstNames.add("Rose");
		
		lastNames.add("Basset");
		lastNames.add("Curteys");
		lastNames.add("Druel");
		lastNames.add("Feu");
		lastNames.add("Godart");
		lastNames.add("Griffin");
		lastNames.add("Malet");
		
		//TODO Use .txt to store and load names from.
	}

	public String randomFirstName(){
		int namesFirstSize = firstNames.size();
		int randFirstName = random.nextInt(namesFirstSize);
		return firstNames.get(randFirstName);
	}
	
	public String randomLastName(){
		int namesLastSize = lastNames.size();
		int randLastName = random.nextInt(namesLastSize);
		return lastNames.get(randLastName);
	}
	
	public void createWorld(){
		try{
			startingPeople = stringInt(JOptionPane.showInputDialog("How many survivers do you want to start with? Max 100", "50"));
			startingZombies = stringInt(JOptionPane.showInputDialog("How many zombies do you want to start with? Max 100", "50"));
			startingBuildings = stringInt(JOptionPane.showInputDialog("How many buildings do you want to start with? Max 500", "250"));
		}catch(Exception e){
			System.exit(1);
		}

		if(startingPeople > 100) startingPeople = 100;
		if(startingPeople < 1) startingPeople = 1;
		
		if(startingZombies > 100) startingZombies = 100;
		if(startingZombies < 1) startingZombies = 1;
		
		if(startingBuildings > 500) startingBuildings = 500;
		if(startingBuildings < 1) startingBuildings = 1;
		
		for(int i = 0; i < startingPeople; i++){
			createPerson(i);
			currentPeople++;
		}
		
		for(int j = 0; j < startingZombies; j++){
			createZombie(j);
			currentZombies++;
		}
		
		for(int k = 0; k < startingBuildings; k++){
			createBuilding(k);
			currentBuildings++;
		}
		
		int itemPot = 0;
		for(int i = 0; i < currentBuildings; i++){
			int currentBuilding = i;
			if(!buildingArray.get(currentBuilding).isEmpty()){
				int buildingSize = buildingArray.get(currentBuilding).getItemArray().size();
				 itemPot =  itemPot + buildingSize;
			}
		}
		startingItems =  itemPot;
	}
	
	public void createPerson(int personId){
		int strength = random.nextInt(10);
		int speed = random.nextInt(10);
		int intelligence = random.nextInt(10);
		int luck = random.nextInt(10);
		
		int[] personThreat = new int[] {strength, speed, intelligence};
		int threat = calculateAverage(personThreat);
		
		personArray.add(new Person(personId, randomFirstName(), randomLastName(), 100, 100, strength, speed, intelligence, threat, luck));
	}

	public void createZombie(int zombieId){
		int strength = random.nextInt(10);
		int speed = random.nextInt(10);
		int intelligence = random.nextInt(10);
		int luck = random.nextInt(10);
		
		int[] zombieThreat = new int[] {strength, speed, intelligence};
		int threat = calculateAverage(zombieThreat);
		
		zombieArray.add(new Zombie(zombieId, 50, strength, speed, intelligence, threat, luck));
	}
	
	public void createBuilding(int buildingId){
		int size = random.nextInt(99) + 1;
		
		buildingArray.add(new Building(buildingId, size));
	}	

	public int calculateAverage(int[] values){
		int sum = 0;
		for(int i = 0; i < values.length; i++){
			sum += values[i];
		}
		return sum / values.length;
	}
	
	public void calculateItems(){
		int sizePot = 0;
		for(int i = 0; i < currentBuildings; i++){
			int currentBuilding = i;
			if(!buildingArray.get(currentBuilding).isEmpty()){
				int buildingSize = buildingArray.get(currentBuilding).getItemArray().size();
				sizePot = sizePot + buildingSize;
			}
		}
		currentItems = sizePot;
	}
	
	public void buildingCheck(){
		for(int i = 0; i < currentBuildings; i++){
			int currentBuilding = i;
			if(buildingArray.get(currentBuilding).getItemArray().size() == 0){
				buildingArray.get(currentBuilding).setEmpty(true);
			}
		}
	}
	
	public void simulateTurn(){
		simulateEncounter(-1);
		simulateHunger();
		simulateHealth();	
		
		try{
			simulateDeath();
		}catch(Exception e){
		}
	}
	
	public void simulateHunger(){
		//Cycle through all people, -1 hunger.
		for(int i = 0; i < currentPeople; i++){
			int currentPerson = i;
			int roll = random.nextInt(10);
			
			if(roll < 3){
				if(personArray.get(currentPerson).getHunger() != 0){
					if(personArray.get(currentPerson).getHunger() > 0){
						personArray.get(currentPerson).setHunger(personArray.get(currentPerson).getHunger() - 1);
						if(personArray.get(currentPerson).getHunger() <= 0){
							personArray.get(currentPerson).setHunger(0);
							personArray.get(currentPerson).setCauseOfDeath(0);
						}
					}
				}
			}
			
			if(personArray.get(currentPerson).getHunger() < 20){
				lookForFood(currentPerson);
			}
		}
	}
	
	public void simulateHealth(){
		for(int i = 0; i < currentPeople; i++){
			int currentPerson = i;
			if(personArray.get(currentPerson).getHunger() <= 0){
				if(personArray.get(currentPerson).getHealth() > 0){
					personArray.get(currentPerson).setHealth(personArray.get(currentPerson).getHealth() - 1);
				}
			}
			
			if(personArray.get(currentPerson).getHealth() <= 0){
				personArray.get(currentPerson).setDead(true);
				try {
					if(personArray.get(currentPerson).getCauseOfDeath() == -1){
						fileUtil.addLog(personArray.get(currentPerson).getFirstName() + " " + personArray.get(currentPerson).getLastName() + " died for unknown reasons.");
					}else if(personArray.get(currentPerson).getCauseOfDeath() == 0){
						fileUtil.addLog(personArray.get(currentPerson).getFirstName() + " " + personArray.get(currentPerson).getLastName() + " starved to death.");
					}else if(personArray.get(currentPerson).getCauseOfDeath() == 1){
						fileUtil.addLog(personArray.get(currentPerson).getFirstName() + " " + personArray.get(currentPerson).getLastName() + " was killed by a zombie.");
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		for(int j = 0; j < currentZombies; j++){
			int currentZombie = j;
			if(zombieArray.get(currentZombie).getHealth() <= 0){
				zombieArray.get(currentZombie).setDead(true);
				try {
					fileUtil.addLog("Zombie #" + zombieArray.get(currentZombie).getId() + " died.");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void simulateDeath(){
		for(int i = 0; i < currentPeople; i++){
			int currentPerson = i;
			if(personArray.get(currentPerson).isDead()){
				peopleDeaths++;
				currentPeople--;
				personArray.remove(currentPerson);
				
			}
		}
		
		for(int j = 0; j < currentZombies; j++){
			int currentZombie = j;
			if(zombieArray.get(currentZombie).isDead()){
				zombieDeaths++;
				currentZombies--;
				zombieArray.remove(currentZombie);
				
			}
		}
		
	}
	
	public void lookForFood(int id){
		try {
			fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " is starving and starts looking for food.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		simulateEncounter(id);
		for(int i = 0; i < currentBuildings; i++){
			int currentBuilding = i;
			if(!buildingArray.get(currentBuilding).isEmpty()){
				//Can search
				int buildingSize = buildingArray.get(currentBuilding).getSize();
				
				if(buildingSize > 10){
					//Chance for encounter
					int roll = random.nextInt(10);
					if(personArray.get(id).getIntelligence() >= roll){
						//Can find item
						int buildingItems = buildingArray.get(currentBuilding).getItemArray().size();
						if(buildingArray.get(currentBuilding).getItemArray().size() > 0){
							int itemRoll = random.nextInt(buildingItems);
	
							int itemType = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getType();
							if(itemType == 0){
								if(personArray.get(id).getHunger() < 100){
									int foodValue = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getValue();
									personArray.get(id).setHunger(personArray.get(id).getHunger() + foodValue);
									buildingArray.get(currentBuilding).getItemArray().remove(itemRoll);
								}else{
									personArray.get(id).setHunger(100);
								}
							}
							if(itemType == 1){
								//medpack
								int healValue = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getValue();
								personArray.get(id).setHealth(personArray.get(id).getHealth() + healValue);
								buildingArray.get(currentBuilding).getItemArray().remove(itemRoll);
							}
						}
					}else if(personArray.get(id).getIntelligence() < roll){
						simulateEncounter(id);
					}
				}else{
					//Chance for food, nothing
					int buildingItems = buildingArray.get(currentBuilding).getItemArray().size();
					if(buildingArray.get(currentBuilding).getItemArray().size() > 0){
						int itemRoll = random.nextInt(buildingItems);
						
						int itemType = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getType();
						if(itemType == 0){
							if(personArray.get(id).getHunger() < 100){
								int foodValue = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getValue();
								personArray.get(id).setHunger(personArray.get(id).getHunger() + foodValue);
								buildingArray.get(currentBuilding).getItemArray().remove(itemRoll);
							}else{
								personArray.get(id).setHunger(100);
							}
						}
						if(itemType == 1){
							//medpack
							int healValue = buildingArray.get(currentBuilding).getItemArray().get(itemRoll).getValue();
							personArray.get(id).setHealth(personArray.get(id).getHealth() + healValue);
							buildingArray.get(currentBuilding).getItemArray().remove(itemRoll);
						}
					}
				}
				
			}//else building is empty, move on
		}
	}
	
	public int getRandomZombie(){
		int randomZombie = 0;
		for(int i = 0; i < currentZombies; i++){
			int randomSelect = random.nextInt(currentZombies);
			if(randomSelect == i){
				randomZombie = i;
				break;
			}
		}
		return randomZombie;
	}
	
	public void simulateEncounter(int id){
		int roll = random.nextInt(200);
		if(id != -1){ //ENCOUNTER FOR SPECIFIC PLAYER
			if(roll < 2){
				try {
					fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " encountered a zombie!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				encounters++;
				encountersToday++;
				int type = random.nextInt(3);
				
				if(type == 0){ //Encounter is chase
					simulateChase(getRandomZombie(), id);
				}
				
				if(type == 1){ //Encounter is brawl
					simulateBrawl(getRandomZombie(), id);
				}
				
				if(type == 2){ //Encounter is hide + seek
					simulateHide(getRandomZombie(), id);
				}
			}
		}
		if(id == -1){ //ENCOUNTER FOR ALL
			for(int i = 0; i < currentPeople; i++){
				int bigRoll = random.nextInt(200);
				if(bigRoll > 196){
					try {
						fileUtil.addLog(personArray.get(i).getFirstName() + " " + personArray.get(i).getLastName() + " encountered a zombie!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					encounters++;
					encountersToday++;
					int type = random.nextInt(3);
		
					if(type == 0){ //Encounter is chase
						simulateChase(getRandomZombie(), i);
					}
					
					if(type == 1){ //Encounter is brawl
						simulateBrawl(getRandomZombie(), i);
					}
					
					if(type == 2){ //Encounter is hide + seek
						simulateHide(getRandomZombie(), i);
					}
				}else{
					continue;
				}
			}
		}
		
	}
	
	public void simulateChase(int randomZombie, int id){	
		int personSpeed = personArray.get(id).getSpeed();
		int zombieSpeed = zombieArray.get(randomZombie).getSpeed(); //TODO Array index out of bounds fix needed
		
		if(personSpeed > zombieSpeed){
			try {
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " managed to out run the zombie!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Person survives encounter
			//TODO Make encounter survival
		}
		if(zombieSpeed >= personSpeed){
			//Person relies on strength to survive
			try {
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " wasn't fast enough!");
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " fights the zombie");
			} catch (IOException e) {
				e.printStackTrace();
			}
			simulateBrawl(randomZombie, id);
		}
	}
	
	public void simulateHide(int randomZombie, int id){	
		try {
			fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " tries to hide from the zombie.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int personIntelligence = personArray.get(id).getIntelligence();
		int zombieIntelligence = zombieArray.get(randomZombie).getIntelligence();
		if(personIntelligence > zombieIntelligence){
			//Person survived encounter
			try {
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " managed to hide from the zombie!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(zombieIntelligence >= personIntelligence){
			try {
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " couldn't hide from the zombie!");
				fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " starts to run!");
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Person relies on speed
			simulateChase(randomZombie, id);
		}
	}

	public void simulateBrawl(int randomZombie, int id){
		int personStrength = personArray.get(id).getStrength();
		int zombieStrength = zombieArray.get(randomZombie).getStrength();
		if(personStrength > zombieStrength){
			//Person survived encounter
			//Zombie looses health depending on person strength
			if(zombieArray.get(randomZombie).getHealth() > 0){
				try {
					fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " managed to punch the zombie and escape!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				zombieArray.get(randomZombie).setHealth(zombieArray.get(randomZombie).getHealth() - personStrength);
			}else{
				zombieArray.get(randomZombie).setHealth(0);
				zombieArray.get(randomZombie).setCauseOfDeath(id);
			}
		}
		if(zombieStrength >= personStrength){
			//Person looses health depending on zombie striength
			if(personArray.get(id).getHunger() > 0){
				try {
					fileUtil.addLog(personArray.get(id).getFirstName() + " " + personArray.get(id).getLastName() + " couldn't fight against a zombie!");
				} catch (IOException e) {
					e.printStackTrace();
				}
				personArray.get(id).setHealth(personArray.get(id).getHealth() - zombieStrength);
			}else{
				personArray.get(id).setHealth(0);
				personArray.get(id).setCauseOfDeath(1);
			}
		}
	}
}
