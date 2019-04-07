package com.user;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.user.model.Address;

/*@FeignClient(name="address-service")*/
@FeignClient(name="zuul-service")
public interface AddressServiceProxy {
	
	/*@RequestMapping(value = "/address/{id}", method = RequestMethod.GET)*/
	@RequestMapping(value = "address-service/address/{id}", method = RequestMethod.GET)
	public Address getUser(@PathVariable("id") String id);

}
