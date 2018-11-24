package com.github.enerccio.ledkm.api.profiles;

import java.util.Set;

public interface IProfile {

	String getName();
	
	String getUuid();

	int getRows();

	int getColumns();

	Set<String> getAssignedDevices();

}