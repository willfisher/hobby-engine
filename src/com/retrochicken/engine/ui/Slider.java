package com.retrochicken.engine.ui;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Font;

public class Slider implements UIElement {
	
	private float x, y;
	
	private float sliderX, sliderY;
	private float sliderWidth = 5, sliderHeight = 11;
	
	private int color = 0xffff00ff;
	private int sliderColor = 0xff0000ff;
	
	private boolean sliding = false;
	
	private float slideWidth = 100, slideHeight = 3;
	
	private float minValue, maxValue, value;
	
	private String origText;
	private String text;
	
	private ArrayList<ActionEvent> events = new ArrayList<>();
	
	private float tickSize;
	
	public Slider(float x, float y, float minValue, float maxValue, float tickSize, float initialValue, String text) {
		this(x, y, minValue, maxValue, tickSize, text);
		setValue(initialValue);
	}
	
	public Slider(float x, float y, float minValue, float maxValue, float tickSize, String text) {
		this.x = x - slideWidth/2.0f;
		this.y = y - slideHeight/2.0f;
		this.minValue = minValue;
		this.maxValue = maxValue;
		sliderY = this.y - (sliderHeight - slideHeight)/2.0f;
		sliderX = this.x + slideWidth - sliderWidth/2.0f;
		value = maxValue;
		this.origText = text;
		this.text = text + ": " + value;
		this.tickSize = tickSize;
	}
	
	public void update() {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && onSlider())
			sliding = true;
		
		if(sliding) {
			setValue(minValue + (maxValue - minValue) * (Input.getMouseX() - x)/slideWidth);
			if(Input.isButtonReleased(MouseEvent.BUTTON1)) {
				sliding = false;
				onValueChange();
			}
		}
		
		text = origText + ": " + value;
	}
	
	@Override
	public void update(Rectangle bounds) {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && onSlider() && Input.mouseInBounds(bounds))
			sliding = true;
		
		if(sliding) {
			setValue(minValue + (maxValue - minValue) * (Input.getMouseX() - x)/slideWidth);
			if(Input.isButtonReleased(MouseEvent.BUTTON1)) {
				sliding = false;
				onValueChange();
			}
		}
		
		text = origText + ": " + value;
	}
	
	public void addActionEvent(ActionEvent event) {
		events.add(event);
	}
	
	public void onValueChange() {
		for(ActionEvent event : events)
			event.valueChanged(value);
	}
	
	public void setValue(float value) {
		setValueNoEvent(value);
		onValueChange();
	}
	
	public void setValueNoEvent(float value) {
		if(Math.abs(value - this.value) < tickSize)
			return;
		
		if(value > maxValue)
			value = maxValue;
		else if(value < minValue)
			value = minValue;
		
		this.value = this.value + (int)((value - this.value)/tickSize) * tickSize;
		
		this.sliderX = this.x + slideWidth * (this.value - minValue)/(maxValue - minValue) - sliderWidth/2.0f;
	}
	
	public void render(Renderer renderer) {
		renderer.drawRect((int)x, (int)y, (int)slideWidth, (int)slideHeight, color);
		renderer.drawRect((int)sliderX, (int)sliderY, (int)sliderWidth, (int)sliderHeight, sliderColor);
		renderer.drawString(text, 0xffff00ff, (int)(x + (slideWidth - renderer.stringWidth(text))/2.0f), (int)(sliderY - 2 * renderer.stringHeight(text)));
	}
	
	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		renderer.drawRect((int)x, (int)y, (int)slideWidth, (int)slideHeight, color, bounds);
		renderer.drawRect((int)sliderX, (int)sliderY, (int)sliderWidth, (int)sliderHeight, sliderColor, bounds);
		renderer.drawString(text, 0xffff00ff, (int)(x + (slideWidth - renderer.stringWidth(text))/2.0f), (int)(sliderY - 2 * renderer.stringHeight(text)), bounds);
	}
	
	private boolean onSlider() {
		return Input.getMouseX() >= sliderX - 2 && Input.getMouseX() <= sliderX + sliderWidth + 2 && Input.getMouseY() >= sliderY && Input.getMouseY() <= sliderY + sliderHeight;
	}
	
	public boolean isSliding() {
		return sliding;
	}

	@Override
	public int getBottom() {
		return (int)(sliderY + sliderHeight);
	}

	@Override
	public int getTop() {
		return (int)(sliderY - 2 * (Font.STANDARD.image.height - 1));
	}

	@Override
	public void setY(float y) {
		this.y += y - getTop();
		sliderY = this.y - (sliderHeight - slideHeight)/2.0f;
	}

	@Override
	public float getY() {
		return getTop();
	}

	@Override
	public int getLeft() {
		return (int)x;
	}

	@Override
	public int getRight() {
		return (int)(x + slideWidth);
	}

	@Override
	public void setX(float x) {
		this.x += x - getLeft() - (getRight() - getLeft())/2.0f;
		this.sliderX = this.x + slideWidth * (this.value - minValue)/(maxValue - minValue) - sliderWidth/2.0f;
	}

	@Override
	public float getX() {
		return x + (getRight() - getLeft())/2.0f;
	}
}
