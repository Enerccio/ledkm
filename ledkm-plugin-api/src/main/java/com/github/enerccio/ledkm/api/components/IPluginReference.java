package com.github.enerccio.ledkm.api.components;

import com.github.enerccio.ledkm.api.IPlugin;
import com.github.enerccio.ledkm.api.PluginException;

public interface IPluginReference<REFERENCED_OBJECT> {
	
	public IPlugin getPlugin();
	
	public void saveReference(StringBuilder builder) throws PluginException;
	
	public REFERENCED_OBJECT get();

}
