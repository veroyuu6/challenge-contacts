package com.challenge.contacts.infrastructure.request;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class RegisterContactRequest {

   private String name;
   private String email;
   private String phone;

}
