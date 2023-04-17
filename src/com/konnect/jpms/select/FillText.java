package com.konnect.jpms.select;

import java.util.ArrayList;
import java.util.List;

public class FillText {

	String textId;
	String textName;

	public FillText(String textId, String textName) {
		this.textId = textId;
		this.textName = textName;
	}

	public FillText() {
	}

	public String getTextId() {
		return textId;
	}

	public void setTextId(String textId) {
		this.textId = textId;
	}

	public String getTextName() {
		return textName;
	}

	public void setTextName(String textName) {
		this.textName = textName;
	}

}
