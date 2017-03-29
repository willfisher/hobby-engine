package game;

import java.awt.Rectangle;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.fx.Font;
import com.retrochicken.engine.fx.Settings;
import com.retrochicken.engine.ui.ActionEvent;
import com.retrochicken.engine.ui.CheckBox;
import com.retrochicken.engine.ui.ScrollPanel;
import com.retrochicken.engine.ui.Slider;
import com.retrochicken.engine.ui.TextField;
import com.retrochicken.engine.ui.UIElement;

public class SettingsPanel implements UIElement {
	
	private Slider volume, frameCap, resolution;
	private CheckBox lighting, dynamicLighting;
	private TextField ambientColor, selectColor;
	
	private ScrollPanel panel;
	
	public SettingsPanel(float x, float y, float width, float height, GameContainer gc) {
		panel = new ScrollPanel(x, y, width, height);
		
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
		
		panel.addElement(volume);
		panel.addElement(frameCap);
		panel.addElement(resolution);
		panel.addElement(lighting);
		panel.addElement(dynamicLighting);
		panel.addElement(ambientColor);
		panel.addElement(selectColor);
	}
	
	public void update() {
		panel.update();
	}
	
	public void render(Renderer renderer) {
		panel.render(renderer);
	}
	
	public int getBottom() {
		return panel.getBottom();
	}

	@Override
	public int getTop() {
		return panel.getTop();
	}

	@Override
	public float getY() {
		return panel.getY();
	}

	@Override
	public void setY(float y) {
		panel.setY(y);
	}

	@Override
	public void update(Rectangle bounds) {
		update();
	}

	@Override
	public int getLeft() {
		return panel.getLeft();
	}

	@Override
	public int getRight() {
		return panel.getRight();
	}

	@Override
	public void setX(float x) {
		panel.setX(x);
	}

	@Override
	public float getX() {
		return panel.getX();
	}

	@Override
	public void render(Renderer renderer, Rectangle bounds) {
		render(renderer);
	}
}
