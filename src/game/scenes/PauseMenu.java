package game.scenes;

import com.retrochicken.engine.GameContainer;
import com.retrochicken.engine.Renderer;
import com.retrochicken.engine.ui.UIMount;

import game.SettingsPanel;

public class PauseMenu {
	
	private UIMount mount;
	private SettingsPanel settings;
	
	public PauseMenu(float x, float y, float width, float height, GameContainer gc) {
		settings = new SettingsPanel(x, y, width, height, gc);
		mount = new UIMount(settings, 0x778899, 15);
	}
	
	public void update() {
		mount.update();
	}
	
	public void render(Renderer renderer) {
		mount.render(renderer);
	}
	
	public void setVisible(boolean isVisible) {
		mount.setVisible(isVisible);
	}
	
	public boolean isVisible() {
		return mount.isVisible();
	}
}
