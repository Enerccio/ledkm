package com.github.enerccio.ledkm.api;

import java.util.Collection;

import org.hid4java.HidServices;

public interface ILKM {
	
	public Collection<IKeyboardPlugin> getKeyboardPlugins();
	
	public void requestRepaint();

	public HidServices getHidService();
	
}
