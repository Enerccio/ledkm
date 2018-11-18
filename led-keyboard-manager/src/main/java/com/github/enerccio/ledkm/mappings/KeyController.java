package com.github.enerccio.ledkm.mappings;

import java.util.HashSet;
import java.util.Set;

import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IAction;
import com.github.enerccio.ledkm.api.components.IImageSource;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
import com.github.enerccio.ledkm.api.components.IPluginReference;
import com.github.enerccio.ledkm.utils.SerializationResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class KeyController {
	public static final String IMAGE_SOURCE_ACCESSOR = "imageSource";
	public static final String ACTION_REFERENCES_ACCESSOR = "actionReferences";
	
	private IPluginReference<IImageSource> imageSource;
	
	private Set<IPluginReference<IAction>> actionReferences = new HashSet<IPluginReference<IAction>>();

	public IPluginReference<IImageSource> getImageSource() {
		return imageSource;
	}

	public void setImageSource(IPluginReference<IImageSource> imageSource) {
		this.imageSource = imageSource;
	}

	public Set<IPluginReference<IAction>> getActionReferences() {
		return actionReferences;
	}

	public void setActionReferences(Set<IPluginReference<IAction>> actionReferences) {
		this.actionReferences = actionReferences;
	}

	public SerializationResult saveKeyController() {
		SerializationResult sr = new SerializationResult();
		
		JsonObject jo = new JsonObject();
		sr.setResult(jo);
		
		try {
			jo.add(IMAGE_SOURCE_ACCESSOR, saveReference(imageSource));
		} catch (PluginException e) {
			sr.getFailures().add(e.getMessage());
		}
		
		JsonArray ja = new JsonArray();
		jo.add(ACTION_REFERENCES_ACCESSOR, ja);
		
		for (IPluginReference<IAction> actionRefeence : actionReferences) {
			try {
				ja.add(saveReference(actionRefeence));
			} catch (PluginException e) {
				sr.getFailures().add(e.getMessage());
			}
		}
		
		return sr;
	}

	private JsonObject saveReference(IPluginReference<?> ref) throws PluginException {
		JsonObject reference = new JsonObject();
		
		String pluginName = ref.getPlugin().getClass().getName();
		
		StringBuilder propValue = new StringBuilder();
		ref.saveReference(propValue);
		
		reference.addProperty(pluginName, propValue.toString());
		
		return reference;
	}

	public void handleKeyPress(IKey key) {
		if (imageSource != null) {
			IImageSource s = imageSource.get();
			s.imageEventChange(key, KeyState.DOWN);
		}
		
		for (IPluginReference<IAction> action : actionReferences) {
			IAction a = action.get();
			a.invoke(key, KeyState.DOWN, 0);
		}
	}

	public void handleKeyRelease(IKey key) {
		if (imageSource != null) {
			IImageSource s = imageSource.get();
			s.imageEventChange(key, KeyState.UP);
		}
		
		for (IPluginReference<IAction> action : actionReferences) {
			IAction a = action.get();
			a.invoke(key, KeyState.UP, 0);
		}
	}

	public void handleKeyHeld(IKey key, float time) {
		if (imageSource != null) {
			IImageSource s = imageSource.get();
			s.imageEventChange(key, KeyState.HELD, time);
		}
		
		for (IPluginReference<IAction> action : actionReferences) {
			IAction a = action.get();
			a.invoke(key, KeyState.HELD, time);
		}
	}

	public void onAssign(IKey kbKey) {
		if (imageSource != null) {
			imageSource.get().keyAssigned(kbKey);
		}
	}
	
	public void onUnassign(IKey kbKey) {
		if (imageSource != null) {
			imageSource.get().keyUnassigned(kbKey);
		}
	}
	
}
