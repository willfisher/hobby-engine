package com.retrochicken.engine.scenes;

import java.util.ArrayList;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;

public class SceneManager {
	
	private ArrayList<Scene> scenes = new ArrayList<>();
	private int currentScene;
	
	private GameContainer gc;
	
	public SceneManager(GameContainer gc) {
		this.gc = gc;
	}
	
	public SceneManager(ArrayList<Scene> scenes, GameContainer gc) {
		this.scenes = scenes;
		this.gc = gc;
	}
	
	public SceneManager(ArrayList<Scene> scenes, int currentScene, GameContainer gc) {
		this.scenes = scenes;
		this.currentScene = currentScene;
		this.gc = gc;
	}
	
	public void update(float timePassed) {
		if(currentScene < 0 || currentScene >= scenes.size())
			return;
		
		scenes.get(currentScene).update(timePassed);
	}
	
	public void render(GameContainer gc, Renderer renderer) {
		if(currentScene < 0 || currentScene >= scenes.size())
			return;
		
		scenes.get(currentScene).render(gc, renderer);
	}
	
	public void addScene(Scene scene) {
		scenes.add(scene);
	}
	
	public void setScene(int sceneId) {
		if(sceneId < 0 || sceneId >= scenes.size())
			return;
		
		scenes.get(sceneId).reset(gc);
		currentScene = sceneId;
	}
}
