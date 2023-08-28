package com.challenge.contacts.util.date;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateUtil {

   public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

   public static String localDateToString(final LocalDateTime localDateTime) {
      final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
      return localDateTime.format(dateTimeFormatter);

   }

}
