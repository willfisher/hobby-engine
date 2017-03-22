package game.interactable;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Animation;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.SpriteSheet;
import com.retrochicken.engine.interactable.Animal;
import com.retrochicken.engine.interactable.AnimalAI;
import com.retrochicken.engine.physics.CollidableImage;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;

public class Pigeon extends CollidableImage implements Animal {
	
	private AnimalAI ai;
	
	private static final Image IMAGE = new Image("/icons/pigeonicon.png");
	private static final SpriteSheet PIGEON = new SpriteSheet("/interactable/pigeontile.png");
	private final Animation flyLeft = new Animation(PIGEON, new int[]{0, 1}, 1, true);
	private final Animation flyRight = new Animation(PIGEON, new int[]{0, 1}, 1, false);
	
	private boolean isFlying = false;
	private boolean flyingLeft = false;
	
	private boolean isDisposable = false;
	
	public Pigeon(float x, float y, CollisionManager manager) {
		super(IMAGE, x, y, true, manager);
		Collider base = super.getObject().getCollider();
		super.setTag("pigeon");
		super.collidesWithSimilarObjs(false);
		ai = new AnimalAI(this, new Collider(base.getX() - base.getWidth(), base.getY() - base.getHeight(), 3 * base.getWidth(), 3 * base.getHeight(), true), manager);
		ai.canRoam(true, 25);
	}
	
	@Override
	public void update(float timePassed) {
		super.update(timePassed);
		ai.update(timePassed);
		if(isFlying) {
			flyLeft.update(timePassed);
			flyRight.update(timePassed);
			super.getObject().setX(super.getObject().getX() + (flyingLeft ? -1 : 1) * 50 * timePassed);
			super.getObject().setY(super.getObject().getY() - 150 * timePassed);
			if(super.getObject().getY() + super.height < 0)
				isDisposable = true;
		}
	}
	
	@Override
	public void render(Renderer renderer) {
		if(!isFlying) {
			if(ai.isWalkingLeft())
				renderer.drawImage(IMAGE, (int)super.getObject().getX(), (int)super.getObject().getY(), true); 
			else
				renderer.drawImage(IMAGE, (int)super.getObject().getX(), (int)super.getObject().getY(), false);
		} else {
			if(flyingLeft)
				flyLeft.render(renderer, (int)super.getObject().getX(), (int)super.getObject().getY());
			else
				flyRight.render(renderer, (int)super.getObject().getX(), (int)super.getObject().getY());
		}
	}

	@Override
	public void livingTrigger(Collider collider) {
		isFlying = true;
		flyingLeft = super.getObject().getCollider().toLeft(collider);
		if(flyingLeft)
			flyLeft.play();
		else
			flyRight.play();
		super.getObject().setKinematic(false);
		super.getObject().getCollider().setDispose(true);
		ai.getCollider().setDispose(true);
		ai.canRoam(false, 0);
	}

	@Override
	public float getX() {
		return super.getObject().getX();
	}

	@Override
	public float getY() {
		return super.getObject().getY();
	}

	@Override
	public void setX(float x) {
		super.getObject().setX(x);
	}

	@Override
	public void setY(float y) {
		super.getObject().setY(y);
	}

	@Override
	public boolean isDisposable() {
		return isDisposable;
	}
}
