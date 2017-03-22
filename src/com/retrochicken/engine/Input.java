package com.retrochicken.engine;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import com.retrochicken.engine.ui.TextField;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	
	private GameContainer gc;
	
	private static boolean[] keys = new boolean[256];
	private static boolean[] keysLast = new boolean[256];
	
	private static boolean[] buttons = new boolean[5];
	private static boolean[] buttonsLast = new boolean[5];
	
	private static int mouseX, mouseY;
	
	private static int lastWheelRot;
	private static int wheelRot;
	private static boolean wheelMoved;
	
	private static ArrayList<TextField> textFields = new ArrayList<>();
	public static void addTextField(TextField field) {
		textFields.add(field);
	}
	
	public Input(GameContainer gc) {
		this.gc = gc;
		gc.getWindow().getCanvas().addKeyListener(this);
		gc.getWindow().getCanvas().addMouseListener(this);
		gc.getWindow().getCanvas().addMouseMotionListener(this);
		gc.getWindow().getCanvas().addMouseWheelListener(this);
	}
	
	public static boolean mouseInBounds(Rectangle bounds) {
		return getMouseX() >= bounds.x && getMouseX() <= bounds.x + bounds.width && getMouseY() >= bounds.y && getMouseY() <= bounds.y + bounds.height;
	}
	
	public void update() {
		keysLast = keys.clone();
		buttonsLast = buttons.clone();
		wheelMoved = wheelRot != 0;
		wheelRot = 0;
	}
	
	public static boolean isKey(int keyCode) {
		return keys[keyCode];
	}
	
	public static boolean isKeyPressed(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}
	
	public static boolean isKeyReleased(int keyCode) {
		return !keys[keyCode] && keysLast[keyCode];
	}
	
	public static boolean isButton(int button) {
		return buttons[button];
	}
	
	public static boolean isButtonPressed(int button) {
		return buttons[button] && !buttonsLast[button];
	}
	
	public static boolean isButtonReleased(int button) {
		return !buttons[button] && buttonsLast[button];
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = (int)(e.getX() / gc.getScale());
		mouseY = (int)(e.getY() / gc.getScale());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = (int)(e.getX() / gc.getScale());
		mouseY = (int)(e.getY() / gc.getScale());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		buttons[e.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		buttons[e.getButton()] = false;
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		for(TextField field : textFields)
			field.keyPressed(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	public static int getMouseX() {
		return mouseX;
	}

	public static void setMouseX(int mouseX) {
		Input.mouseX = mouseX;
	}

	public static int getMouseY() {
		return mouseY;
	}

	public static void setMouseY(int mouseY) {
		Input.mouseY = mouseY;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		lastWheelRot = wheelRot = e.getWheelRotation();
	}
	
	public static boolean isWheelRot() {
		return wheelMoved;
	}
	
	public static int getWheelRots() {
		return lastWheelRot;
	}
}
