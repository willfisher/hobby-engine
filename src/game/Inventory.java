package game;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Settings;
import com.retrochicken.engine.physics.CollidableImage;

import game.crafting.CraftableItems;
import game.interactable.Food;
import game.interactable.Noose;
import game.interactable.Pigeon;
import game.interactable.Zombie;
import game.scenes.MainScene;

public class Inventory {
	private ArrayList<Icon> icons = new ArrayList<>();
	private ArrayList<Icon> hotKey = new ArrayList<>();
	private int hotKeySize = 6;
	private int activeIndex = 0;
	
	private int rowSize = 8;
	private int inventorySize = 24;
	
	private int activeColor = 0xff7fff00;
	private int outerBorder = 0xff2b1d0e;
	private int innerBorder = 0xff3e2a14;
	
	private MainScene scene;
	
	private boolean isHotkeyVisible = false;
	private long visibleTime;
	
	private boolean isVisible = false;
	
	private CraftableItems ci;
	
	private boolean movingInv = false;
	private boolean movingHot = false;
	private int invIndex, hotIndex;
	
	public Inventory(MainScene scene) {
		this.scene = scene;
		ci = new CraftableItems(scene);
	}
	
	public void addIcon(Icon icon) {
		for(int i = 0; i < icons.size(); i++) {
			if(icons.get(i).getTag().equals(icon.getTag())) {
				icons.get(i).setAmount(icons.get(i).getAmount() + icon.getAmount());
				return;
			}
		}
		if(icons.size() >= inventorySize)
			return;
		icons.add(icon);
		if(hotKey.size() < hotKeySize)
			hotKey.add(icon);
	}
	
	public void update() {
		if(Input.isButtonPressed(MouseEvent.BUTTON3)) {
			switch(hotKey.get(activeIndex).getTag().toLowerCase()) {
				case "zombie":
					scene.addEnemy(new Zombie(Input.getMouseX(), Input.getMouseY(), scene.getCharacter(), scene.getCollisionManager()));
					break;
				case "food":
					scene.addConsumable(new Food(Input.getMouseX(), Input.getMouseY(), 20, scene.getCollisionManager()));
					break;
				case "barrel":
					scene.addGameObject((new CollidableImage("/props/barrel.png", Input.getMouseX(), Input.getMouseY(), true, scene.getCollisionManager())).getObject());
					break;
				case "pigeon":
					scene.addAnimal(new Pigeon(Input.getMouseX(), Input.getMouseY(), scene.getCollisionManager()));
					break;
				case "noose":
					scene.addGameObject(new Noose(Input.getMouseX(), Input.getMouseY(), scene.getCharacter()));
					break;
				case "wood":
					scene.addGameObject(ci.new Wood(Input.getMouseX(), Input.getMouseY(), scene.getCollisionManager()));
					break;
				case "metal":
					scene.addGameObject(ci.new Metal(Input.getMouseX(), Input.getMouseY(), scene.getCollisionManager()));
					break;
			}
			int i = hotKey.get(activeIndex).getAmount() - 1;
			hotKey.get(activeIndex).setAmount(i);
			if(hotKey.get(activeIndex).isExpendable() && i <= 0)
				removeItem(hotKey.get(activeIndex));
		}
		
		if(Input.isKeyPressed(KeyEvent.VK_I)) {
			isVisible = !isVisible;
			isHotkeyVisible = isVisible;
			if(!isVisible) {
				movingHot = movingInv = false;
				Settings.CAN_SELECT = true;
			} else
				Settings.CAN_SELECT = false;
		}
		
		if(Input.isWheelRot()) {
			isHotkeyVisible = true;
			activeIndex -= Math.signum(Input.getWheelRots());
			if(activeIndex < 0)
				activeIndex = 0;
			else if(activeIndex >= hotKey.size())
				activeIndex = hotKey.size() - 1;
			visibleTime = System.currentTimeMillis();
		}
		
		if(!isVisible && isHotkeyVisible && System.currentTimeMillis() - visibleTime >= 3000)
			isHotkeyVisible = false;
		
		if(isVisible && Input.isButtonPressed(MouseEvent.BUTTON1)) {
			int i = mouseOnHotkeyIndex();
			if(i != -1 && i < hotKey.size()) {
				movingHot = true;
				hotIndex = i;
			}
			i = mouseOnInventoryIndex();
			if(i != -1 && i < icons.size()) {
				movingInv = true;
				invIndex = i;
			}
		}
		if((movingHot || movingInv) && Input.isButtonReleased(MouseEvent.BUTTON1)) {
			int i = mouseOnInventoryIndex();
			if(movingHot && i != -1 && i < icons.size())
				hotKey.set(hotIndex, icons.get(i));
			i = mouseOnHotkeyIndex();
			if(movingInv && i != -1) {
				if(i < hotKey.size())
					hotKey.set(i, icons.get(invIndex));
				else
					hotKey.add(icons.get(invIndex));
			}
			movingHot = movingInv = false;
		}
	}
	
	public void render(Renderer renderer) {
		if(isHotkeyVisible) {
			int startX = (int)((scene.getWidth() - 20 * hotKeySize)/2.0f);
			int startY = 10;
			for(int i = 0; i < hotKey.size(); i++) {
				renderer.drawRect(startX + i * 20, startY, 20, 20, outerBorder);
				renderer.drawRect(startX + i * 20 + 1, startY + 1, 18, 18, i == activeIndex ? activeColor : innerBorder);
				if(movingHot && i == hotIndex)
					continue;
				hotKey.get(i).render(renderer, startX + i * 20 + 2, startY + 2);
				if(hotKey.get(i).isExpendable())
					renderer.drawString("X" + hotKey.get(i).getAmount(), 0xffffffff, startX + (i + 1) * 20 - 3 - (int)renderer.stringWidth("X" + hotKey.get(i).getAmount()), startY + 20 - 3 - (int)renderer.stringHeight("X" + hotKey.get(i).getAmount()));
			}
			for(int i = hotKey.size(); i < hotKeySize; i++) {
				renderer.drawRect(startX + i * 20, startY, 20, 20, outerBorder);
				renderer.drawRect(startX + i * 20 + 1, startY + 1, 18, 18, i == activeIndex ? activeColor : innerBorder);
			}
		}
		if(isVisible) {
			int startX = (int)((scene.getWidth() - 20 * rowSize)/2.0f);
			int startY = 50;
			for(int i = 0; i < icons.size(); i++) {
				renderer.drawRect(startX + (i % rowSize) * 20, startY + 20 * Math.floorDiv(i, rowSize), 20, 20, outerBorder);
				renderer.drawRect(startX + (i % rowSize) * 20 + 1, startY + 20 * Math.floorDiv(i, rowSize) + 1, 18, 18, innerBorder);
				icons.get(i).render(renderer, startX + i * 20 + 2, startY + 2);
				if(icons.get(i).isExpendable())
					renderer.drawString("X" + icons.get(i).getAmount(), 0xffffffff, startX + ((i % rowSize) + 1) * 20 - 3 - (int)renderer.stringWidth("X" + icons.get(i).getAmount()), startY + 20 * (Math.floorDiv(i, rowSize) + 1) - 3 - (int)renderer.stringHeight("X" + icons.get(i).getAmount()));
			}
			for(int i = icons.size(); i < inventorySize; i++) {
				renderer.drawRect(startX + (i % rowSize) * 20, startY + 20 * Math.floorDiv(i, rowSize), 20, 20, outerBorder);
				renderer.drawRect(startX + (i % rowSize) * 20 + 1, startY + 20 * Math.floorDiv(i, rowSize) + 1, 18, 18, innerBorder);
			}
		}
		if(movingHot)
			hotKey.get(hotIndex).render(renderer, (int)Input.getMouseX(), (int)Input.getMouseY());
		else if(movingInv)
			icons.get(invIndex).render(renderer, (int)Input.getMouseX(), (int)Input.getMouseY());
	}
	
	private int mouseOnHotkeyIndex() {
		int startX = (int)((scene.getWidth() - 20 * hotKeySize)/2.0f);
		int startY = 10;
		for(int i = 0; i < hotKeySize; i++) {
			if((new Rectangle(startX + i * 20, startY, 20, 20)).contains((int)Input.getMouseX(), (int)Input.getMouseY()))
				return i;
		}
		return -1;
	}
	
	private int mouseOnInventoryIndex() {
		int startX = (int)((scene.getWidth() - 20 * rowSize)/2.0f);
		int startY = 50;
		for(int i = 0; i < inventorySize; i++) {
			if((new Rectangle(startX + (i % rowSize) * 20, startY + 20 * Math.floorDiv(i, rowSize), 20, 20)).contains((int)Input.getMouseX(), (int)Input.getMouseY()))
				return i;
		}
		return -1;
	}
	
	public void removeItem(Icon icon) {
		for(int i = hotKey.size() - 1; i >= 0; i--) {
			if(hotKey.get(i).getTag().equals(icon.getTag()))
				hotKey.remove(i);
		}
		for(int i = icons.size() - 1; i >= 0; i--) {
			if(icons.get(i).getTag().equals(icon.getTag()))
				icons.remove(i);
		}
	}
}
