package com.github.enerccio.ledkm.api;

import java.util.Collection;

public interface ILKM {

	public enum PluginState {
		
		LOADED, UNLOADED
		
	}
	
	public interface PluginStateEvent {
		
		IPlugin getPlugin();
		
		PluginState getState();
		
	}
	
	public interface PluginStateListener {
		
		public void onPluginState(PluginStateEvent event);
		
	}
	
	public void registerPluginStateListener(PluginStateListener listener);
	
	public void unregisterPluginStateListener(PluginStateListener listener);
	
	public Collection<PluginStateListener> getPluginStateListeners();
	
	public Collection<IKeyboardPlugin> getKeyboardPlugins();
	
	public void requestRepaint();
	
}
