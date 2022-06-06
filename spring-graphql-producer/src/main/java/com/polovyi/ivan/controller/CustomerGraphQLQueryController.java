package com.polovyi.ivan.controller;

import com.polovyi.ivan.dto.CustomerResponse;
import com.polovyi.ivan.service.CustomerService;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Component
public record CustomerGraphQLQueryController(CustomerService customerService) implements GraphQLQueryResolver {

    public List<CustomerResponse> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    public List<CustomerResponse> getAllCustomersWithFilters(
            String fullName,
            String phoneNumber,
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate createdAt) {
        return customerService.getCustomersWithFilters(fullName, phoneNumber, createdAt);
    }

}
