package com.retrochicken.engine.ui;

import java.awt.event.MouseEvent;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;

public class UIMount {
	private UIElement element;
	private int color, padding;
	
	private boolean isDragging = false;
	
	private int[] dragOffset = new int[]{-1, -1};
	
	private boolean isVisible = false;
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	public boolean isVisible() {
		return isVisible;
	}
	
	public UIMount(UIElement element, int color, int padding) {
		this.element = element;
		this.color = color;
		this.padding = padding;
	}
	
	public void update() {
		if(!isVisible)
			return;
		element.update();
		if(isDragging) {
			if(Input.isButtonReleased(MouseEvent.BUTTON1)) {
				isDragging = false;
				dragOffset[0] = dragOffset[1] = -1;
			} else {
				element.setX(Input.getMouseX() + dragOffset[0]);
				element.setY(Input.getMouseY() + dragOffset[1]);
			}
		}
		if(!isDragging && Input.isButton(MouseEvent.BUTTON1) && inBounds()) {
			isDragging = true;
			dragOffset[0] = (int)(element.getX() - Input.getMouseX());
			dragOffset[1] = (int)(element.getY() - Input.getMouseY());
		}
	}
	
	public void render(Renderer renderer) {
		if(!isVisible)
			return;
		renderer.drawTransparentRect(color, 0.5f, (int)(element.getLeft() - padding), (int)(element.getTop() - padding), element.getRight() - element.getLeft() + 2*padding, element.getBottom() - element.getTop() + 2*padding);
		element.render(renderer);
	}
	
	private boolean inBounds() {
		return Input.getMouseX() >= element.getLeft() - padding && Input.getMouseX() <= element.getRight() + padding
				&& Input.getMouseY() >= element.getTop() - padding && Input.getMouseY() <= element.getTop();
	}
}
