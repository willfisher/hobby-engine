package game.scenes;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.SoundClip;
import com.retrochicken.engine.scenes.Scene;
import com.retrochicken.engine.scenes.SceneManager;
import com.retrochicken.engine.ui.ActionEvent;
import com.retrochicken.engine.ui.Button;

import game.SettingsPanel;

public class SettingsScene implements Scene {
	
	private SettingsPanel panel;
	
	private Button back;
	
	private SoundClip menuMusic;
	
	public SettingsScene(GameContainer gc, SceneManager manager) {
		menuMusic = new SoundClip("/sounds/menumusic.wav");
		menuMusic.setVolumeStandard(75);
		
		panel = new SettingsPanel(gc.getWidth()/2.0f, 20, 0, 150, gc);
		
		back = new Button(new Image("/ui/testbutton.png"), "BACK", gc.getWidth()/2.0f, 0, 0xffff00ff);
		back.addOnClickEvent(new ActionEvent() {
			@Override
			public void onClick() {
				menuMusic.stop();
				manager.setScene(1);
			}
		});
		back.setY(panel.getBottom() + 15);
	}
	
	@Override
	public void update(float timePassed) {
		panel.update();
		back.update();
	}

	@Override
	public void render(GameContainer gc, Renderer renderer) {
		panel.render(renderer);
		back.render(renderer);
	}

	@Override
	public void reset(GameContainer gc) {
		menuMusic.loop();
	}
}
