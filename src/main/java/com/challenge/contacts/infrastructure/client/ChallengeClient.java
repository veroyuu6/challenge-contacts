package com.challenge.contacts.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.challenge.contacts.infrastructure.request.RegisterContactRequest;
import com.challenge.contacts.infrastructure.response.ContactResponse;

@Component
public class ChallengeClient {

   private static final String ENDPOINT_SAMPLES = "/production/tests/trucode/samples";

   private static final String ENDPOINT_REGISTER_CONTACT = "/production/tests/trucode/items";

   @Value("${endpoint.challenge.url}")
   private String baseUrl;

   private WebClient getWebClient() {
      return WebClient.builder().baseUrl(this.baseUrl).defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
   }

   public byte[] samples() {
      return this.getWebClient().get().uri(ENDPOINT_SAMPLES).retrieve().bodyToMono(byte[].class).block();
   }

   public String registerContact(final RegisterContactRequest request) {
      return this
            .getWebClient()
            .post()
            .uri(ENDPOINT_REGISTER_CONTACT)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(String.class)
            .block();
   }

   public ContactResponse getContacts() {
      return this
            .getWebClient()
            .get()
            .uri(ENDPOINT_REGISTER_CONTACT)
            .retrieve()
            .bodyToMono(ContactResponse.class)
            .block();
   }


}
