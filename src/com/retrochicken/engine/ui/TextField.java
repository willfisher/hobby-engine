package com.retrochicken.engine.ui;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Font;

public class TextField implements UIElement {
	
	private Rectangle box;
	private Font font;
	private String text = "";
	private String name;
	private float x, y;
	private float yDiff;
	
	private boolean typing = false;
	private int currentIndex = 1;
	
	private boolean cursorVisible = false;
	private long cursorStart;
	
	private ArrayList<ActionEvent> events = new ArrayList<>();
	public void addActionEvent(ActionEvent event) {
		events.add(event);
	}
	
	public TextField(float x, float y, String name, Font font, Rectangle box) {
		this.x = x;
		this.y = y;
		if(box.height < font.getHeight(name) + 4)
			box.height = (int)(font.getHeight(name) + 4);
		box.y = (int)(this.y - box.height/2.0f);
		box.x = (int)(x + font.getWidth(name) + 5);
		this.yDiff = box.y - this.y;
		this.box = box;
		this.font = font;
		this.name = name;
		Input.addTextField(this);
		cursorStart = System.currentTimeMillis();
	}

	@Override
	public int getBottom() {
		return (int)Math.max(y + box.height/2.0f, y + font.getHeight(name)/2.0f);
	}

	@Override
	public int getTop() {
		return (int)Math.min(y - box.height/2.0f, y - font.getHeight(name)/2.0f);
	}

	@Override
	public float getY() {
		return getTop();
	}

	@Override
	public void setY(float y) {
		float change = y - getTop();
		this.y += change;
		box.y = (int)(this.y + yDiff);
	}

	@Override
	public void update() {
		if(Input.isButtonPressed(MouseEvent.BUTTON1) && Input.mouseInBounds(box))
			typing = true;
	}

	@Override
	public void update(Rectangle bounds) {
		if(Input.isButtonPressed(MouseEvent.BUTTON1)) {
			if(Input.mouseInBounds(box.intersection(bounds)))
				typing = true;
			else
				typing = false;
		}
		if(System.currentTimeMillis() - cursorStart >= 1000) {
			cursorStart = System.currentTimeMillis();
			cursorVisible = !cursorVisible;
		}
	}

	@Override
	public void render(Renderer renderer) {
		renderer.drawString(name, 0xffff00ff, (int)x, (int)(y - font.getHeight(name)/2.0f));
		renderer.drawEmptyRect(box.x, box.y, box.width, box.height, 0xffff00ff);
		renderer.drawString(text, 0xff4b0082, box.x + 1, (int)(box.y + (box.height - font.getHeight(text))/2.0f), new Rectangle(box.x + 1, box.y + 1, box.width - 2, box.height - 2));
		if(typing && cursorVisible) {
			renderer.drawRect((int)(box.x + 1 + renderer.stringWidth(text.substring(0, currentIndex))), (int)(box.y + (box.height - font.getHeight(text))/2.0f) - 1, 1, (int)(font.getHeight(text) + 2), 0xff000000);
		}
	}

	@Override
	public int getLeft() {
		return (int)x;
	}

	@Override
	public int getRight() {
		return (int)(x + font.getWidth(name) + 5 + box.width);
	}

	@Override
	public void setX(float x) {
		this.x = x - getLeft() - (getRight() - getLeft())/2.0f;
		box.x = (int)(this.x + font.getWidth(name) + 5);
	}

	@Override
	public float getX() {
		return x + getLeft() + (getRight() - getLeft())/2.0f;
	}

	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		renderer.drawString(name, 0xffff00ff, (int)x, (int)(y - font.getHeight(name)/2.0f), bounds);
		renderer.drawEmptyRect(box.x, box.y, box.width, box.height, 0xffff00ff, bounds);
		renderer.drawString(text, 0xff4b0082, box.x + 1, (int)(box.y + (box.height - font.getHeight(text))/2.0f), box.intersection(bounds));
		if(typing && cursorVisible) {
			float offset = text.length() > 0 ? renderer.stringWidth(text.substring(0, currentIndex).toUpperCase()) : 0;
			renderer.drawRect((int)(box.x + 1 + offset), (int)(box.y + (box.height - font.getHeight(text))/2.0f) - 1, 1, (int)(font.getHeight(text) + 2), 0xff000000, bounds);
		}
	}

	public void keyPressed(KeyEvent e) {
		if(!typing)
			return;
		if(Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == ' ') {
			if(text.length() > 0) {
				text = text.substring(0, currentIndex) + e.getKeyChar() + (currentIndex < text.length() ? text.substring(currentIndex) : "");
				currentIndex++;
			}
			else {
				text = "" + e.getKeyChar();
				currentIndex = text.length();
			}
			textChanged();
		} else {
			switch(e.getKeyCode()) {
				case KeyEvent.VK_BACK_SPACE:
					text = text.substring(0, currentIndex - 1) + (currentIndex < text.length() ? text.substring(currentIndex) : "");
					currentIndex--;
					if(currentIndex < 1) currentIndex = 1;
					break;
				case KeyEvent.VK_LEFT:
					currentIndex--;
					if(currentIndex < 0) currentIndex = 0;
					break;
				case KeyEvent.VK_RIGHT:
					currentIndex++;
					if(currentIndex > text.length()) currentIndex = text.length();
					break;
			}
		}
	}
	
	private void textChanged() {
		for(ActionEvent event : events)
			event.textChanged(text);
	}
	
	public void setText(String text) {
		this.text = text;
		currentIndex = text.length();
	}
}
