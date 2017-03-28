package com.retrochicken.engine.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;

public class ScrollPanel implements UIElement {
	private ArrayList<UIElement> elements = new ArrayList<>();
	
	private int padding = 10;
	
	private float maxWidth;
	
	private boolean scrollVisible = false;
	private float origX;
	private float x, y, width;
	private Rectangle bounds;
	
	private float scrollY;
	private boolean scrolling;
	private float scrollHeight;
	private float startY;
	private float startMouse;
	
	public ScrollPanel(float x, float y, float width, float height) {
		this.x = origX = x;
		this.y = scrollY = y;
		this.width = width;
		bounds = new Rectangle((int)x, (int)y, (int)width, (int)height);
	}
	
	public void addElement(UIElement element) {
		if(elements.size() > 0)
			element.setY(elements.get(elements.size() - 1).getBottom() + padding);
		else
			element.setY(y);
		if(element.getRight() - element.getLeft() > maxWidth) {
			maxWidth = element.getRight() - element.getLeft();
			this.x = origX - maxWidth/2.0f;
			this.width = maxWidth;
			bounds.x = (int)this.x - 2;
			bounds.width = (int)this.width + 4;
		}
		element.setX(origX);
		elements.add(element);
		
		if(!scrollVisible && elements.size() > 0 && elements.get(elements.size() - 1).getBottom() - elements.get(0).getTop() > bounds.height) {
			scrollVisible = true;
			scrollHeight = bounds.height * bounds.height/(float)(elements.get(elements.size() - 1).getBottom() - elements.get(0).getTop());
		}
	}
	
	private boolean inBounds() {
		return Input.getMouseX() >= x + bounds.width + 5 && Input.getMouseX() <= x + bounds.width + 15
				&& Input.getMouseY() >= scrollY && Input.getMouseY() <= scrollY + scrollHeight;
	}
	
	public void update() {
		if(scrollVisible) {
			if(scrolling) {
				float newY = startY + Input.getMouseY() - startMouse;
				if(newY < y - 1)
					newY = y - 1;
				else if(newY + scrollHeight > y + bounds.height + 1)
					newY = y + bounds.height - scrollHeight + 1;
				if(Math.abs(scrollY - newY) > 1) {
					scrollY = newY;
					float currY = y - (elements.get(elements.size() - 1).getBottom() - elements.get(0).getTop() - bounds.height) * (scrollY - y)/(bounds.height - scrollHeight);
					for(int i = 0; i < elements.size(); i++) {
						elements.get(i).setY(currY);
						currY += elements.get(i).getBottom() - elements.get(i).getTop() + padding;
					}
				}
				if(Input.isButtonReleased(MouseEvent.BUTTON1))
					scrolling = false;
			} else if(Input.isButtonPressed(MouseEvent.BUTTON1) && inBounds()) {
				scrolling = true;
				startY = scrollY;
				startMouse = Input.getMouseY();
			}
		}
		
		for(UIElement element : elements)
			element.update(bounds);
	}
	
	public void render(Renderer renderer) {
		for(UIElement element : elements)
			element.render(renderer, bounds);
		if(scrollVisible) {
			renderer.drawRect((int)(bounds.x + bounds.width + 5), (int)y, 10, bounds.height, 0xffff00ff);
			renderer.drawRect((int)(bounds.x + bounds.width + 5), (int)scrollY, 10, (int)scrollHeight, 0xff4b0082);
			renderer.drawRect((int)(bounds.x + bounds.width + 7), (int)scrollY + 3, 2, (int)scrollHeight - 5, 0xff6000a7);
		}
	}
	
	public int getBottom() {
		return bounds.y + bounds.height;
	}

	@Override
	public int getTop() {
		return bounds.y;
	}

	@Override
	public float getY() {
		return bounds.y;
	}
	
	//TODO: Make these more accurate, center on X.
	@Override
	public void setY(float y) {
		float deltaY = (int)(y - this.y);
		for(UIElement element : elements)
			element.setY(element.getY() + deltaY);
		bounds.y += deltaY;
		scrollY += deltaY;
		this.y += deltaY;
	}

	@Override
	public void update(Rectangle bounds) {
		update();
	}

	@Override
	public int getLeft() {
		return bounds.x;
	}

	@Override
	public int getRight() {
		return bounds.x + bounds.width;
	}

	@Override
	public void setX(float x) {
		float deltaX = (int)(x - bounds.x);
		for(UIElement element : elements)
			element.setX(element.getX() + deltaX);
		bounds.x += deltaX;
		origX += deltaX;
		this.x += deltaX;
	}

	@Override
	public float getX() {
		return bounds.x;
	}

	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		render(renderer);
	}
}
