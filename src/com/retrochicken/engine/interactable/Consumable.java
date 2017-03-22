package com.retrochicken.engine.interactable;

import com.retrochicken.engine.physics.GameObject;

public abstract class Consumable extends GameObject {
	
	private boolean used = false;
	
	public Consumable(float x, float y) {
		super(x, y);
	}
	
	public Consumable(float x, float y, boolean isKinematic) {
		super(x, y, isKinematic);
	}
	
	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public abstract void used();
}
