package com.polovyi.ivan.client;

import com.polovyi.ivan.dto.CreateCustomerRequest;
import com.polovyi.ivan.dto.CustomerResponse;
import com.polovyi.ivan.dto.PartiallyUpdateCustomerRequest;
import com.polovyi.ivan.dto.UpdateCustomerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestClient {

    private final static String URL = "http://localhost:8001/spring-graphql-producer/v1/customers";

    private final RestTemplateBuilder restTemplate;

    public List<CustomerResponse> getAllCustomers() {
        log.info("[RestClient] Getting all customers...");
        return restTemplate.build().exchange(URL, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CustomerResponse>>() {
                }).getBody();
    }

    public List<CustomerResponse> getCustomersWithFilters(String fullName, String phoneNumber, LocalDate createdAt) {
        log.info("[RestClient] Getting all customers...");
        UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(URL + "-with-filters");
        Map<String, Object> map = new HashMap<>();
        map.computeIfAbsent("fullName", value -> fullName);
        map.computeIfAbsent("phoneNumber", value -> phoneNumber);
        map.computeIfAbsent("createdAt", value -> createdAt);
        map.forEach(uri::queryParam);

        return restTemplate.build().exchange(uri.build().toUriString(), HttpMethod.GET, null,
                new ParameterizedTypeReference<List<CustomerResponse>>() {
                }).getBody();
    }

    public String createCustomer(CreateCustomerRequest createCustomerRequest) {
        log.info("[RestClient] Creating customer...");
        HttpEntity<?> entity = new HttpEntity<>(createCustomerRequest);
        return restTemplate.build().exchange(URL,
                HttpMethod.POST, entity, Void.class).getHeaders().get("location").get(0);
    }

    public void updateCustomer(String customerId, UpdateCustomerRequest updateCustomerRequest) {
        HttpEntity<?> entity = new HttpEntity<>(updateCustomerRequest);
        restTemplate.build().exchange(URL + "/{customerId}",
                HttpMethod.PUT, entity, Void.class, customerId);
    }

    public void partiallyUpdateCustomer(String customerId,
            PartiallyUpdateCustomerRequest partiallyUpdateCustomerRequest) {
        HttpEntity<?> entity = new HttpEntity<>(partiallyUpdateCustomerRequest);
        restTemplate.build().exchange(URL + "/{customerId}",
                HttpMethod.PATCH, entity, Void.class, customerId);

    }

    public void deleteCustomer(String customerId) {
        restTemplate.build().exchange(URL + "/{customerId}",
                HttpMethod.DELETE, null, Void.class, customerId);
    }

}
