package com.challenge.contacts.infrastructure.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ItemContact {

   private String name;
   private String phone;
   private String email;

}
