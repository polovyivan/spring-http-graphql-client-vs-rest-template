package com.polovyi.ivan.client;

import com.polovyi.ivan.dto.CreateCustomerRequest;
import com.polovyi.ivan.dto.CustomerResponse;
import com.polovyi.ivan.dto.PartiallyUpdateCustomerRequest;
import com.polovyi.ivan.dto.UpdateCustomerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GraphQLClient {

    private WebClient webClient = WebClient.create();
    private HttpGraphQlClient graphQlClient = HttpGraphQlClient.builder(webClient)
            .build();

    public List<CustomerResponse> getAllCustomers() {
        log.info("[GraphQLClient] Calling getAllCustomers query...");

        String document = """
                query {
                    getAllCustomers {
                                id
                                fullName
                                phoneNumber
                                address
                                createdAt
                                }
                }
                """;

        return graphQlClient.mutate()
                .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                .build()
                .document(document)
                .retrieve("getAllCustomers")
                .toEntityList(CustomerResponse.class)
                .block();
    }

    public List<CustomerResponse> getCustomersWithFilters(String fullName, String phoneNumber, LocalDate createdAt) {
        log.info("[GraphQLClient] Calling getAllCustomersWithFilters query...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.computeIfAbsent("fullName", value -> fullName);
        varMap.computeIfAbsent("phoneNumber", value -> phoneNumber);
        varMap.computeIfAbsent("createdAt",
                value -> Optional.ofNullable(createdAt).map(LocalDate::toString).orElse(null));

        String document = """
                query ($fullName : String
                       $phoneNumber : String
                       $createdAt : Date ){
                       getAllCustomersWithFilters(fullName: $fullName
                                                  phoneNumber : $phoneNumber
                                                  createdAt : $createdAt) {
                                                                            id
                                                                            fullName
                                                                            phoneNumber
                                                                            address
                                                                            createdAt
                                                                          }
                }
                """;
        return graphQlClient.mutate()
                .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                .build()
                .document(document)
                .variables(varMap)
                .retrieve("getAllCustomersWithFilters")
                .toEntityList(CustomerResponse.class)
                .block();
    }

    public String createCustomer(CreateCustomerRequest createCustomerRequest) {
        log.info("[GraphQLClient] Calling create customer mutation...");
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("createCustomerRequest", createCustomerRequest);
        String document = """
                mutation ($createCustomerRequest : CreateCustomerRequest) {
                    createCustomer (createCustomerRequest : $createCustomerRequest)
                    {
                        id
                        fullName
                        phoneNumber
                        address
                        createdAt
                    }
                }
                """;
        return graphQlClient.mutate()
                .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                .build()
                .document(document)
                .variables(varMap)
                .retrieve("createCustomer")
                .toEntity(CustomerResponse.class)
                .block().getId();
    }

        // Using file instead of mutation string
        public void updateCustomer(String customerId, UpdateCustomerRequest updateCustomerRequest) {
            log.info("[GraphQLClient] Calling update customer mutation...");
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("customerId", customerId);
            varMap.put("updateCustomerRequest", updateCustomerRequest);
             graphQlClient.mutate()
                    .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                    .build()
                    .documentName("update-customer")
                    .variables(varMap)
                    .retrieve("updateCustomer")
                    .toEntity(CustomerResponse.class)
                    .block();
        }

        public void partiallyUpdateCustomer(String customerId,
                PartiallyUpdateCustomerRequest partiallyUpdateCustomerRequest) {
            log.info("[GraphQLClient] Calling partially update customer mutation...");
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("customerId", customerId);
            varMap.put("partiallyUpdateCustomerRequest", partiallyUpdateCustomerRequest);

            String mutation = """
                    mutation ($partiallyUpdateCustomerRequest : PartiallyUpdateCustomerRequest
                               $customerId : String) {
                             partiallyUpdateCustomer (
                                customerId : $customerId
                                partiallyUpdateCustomerRequest : $partiallyUpdateCustomerRequest)
                                                      {
                                                        id
                                                        fullName
                                                        phoneNumber
                                                        address
                                                        createdAt
                                                      }
                    }
                                    """;
            graphQlClient.mutate()
                    .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                    .build()
                    .document(mutation)
                    .variables(varMap)
                    .retrieve("partiallyUpdateCustomer")
                    .toEntity(CustomerResponse.class)
                    .block();
        }

        public void deleteCustomer(String customerId) {
            log.info("[GraphQLClient] Calling delete customer mutation...");
            Map<String, Object> varMap = new HashMap<>();
            varMap.put("customerId", customerId);
            String mutation = """
                    mutation ( $customerId : String) {
                             deleteCustomer (
                                             customerId : $customerId)
                                                      }
                       """;
            graphQlClient.mutate()
                    .url("http://localhost:8001/spring-graphql-producer/customers-graphql")
                    .build()
                    .document(mutation)
                    .variables(varMap)
                    .retrieve("deleteCustomer")
                    .toEntity(String.class)
                    .block();
        }

}
