package com.edapoc.zlm.controller;

import com.edapoc.zlm.ResponseHandler;
import com.edapoc.zlm.model.Customer;
import com.edapoc.zlm.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Customer", description = "Customer management APIs")
@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class CustomerController {
  @Autowired
  CustomerService customerService;
  @Operation(summary = "Retrieve all customers", tags = { "customers", "get", "filter" })
  @ApiResponses({
      @ApiResponse(responseCode = "200", content = {
          @Content(schema = @Schema(implementation = Customer.class), mediaType = "application/json") }),
      @ApiResponse(responseCode = "204", description = "There are no customers", content = {
          @Content(schema = @Schema()) }),
      @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) }) })
  @GetMapping("/v1/customer")
  public ResponseEntity<Object> getAllUsers(@RequestParam(required = false) Long userId){

    try {
      List<Customer> found = customerService.getAllCustomers();
      if (userId != null) {
        found = found.stream().filter(customers -> customers.getId() == (userId)).toList();
      }
      return found.isEmpty()
              ? ResponseHandler.generateResponse("No customers found", HttpStatus.NO_CONTENT, null)
              : ResponseHandler.generateResponse(null, HttpStatus.OK, found);
    } catch (Exception e) {
      return ResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
  }

  @Operation(summary = "Retrieve customer by id", tags = { "customers", "get", "filter" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Customer successfully retrieved by id", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)) }),
          @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content) })
  @GetMapping("/v1/customer/{id}")
  public ResponseEntity<Object> getCustomerById(@PathVariable Long id) {
    try {
      Optional<Customer> found = customerService.getCustomerById(id);

      return found.map(customer -> ResponseHandler.generateResponse(null, HttpStatus.OK, customer))
              .orElseGet(() -> ResponseHandler.generateResponse("Customer with id " + id + " not found", HttpStatus.NOT_FOUND, null));
    } catch (Exception e) {
      return ResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
  }


  @Operation(summary = "Add a new customer", tags = { "customer", "post", "filter" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Customer created",content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)) }),
          @ApiResponse(responseCode = "400", description = "Bad request",content = @Content),
          @ApiResponse(responseCode = "404", description = "No customer found",content = @Content) })
  @PostMapping("/v1/customer")
  public ResponseEntity<Object> create(@RequestBody Customer customer) {
    try {
      Optional<Customer> existingCustomer = customerService.getCustomerById(customer.getId());
      if (existingCustomer.isPresent()) {
        return ResponseHandler.generateResponse("Customer with id " + customer.getId() + " already exists", HttpStatus.BAD_REQUEST, null);
      }

      Customer createdCustomer = customerService.create(customer.getName(), customer.getEmail());

      return ResponseHandler.generateResponse("Customer created", HttpStatus.CREATED, createdCustomer);

    } catch (IllegalArgumentException e) {
      return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }
  }

  @Operation(summary = "Update an existing customer", tags = { "customers", "put", "filter" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)) }),
          @ApiResponse(responseCode = "400", description = "Bad request", content = @Content),
          @ApiResponse(responseCode = "404", description = "Cusatomer not found", content = @Content) })
  @PutMapping("/v1/customer/{id}")
  public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
    try {
      Optional<Customer> existingCustomer = customerService.getCustomerById(id);

      if (existingCustomer.isEmpty()) {
        return ResponseHandler.generateResponse("Customer with id " + id + " not found", HttpStatus.NOT_FOUND, null);
      }

      Customer customerToUpdate = existingCustomer.get();
      customerToUpdate.setName(updatedCustomer.getName());
      customerToUpdate.setEmail(updatedCustomer.getEmail());

      Customer savedCustomer = customerService.save(customerToUpdate);

      return ResponseHandler.generateResponse("Customer updated successfully", HttpStatus.OK, savedCustomer);
    } catch (IllegalArgumentException e) {
      return ResponseHandler.generateResponse(e.getMessage(), HttpStatus.BAD_REQUEST, null);
    }
  }

  @Operation(summary = "Delete customer by id", tags = { "customers", "delete", "filter" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Customer deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content) })
  @DeleteMapping("/v1/customer/{id}")
  public ResponseEntity<Object> delete(@PathVariable Long id) {
    try {
      Optional<Customer> existingCustomer = customerService.getCustomerById(id);

      if (existingCustomer.isEmpty()) {
        return ResponseHandler.generateResponse("Customerser with id " + id + " not found", HttpStatus.NOT_FOUND, null);
      }

      customerService.deleteCustomerById(id);

      return ResponseHandler.generateResponse("Customer deleted successfully", HttpStatus.NO_CONTENT, null);
    } catch (Exception e) {
      return ResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
  }

  @Operation(summary = "Retrieve customer by email", tags = { "customers", "get", "filter" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Customer successfully retrieved by email", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Customer.class)) }),
          @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content) })
  @GetMapping("/v1/customer/email")
  public ResponseEntity<Object> getCustomerByEmail(@RequestParam String email) {
    try {
      Optional<Customer> found = customerService.findCustomerByEmail(email);

      return found.map(customer -> ResponseHandler.generateResponse(null, HttpStatus.OK, customer))
              .orElseGet(() -> ResponseHandler.generateResponse("Customer with email " + email + " not found", HttpStatus.NOT_FOUND, null));
    } catch (Exception e) {
      return ResponseHandler.generateResponse("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }
  }




}
