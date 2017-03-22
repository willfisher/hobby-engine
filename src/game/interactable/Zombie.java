package game.interactable;

import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Animation;
import com.retrochicken.engine.fx.SoundClip;
import com.retrochicken.engine.fx.SpriteSheet;
import com.retrochicken.engine.interactable.EnemyAI;
import com.retrochicken.engine.interactable.Living;
import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;

public class Zombie extends Living implements Collidable {
	
	public static enum State {
		IDLE_LEFT(0), IDLE_RIGHT(1), WALK_LEFT(2), WALK_RIGHT(3);
		
		public int id;
		private State(int id) {
			this.id = id;
		}
	}
	
	private EnemyAI ai;
	
	private Collider collider;
	
	private Animation[] animations = new Animation[4];
	private State currentState = State.IDLE_LEFT;
	
	private SoundClip sound;
	private float elapsedTime = 1;
	
	public Zombie(float x, float y, Living target, CollisionManager manager) {
		super(x, y, true);
		
		SpriteSheet zombieSprite = new SpriteSheet("/characters/zombiechar.png");
		animations = new Animation[]{new Animation(zombieSprite, new int[]{0}, 5, true),
									 new Animation(zombieSprite, new int[]{0}, 5),
									 new Animation(zombieSprite, new int[]{1, 2, 3, 4}, 0.5f, true),
									 new Animation(zombieSprite, new int[]{1, 2, 3, 4}, 0.5f)};
		
		collider = new Collider(x, y, animations[0].getWidth(), animations[0].getHeight(), "enemy");
		manager.addCollidable(this);
		
		setState(State.IDLE_LEFT);
		
		ai = new EnemyAI(this, target, 25, 10, 1);
		
		sound = new SoundClip("/sounds/zombie/zombie-" + ThreadLocalRandom.current().nextInt(1, 24 + 1) + ".wav");
		sound.setVolumeStandard(60);
	}
	
	public void update(float timePassed) {
		if(elapsedTime >= 1) {
			if(!sound.isRunning())
				trySound();
			elapsedTime = 0;
		}
		elapsedTime += timePassed;
		
		super.update(timePassed);
		
		ai.update(timePassed);
		
		collider.setPos(super.getX(), super.getY());
		
		if(ai.isMoving())
			setState(ai.isMovingLeft() ? State.WALK_LEFT : State.WALK_RIGHT);
		else
			setState(ai.isMovingLeft() ? State.IDLE_LEFT : State.IDLE_RIGHT);
		
		animations[currentState.id].update(timePassed);
	}
	
	private void trySound() {
		if(Math.random() >= 0.5)
			sound.play();
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
	
	public void render(Renderer renderer) {
		render(renderer, (int)super.getX(), (int)super.getY());
	}
	
	public void render(Renderer renderer, int x, int y) {
		animations[currentState.id].render(renderer, x, y);
		super.drawHealthBar(renderer, (int)super.getX() + (int)(getWidth()/2.0f) - 15);
	}
	
	public float getWidth() {
		return collider.getWidth();
	}
	
	public float getHeight() {
		return collider.getHeight();
	}
	
	@Override
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		if(!collider.getTag().equals("enemy") && !collider.isTrigger()) {
			if(Math.abs(super.getY() - super.previousY()) > 0 && collider.fullyAboveBelow(super.previousY(), this.collider.getHeight())) {
				super.setY(super.getY() + (this.collider.toAbove(collider) ? -1 : 1) * collision.height);
				if(this.collider.toAbove(collider)) super.hitGround(collider);
			}
			if(Math.abs(super.getX() - super.previousX()) > 0 && collider.fullyLeftRight(super.previousX(), this.collider.getWidth())) {
				super.setX(super.getX() + (this.collider.toLeft(collider) ? -1 : 1) * collision.width);
				ai.collided(collider);
			}
		}
		
		this.collider.setPos(super.getX(), super.getY());
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public void dead() {
		//sound.close();
		ai.setDead(true);
		collider.setDispose(true);
	}
}
