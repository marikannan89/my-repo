package com.user.model;

public class User {

	private long id;
	private String name;
	private int ext;
	private Address address;
	
	public User() {
		id = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public User(long id, String name, int ext) {
		super();
		this.id = id;
		this.name = name;
		this.ext = ext;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getExt() {
		return ext;
	}

	public void setExt(int ext) {
		this.ext = ext;
	}
	
	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}
