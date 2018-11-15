package com.github.enerccio.plugins.keyboards.elgato.standard;

import java.awt.image.BufferedImage;

import com.github.enerccio.ledkm.api.components.IKey;
import com.github.enerccio.ledkm.api.components.IKeyboard;

public class ElgatoKey implements IKey {
	
	private ElgatoStreamDeck deck;
	private int row, column;
	private KeyState state;
	private boolean down = false;
	private long lastKeyPress;
	private BufferedImage image;
	
	public ElgatoKey(ElgatoStreamDeck deck, int row, int column) {
		this.deck = deck;
		this.row = row;
		this.column = column;
	}

	@Override
	public IKeyboard getKeyboard() {
		return deck;
	}

	@Override
	public int getRow() {
		return row;
	}

	@Override
	public int getColumn() {
		return column;
	}
	
	public void setState(KeyState state) {
		if (state == KeyState.DOWN)
			lastKeyPress = System.currentTimeMillis();
		this.state = state;
	}

	@Override
	public KeyState getState() {
		return state;
	}

	@Override
	public BufferedImage getCurrentImage() {
		return image;
	}

	@Override
	public void setCurrentImage(BufferedImage image) {
		this.image = image;
	}

	public long getLastKeyPress() {
		return lastKeyPress;
	}

	public boolean isDown() {
		return down;
	}

	public void setDown(boolean down) {
		this.down = down;
	}

}
