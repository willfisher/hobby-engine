package game.scenes;

import java.awt.event.KeyEvent;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Input;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.FadeText;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.scenes.Scene;
import com.retrochicken.engine.scenes.SceneManager;

public class SplashScreen implements Scene {
	
	private static final Image logo = new Image("/rclogo.png");
	private SceneManager manager;
	private static final String text = "PRESS SPACE";
	private FadeText fadeText;
			
	public SplashScreen(GameContainer gc, SceneManager manager) {
		fadeText = new FadeText(gc.getWidth()/2.0f, gc.getHeight()/1.2f, text, 0xff000000, 0xffffffff, 1);
		this.manager = manager;
	}
	
	@Override
	public void update(float timePassed) {
		if(Input.isKeyPressed(KeyEvent.VK_SPACE))
			manager.setScene(1);
		fadeText.update();
	}

	@Override
	public void render(GameContainer gc, Renderer renderer) {
		renderer.drawRect(0, 0, gc.getWidth(), gc.getHeight(), 0xffffffff);
		renderer.drawImage(logo, (int)((gc.getWidth() - logo.width)/2.0f), 20);
		fadeText.render(renderer);
	}

	@Override
	public void reset(GameContainer gc) {
		
	}
}
