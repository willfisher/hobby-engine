package com.retrochicken.engine.interactable;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.physics.Collider;

public interface Animal {
	public void livingTrigger(Collider collider);
	public float getX();
	public float getY();
	public void update(float timePassed);
	public void render(Renderer renderer);
	public void setX(float x);
	public void setY(float y);
	public boolean isDisposable();
}
