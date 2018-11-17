package com.github.enerccio.ledkm.mappings;

import java.util.ArrayList;
import java.util.List;

import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyHeldEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyHeldListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyPressEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyPressListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyReleaseEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyReleaseListener;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.utils.SerializationResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Page implements KeyPressListener, KeyHeldListener, KeyReleaseListener {
	public static final String PAGE_NAME_ACCESSOR = "pageName";
	public static final String KEYS_ACCESSOR = "keys";
	
	public Page() {
		
	}
	
	public Page(Profile profile) {
		pageName = "Unnamed";
		
		for (int i=0; i<profile.getColumns()*profile.getRows(); i++) {
			keys.add(new KeyController());
		}
	}
	
	public String pageName;
	
	public List<KeyController> keys = new ArrayList<>();

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	public List<KeyController> getKeys() {
		return keys;
	}

	public void setKeys(List<KeyController> keys) {
		this.keys = keys;
	}
	
	public void setPage(IKeyboard keyboard) {
		keyboard.registerKeyPressListener(this);
		keyboard.registerKeyHeldListener(this);
		keyboard.registerKeyReleaseListener(this);
	}
	
	public void unsetPage(IKeyboard keyboard) {
		keyboard.unregisterKeyPressListener(this);
		keyboard.unregisterKeyHeldListener(this);
		keyboard.unregisterKeyReleaseListener(this);
	}

	public SerializationResult savePage() {
		SerializationResult r = new SerializationResult();
		
		JsonObject jo = new JsonObject();
		jo.addProperty(PAGE_NAME_ACCESSOR, getPageName());
		
		JsonArray ja = new JsonArray();
		
		for (KeyController kc : getKeys()) {
			SerializationResult sr = kc.saveKeyController();
			ja.add(sr.getResult());
			r.getFailures().addAll(sr.getFailures());
		}
		
		jo.add(KEYS_ACCESSOR, ja);
		r.setResult(jo);
		
		return r;
	}
	
	@Override
	public void onKeyPress(KeyPressEvent event) {
		KeyController kc = getKeyController(event.getKey());
		if (kc != null) {
			kc.handleKeyPress(event.getKey());
		}
	}

	@Override
	public void onKeyRelease(KeyReleaseEvent event) {
		KeyController kc = getKeyController(event.getKey());
		if (kc != null) {
			kc.handleKeyRelease(event.getKey());
		}
	}

	@Override
	public void onKeyHeld(KeyHeldEvent event) {
		KeyController kc = getKeyController(event.getKey());
		if (kc != null) {
			kc.handleKeyHeld(event.getKey(), event.getDuration());
		}
	}

	private KeyController getKeyController(IKey key) {
		int c = key.getColumn();
		int r = key.getRow();
		IKeyboard kb = key.getKeyboard();
		
		int arrayPos = (r * kb.getColumns()) + c;
		
		if (arrayPos >= keys.size()) {
			return null;
		}
		
		return keys.get(arrayPos);
	}

}
