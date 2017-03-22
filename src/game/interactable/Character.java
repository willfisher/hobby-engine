package game.interactable;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Animation;
import com.retrochicken.engine.fx.ShadowType;
import com.retrochicken.engine.interactable.Consumable;
import com.retrochicken.engine.interactable.Living;
import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;
import com.retrochicken.engine.physics.Physics;

import game.Inventory;

public class Character extends Living implements Collidable {
	
	public static enum State {
		IDLE(0), WALK_LEFT(1), WALK_RIGHT(2);
		
		public int id;
		private State(int id) {
			this.id = id;
		}
	}
	
	private Collider collider;
	
	private Animation[] animations = new Animation[3];
	private float[] origLengths = new float[3];
	private State currentState;
	
	private float sprintFactor = 2;
	private boolean isSprinting = false;
	
	private ArrayList<Consumable> consumables = new ArrayList<>();
	
	private CollisionManager manager;
	
	private Inventory inventory;
	
	public Character(Animation idle, Animation walkLeft, Animation walkRight, CollisionManager manager, Inventory inventory) {
		super(0, 0, true);
		animations[0] = idle;
		origLengths[0] = idle.getAnimTime();
		animations[1] = walkLeft;
		origLengths[1] = walkLeft.getAnimTime();
		animations[2] = walkRight;
		origLengths[2] = walkRight.getAnimTime();
		for(Animation anim : animations)
			anim.setShadowType(ShadowType.FADE);
		
		currentState = State.WALK_LEFT;
		
		this.manager = manager;
		
		collider = new Collider(0, 0, idle.getWidth(0), idle.getHeight(0), "player");
		manager.addCollidable(this);
		
		this.inventory = inventory;
	}
	
	public Character(Animation idle, Animation walkLeft, Animation walkRight, float x, float y, CollisionManager manager, Inventory inventory) {
		this(idle, walkLeft, walkRight, manager, inventory);
		super.setPos(x, y);
		collider.setPos(x, y);
	}
	
	public void onCollide(Collider collider) {
		
	}
	
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		if(!collider.isTrigger()) {
			if(Math.abs(super.getY() - super.previousY()) > 0 && collider.fullyAboveBelow(super.previousY(), this.collider.getHeight())) {
				super.setY(super.getY() + (this.collider.toAbove(collider) ? -1 : 1) * collision.height);
				if(this.collider.toAbove(collider)) super.hitGround(collider);
			}
			if(Math.abs(super.getX() - super.previousX()) > 0 && collider.fullyLeftRight(super.previousX(), this.collider.getWidth()))
				super.setX(super.getX() + (this.collider.toLeft(collider) ? -1 : 1) * collision.width);
		}
		
		this.collider.setPos(super.getX(), super.getY());
	}
	
	public void setState(State state) {
		if(currentState != state)
			currentState = state;
		else
			return;
		
		for(int i = 0; i < animations.length; i++) {
			if(i == currentState.id)
				animations[i].play();
			else
				animations[i].stop();
		}
	}
	
	public void update(float timePassed) {
		if(Input.isKey(KeyEvent.VK_A)) {
			super.setX(super.getX() - timePassed * 50 * (isSprinting ? sprintFactor : 1));
			setState(Character.State.WALK_LEFT);
		} else if(Input.isKey(KeyEvent.VK_D)) {
			super.setX(super.getX() + timePassed * 50 * (isSprinting ? sprintFactor : 1));
			setState(Character.State.WALK_RIGHT);
		} else
			setState(Character.State.IDLE);
		if(Input.isKeyPressed(KeyEvent.VK_SPACE) && grounded)
			super.verticalSpeed += Physics.GRAVITY * 0.5f;
		
		if(Input.isKeyPressed(KeyEvent.VK_E))
			consumables.add(new Bullet(super.getX() + getWidth() + 10, super.getY() + animations[0].getHeight()/4.0f, 25, true, manager));
		if(Input.isKeyPressed(KeyEvent.VK_Q))
			consumables.add(new Bullet(super.getX() - 10 - Bullet.IMAGE.width, super.getY() + animations[0].getHeight()/4.0f, 25, false, manager));
		
		
		if(Input.isKey(KeyEvent.VK_SHIFT))
			isSprinting = true;
		if(Input.isKeyReleased(KeyEvent.VK_SHIFT))
			isSprinting = false;
		
		super.update(timePassed);
		
		collider.setPos(super.getX(), super.getY());
		
		for(int i = consumables.size() - 1; i >= 0; i--) {
			consumables.get(i).update(timePassed);
			if(consumables.get(i).isUsed())
				consumables.remove(i);
		}
		
		if(isSprinting && animations[currentState.id].getAnimTime() == origLengths[currentState.id])
			animations[currentState.id].setAnimTime(origLengths[currentState.id]/sprintFactor);
		else if(!isSprinting && animations[currentState.id].getAnimTime() != origLengths[currentState.id])
			animations[currentState.id].setAnimTime(origLengths[currentState.id]);
		
		animations[currentState.id].update(timePassed);
	}
	
	public void render(Renderer renderer) {
		render(renderer, (int)super.getX(), (int)super.getY());
	}
	
	public void render(Renderer renderer, int x, int y) {
		animations[currentState.id].render(renderer, x, y);
		super.drawHealthBar(renderer, (int)(super.getX()) + (int)(getWidth()/2.0f) - 15);
		for(Consumable con : consumables)
			con.render(renderer);
	}
	
	public Collider getCollider() {
		return collider;
	}
	
	public String getTag() {
		return "player";
	}
	
	public float getWidth() {
		return collider.getWidth();
	}
	
	public float getHeight() {
		return collider.getHeight();
	}

	@Override
	public void dead() {
		
	}

	public void setManager(CollisionManager manager) {
		this.manager = manager;
	}

	public Inventory getInventory() {
		return inventory;
	}
}
