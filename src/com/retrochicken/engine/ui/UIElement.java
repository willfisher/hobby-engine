package com.retrochicken.engine.ui;

import java.awt.Rectangle;

import com.retrochicken.engine.Renderer;

public interface UIElement {
	public int getBottom();
	public int getTop();
	public float getY();
	public void setY(float y);
	public void update();
	public void update(Rectangle bounds);
	public void render(Renderer renderer);
	public int getLeft();
	public int getRight();
	public void setX(float x);
	public float getX();
	public void render(Renderer renderer, Rectangle bounds);
}
