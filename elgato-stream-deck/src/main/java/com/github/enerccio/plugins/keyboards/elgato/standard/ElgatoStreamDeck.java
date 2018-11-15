package com.github.enerccio.plugins.keyboards.elgato.standard;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.hid4java.HidDevice;

import com.github.enerccio.ledkm.api.PluginException;
import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKey.KeyHeldEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyHeldListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyPressEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyPressListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyReleaseEvent;
import com.github.enerccio.ledkm.api.components.IKey.KeyReleaseListener;
import com.github.enerccio.ledkm.api.components.IKey.KeyState;
import com.github.enerccio.ledkm.api.components.IKeyboard;

public class ElgatoStreamDeck implements IKeyboard {

	public static final Short MANUFACTURER_ID = 0xfd9;
	public static final Short DEVICE_ID = 0x60;

	private static final int PAGE_PACKET_SIZE = 8191;
	private static final int NUM_FIRST_PAGE_PIXELS = 2583;
	private static final int NUM_SECOND_PAGE_PIXELS = 2601;

	private HidDevice device;
	private ElgatoStreamDeckKeyboardPlugin plugin;
	private ElgatoKey[][] keys;
	private float brightness = 1.0f;

	public ElgatoStreamDeck(ElgatoStreamDeckKeyboardPlugin plugin, HidDevice device) {
		this.plugin = plugin;
		this.device = device;
		this.keys = new ElgatoKey[getRows()][getColumns()];

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				this.keys[i][j] = new ElgatoKey(this, i, j);
				this.keys[i][j].clearKey();
			}
	}

	public void setup() {
		device.open();
		device.setNonBlocking(true);
		setBrightness(1.0f);
	}

	@Override
	public String toString() {
		return "ElgatoDeck [device=" + getIdentification() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ElgatoStreamDeck other = (ElgatoStreamDeck) obj;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		return true;
	}

	@Override
	public String getManufacturer() {
		return "Elgato";
	}

	@Override
	public String getName() {
		return "Stream deck";
	}

	@Override
	public int getRows() {
		return 3;
	}

	@Override
	public int getColumns() {
		return 5;
	}

	@Override
	public int getButtonHeight() {
		return 72;
	}

	@Override
	public int getButtonWidth() {
		return 72;
	}

	@Override
	public float getBrightness() {
		return brightness;
	}

	@Override
	public void setBrightness(float brightness) {
		if (brightness <= 0f)
			brightness = 0f;
		else if (brightness >= 1.0)
			brightness = 1.0f;

		this.brightness = brightness;

		byte percBrightness = (byte) (brightness * 100);

		byte[] buffer = new byte[] { 0x55, (byte) 0xaa, (byte) 0xd1, 0x01, percBrightness, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0 };

		if (device.isOpen())
			device.sendFeatureReport(buffer, (byte) 0x05);
	}

	@Override
	public IKey getKey(int row, int column) {
		return keys[row][column];
	}

	private Set<KeyPressListener> pressSet = Collections.synchronizedSet(new LinkedHashSet<>());
	private Set<KeyReleaseListener> releaseSet = Collections.synchronizedSet(new LinkedHashSet<>());
	private Set<KeyHeldListener> heldSet = Collections.synchronizedSet(new LinkedHashSet<>());

	@Override
	public void registerKeyPressListener(KeyPressListener listener) {
		pressSet.add(listener);
	}

	@Override
	public void unregisterKeyPressListener(KeyPressListener listener) {
		pressSet.remove(listener);
	}

	@Override
	public Collection<KeyPressListener> getKeyPressListeners() {
		return new LinkedHashSet<>(pressSet);
	}

	@Override
	public void registerKeyReleaseListener(KeyReleaseListener listener) {
		releaseSet.add(listener);
	}

	@Override
	public void unregisterKeyReleaseListener(KeyReleaseListener listener) {
		releaseSet.remove(listener);
	}

	@Override
	public Collection<KeyReleaseListener> getKeyReleaseListeners() {
		return new LinkedHashSet<>(releaseSet);
	}

	@Override
	public void registerKeyHeldListener(KeyHeldListener listener) {
		heldSet.add(listener);
	}

	@Override
	public void unregisterKeyHeldListener(KeyHeldListener listener) {
		heldSet.remove(listener);
	}

	@Override
	public Collection<KeyHeldListener> getKeyHeldListeners() {
		return new LinkedHashSet<>(heldSet);
	}

	public ElgatoStreamDeckKeyboardPlugin getPlugin() {
		return plugin;
	}

	@Override
	public String getIdentification() {
		return device.getSerialNumber() == null ? device.getPath() : device.getSerialNumber();
	}

	@Override
	public BufferedImage getIcon() {
		return null;
	}

	public void resolveReadEvents() {
		if (device.isOpen()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[2048];

			while (true) {
				int readCount = device.read(buffer);
				if (readCount == -1)
					return;
				if (readCount == 0)
					break;
				bos.write(buffer, 0, readCount);
			}

			if (bos.size() > 0) {
				resolveRead(bos.toByteArray());

				for (int i = 0; i < getRows(); i++)
					for (int j = 0; j < getColumns(); j++) {
						ElgatoKey key = (ElgatoKey) getKey(i, j);
						if (key.getState() == KeyState.DOWN) {
							key.setState(KeyState.HELD);
						}
					}
			} else {
				for (int i = 0; i < getRows(); i++)
					for (int j = 0; j < getColumns(); j++) {
						ElgatoKey key = (ElgatoKey) getKey(i, j);

						if (key.getState() == KeyState.HELD) {
							float duration = (System.currentTimeMillis() - key.getLastKeyPress()) / 1000.0f;
							KeyHeldEvent event = new KeyHeldEvent() {

								@Override
								public IKey getKey() {
									return key;
								}

								@Override
								public float getDuration() {
									return duration;
								}
							};

							synchronized (heldSet) {
								heldSet.forEach(listener -> listener.onKeyHeld(event));
							}

						}
					}
			}
		}
	}

	private void resolveRead(byte[] byteArray) {

		boolean[][] keyStates = new boolean[getRows()][getColumns()];

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				ElgatoKey key = (ElgatoKey) getKey(i, j);
				keyStates[i][j] = key.isDown();
			}

		for (int i = 1; i < getColumns() * getRows() + 1; i++) {
			byte data = byteArray[i];

			int column = (getColumns() - 1) - ((i - 1) % getColumns());
			int row = (i - 1) / getColumns();

			ElgatoKey key = (ElgatoKey) getKey(row, column);

			if (data == 0) {
				key.setDown(false);
			} else {
				key.setDown(true);
			}
		}

		for (int i = 0; i < getRows(); i++)
			for (int j = 0; j < getColumns(); j++) {
				ElgatoKey key = (ElgatoKey) getKey(i, j);

				boolean state = keyStates[i][j];
				boolean newState = key.isDown();

				if (newState != state) {
					if (newState) {
						if (key.getState() == KeyState.DOWN || key.getState() == KeyState.HELD) {
							key.setState(KeyState.HELD);
							float duration = (System.currentTimeMillis() - key.getLastKeyPress()) / 1000.0f;
							KeyHeldEvent event = new KeyHeldEvent() {

								@Override
								public IKey getKey() {
									return key;
								}

								@Override
								public float getDuration() {
									return duration;
								}
							};

							synchronized (heldSet) {
								heldSet.forEach(listener -> listener.onKeyHeld(event));
							}
						} else {
							key.setState(KeyState.DOWN);
							KeyPressEvent event = new KeyPressEvent() {

								@Override
								public IKey getKey() {
									return key;
								}
							};

							synchronized (pressSet) {
								pressSet.forEach(listener -> listener.onKeyPress(event));
							}
						}
					} else {
						key.setState(KeyState.UP);
						KeyReleaseEvent event = new KeyReleaseEvent() {

							@Override
							public IKey getKey() {
								return key;
							}
						};

						synchronized (releaseSet) {
							releaseSet.forEach(listener -> listener.onKeyRelease(event));
						}
					}
				}
			}

	}

	@Override
	public void renderLoop(BufferedImage[][] backgrounds) throws PluginException {
		byte[] pixel = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(pixel);

		byte[] firstPage = new byte[NUM_FIRST_PAGE_PIXELS * 3];
		byte[] secondPage = new byte[NUM_SECOND_PAGE_PIXELS * 3];

		for (int row = 0; row < getRows(); row++)
			for (int column = 0; column < getColumns(); column++) {
				ElgatoKey key = (ElgatoKey) getKey(row, column);

				BufferedImage currentImage = key.getCurrentImage();
				BufferedImage combined = combineImage(currentImage, backgrounds == null ? null : backgrounds[row][column]);
				int it = 0;

				for (int i = 0; i < getButtonHeight(); i++) {
					for (int j = getButtonWidth() - 1; j >= 0; j--) {
						int rgb = combined.getRGB(i, j);
						bb.asIntBuffer().put(rgb);

						for (int cId = 1; cId < 4; cId++) {
							byte color = pixel[cId];
							int pixelPos = it++;
							if (pixelPos >= NUM_FIRST_PAGE_PIXELS * 3) {
								pixelPos -= NUM_FIRST_PAGE_PIXELS * 3;
								secondPage[pixelPos] = color;
							} else {
								firstPage[pixelPos] = color;
							}
						}

					}
				}

				writePage1((row * getColumns()) + (getColumns() - column), firstPage);
				writePage2((row * getColumns()) + (getColumns() - column), secondPage);
			}

	}

	private void writePage1(int key, byte[] firstPage) {
		byte[] buffer = new byte[PAGE_PACKET_SIZE - 1];
		ByteBuffer bb = ByteBuffer.wrap(buffer);

		byte[] header = new byte[] { 0x01, 0x01, 0x00, 0x00, (byte) key, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00, 0x42, 0x4d, (byte) 0xf6, 0x3c, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x36, 0x00, 0x00,
				0x00, 0x28, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x48, 0x00, 0x00, 0x00, 0x01, 0x00, 0x18, 0x00,
				0x00, 0x00, 0x00, 0x00, (byte) 0xc0, 0x3c, 0x00, 0x00, (byte) 0xc4, 0x0e, 0x00, 0x00, (byte) 0xc4, 0x0e,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

		bb.put(header);
		bb.put(firstPage);

		if (device.isOpen()) {
			device.write(buffer, PAGE_PACKET_SIZE - 1, (byte) 0x02);
		}
	}

	private void writePage2(int key, byte[] secondPage) {
		byte[] buffer = new byte[PAGE_PACKET_SIZE];
		ByteBuffer bb = ByteBuffer.wrap(buffer);

		byte[] header = new byte[] { 0x01, 0x02, 0x00, 0x01, (byte) key, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
				0x00, 0x00, 0x00 };

		bb.put(header);
		bb.put(secondPage);

		if (device.isOpen()) {
			device.write(buffer, PAGE_PACKET_SIZE - 1, (byte) 0x02);
		}
	}

	private BufferedImage combineImage(BufferedImage currentImage, BufferedImage background) {
		if (background == null) {
			background = new BufferedImage(getButtonWidth(), getButtonHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) background.getGraphics();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getButtonWidth(), getButtonHeight());
			g.dispose();
		}

		BufferedImage img = null;
		if (currentImage != null) {
			img = new BufferedImage(getButtonWidth(), getButtonHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			g.drawImage(background, 0, 0, null);
			g.drawImage(currentImage, 0, 0, null);
			g.dispose();
		} else {
			img = background;
		}
		return img;
	}
}
