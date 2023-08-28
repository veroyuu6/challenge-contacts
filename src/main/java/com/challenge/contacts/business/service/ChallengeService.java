package com.challenge.contacts.business.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.challenge.contacts.infrastructure.client.ChallengeClient;
import com.challenge.contacts.infrastructure.request.RegisterContactRequest;
import com.challenge.contacts.infrastructure.response.ContactResponse;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import lombok.Cleanup;

@Service
public class ChallengeService {

   private final Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

   private static final String MESSAGE_EMPTY = "No se han encontrado registros";

   private static final String MESSAGE_STRUCTURED_FILE = "Se han encontrado mas de 3 registros en la fila %s";

   private static final String MESSAGE_MAIL_INVALID = "Email invalido en la fila %s";

   private static final String MESSAGE_PHONE_INVALID =
         "Telefono invalido debe tener la siguiente estructura 5555555555 -> 555-555-5555 en la fila " + "%s";

   private static final Integer INTEGER_THREE = 3;

   private static final Integer INTEGER_FOUR = 4;

   @Autowired
   private ChallengeClient challengeClient;

   public String registerContact(final InputStream inputStream) {
      try {
         @Cleanup
         final CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
         final List<String[]> rows = csvReader.readAll();
         if (rows == null || rows.isEmpty()) {
            return MESSAGE_EMPTY;
         }
         final List<RegisterContactRequest> listRequest = new ArrayList<>();
         final AtomicInteger rowCount = new AtomicInteger(NumberUtils.INTEGER_ZERO);
         for (final String[] row : rows) {
            if (row.length > INTEGER_THREE) {
               return String.format(MESSAGE_STRUCTURED_FILE, (rowCount.get() + NumberUtils.INTEGER_ONE));
            } else {
               final String[] rowArray = getColumns(row);
               if (!isEmail(rowArray[NumberUtils.INTEGER_TWO])) {
                  return String.format(MESSAGE_MAIL_INVALID, (rowCount.get() + NumberUtils.INTEGER_ONE));
               }
               if (!isPhone(rowArray[NumberUtils.INTEGER_ONE])) {
                  return String.format(MESSAGE_PHONE_INVALID, (rowCount.get() + NumberUtils.INTEGER_ONE));
               }
               listRequest.add(RegisterContactRequest
                     .builder()
                     .name(rowArray[NumberUtils.INTEGER_ZERO])
                     .phone(rowArray[NumberUtils.INTEGER_ONE])
                     .email(rowArray[NumberUtils.INTEGER_TWO])
                     .build());
            }
            rowCount.incrementAndGet();
         }
         insertContacts(listRequest);
      } catch (IOException | CsvException e) {
         return e.getMessage();
      }
      return StringUtils.EMPTY;

   }

   private void insertContacts(final List<RegisterContactRequest> listRequest) {
      listRequest.forEach(request -> challengeClient.registerContact(request));
   }

   private boolean isEmail(final String email) {
      final Matcher mather = pattern.matcher(email);
      return mather.find();
   }

   private boolean isPhone(final String phone) {
      if (phone.contains("-")) {
         final String[] phoneNumbers = phone.split("-");
         return phoneNumbers[NumberUtils.INTEGER_ZERO].length() == INTEGER_THREE && phoneNumbers[NumberUtils.INTEGER_ONE].length() == INTEGER_THREE
               && phoneNumbers[NumberUtils.INTEGER_TWO].length() == INTEGER_FOUR;
      }
      return false;
   }

   public byte[] getSampleCsv() {
      return challengeClient.samples();
   }

   public ContactResponse getContacts() {
      return challengeClient.getContacts();
   }

   private String[] getColumns(final String[] rows) {
      if (rows.length == INTEGER_THREE) {
         return rows;
      } else {
         final String row = rows[NumberUtils.INTEGER_ZERO];
         if (row.contains(",")) {
            return row.split(",");
         } else {
            return row.split(";");
         }
      }
   }

}
