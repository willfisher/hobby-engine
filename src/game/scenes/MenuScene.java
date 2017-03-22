package game.scenes;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.ShadowType;
import com.retrochicken.engine.fx.SoundClip;
import com.retrochicken.engine.scenes.Scene;
import com.retrochicken.engine.scenes.SceneManager;
import com.retrochicken.engine.ui.ActionEvent;
import com.retrochicken.engine.ui.Button;

public class MenuScene implements Scene {
	
	private Button play;
	private Button settings;
	
	private Image title = new Image("/misc/warseffectontheindividual.png", ShadowType.UNAFFECTED);
	
	private SoundClip menuMusic;
	
	public MenuScene(GameContainer gc, SceneManager manager) {
		menuMusic = new SoundClip("/sounds/menumusic.wav");
		menuMusic.setVolumeStandard(75);

		play = new Button(new Image("/ui/testbutton.png"), "PLAY", gc.getWidth()/2.0f, 2 * gc.getHeight()/3.0f, 0xffff00ff);
		play.addOnClickEvent(new ActionEvent() {
			@Override
			public void onClick() {
				menuMusic.stop();
				manager.setScene(3);
			}
		});
		
		settings = new Button(new Image("/ui/testbutton.png"), "SETTINGS", gc.getWidth()/2.0f, 2 * gc.getHeight()/3.0f + 2 * play.getHeight(), 0xffff00ff);
		settings.addOnClickEvent(new ActionEvent() {
			@Override
			public void onClick() {
				menuMusic.stop();
				manager.setScene(2);
			}
		});
	}
	
	@Override
	public void update(float timePassed) {
		play.update();
		settings.update();
	}

	@Override
	public void render(GameContainer gc, Renderer renderer) {
		renderer.clear();
		play.render(renderer);
		settings.render(renderer);
		renderer.drawImage(title, (int)((gc.getWidth() - title.width)/2.0f), (int)(gc.getHeight()/4.0f - title.height/2.0f));
	}

	@Override
	public void reset(GameContainer gc) {
		menuMusic.loop();
	}
}
