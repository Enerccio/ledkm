package com.github.enerccio.ledkm.api;

import java.util.Collection;

import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateListener;

public interface IKeyboardPlugin extends IPlugin {

	public void registerKeyboardStateListener(KeyboardStateListener listener);
	
	public void unregisterKeyboardStateListener(KeyboardStateListener listener);
	
	public Collection<KeyboardStateListener> getKeyboardStateListeners();
	
}
