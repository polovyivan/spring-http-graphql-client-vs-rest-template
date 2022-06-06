package com.polovyi.ivan.controller;

import com.polovyi.ivan.client.RestClient;
import com.polovyi.ivan.dto.CreateCustomerRequest;
import com.polovyi.ivan.dto.CustomerResponse;
import com.polovyi.ivan.dto.PartiallyUpdateCustomerRequest;
import com.polovyi.ivan.dto.UpdateCustomerRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/for-rest-client")
public record CustomerControllerForRESTClient(RestClient restClient) {

    @GetMapping(path = "/customers")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponse> getAllCustomers() {
        return restClient.getAllCustomers();
    }

    @GetMapping(path = "/customers-with-filters")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerResponse> getAllCustomersWithFilters(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt) {
        return restClient.getCustomersWithFilters(fullName, phoneNumber, createdAt);
    }

    @PostMapping(path = "/customers")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCustomer(@Valid @RequestBody CreateCustomerRequest createCustomerRequest,
            UriComponentsBuilder uriBuilder, HttpServletResponse response) {
        String location = restClient.createCustomer(createCustomerRequest);
        response.addHeader("location", location);
    }

    @PutMapping(path = "/customers/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCustomer(@PathVariable String customerId,
            @Valid @RequestBody UpdateCustomerRequest updateCustomerRequest) {
        restClient.updateCustomer(customerId, updateCustomerRequest);
    }

    @PatchMapping(path = "/customers/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void partiallyUpdateCustomer(@PathVariable String customerId,
            @Valid @RequestBody PartiallyUpdateCustomerRequest partiallyUpdateCustomerRequest) {
        restClient.partiallyUpdateCustomer(customerId, partiallyUpdateCustomerRequest);
    }

    @DeleteMapping(path = "/customers/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable String customerId) {
        restClient.deleteCustomer(customerId);
    }

}
