package com.github.enerccio.ledkm.api.components;

import java.awt.image.BufferedImage;

public interface IKey {
	
	public enum KeyState {
		
		DOWN, HELD, UP
		
	}
	
	public interface KeyPressEvent {
		
		public IKey getKey();
		
	}
	
	public interface KeyPressListener {
		
		public void onKeyPress(KeyPressEvent event);
		
	}
	
	public interface KeyReleaseEvent {
		
		public IKey getKey();
		
	}
	
	public interface KeyReleaseListener {
		
		public void onKeyRelease(KeyPressEvent event);
		
	}
	
	public interface KeyHeldEvent {
		
		public IKey getKey();
		
		public float getDuration();
		
	}
	
	public interface KeyHeldListener {
		
		public void onKeyHeld(KeyHeldEvent event);
		
	}
	
	public IKeyboard getKeyboard();
	
	public int getRow();
	
	public int getColumn();
	
	public KeyState getState();

	public BufferedImage getCurrentImage();
	
	public void setCurrentImage(BufferedImage image);
	
}
