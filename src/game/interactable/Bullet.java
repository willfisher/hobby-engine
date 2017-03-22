package game.interactable;

import java.awt.Rectangle;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.interactable.Consumable;
import com.retrochicken.engine.interactable.Living;
import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;
import com.retrochicken.engine.physics.Physics;

public class Bullet extends Consumable implements Collidable {
	
	public static final Image IMAGE = new Image("/misc/bullet.png");
	private Collider collider;
	
	private static final float SPEED = 3 * 1/Physics.PIXELS_TO_METERS;
	private boolean direction;
	
	private float damage;
	
	public Bullet(float x, float y, float damage, boolean direction, CollisionManager manager) {
		super(x, y, false);
		
		collider = new Collider(x, y, IMAGE.width, IMAGE.height, true);
		manager.addCollidable(this);
		
		this.damage = damage;
		this.direction = direction;
	}
	
	public void update(float timePassed) {
		super.setX(super.getX() + (direction ? 1 : -1) * SPEED * timePassed);
		collider.setPos(super.getX(), super.getY());
	}
	
	public void render(Renderer renderer) {
		renderer.drawImage(IMAGE, (int)super.getX(), (int)super.getY(), !direction);
	}

	@Override
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		if(!isUsed() && collider.getTag().equals("enemy") && Living.class.isAssignableFrom(collidable.getClass())) {
			((Living) collidable).recieveDamage(damage);
		}
		if(!collider.isTrigger())
			used();
	}

	@Override
	public Collider getCollider() {
		return collider;
	}

	@Override
	public float getWidth() {
		return IMAGE.width;
	}
	
	public float getHeight() {
		return IMAGE.height;
	}

	@Override
	public void used() {
		setUsed(true);
		collider.setDispose(true);
	}
}
