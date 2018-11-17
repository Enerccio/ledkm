package com.github.enerccio.ledkm.api.components;

import com.github.enerccio.ledkm.api.components.IKey.KeyState;

public interface IAction {
	
	public void invoke(IKey key, KeyState state, float time);

}
