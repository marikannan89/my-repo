package com.address;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.address.model.Address;

@SpringBootApplication
@EnableDiscoveryClient
@PropertySource({"classpath:messages.properties"})
public class AddressServiceBootConfiguration {
	
	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(AddressServiceBootConfiguration.class, args);
	}
	
	@Bean
	public Address getEmployee() {
		Address address = new Address();
		address.setStreetName(env.getProperty("address.streetName"));
		address.setHouseNumber(Integer.parseInt(env.getProperty("address.houseNumber")));
		address.setCounty(env.getProperty("address.county"));
		address.setCommunity(env.getProperty("address.community"));
		address.setState(env.getProperty("address.state"));
		address.setCountry(env.getProperty("address.country"));
		return address;
	}
	
	@Bean
	public AlwaysSampler defualtSampler() {
		return new AlwaysSampler();
	}
	
}
