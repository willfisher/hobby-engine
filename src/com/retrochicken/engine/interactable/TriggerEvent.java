package com.retrochicken.engine.interactable;

import java.awt.Rectangle;

import com.retrochicken.engine.physics.Collidable;
import com.retrochicken.engine.physics.Collider;
import com.retrochicken.engine.physics.CollisionManager;

public class TriggerEvent {
	private Collider collider;
	
	public TriggerEvent(Collider collider, String triggerTag, CollisionManager manager) {
		manager.addCollidable(new Collidable() {
			@Override
			public void onCollide(Collider collider, Rectangle collision, Collidable collidable) {
				if(collidable.getCollider().getTag().equals(triggerTag))
					onTagCollide();
			}

			@Override
			public Collider getCollider() {
				return collider;
			}
		});
		this.collider = collider;
	}
	
	public void deactivate() {
		collider.setDispose(true);
	}
	
	public void onTagCollide() {
		
	}
	
	public void setPos(float x, float y) {
		collider.setPos(x, y);
	}
}
