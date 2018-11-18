import org.hid4java.HidServices;

import com.github.enerccio.ledkm.LKM;
import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateEvent;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateListener;
import com.github.enerccio.ledkm.mappings.Page;
import com.github.enerccio.ledkm.mappings.Profile;

public class SimpleLoop {
	
	protected static ILKM mkLkm() throws Exception {
		return new LKM();
	}
	
	protected static Profile mkProfile() {
		Profile p = new Profile();
		p.setColumns(5);
		p.setRows(3);
		p.setName("TEST");
		
		Page pp = new Page(p);
		p.getPages().add(pp);
		
		return p;
	}
	
	protected static volatile boolean running = true;

	protected static void runEventLoop(ILKM lkm) {
		
		HidServices service = lkm.getHidService();
		
		while (running) {
			service.scan();
			
			for (IKeyboardPlugin kbp : lkm.getKeyboardPlugins()) {
				kbp.keyboardEventTick();
			}
			
			for (IKeyboardPlugin kbp : lkm.getKeyboardPlugins()) {
				for (IKeyboard kb : kbp.getKeyboards()) {
					kb.renderLoop(null);
				}
			}
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		((LKM) lkm).shutdown();
	}
	
	protected static void bindProfileToNewDevice(Profile p, ILKM lkm) {
		for (IKeyboardPlugin kbp : lkm.getKeyboardPlugins()) {
			for (IKeyboard kb : kbp.getKeyboards()) {
				connect(p, kb);
			}
			
			kbp.registerKeyboardStateListener(new KeyboardStateListener() {

				@Override
				public void onKeyboardStateChanged(KeyboardStateEvent event) {
					if (event.getState() == KeyboardState.CONNECTED) {
						connect(p, event.getKeyboard());
					} else {
						disconnect(p, event.getKeyboard());
					}
				}
				
			});
		}
	}

	
	protected static void connect(Profile p, IKeyboard keyboard) {
		p.getPages().get(0).setPage(keyboard);
	}

	protected static void disconnect(Profile p, IKeyboard keyboard) {
		p.getPages().get(0).unsetPage(keyboard);
	}

	protected static void exit(IKey key, KeyState state, float time) {
		if (state == KeyState.UP) {
			running = false;
		}
	}
}
