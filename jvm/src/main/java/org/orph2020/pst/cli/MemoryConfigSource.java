package org.orph2020.pst.cli;
/*
 * Created on 02/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import org.eclipse.microprofile.config.spi.ConfigSource;


import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MemoryConfigSource implements ConfigSource {
   public MemoryConfigSource() {
      System.out.println("*** memory config constructor");
   }

   private static final Map<String, String> configuration = new HashMap<>();
   static {
      configuration.put("quarkus.oidc-client.grant-options.password.username","pi");
      configuration.put("quarkus.oidc-client.grant-options.password.password","pi");

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
