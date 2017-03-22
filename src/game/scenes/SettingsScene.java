package game.scenes;

import java.awt.Rectangle;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Font;
import com.retrochicken.engine.fx.Image;
import com.retrochicken.engine.fx.Settings;
import com.retrochicken.engine.fx.SoundClip;
import com.retrochicken.engine.scenes.Scene;
import com.retrochicken.engine.scenes.SceneManager;
import com.retrochicken.engine.ui.ActionEvent;
import com.retrochicken.engine.ui.Button;
import com.retrochicken.engine.ui.CheckBox;
import com.retrochicken.engine.ui.ScrollPanel;
import com.retrochicken.engine.ui.Slider;
import com.retrochicken.engine.ui.TextField;

public class SettingsScene implements Scene {
	
	private Slider volume, frameCap, resolution;
	private CheckBox lighting, dynamicLighting;
	private TextField ambientColor, selectColor;
	
	private Button back;
	
	private SoundClip menuMusic;
	
	private ScrollPanel panel;
	
	public SettingsScene(GameContainer gc, SceneManager manager) {
		panel = new ScrollPanel(gc.getWidth()/2.0f, 20, 0, 150);
		menuMusic = new SoundClip("/sounds/menumusic.wav");
		menuMusic.setVolumeStandard(75);
		
		volume = new Slider(gc.getWidth()/2.0f, 0, 0, 100, 1, "VOLUME");
		volume.addActionEvent(new ActionEvent() {
			@Override
			public void valueChanged(float value) {
				Settings.setVolume((int)value);
			}
		});
		volume.setValue(Settings.VOLUME);
		
		frameCap = new Slider(gc.getWidth()/2.0f, 0, 30, 100, 1, 60, "FPS CAP");
		frameCap.addActionEvent(new ActionEvent() {
			@Override
			public void valueChanged(float value) {
				gc.setFrameCap((int)value);
			}
		});
		frameCap.setValue(gc.getFrameCap());
		
		resolution = new Slider(gc.getWidth()/2.0f, 0, 1, 5, 1, 1, "RESOLUTION SCALE") {
			@Override
			public void setValue(float value) {
				setValueNoEvent(value);
			}
		};
		resolution.addActionEvent(new ActionEvent() {
			@Override
			public void valueChanged(float value) {
				Settings.RES_SCALE = (int)value;
				gc.setResolutionScale((int)value);
			}
		});
		resolution.setValueNoEvent(Settings.RES_SCALE);
		
		lighting = new CheckBox(0, 0, "LIGHTING");
		lighting.addActionEvent(new ActionEvent() {
			@Override
			public void onCheck(boolean value) {
				gc.setLightingEnabled(value);
			}
		});
		lighting.setValue(gc.isLightingEnabled());
		
		dynamicLighting = new CheckBox(0, 0, "DYNAMIC LIGHTING");
		dynamicLighting.addActionEvent(new ActionEvent() {
			@Override
			public void onCheck(boolean value) {
				gc.setDynamicLights(value);
			}
		});
		dynamicLighting.setValue(gc.isDynamicLights());
		
		ambientColor = new TextField(0, 0, "AMBIENT COLOR", Font.STANDARD, new Rectangle(0, 0, 50, 10));
		ambientColor.addActionEvent(new ActionEvent() {
			@Override
			public void textChanged(String text) {
				if(text.length() == 8) {
					try {
						Settings.AMBIENT_COLOR = Long.decode("0x" + text).intValue();
					} catch(Exception e) {}
				}
			}
		});
		ambientColor.setText(Integer.toHexString(Settings.AMBIENT_COLOR));
		
		selectColor = new TextField(0, 0, "SELECT COLOR", Font.STANDARD, new Rectangle(0, 0, 50, 10));
		selectColor.addActionEvent(new ActionEvent() {
			@Override
			public void textChanged(String text) {
				if(text.length() == 8) {
					try {
						Settings.SELECT_COLOR = Long.decode("0x" + text).intValue();
					} catch(Exception e) {}
				}
			}
		});
		selectColor.setText(Integer.toHexString(Settings.SELECT_COLOR));
		
		back = new Button(new Image("/ui/testbutton.png"), "BACK", gc.getWidth()/2.0f, 0, 0xffff00ff);
		back.addOnClickEvent(new ActionEvent() {
			@Override
			public void onClick() {
				menuMusic.stop();
				manager.setScene(1);
			}
		});
		back.setY(panel.getBottom() + 15);
		
		panel.addElement(volume);
		panel.addElement(frameCap);
		panel.addElement(resolution);
		panel.addElement(lighting);
		panel.addElement(dynamicLighting);
		panel.addElement(ambientColor);
		panel.addElement(selectColor);
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
