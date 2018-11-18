package com.github.enerccio.plugins.keyboards.elgato.standard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.hid4java.HidDevice;
import org.hid4java.HidServices;
import org.hid4java.HidServicesListener;
import org.hid4java.event.HidServicesEvent;

import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.PluginLoadException;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateEvent;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateListener;

public class ElgatoStreamDeckKeyboardPlugin implements IKeyboardPlugin {

	@Override
	public String getPluginName() {
		return "Elgato stream deck";
	}

	@Override
	public String getPluginVersion() {
		return "0.0.1";
	}

	@Override
	public String getPluginAuthor() {
		return "admin@en-circle.com";
	}

	private ILKM lkm;

	private Set<KeyboardStateListener> stateListeners = new LinkedHashSet<>();
	private Map<HidDevice, ElgatoStreamDeck> connectedDecks = Collections.synchronizedMap(new LinkedHashMap<>());
	private HidServices service;

	@Override
	public void setLKM(ILKM lkm) {
		this.lkm = lkm;
	}

	@Override
	public void registerKeyboardStateListener(KeyboardStateListener listener) {
		stateListeners.add(listener);
	}

	@Override
	public void unregisterKeyboardStateListener(KeyboardStateListener listener) {
		stateListeners.remove(listener);
	}

	@Override
	public Collection<KeyboardStateListener> getKeyboardStateListeners() {
		return new LinkedHashSet<>(stateListeners);
	}

	@Override
	public void load() throws PluginLoadException {
		service = lkm.getHidService();

		service.addHidServicesListener(new HidServicesListener() {

			@Override
			public void hidFailure(HidServicesEvent event) {
				if (isElgatoDevice(event.getHidDevice())) {
					ElgatoStreamDeck deck = connectedDecks.get(event.getHidDevice());
					if (deck != null) {
						KeyboardStateEvent e = new KeyboardStateEvent() {
							
							@Override
							public KeyboardState getState() {
								return KeyboardState.ERROR;
							}
							
							@Override
							public IKeyboard getKeyboard() {
								return deck;
							}
						};
						
						for (KeyboardStateListener listener : stateListeners) {
							listener.onKeyboardStateChanged(e);
						}
					}
				}
			}

			@Override
			public void hidDeviceDetached(HidServicesEvent event) {
				if (isElgatoDevice(event.getHidDevice())) {
					ElgatoStreamDeck deck = connectedDecks.get(event.getHidDevice());
					if (deck != null) {
						KeyboardStateEvent e = new KeyboardStateEvent() {
							
							@Override
							public KeyboardState getState() {
								return KeyboardState.DISCONNECTED;
							}
							
							@Override
							public IKeyboard getKeyboard() {
								return deck;
							}
						};
						
						for (KeyboardStateListener listener : stateListeners) {
							listener.onKeyboardStateChanged(e);
						}
						
						connectedDecks.remove(event.getHidDevice());
					}
				}
			}

			@Override
			public void hidDeviceAttached(HidServicesEvent event) {
				if (isElgatoDevice(event.getHidDevice())) {
					ElgatoStreamDeck deck = loadDevice(event.getHidDevice());
					deck.setup();
					
					KeyboardStateEvent e = new KeyboardStateEvent() {
						
						@Override
						public KeyboardState getState() {
							return KeyboardState.CONNECTED;
						}
						
						@Override
						public IKeyboard getKeyboard() {
							return deck;
						}
					};
					
					for (KeyboardStateListener listener : stateListeners) {
						listener.onKeyboardStateChanged(e);
					}
				}
			}
		});
		
		for (HidDevice hidDevice : service.getAttachedHidDevices()) {
			if (isElgatoDevice(hidDevice)) {
				loadDevice(hidDevice);
			}
		}
		
		for (HidDevice device : connectedDecks.keySet()) {
			KeyboardStateEvent e = new KeyboardStateEvent() {
				
				@Override
				public KeyboardState getState() {
					return KeyboardState.CONNECTED;
				}
				
				@Override
				public IKeyboard getKeyboard() {
					return connectedDecks.get(device);
				}
			};
			
			for (KeyboardStateListener listener : stateListeners) {
				listener.onKeyboardStateChanged(e);
			}
		}
	}

	private ElgatoStreamDeck loadDevice(HidDevice hidDevice) {
		ElgatoStreamDeck deck = new ElgatoStreamDeck(this, hidDevice);
		connectedDecks.put(hidDevice, deck);
		return deck;
	}

	protected boolean isElgatoDevice(HidDevice hidDevice) {
		if (ElgatoStreamDeck.MANUFACTURER_ID.equals(hidDevice.getVendorId()) && 
				ElgatoStreamDeck.DEVICE_ID.equals(hidDevice.getProductId())) {
			return true;
		}
		return false;
	}

	public ILKM getLKM() {
		return lkm;
	}
	
	private volatile boolean running = false;
	
	@Override
	public void shutdown() throws Exception {
		synchronized (connectedDecks) {
			for (HidDevice hd : connectedDecks.keySet()) {
				hd.close();
			}
			connectedDecks.clear();
		}
	}

	@Override
	public void keyboardEventTick() {
		if (!running) {
			running = true;
			
			synchronized (connectedDecks) {
				connectedDecks.keySet().forEach(hidDevice -> {
					if (!hidDevice.isOpen())
						connectedDecks.get(hidDevice).setup();
				});
			}
		}
		
		Set<ElgatoStreamDeck> decks = new HashSet<>();
		synchronized (connectedDecks) {
			decks.addAll(connectedDecks.values());
		}
		
		for (ElgatoStreamDeck deck : decks) {
			deck.resolveReadEvents();
		}
	}

	@Override
	public Collection<IKeyboard> getKeyboards() {
		return new ArrayList<IKeyboard>(connectedDecks.values());
	}

}
