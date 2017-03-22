package game;

import com.retrochicken.engine.AbstractGame;
import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.scenes.SceneManager;

import game.scenes.MainScene;
import game.scenes.MenuScene;
import game.scenes.SettingsScene;
import game.scenes.SplashScreen;

public class Game extends AbstractGame {
	
	private SceneManager manager;
	
	public static void main(String[] args) {
		GameContainer gc = new GameContainer();
		gc.setClearScreen(true);
		gc.setDynamicLights(true);
		gc.setWidth(340);
		gc.setHeight(220);
		gc.setGame(new Game(gc));
		gc.start();
	}
	
	public Game(GameContainer gc) {
		manager = new SceneManager(gc);
		manager.addScene(new SplashScreen(gc, manager));
		manager.addScene(new MenuScene(gc, manager));
		manager.addScene(new SettingsScene(gc, manager));
		manager.addScene(new MainScene(gc, manager));
	}
	
	@Override
	public void update(GameContainer gc, float dt) {
		manager.update(dt);
	}

	@Override
	public void render(GameContainer gc, Renderer r) {
		manager.render(gc, r);
	}
}
