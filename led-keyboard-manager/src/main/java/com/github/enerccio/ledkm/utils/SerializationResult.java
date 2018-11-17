package com.github.enerccio.ledkm.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;

public class SerializationResult {
	
	private JsonElement result;
	private List<String> failures = new ArrayList<String>();
	
	public JsonElement getResult() {
		return result;
	}
	public void setResult(JsonElement result) {
		this.result = result;
	}
	public List<String> getFailures() {
		return failures;
	}
	public void setFailures(List<String> failures) {
		this.failures = failures;
	}

}
