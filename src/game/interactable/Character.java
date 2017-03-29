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
	
	private boolean facingRight = true;
	public static enum State {
		IDLE_LEFT(0), IDLE_RIGHT(1), WALK_LEFT(2), WALK_RIGHT(3), ATTACKING(4);
		
		public int id;
		private State(int id) {
			this.id = id;
		}
	}
	
	private Collider collider;
	
	private Animation[] animations;
	private float[] origLengths;
	private State currentState;
	
	private Animation[][] attackAnimations;
	private int currentAttack;
	
	private float sprintFactor = 2;
	private boolean isSprinting = false;
	
	private ArrayList<Consumable> consumables = new ArrayList<>();
	
	private CollisionManager manager;
	
	private Inventory inventory;
	
	public Character(Animation[] animations, Animation[][] attackAnimations, CollisionManager manager, Inventory inventory) {
		super(0, 0, true);
		this.attackAnimations = attackAnimations;
		this.animations = animations;
		origLengths = new float[animations.length];
		for(int i = 0; i < animations.length; i++)
			origLengths[i] = animations[i].getAnimTime();
		for(Animation anim : animations)
			anim.setShadowType(ShadowType.FADE);
		
		currentState = State.WALK_LEFT;
		
		this.manager = manager;
		
		collider = new Collider(0, 0, animations[0].getWidth(0), animations[0].getHeight(0), "player");
		manager.addCollidable(this);
		
		this.inventory = inventory;
	}
	
	public Character(Animation[] animations, Animation[][] attackAnimations, float x, float y, CollisionManager manager, Inventory inventory) {
		this(animations, attackAnimations, manager, inventory);
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
			facingRight = false;
		} else if(Input.isKey(KeyEvent.VK_D)) {
			super.setX(super.getX() + timePassed * 50 * (isSprinting ? sprintFactor : 1));
			setState(Character.State.WALK_RIGHT);
			facingRight = true;
		} else
			setState(facingRight ? Character.State.IDLE_RIGHT : Character.State.IDLE_LEFT);
		if(Input.isKeyPressed(KeyEvent.VK_SPACE) && grounded)
			super.verticalSpeed += Physics.GRAVITY * 0.5f;
		
		if(Input.isKeyPressed(KeyEvent.VK_RIGHT))
			consumables.add(new Bullet(super.getX() + getWidth() + 10, super.getY() + animations[0].getHeight()/4.0f, 25, true, manager));
		if(Input.isKeyPressed(KeyEvent.VK_LEFT))
			consumables.add(new Bullet(super.getX() - 10 - Bullet.IMAGE.width, super.getY() + animations[0].getHeight()/4.0f, 25, false, manager));
		if(Input.isKeyPressed(KeyEvent.VK_UP))
			currentAttack = (currentAttack + 1) % attackAnimations.length;
		if(Input.isKeyPressed(KeyEvent.VK_DOWN)) {
			currentAttack -= 1;
			if(currentAttack < 0)
				currentAttack = attackAnimations.length - currentAttack;
		}
		
		
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
