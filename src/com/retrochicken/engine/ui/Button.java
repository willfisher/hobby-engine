package com.retrochicken.engine.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.ShadowType;

public class Button implements UIElement {
	
	private Image image;
	private String text;
	
	private float x, y;
	private int color;
	
	private ArrayList<ActionEvent> events = new ArrayList<>();
	
	public Button(Image image, String text, float x, float y, int color) {
		this.image = image;
		this.image.shadowType = ShadowType.UNAFFECTED;
		this.text = text;
		this.x = x - image.width/2.0f;
		this.y = y - image.height/2.0f;
		this.color = color;
	}
	
	public void update() {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && mouseOver())
			for(ActionEvent event : events)
				event.onClick();
	}
	
	@Override
	public void update(Rectangle bounds) {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && mouseOver() && Input.mouseInBounds(bounds))
			for(ActionEvent event : events)
				event.onClick();
	}
	
	private boolean mouseOver() {
		return Input.getMouseX() <= x + image.width && Input.getMouseX() >= x && Input.getMouseY() >= y && Input.getMouseY() <= y + image.height;
	}
	
	public void render(Renderer renderer) {
		renderer.drawImage(image, (int)x, (int)y);
		renderer.drawString(text, color, (int)(x + (image.width - renderer.stringWidth(text))/2.0f), (int)(y + (image.height - renderer.stringHeight(text))/2.0f));
	}
	
	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		renderer.drawImage(image, (int)x, (int)y, bounds);
		renderer.drawString(text, color, (int)(x + (image.width - renderer.stringWidth(text))/2.0f), (int)(y + (image.height - renderer.stringHeight(text))/2.0f), bounds);
	}
	
	public void addOnClickEvent(ActionEvent event) {
		events.add(event);
	}
	
	public float getHeight() {
		return image.height;
	}
	
	public float getWidht() {
		return image.width;
	}

	@Override
	public int getBottom() {
		return (int)(this.y + image.height);
	}

	@Override
	public int getTop() {
		return (int)this.y;
	}

	@Override
	public void setY(float y) {
		this.y += y - getTop();
	}

	@Override
	public float getY() {
		return y;
	}

	@Override
	public int getLeft() {
		return (int)x;
	}

	@Override
	public int getRight() {
		return (int)(x + image.width);
	}

	@Override
	public void setX(float x) {
		this.x += x - getLeft() - (getRight() - getLeft())/2.0f;
	}

	@Override
	public float getX() {
		return x + (getRight() - getLeft())/2.0f;
	}
}
