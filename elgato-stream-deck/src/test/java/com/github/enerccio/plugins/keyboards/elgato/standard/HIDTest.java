package com.github.enerccio.plugins.keyboards.elgato.standard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKeyboard;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardState;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateEvent;
import com.github.enerccio.ledkm.api.components.IKeyboard.KeyboardStateListener;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HIDTest extends TestCase {

	public HIDTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(HIDTest.class);
	}

	private IKeyboard kb;
	private BufferedImage[][] backgrounds;

	public void testApp() throws Exception {
		ElgatoStreamDeckKeyboardPlugin plugin = new ElgatoStreamDeckKeyboardPlugin();
		plugin.setLKM(null);

		plugin.registerKeyboardStateListener(new KeyboardStateListener() {

			Float lastTick;

			@Override
			public void onKeyboardStateChanged(KeyboardStateEvent event) {
				System.out.println(event.getKeyboard() + ": " + event.getState());

				kb = event.getKeyboard();
				backgrounds = new BufferedImage[kb.getRows()][kb.getColumns()];

				for (int row = 0; row < kb.getRows(); row++)
					for (int column = 0; column < kb.getColumns(); column++) {
						backgrounds[row][column] = new BufferedImage(kb.getButtonWidth(), kb.getButtonHeight(),
								BufferedImage.TYPE_INT_RGB);
					}

				if (event.getState() == KeyboardState.CONNECTED) {
					IKeyboard kb = event.getKeyboard();
					
					try {
						kb.getKey(0, 0).setCurrentImage(ImageIO.read(HIDTest.class.getResourceAsStream("/minus.png")));
						kb.getKey(0, 1).setCurrentImage(ImageIO.read(HIDTest.class.getResourceAsStream("/plus.png")));
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					kb.registerKeyPressListener(e -> {
						IKey key = e.getKey();
						System.out.println("Key " + key.getRow() + ", " + key.getColumn() + " PRESSED");
					});

					kb.registerKeyHeldListener(e -> {
						lastTick = e.getDuration();
					});

					kb.registerKeyReleaseListener(e -> {
						IKey key = e.getKey();
						if (lastTick != null) {
							System.out.println("Was held for " + lastTick);
							lastTick = null;
						}
						System.out.println("Key " + key.getRow() + ", " + key.getColumn() + " RELEASED");

						if (key.getColumn() == 0 && key.getRow() == 0) {
							event.getKeyboard().setBrightness(event.getKeyboard().getBrightness() - 0.1f);
						}

						if (key.getColumn() == 1 && key.getRow() == 0) {
							event.getKeyboard().setBrightness(event.getKeyboard().getBrightness() + 0.1f);
						}
					});
				}
			}
		});

		plugin.load();
		System.out.println();
		
		Rainbow top = new Rainbow(0.1f);
		Rainbow mid = new Rainbow(0.1f);
		Rainbow bot = new Rainbow(0.1f);
		
		mid.next();
		bot.next();
		bot.next();
		
		LinkedList<Color> topc = new LinkedList<>();
		LinkedList<Color> midc = new LinkedList<>();
		LinkedList<Color> botc = new LinkedList<>();
		
		for (int i=0; i<5; i++) {
			topc.add(top.next());
		}
		
		for (int i=0; i<5; i++) {
			midc.add(mid.next());
		}
		
		for (int i=0; i<5; i++) {
			botc.add(bot.next());
		}
		
		int step = 0;

		while (System.in.available() == 0) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

			}
			
			plugin.keyboardEventTick();
			++step;

			if (kb != null) {
				
				if (step % 10 == 0) {				
					fillBackground(backgrounds[0][4], topc.get(0));
					fillBackground(backgrounds[0][3], topc.get(1));
					fillBackground(backgrounds[0][2], topc.get(2));
					fillBackground(backgrounds[0][1], topc.get(3));
					fillBackground(backgrounds[0][0], topc.get(4));
					
					fillBackground(backgrounds[1][4], midc.get(0));
					fillBackground(backgrounds[1][3], midc.get(1));
					fillBackground(backgrounds[1][2], midc.get(2));
					fillBackground(backgrounds[1][1], midc.get(3));
					fillBackground(backgrounds[1][0], midc.get(4));
					
					fillBackground(backgrounds[2][4], botc.get(0));
					fillBackground(backgrounds[2][3], botc.get(1));
					fillBackground(backgrounds[2][2], botc.get(2));
					fillBackground(backgrounds[2][1], botc.get(3));
					fillBackground(backgrounds[2][0], botc.get(4));
					
					kb.renderLoop(backgrounds);
					
					topc.removeFirst();
					topc.add(top.next());
					
					midc.removeFirst();
					midc.add(mid.next());
					
					botc.removeFirst();
					botc.add(bot.next());
				}
			}
		}
		
		plugin.shutdown();
	}
	
	private void fillBackground(BufferedImage bufferedImage, Color color) {
		Graphics2D g = (Graphics2D) bufferedImage.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		g.dispose();
	}

	private class Rainbow implements Iterator<Color> {

		private final float step;
		private float hue = 0;
		
		public Rainbow(float step) {
			this.step = step;
		}
		
		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Color next() {
			Color c = Color.getHSBColor(hue, 1, 1);
			hue += step;
			return c;
		}
		
	}
}
