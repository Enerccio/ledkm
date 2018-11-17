import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.IPlugin;
import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IAction;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateEvent;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateListener;
import com.github.enerccio.ledkm.api.components.IPluginReference;
import com.github.enerccio.ledkm.mappings.KeyController;
import com.github.enerccio.ledkm.mappings.Profile;

public class SimpleTest extends SimpleLoop {

	public static void main(String[] args) throws Exception {
		ILKM lkm = mkLkm();
		Profile p = mkProfile();
		
		boolean first = true;
		for (KeyController kc : p.getPages().get(0).getKeys()) {
			kc.getActionReferences().add(new IPluginReference<IAction>() {
				
				@Override
				public void saveReference(StringBuilder builder) throws PluginException {
					
				}
				
				@Override
				public IPlugin getPlugin() {
					return null;
				}
				
				@Override
				public IAction get() {
					return SimpleTest::print;
				}
			});
			
			if (first) {
				first = false;
				kc.getActionReferences().add(new IPluginReference<IAction>() {
					
					@Override
					public void saveReference(StringBuilder builder) throws PluginException {
						
					}
					
					@Override
					public IPlugin getPlugin() {
						return null;
					}
					
					@Override
					public IAction get() {
						return SimpleTest::exit;
					}
				});
			}
		}
		
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
		
		runEventLoop(lkm);
	}

	protected static void connect(Profile p, IKeyboard keyboard) {
		p.getPages().get(0).setPage(keyboard);
	}

	protected static void disconnect(Profile p, IKeyboard keyboard) {
		p.getPages().get(0).unsetPage(keyboard);
	}

	private static void exit(IKey key, KeyState state, float time) {
		if (state == KeyState.UP) {
			running = false;
		}
	}
	
	private static void print(IKey key, KeyState state, float time) {
		System.out.println(String.format("%s, %s: %s (%s)", key.getColumn(), key.getRow(), state, time));
	}
}
