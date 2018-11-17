package com.github.enerccio.ledkm.api.components;

import com.github.enerccio.ledkm.api.components.IKey.KeyState;

public interface IImageSource {

	public void keyAssigned(IKey key);
	
	public void keyUnassigned(IKey key);
	
	public default boolean imageEventChange(IKey key, KeyState state) {
		return imageEventChange(key, state, 0);
	}
	
	public boolean imageEventChange(IKey key, KeyState state, float time);

}