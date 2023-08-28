package com.challenge.contacts.view.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.challenge.contacts.business.service.ChallengeService;
import com.challenge.contacts.infrastructure.response.ItemContact;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Named
@RequestScoped
public class HomeController implements Serializable {

   @Getter
   @Setter
   private List<ItemContact> items;

   private static final String MESSAGE_VALIDATION = "Mensaje validación";

   private static final String MESSAGE_SUCCESS = "Se han registrado los contactos correctamente";

   @Getter
   @Setter
   private UploadedFile file;

   @Autowired
   private ChallengeService challengeService;

   @PostConstruct
   public void init() {
      loadRefresh();
   }

   public void upload() {
      if (file == null) {
         sentMessage("No se ha cargado ningún archivo", FacesMessage.SEVERITY_ERROR);
      } else {
         try {
            final String message = challengeService.registerContact(file.getInputStream());
            if (message != null && !message.isEmpty()) {
               sentMessage(message, FacesMessage.SEVERITY_ERROR);
            } else {
               sentMessage(MESSAGE_SUCCESS, FacesMessage.SEVERITY_INFO);
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }
   }

   public void downloadSample() throws IOException {
      byte[] content = challengeService.getSampleCsv();
      final FacesContext facesContext = FacesContext.getCurrentInstance();
      final ExternalContext externalContext = facesContext.getExternalContext();
      externalContext.setResponseHeader("Content-Type", "application/CSV");
      externalContext.setResponseHeader("Content-Length", String.valueOf(content.length));
      externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + "samples.csv" + "\"");
      externalContext.getResponseOutputStream().write(content);
      facesContext.responseComplete();
   }

   public void refresh() throws InterruptedException {
      loadRefresh();
      final PrimeFaces current = PrimeFaces.current();
      current.executeScript("buttonEnable()");
      current.ajax().update("loadId");
      TimeUnit.SECONDS.sleep(1);
   }

   private void sentMessage(final String detail, final FacesMessage.Severity severity) {
      FacesContext.getCurrentInstance().addMessage("sticky-key", new FacesMessage(severity, MESSAGE_VALIDATION, detail));
   }

   private void loadRefresh() {
      items = challengeService.getContacts().getItems();
   }

}
