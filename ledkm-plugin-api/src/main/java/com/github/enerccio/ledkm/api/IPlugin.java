package com.github.enerccio.ledkm.api;

import com.github.enerccio.ledkm.api.components.IPluginReference;

public interface IPlugin {
	
	public String getPluginName();
	
	public String getPluginVersion();
	
	public String getPluginAuthor();
	
	public void setLKM(ILKM lkm);
	
	public void load() throws PluginLoadException;
	
	public void shutdown() throws Exception;
	
	public default <R> IPluginReference<R> restoreReference(String value) throws PluginException {
		return null;
	}

}
