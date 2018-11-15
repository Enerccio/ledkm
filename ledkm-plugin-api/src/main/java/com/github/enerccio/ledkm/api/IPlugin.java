package com.github.enerccio.ledkm.api;

public interface IPlugin {
	
	public String getPluginName();
	
	public String getPluginVersion();
	
	public String getPluginAuthor();
	
	public void setLKM(ILKM lkm);
	
	public void load() throws PluginLoadException;
	
	public void shutdown() throws Exception;

}
