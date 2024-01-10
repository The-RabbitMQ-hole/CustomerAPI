package com.edapoc.zlm.service;

import com.edapoc.zlm.model.Customer;
import com.edapoc.zlm.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id){
        return customerRepository.findById(id);
    }

    public Customer save(Customer customer) { return customerRepository.save(customer); }

    public Customer create(String name, String email){
        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customerRepository.save(customer);
        return customer;
    }

    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }


}

