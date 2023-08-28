package com.challenge.contacts.infrastructure.response;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ContactResponse {

   private List<ItemContact> items;

}
