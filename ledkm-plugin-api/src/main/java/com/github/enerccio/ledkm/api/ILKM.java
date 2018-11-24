package com.github.enerccio.ledkm.api;

import java.util.Collection;

import org.hid4java.HidServices;

import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.profiles.IProfile;

public interface ILKM {
	
	public Collection<IKeyboardPlugin> getKeyboardPlugins();
	
	public Collection<IKeyboard> getKeyboards();
	
	public void requestRepaint();

	public HidServices getHidService();
	
	public IProfile getActiveProfile();
	
	public Collection<IProfile> getAllProfiles();
	
	public IProfile createNewProfile();
	
	public void removeProfile(IProfile profile);
	
	public void setActiveProfile(IProfile profile);
	
}
