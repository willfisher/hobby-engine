package com.retrochicken.engine.physics;

import java.awt.Rectangle;

public class EmptyCollider implements Collidable {
	
	private Collider collider;
	
	public EmptyCollider(Collider collider, CollisionManager manager) {
		this.collider = collider;
		manager.addCollidable(this);
	}
	
	@Override
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
		
	}

	@Override
	public Collider getCollider() {
		return collider;
	}
}
