package com.retrochicken.engine.physics;

import java.awt.Rectangle;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;

public class CollidableImage extends Image {
	
	private AssociatedObject object;
	private boolean collidesWithSimilarObjs = true;
	
	public CollidableImage(Image image, CollisionManager manager) {
		super();
		super.pixels = image.pixels;
		super.width = image.width;
		super.height = image.height;
		this.object = new AssociatedObject(new Collider(), this, manager);
	}
	
	public CollidableImage(Image image, float x, float y, CollisionManager manager) {
		super();
		super.pixels = image.pixels;
		super.width = image.width;
		super.height = image.height;
		this.object = new AssociatedObject(new Collider(x, y, this.width, this.height), this, manager);
	}
	
	public CollidableImage(Image image, float x, float y, boolean isKinematic, CollisionManager manager) {
		super();
		super.pixels = image.pixels;
		super.width = image.width;
		super.height = image.height;
		this.object = new AssociatedObject(new Collider(x, y, this.width, this.height), this, isKinematic, manager);
	}
	
	public CollidableImage(Image image, Collider collider, CollisionManager manager) {
		super();
		super.pixels = image.pixels;
		super.width = image.width;
		super.height = image.height;
		this.object = new AssociatedObject(collider, this, manager);
	}
	
	public CollidableImage(Image image, Collider collider, boolean isKinematic, CollisionManager manager) {
		super();
		super.pixels = image.pixels;
		super.width = image.width;
		super.height = image.height;
		this.object = new AssociatedObject(collider, this, isKinematic, manager);
	}
	
	public CollidableImage(String path, CollisionManager manager) {
		super(path);
		this.object = new AssociatedObject(new Collider(), this, manager);
	}
	
	public CollidableImage(String path, float x, float y, CollisionManager manager) {
		super(path);
		this.object = new AssociatedObject(new Collider(x, y, this.width, this.height), this, manager);
	}
	
	public CollidableImage(String path, float x, float y, boolean isKinematic, CollisionManager manager) {
		super(path);
		this.object = new AssociatedObject(new Collider(x, y, this.width, this.height), this, isKinematic, manager);
	}
	
	public CollidableImage(String path, Collider collider, CollisionManager manager) {
		super(path);
		this.object = new AssociatedObject(collider, this, manager);
	}
	
	public CollidableImage(String path, Collider collider, boolean isKinematic, CollisionManager manager) {
		super(path);
		this.object = new AssociatedObject(collider, this, isKinematic, manager);
	}
	
	public void setCollider(Collider collider) {
		object.setCollider(collider);;
	}
	
	public AssociatedObject getObject() {
		return object;
	}
	
	public void update(float timePassed) {
		object.update(timePassed);
	}
	
	public void render(Renderer renderer) {
		object.render(renderer);
	}
	
	public void setTag(String tag) {
		object.getCollider().setTag(tag);
	}
	
	public void collidesWithSimilarObjs(boolean collidesWithSimilarObjs) {
		this.collidesWithSimilarObjs = collidesWithSimilarObjs;
	}
	
	public class AssociatedObject extends GameObject implements Collidable {
		
		private Collider collider;
		private CollidableImage image;
		
		public void update(float timePassed) {
			super.update(timePassed);
			collider.setPos(super.getX(), super.getY());
		}
		
		@Override
		public void render(Renderer renderer) {
			renderer.drawImage(image, (int)super.getX(), (int)super.getY());
		}
		
		@Override
		public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
			if(!collider.isTrigger() && (collidesWithSimilarObjs || !collider.getTag().equals(this.collider.getTag()))) {
				if(Math.abs(super.getY() - super.previousY()) > 0 && collider.fullyAboveBelow(super.previousY(), this.collider.getHeight())) {
					super.setY(super.getY() + (this.collider.toAbove(collider) ? -1 : 1) * collision.height);
					if(this.collider.toAbove(collider)) super.hitGround(collider);
				}
				if(Math.abs(super.getX() - super.previousX()) > 0 && collider.fullyLeftRight(super.previousX(), this.collider.getWidth()))
					super.setX(super.getX() + (this.collider.toLeft(collider) ? -1 : 1) * collision.width);
			}
			
			this.collider.setPos(super.getX(), super.getY());
		}

		@Override
		public Collider getCollider() {
			return collider;
		}
		
		public AssociatedObject(Collider collider, CollidableImage image, boolean isKinematic, CollisionManager manager) {
			super(collider.getX(), collider.getY(), isKinematic);
			this.collider = collider;
			this.image = image;
			manager.addCollidable(this);
		}
		
		public AssociatedObject(Collider collider, CollidableImage image, CollisionManager manager) {
			super(collider.getX(), collider.getY());
			this.collider = collider;
			this.image = image;
			manager.addCollidable(this);
		}
		
		public void setCollider(Collider collider) {
			super.setPos(collider.getX(), collider.getY());
			super.setPos(collider.getX(), collider.getY());
			this.collider = collider;
		}
		
		public float getWidth() {
			return image.width;
		}
		
		public float getHeight() {
			return image.height;
		}
	}
}
