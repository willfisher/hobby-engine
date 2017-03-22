package com.retrochicken.engine.scenes;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;

public interface Scene {
	public void update(float timePassed);
	public void render(GameContainer gc, Renderer renderer);
	public void reset(GameContainer gc);
}
