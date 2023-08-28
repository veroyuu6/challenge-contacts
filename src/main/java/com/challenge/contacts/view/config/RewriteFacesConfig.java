package com.challenge.contacts.view.config;

import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.rule.Join;
import org.springframework.beans.factory.annotation.Value;

import jakarta.servlet.ServletContext;

@RewriteConfiguration
public class RewriteFacesConfig extends HttpConfigurationProvider {

   @Value("${base.path.name}")
   private String basePathName;

   @Override
   public int priority() {
      return 10;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context) {
      return ConfigurationBuilder.begin().addRule(Join.path(this.basePathName.concat("/home")).to("/home.xhtml").withInboundCorrection());
   }

}
