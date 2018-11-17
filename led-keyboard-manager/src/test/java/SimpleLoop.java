import org.hid4java.HidServices;

import com.github.enerccio.ledkm.LKM;
import com.github.enerccio.ledkm.api.IKeyboardPlugin;
import com.github.enerccio.ledkm.api.ILKM;
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
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		((LKM) lkm).shutdown();
	}
}
