package com.retrochicken.engine.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Font;

public class CheckBox implements UIElement {
	private boolean value = false;
	private String text;
	private ArrayList<ActionEvent> events = new ArrayList<>();
	private float x, y;
	
	public CheckBox(float x, float y, String text) {
		this.text = text;
		this.x = x;
		this.y = y;
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public void addActionEvent(ActionEvent event) {
		events.add(event);
	}

	@Override
	public int getBottom() {
		return (int)Math.max(y + 5, y + Font.STANDARD.getHeight(text)/2.0f);
	}

	@Override
	public int getTop() {
		return (int)Math.min(y - 5, y - Font.STANDARD.getHeight(text)/2.0f);
	}

	@Override
	public float getY() {
		return getTop();
	}

	@Override
	public void setY(float y) {
		this.y += y - getTop();
	}

	@Override
	public void update() {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && inBounds()) {
			value = !value;
			onCheck();
		}
	}
	
	private boolean inBounds() {
		return Input.getMouseX() >= x && Input.getMouseX() <= x + 10 && Input.getMouseY() >= y - 5 && Input.getMouseY() <= y + 5;
	}

	@Override
	public void update(Rectangle bounds) {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && inBounds() && Input.mouseInBounds(bounds)) {
			value = !value;
			onCheck();
		}
	}
	
	private void onCheck() {
		for(ActionEvent event : events)
			event.onCheck(value);
	}

	@Override
	public void render(Renderer renderer) {
		render(renderer, null);
	}

	@Override
	public int getLeft() {
		return (int)x;
	}

	@Override
	public int getRight() {
		return (int)(x + 10 + 10 + Font.STANDARD.getWidth(text));
	}

	@Override
	public void setX(float x) {
		this.x += x - getLeft() - (getRight() - getLeft())/2.0f;
	}

	@Override
	public float getX() {
		return x + (getRight() - getLeft())/2.0f;
	}

	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		renderer.drawEmptyRect((int)x, (int)(y - 5), 10, 10, 0xffff00ff, bounds);
		if(value) renderer.drawRect((int)(x + 2), (int)(y - 3), 6, 6, 0xff4b0082, bounds);
		renderer.drawString(text, 0xffff00ff, (int)(x + 10 + 10), (int)(y - renderer.stringHeight(text)/2.0f), bounds);
	}
}
