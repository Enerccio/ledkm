package com.github.enerccio.ledkm.api.components;

import java.awt.image.BufferedImage;
import java.util.Collection;

import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IKey.KeyHeldListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyPressListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyReleaseListener;

public interface IKeyboard {

	public enum KeyboardState {
		
		CONNECTED, DISCONNECTED, ERROR
		
	}
	
	public interface KeyboardStateEvent {
		
		public IKeyboard getKeyboard();
		
		public KeyboardState getState();
		
	}
	
	public interface KeyboardStateListener {
		
		void onKeyboardStateChanged(KeyboardStateEvent event);
		
	}
	
	public String getManufacturer();
	
	public String getName();
	
	public String getIdentification();
	
	public BufferedImage getIcon();
	
	public int getRows();
	
	public int getColumns();
	
	public int getButtonHeight();
	
	public int getButtonWidth();
	
	public float getBrightness();
	
	public void setBrightness(float brightness);
	
	public IKey getKey(int row, int column);
	
	public void registerKeyPressListener(KeyPressListener listener);
	
	public void unregisterKeyPressListener(KeyPressListener listener);
	
	public Collection<KeyPressListener> getKeyPressListeners();
	
	public void registerKeyReleaseListener(KeyReleaseListener listener);
	
	public void unregisterKeyReleaseListener(KeyReleaseListener listener);
	
	public Collection<KeyReleaseListener> getKeyReleaseListeners();
	
	public void registerKeyHeldListener(KeyHeldListener listener);
	
	public void unregisterKeyHeldListener(KeyHeldListener listener);
	
	public Collection<KeyHeldListener> getKeyHeldListeners();
	
	public void renderLoop(BufferedImage[][] backgrounds) throws PluginException;
	
}
