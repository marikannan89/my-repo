package com.address.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.address.model.Address;

@RestController
public class AddressController {
	
	@Autowired
	Environment environment;
	
	@Autowired
	Address address;
	
	public static final Logger logger = LoggerFactory.getLogger(AddressController.class);
	
	@RequestMapping(value="/address/{name}",method=RequestMethod.GET)
	public Address getAddress(@PathVariable("name") String name) {
		logger.info("getAddress() - Fetching adress with name {}", name);
		address.setName(name);
		address.setServerPort(environment.getProperty("server.port"));
		return address;
	}
	
}
