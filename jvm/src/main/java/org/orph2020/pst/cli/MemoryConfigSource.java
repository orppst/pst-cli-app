package org.orph2020.pst.cli;
/*
 * Created on 02/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 * @see https://quarkus.io/guides/config-extending-support#custom-config-source
 */

import org.eclipse.microprofile.config.spi.ConfigSource;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Attempt at dynamic config source
 * FIXME - this does not really work as beans get configured before thy dynamic values can be set.
 */
public class MemoryConfigSource implements ConfigSource {
   public MemoryConfigSource() {
      System.out.println("*** memory config constructor");
   }

   private static final Map<String, String> configuration = new HashMap<>();
   private static final Map<String, String> grantOptions = new HashMap<>();
   static {

      configuration.put("quarkus.oidc-client.grant-options.device", "");//TODO want to put grantOptions here.....

   }

   @Override
   public Set<String> getPropertyNames() {
      return configuration.keySet();
   }

   @Override
   public String getValue(String propertyName) {
       return configuration.get(propertyName);
   }

   @Override
   public String getName() {
      return "CliConfig";
   }

   @Override
   public int getOrdinal() {
      return 275;
   }

   public void setUserName(String name)
   {
      configuration.put("quarkus.oidc-client.grant-options.password.username",name);
   }
   public void setPassword(String pwd)
   {
      configuration.put("quarkus.oidc-client.grant-options.password.password",pwd);
   }
}
