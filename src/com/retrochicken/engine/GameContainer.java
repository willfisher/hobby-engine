package com.retrochicken.engine;

import java.awt.event.KeyEvent;

import com.retrochicken.engine.fx.Settings;

public class GameContainer implements Runnable {
	
	private Thread thread;
	private AbstractGame game;
	private Window window;
	private Renderer renderer;
	private Input input;
	
	private int width = 320, height = 240;
	private float scale = Settings.RES_SCALE;
	private String title = "HobbyEngine v1.0 by Will Fisher";
	private double frameCap = 1.0/60.0;
	private boolean isRunning = false;
	
	private boolean lightingEnabled = false;
	private boolean dynamicLights = false;
	private boolean clearScreen = false;
	//F2 to enter debug mode
	private boolean debug = false;
	
	public GameContainer(AbstractGame game) {
		this.game = game;
	}
	
	public GameContainer() {
		
	}
	
	public void setGame(AbstractGame game) {
		this.game = game;
	}
	
	public void start() {
		if(isRunning)
			return;
		
		//Initialize engine components
		window = new Window(this);
		renderer = new Renderer(this);
		input = new Input(this);
		
		thread = new Thread(this);
		
		thread.run();
	}
	
	public void stop() {
		if(!isRunning)
			return;
		
		isRunning = false;
	}
	
	public void run() {
		isRunning = true;
		
		double firstTime = 0;
		double lastTime = System.nanoTime()/1000000000.0;
		double passedTime = 0;
		double unprocessedTime = 0;
		double frameTime = 0;
		int frames = 0;
		int fps = 0;
		
		while(isRunning) {
			//Lock max frame rate
			boolean render = false;
			
			firstTime = System.nanoTime()/1000000000.0;
			passedTime = firstTime - lastTime;
			lastTime = firstTime;
			
			unprocessedTime += passedTime;
			frameTime += passedTime;
			
			while(unprocessedTime >= frameCap) {
				if(Input.isKeyPressed(KeyEvent.VK_F2)) debug = !debug;
				
				//Update game
				game.update(this, (float)frameCap);
				input.update();
				unprocessedTime -= frameCap;
				render = true;
				
				if(frameTime >= 1) {
					frameTime = 0;
					fps = frames;
					frames = 0;
				}
			}
			
			if(render) {
				//Clear screen
				if(clearScreen) renderer.clear();
				//Render game
				game.render(this, renderer);
				//Lighting
				if(lightingEnabled || dynamicLights) {renderer.drawLightArray(); renderer.flushMaps();}
				//Debug FPS
				if(debug) renderer.drawString("FPS: " + fps, 0xff00ffff, 10, 10);
				//Update window
				window.update();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		cleanUp();
	}
	
	private void cleanUp() {
		window.cleanUp();
	}
	
	public void setFrameCap(int number) {
		frameCap = 1.0 / number;
	}
	
	public int getFrameCap() {
		return (int)(1.0/frameCap);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Window getWindow() {
		return window;
	}

	public boolean isDynamicLights() {
		return dynamicLights;
	}

	public void setDynamicLights(boolean dynamicLights) {
		if(!this.dynamicLights && dynamicLights && renderer != null)
			renderer.flushMaps();
		this.dynamicLights = dynamicLights;
	}

	public boolean isLightingEnabled() {
		return lightingEnabled;
	}

	public void setLightingEnabled(boolean lightingEnabled) {
		if(!this.lightingEnabled && lightingEnabled && renderer != null)
			renderer.flushMaps();
		this.lightingEnabled = lightingEnabled;
	}

	public boolean isClearScreen() {
		return clearScreen;
	}

	public void setClearScreen(boolean clearScreen) {
		this.clearScreen = clearScreen;
	}
	
	public void setResolutionScale(int scale) {
		if(scale < 1)
			return;
		this.scale = scale;
		window.cleanUp();
		this.window = new Window(this);
		this.renderer = new Renderer(this);
		this.input = new Input(this);
	}
}
