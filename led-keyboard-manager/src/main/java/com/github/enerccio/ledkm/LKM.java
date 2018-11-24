package com.github.enerccio.ledkm;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
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
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.profiles.IProfile;
import com.github.enerccio.ledkm.mappings.Profile;
import com.github.enerccio.ledkm.utils.SerializationResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LKM implements ILKM {
	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
		
		restoreAll();
	}

	public void shutdown() {
		try {
			saveState();
			
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
	public Collection<IKeyboard> getKeyboards() {
		Set<IKeyboard> kbs = new LinkedHashSet<IKeyboard>();
		
		for (IKeyboardPlugin kbp : getKeyboardPlugins()) {
			kbs.addAll(kbp.getKeyboards());
		}
		
		return kbs;
	}

	@Override
	public void requestRepaint() {
		isDirty = true;
	}

	@Override
	public HidServices getHidService() {
		return service;
	}

	public boolean isDirty() {
		return isDirty;
	}
	
	public void clearDirty() {
		isDirty = false;
	}
	
	private List<Profile> profiles = new ArrayList<Profile>();
	private Collection<Profile> profilesView = Collections.unmodifiableList(profiles);
	private int currentProfile = -1;

	@SuppressWarnings("unused")
	private void restoreAll() throws Exception {
		if (!Configuration.getProfileDirectory().toFile().exists()) {
			Configuration.getProfileDirectory().toFile().mkdirs();
		}

		currentProfile = -1;
		TreeSet<File> profiles = new TreeSet<File>();

		for (File f : Configuration.getProfileDirectory().toFile().listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				return pathname.getName().endsWith(".lkmp");
			}
		})) {
			profiles.add(f);
		}

		for (File f : profiles) {
			if (currentProfile == -1)
				currentProfile = 0;
			
			Profile p = new Profile();
			// TODO
		}
	}

	public void saveState() throws Exception {
		FileUtils.deleteDirectory(Configuration.getProfileDirectory().toFile());
		Configuration.getProfileDirectory().toFile().mkdirs();

		int ord = 0;
		for (Profile p : profiles) {
			SerializationResult sr = p.saveProfile();
			if (sr.getFailures().isEmpty()) {
				String profileName = String.format("%s/%04d.lkmp",
						Configuration.getProfileDirectory().toFile().getAbsolutePath(), ++ord);
				FileUtils.write(new File(profileName), gson.toJson(sr.getResult()), Charset.forName("utf-8"));
			} else {
				// TODO
			}
		}
	}

	@Override
	public IProfile getActiveProfile() {
		try {
			if (currentProfile >= 0) {
				return profiles.get(currentProfile);
			}
		} catch (Exception e) {
			
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IProfile> getAllProfiles() {
		return (Collection<IProfile>) (Object) profilesView;
	}

	@Override
	public void removeProfile(IProfile profile) {
		if (profiles.indexOf(profile) == currentProfile)
			currentProfile = -1;
		profiles.remove(profile);
	}

	@Override
	public void setActiveProfile(IProfile profile) {
		currentProfile = profiles.indexOf(profile);
	}

	@Override
	public IProfile createNewProfile() {
		Profile p = new Profile();
		p.setUuid(UUID.randomUUID().toString());
		profiles.add(p);
		return p;
	}

}
