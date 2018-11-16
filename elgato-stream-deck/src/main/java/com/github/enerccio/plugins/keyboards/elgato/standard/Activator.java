package com.github.enerccio.plugins.keyboards.elgato.standard;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.github.enerccio.ledkm.api.IKeyboardPlugin;

public class Activator implements BundleActivator {

	private ElgatoStreamDeckKeyboardPlugin plugin = new ElgatoStreamDeckKeyboardPlugin();
	
	@Override
	public void start(BundleContext context) throws Exception {
		context.registerService(IKeyboardPlugin.class.getName(), plugin, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin.shutdown();
	}

}
