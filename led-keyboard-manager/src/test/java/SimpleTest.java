import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.IPlugin;
import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IAction;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
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
		
		bindProfileToNewDevice(p, lkm);
		
		runEventLoop(lkm);
	}
	
	private static void print(IKey key, KeyState state, float time) {
		System.out.println(String.format("%s, %s: %s (%s)", key.getColumn(), key.getRow(), state, time));
	}
}
