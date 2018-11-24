package com.github.enerccio.ledkm.mappings;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.enerccio.ledkm.api.profiles.IProfile;
import com.github.enerccio.ledkm.utils.SerializationResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Profile implements IProfile {
	public static final String UUID = "uuid";
	public static final String ROWS_ACCESSOR = "rows";
	public static final String COLUMNS_ACCESSOR = "columns";
	public static final String PAGES_ACCESSOR = "pages";
	public static final String LAST_DEVICES_ACCESSOR = "assignedDevices";
	
	private String uuid;
	private String name;
	private int rows, columns;
	private List<Page> pages = new ArrayList<>();
	private Set<String> assignedDevices = new LinkedHashSet<String>();

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Override
	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public List<Page> getPages() {
		return pages;
	}

	public void setPages(List<Page> pages) {
		this.pages = pages;
	}	

	@Override
	public Set<String> getAssignedDevices() {
		return assignedDevices;
	}

	public void setAssignedDevices(Set<String> assignedDevices) {
		this.assignedDevices = assignedDevices;
	}
	
	public SerializationResult saveProfile() {
		SerializationResult r = new SerializationResult();
		
		JsonObject jo = new JsonObject();
		
		jo.addProperty(UUID, getUuid());
		jo.addProperty(COLUMNS_ACCESSOR, getColumns());
		jo.addProperty(ROWS_ACCESSOR, getRows());
		
		JsonArray ja = new JsonArray();
		
		for (String device : assignedDevices) {
			ja.add(device);
		}
		
		jo.add(LAST_DEVICES_ACCESSOR, ja);
		
		ja = new JsonArray();
		jo.add(PAGES_ACCESSOR, ja);
		
		for (Page p : getPages()) {
			SerializationResult rr = p.savePage();
			ja.add(rr.getResult());
			r.getFailures().addAll(rr.getFailures());
		}
		
		r.setResult(jo);
		return r;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public String getUuid() {
		return uuid;
	}
}
