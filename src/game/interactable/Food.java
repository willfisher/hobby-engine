package game.interactable;

import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.SpriteSheet;
import com.retrochicken.engine.interactable.Consumable;
import com.retrochicken.engine.interactable.Living;
import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;

public class Food extends Consumable implements Collidable {

	private static final SpriteSheet FOODS = new SpriteSheet("/interactable/fruittiles.png");
	
	private Collider collider;
	private int foodId;
	private float healthBoost;
	
	public Food(float x, float y, float healthBoost, CollisionManager manager) {
		super(x, y, true);
		this.healthBoost = healthBoost;
		foodId = ThreadLocalRandom.current().nextInt(1, 24 + 1);
		this.collider = new Collider(x, y, FOODS.getWidth(foodId - 1), FOODS.getHeight(0), true);
		manager.addCollidable(this);
	}
	
	public void update(float timePassed) {
		super.update(timePassed);
		collider.setPos(super.getX(), super.getY());
	}
	
	public void render(Renderer renderer)  {
		FOODS.drawSprite(renderer, foodId - 1, false, (int)super.getX(), (int)super.getY());
	}
	
	@Override
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		if(!collider.getTag().equals("enemy") && !collider.getTag().equals("player") && !collider.isTrigger()) {
			if(Math.abs(super.getY() - super.previousY()) > 0 && collider.fullyAboveBelow(super.previousY(), this.collider.getHeight())) {
				super.setY(super.getY() + (this.collider.toAbove(collider) ? -1 : 1) * collision.height);
				super.hitGround(collider);
			}
			if(Math.abs(super.getX() - super.previousX()) > 0 && collider.fullyLeftRight(super.previousX(), this.collider.getWidth()))
				super.setX(super.getX() + (this.collider.toLeft(collider) ? -1 : 1) * collision.width);
		}
		
		if(collider.getTag().equals("player") && !isUsed()) {
			used();
			((Living)collidable).recieveHealth(healthBoost);
		}
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public float getWidth() {
		return FOODS.getWidth(foodId - 1);
	}
	
	public float getHeight() {
		return FOODS.getHeight(foodId - 1);
	}

	@Override
	public void used() {
		setUsed(true);
		collider.setDispose(true);
	}
}
