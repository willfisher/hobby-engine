package com.retrochicken.engine.physics;

import java.awt.Rectangle;

public class Collider {
	
	private float width, height;
	private float x, y;
	
	private Rectangle rect;
	
	private String tag = "";
	
	private boolean isTrigger = false;
	
	private boolean dispose = false;
	
	public Collider(float x, float y, float width, float height, String tag, boolean isTrigger) {
		this(x, y, width, height, tag);
		this.isTrigger = isTrigger;
	}
	
	public Collider(float x, float y, float width, float height, boolean isTrigger) {
		this(x, y, width, height);
		this.isTrigger = isTrigger;
	}
	
	public Collider(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		rect = new Rectangle((int)x, (int)y, (int)width, (int)height);
	}
	
	public Collider(float x, float y, float width, float height, String tag) {
		this(x, y, width, height);
		this.tag = tag;
	}
	
	public Collider(String tag) {
		this(0, 0, 0, 0);
		this.tag = tag;
	}
	
	public Collider() {
		this(0, 0, 0, 0);
	}
	
	public Rectangle collides(Collider collider) {
		return rect.intersection(collider.getRect());
	}
	
	public boolean fullyLeftRight(Collider collider) {
		return x + width <= collider.getX() || x >= collider.getX() + collider.getWidth();
	}
	
	public boolean fullyAboveBelow(Collider collider) {
		return y + height <= collider.getY() || y >= collider.getY() + collider.getHeight();
	}
	
	public boolean fullyLeftRight(float x, float width) {
		return this.x + this.width <= x + 1 || this.x + 1 >= x + width;
	}
	
	public boolean fullyAboveBelow(float y, float height) {
		return this.y + this.height <= y + 1 || this.y + 1 >= y + height;
	}
	
	public boolean toLeft(Collider collider) {
		return x <= collider.getX();
	}
	
	public boolean toAbove(Collider collider) {
		return y <= collider.getY();
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		rect.width = (int)width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		rect.height = (int)height;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
		rect.x = (int)x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
		rect.y = (int)y;
	}
	
	public void setPos(float x, float y) {
		setX(x);
		setY(y);
	}

	public Rectangle getRect() {
		return rect;
	}
	
	public String getTag() {
		return tag;
	}
	
	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isTrigger() {
		return isTrigger;
	}

	public void setTrigger(boolean isTrigger) {
		this.isTrigger = isTrigger;
	}

	public boolean isDispose() {
		return dispose;
	}

	public void setDispose(boolean dispose) {
		this.dispose = dispose;
	}
}
