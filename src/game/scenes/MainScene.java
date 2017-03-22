package game.scenes;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Animation;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.Settings;
import com.retrochicken.engine.fx.SoundClip;
import com.retrochicken.engine.fx.SpriteSheet;
import com.retrochicken.engine.interactable.Animal;
import com.retrochicken.engine.interactable.Consumable;
import com.retrochicken.engine.interactable.Living;
import com.retrochicken.engine.physics.CollidableImage;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;
import com.retrochicken.engine.physics.EmptyCollider;
import com.retrochicken.engine.physics.GameObject;
import com.retrochicken.engine.scenes.Scene;
import com.retrochicken.engine.scenes.SceneManager;

import game.Icon;
import game.Inventory;
import game.crafting.Craftable;
import game.crafting.CraftableItems;
import game.crafting.CraftingManager;
import game.interactable.Character;

public class MainScene implements Scene {
	
	private CollisionManager collisionManager = new CollisionManager();
	
	private CollidableImage grass;
	
	//private Light light = new Light(0xffffffff, 200);
	private SoundClip clip = new SoundClip("/sounds/slow_melancholic_theme_c64_style.wav");
	
	private Character paul;
	
	private ArrayList<GameObject> gameObjects = new ArrayList<>();
	private ArrayList<Living> enemies = new ArrayList<>();
	private ArrayList<Consumable> consumables = new ArrayList<>();
	private ArrayList<Animal> animals = new ArrayList<>();
	
	private int width, height;
	
	private Inventory inventory;
	
	private boolean dragging = false;
	private float selectX, selectY;
	
	private CraftingManager craftingManager;
	
	public void addGameObject(GameObject obj) {
		gameObjects.add(obj);
	}
	
	public void addEnemy(Living obj) {
		enemies.add(obj);
	}
	
	public void addConsumable(Consumable obj) {
		consumables.add(obj);
	}
	
	public void addAnimal(Animal obj) {
		animals.add(obj);
	}
	
	private SceneManager sManager;
	
	public MainScene(GameContainer gc, SceneManager sManager) {
		craftingManager = new CraftingManager(this);
		
		inventory = new Inventory(this);
		inventory.addIcon(new Icon(new Image("/icons/zombieicon.png"), "zombie"));
		inventory.addIcon(new Icon(new Image("/icons/foodicon.png"), "food"));
		inventory.addIcon(new Icon(new Image("/icons/barrelicon.png"), "barrel"));
		inventory.addIcon(new Icon(new Image("/icons/pigeonicon.png"), "pigeon"));
		inventory.addIcon(new Icon(new Image("/icons/nooseicon.png"), "noose"));
		inventory.addIcon(new Icon(new Image("/icons/woodicon.png"), "wood", true));
		inventory.addIcon(new Icon(new Image("/icons/metalicon.png"), "metal", true));
		
		this.width = gc.getWidth();
		this.height = gc.getHeight();
		SpriteSheet paulSprite = new SpriteSheet("/characters/paulchar.png");
		paul = new Character(new Animation(paulSprite, new int[]{0}, 5),
							 new Animation(paulSprite, new int[]{1, 2, 3, 4}, 0.5f, true),
							 new Animation(paulSprite, new int[]{1, 2, 3, 4}, 0.5f),
							 50, 50, collisionManager, inventory);
		
		grass = new CollidableImage("/environment/tileset_dirt_and_grass_1.png", collisionManager);
		grass.setCollider(new Collider(0, height - grass.height + 2, width, grass.height - 2, "ground"));
		
		gameObjects.add(paul);
		
		clip.setVolumeStandard(75);
		
		this.sManager = sManager;
	}
	
	@Override
	public void update(float timePassed) {
		if(Input.isKeyPressed(KeyEvent.VK_V)) {
			gameObjects.add((new CraftableItems(this)).new Wood(Input.getMouseX(), Input.getMouseY(), getCollisionManager()));
		}
		
		for(int i = gameObjects.size() - 1; i >= 0; i--) {
			if(gameObjects.get(i) instanceof Craftable && ((Craftable)gameObjects.get(i)).isUsed()) {
				gameObjects.remove(i);
				continue;
			}
			gameObjects.get(i).update(timePassed);
		}
		for(int i = enemies.size() - 1; i >= 0; i--) {
			enemies.get(i).update(timePassed);
			if(enemies.get(i).isDead())
				enemies.remove(i);
		}
		for(int i = consumables.size() - 1; i >= 0; i--) {
			if(consumables.get(i).isUsed())
				consumables.remove(i);
			else
				consumables.get(i).update(timePassed);
		}
		for(int i = animals.size() - 1; i >= 0; i--) {
			animals.get(i).update(timePassed);
			if(animals.get(i).isDisposable())
				animals.remove(i);
		}
		grass.update(timePassed);
		
		collisionManager.update();
		
		inventory.update();
		
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && !dragging) {
			dragging = true;
			selectX = Input.getMouseX();
			selectY = Input.getMouseY();
		}
		if(Input.isButtonReleased(MouseEvent.BUTTON1) && dragging) {
			dragging = false;
			int width = (int)Math.abs(selectX - Input.getMouseX());
			int height = (int)Math.abs(selectY - Input.getMouseY());
			craftingManager.processSelect(new Rectangle((int)Math.min(selectX, Input.getMouseX()), (int)Math.min(selectY, Input.getMouseY()), width, height));
		}
		
		if(paul.isDead()) {
			sManager.setScene(1);
			clip.stop();
		}
	}

	@Override
	public void render(GameContainer gc, Renderer renderer) {
		for(GameObject obj : gameObjects)
			obj.render(renderer);
		for(Living obj : enemies)
			obj.render(renderer);
		for(Consumable obj : consumables)
			obj.render(renderer);
		for(Animal obj : animals)
			obj.render(renderer);
		grass.drawCopies(renderer, (int)Math.ceil(gc.getWidth()/(float)grass.width), 1, 0, gc.getHeight() - grass.height);
		//renderer.drawLight(light, Input.getMouseX(), Input.getMouseY());
		inventory.render(renderer);
		if(dragging && Settings.CAN_SELECT) {
			int width = (int)Math.abs(selectX - Input.getMouseX());
			int height = (int)Math.abs(selectY - Input.getMouseY());
			renderer.drawTransparentRect(Settings.SELECT_COLOR, 0.5f, (int)Math.min(Input.getMouseX(), selectX), (int)Math.min(Input.getMouseY(), selectY), width, height);
		}
		//collisionManager.debug(renderer);
	}

	@Override
	public void reset(GameContainer gc) {
		gameObjects = new ArrayList<>();
		consumables = new ArrayList<>();
		enemies = new ArrayList<>();
		animals = new ArrayList<>();
		collisionManager = new CollisionManager();
		
		paul.setPos(50, 50);
		paul.setManager(collisionManager);
		paul.restoreFullHealth();
		paul.setDead(false);
		
		collisionManager.addCollidable(paul);
		collisionManager.addCollidable(grass.getObject());
		
		gameObjects.add(paul);
		
		new EmptyCollider(new Collider(-2, 0, 3, gc.getHeight()), collisionManager);
		new EmptyCollider(new Collider(gc.getWidth() - 1, 0, 3, gc.getHeight()), collisionManager);

		clip.adjustVolume();
		clip.loop();
	}
	
	
	public CollisionManager getCollisionManager() {
		return collisionManager;
	}
	
	public Character getCharacter() {
		return paul;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
