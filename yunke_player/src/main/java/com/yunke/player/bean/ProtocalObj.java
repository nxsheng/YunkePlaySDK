package com.yunke.player.bean;

import java.io.Serializable;

import com.google.gson.Gson;

public class ProtocalObj implements Serializable{
	public String toJson() {
		Gson gson=new Gson();
		return gson.toJson(this);
	}
	public Object fromJson(String json) {
		Gson gson=new Gson();
		return gson.fromJson(json, this.getClass());
	}
}
