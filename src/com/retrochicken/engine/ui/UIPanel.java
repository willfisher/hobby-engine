package com.retrochicken.engine.ui;

import java.util.ArrayList;

import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;

public class UIPanel {
	
	private ArrayList<UIElement> elements = new ArrayList<>();
	private float x, y;
	private Image bg;
	
	private boolean isVisible = false;
	
	public UIPanel(float x, float y, Image image) {
		bg = image;
		this.x = x - image.width/2.0f;
		this.y = y;
	}
	
	public void update() {
		for(UIElement element : elements)
			element.update();
	}
	
	public void render(Renderer renderer) {
		if(isVisible) {
			renderer.drawImage(bg, (int)x, (int)y);
			for(UIElement element : elements)
				element.render(renderer);
		}
	}
}
