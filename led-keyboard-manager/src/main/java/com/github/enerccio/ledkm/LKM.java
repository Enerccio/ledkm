package com.github.enerccio.ledkm;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

import org.hid4java.HidManager;
import org.hid4java.HidServices;
import org.hid4java.HidServicesSpecification;
import org.hid4java.ScanMode;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.IPlugin;

public class LKM implements ILKM {

	private HidServices service;
	private Framework f = null;
	private FrameworkFactory ff = null;
	private BundleContext ctx = null;

	private Set<IPlugin> plugins;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public LKM() throws Exception {
		Map<String, String> config = new HashMap<String, String>();
		ff = ServiceLoader.load(FrameworkFactory.class).iterator().next();
		config.put(Constants.FRAMEWORK_STORAGE,
				Files.createTempDirectory("lkm-framework-plugins").toAbsolutePath().toString());
		config.put(Constants.FRAMEWORK_BUNDLE_PARENT, Constants.FRAMEWORK_BUNDLE_PARENT_FRAMEWORK);
		config.put(Constants.FRAMEWORK_BOOTDELEGATION, "*");

		f = ff.newFramework(config);
		f.start();
		ctx = f.getBundleContext();

		for (Path p : (Iterable<Path>) Files.walk(Configuration.getPluginDirectory(), 2)
				.filter(p -> !Files.isDirectory(p)).filter(p -> p.toString().endsWith(".jar"))::iterator) {
			String bundleJarFile = "file:" + p.toAbsolutePath().toString();
			Bundle b = ctx.installBundle(bundleJarFile);
			b.start();
		}

		plugins = new HashSet<IPlugin>();

		for (ServiceReference pluginService : ctx.getServiceReferences((String) null, null)) {
			Object plugin = pluginService.getBundle().getBundleContext().getService(pluginService);
			boolean serviced = false;
			if (plugin instanceof IKeyboardPlugin) {
				kbPlugins.add((IKeyboardPlugin) plugin);
				serviced = true;
			}

			if (!serviced) {
				pluginService.getBundle().getBundleContext().ungetService(pluginService);
			} else {
				plugins.add((IPlugin) plugin);
			}
		}

		HidServicesSpecification hidServicesSpecification = new HidServicesSpecification();
		hidServicesSpecification.setAutoShutdown(true);
		hidServicesSpecification.setScanMode(ScanMode.NO_SCAN);
		service = HidManager.getHidServices(hidServicesSpecification);

		for (IPlugin plugin : plugins) {
			plugin.setLKM(this);
		}

		for (IPlugin plugin : plugins) {
			plugin.load();
		}

		service.start();
	}

	public void shutdown() {
		try {
			for (IPlugin plugin : plugins) {
				plugin.shutdown();
			}

			f.stop();
			f.waitForStop(10000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		service.shutdown();
	}

	private Set<IKeyboardPlugin> kbPlugins = new HashSet<IKeyboardPlugin>();
	private Set<IKeyboardPlugin> kbPluginsRef = Collections.unmodifiableSet(kbPlugins);
	private boolean isDirty = false;

	@Override
	public Collection<IKeyboardPlugin> getKeyboardPlugins() {
		return kbPluginsRef;
	}

	@Override
	public void requestRepaint() {
		isDirty = true;
	}

	@Override
	public HidServices getHidService() {
		return service;
	}

	public void saveState() {
		
	}

	public boolean isDirty() {
		return isDirty;
	}
	
	public void clearDirty() {
		isDirty = false;
	}

	public void redrawDevice() {
		
	}

}
