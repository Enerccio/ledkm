import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import com.github.enerccio.ledkm.api.ILKM;
import com.github.enerccio.ledkm.api.IPlugin;
import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IAction;
import com.github.enerccio.ledkm.api.components.IImageSource;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
import com.github.enerccio.ledkm.api.components.IPluginReference;
import com.github.enerccio.ledkm.mappings.KeyController;
import com.github.enerccio.ledkm.mappings.Profile;

public class ImageTest extends SimpleTest implements IImageSource {

	private static ImageTest test = new ImageTest();

	public static void main(String[] args) throws Exception {
		ILKM lkm = mkLkm();
		Profile p = mkProfile();

		p.getPages().get(0).getKeys().get(0).getActionReferences().add(new IPluginReference<IAction>() {

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

		for (KeyController kc : p.getPages().get(0).getKeys()) {
			kc.setImageSource(new IPluginReference<IImageSource>() {

				@Override
				public IPlugin getPlugin() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void saveReference(StringBuilder builder) throws PluginException {
					// TODO Auto-generated method stub

				}

				@Override
				public IImageSource get() {
					return test;
				}
			});
		}

		bindProfileToNewDevice(p, lkm);

		runEventLoop(lkm);
	}

	@Override
	public void keyAssigned(IKey key) {
		setIdleImage(key);
	}

	@Override
	public void keyUnassigned(IKey key) {
		key.setCurrentImage(null);
	}

	@Override
	public boolean imageEventChange(IKey key, KeyState state, float time) {
		if (state == KeyState.UP)
			setIdleImage(key);
		else if (state == KeyState.DOWN)
			setHeldImage(key);
		return true;
	}

	private Font font = new Font("Helvetica", Font.PLAIN, 20);

	private void setHeldImage(IKey key) {
		BufferedImage img = new BufferedImage(key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight(),
				BufferedImage.TYPE_INT_RGB);

		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(mkKeyColor(key, key.getKeyboard().getColumns(), key.getKeyboard().getRows()));
		g.fillRect(0, 0, key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight());

		String text = String.format("%s:%s", key.getColumn(), key.getRow());
		g.setColor(Color.WHITE);

		drawCenteredString(g, text,
				new Rectangle(0, 0, key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight()), font);
		g.dispose();
		
		key.setCurrentImage(img);
	}

	private void setIdleImage(IKey key) {
		BufferedImage img = new BufferedImage(key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight(),
				BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = (Graphics2D) img.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight());

		String text = String.format("%s:%s", key.getColumn(), key.getRow());
		g.setColor(Color.WHITE);

		drawCenteredString(g, text,
				new Rectangle(0, 0, key.getKeyboard().getButtonWidth(), key.getKeyboard().getButtonHeight()), font);
		g.dispose();
		
		key.setCurrentImage(img);
	}

	private Color mkKeyColor(IKey key, int columns, int rows) {
		int totalColors = columns*rows;
		return new Color(Color.HSBtoRGB((1.0f/totalColors) * (key.getColumn() + (key.getRow()*columns)), 1.0f, 1.0f));
	}

	private void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font) {
		FontMetrics metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(font);
		g.drawString(text, x, y);
	}

}
