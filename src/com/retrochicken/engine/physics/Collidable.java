package com.retrochicken.engine.physics;

import java.awt.Rectangle;

public interface Collidable {
	public void onCollide(Collider collider, Rectangle collision, Collidable collidable);
	public Collider getCollider();
}
