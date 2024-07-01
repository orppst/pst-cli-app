package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;

import java.awt.*;
import java.net.URI;
import java.util.stream.StreamSupport;
import jakarta.inject.Inject;

@CommandLine.Command(name = "fetch", mixinStandardHelpOptions = true)
@QuarkusMain
public class FetchCommand implements Runnable, QuarkusApplication {


   @CommandLine.Option(names = {"-u","--user"})
   String user;

   @CommandLine.Option(names = {"-p","--password"}, arity = "0..1", interactive = true)
   String password;
   @Inject
   OidcClient oidcClient;

   @Inject
   CommandLine.IFactory factory;


   @Inject
   protected ObjectMapper mapper;
    @RestClient
    ProposalRestAPI  apiService;
    @Override
    public void run() {
       try {

          System.out.println(mapper.writeValueAsString(apiService.getObservatories()));
          System.out.println(mapper.writeValueAsString(apiService.getObservingProposal(1)));
       } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
       }
    }

   @Override
   public int run(String... args) throws Exception {
      CommandLine.ParseResult parseResult = new CommandLine(this, factory).parseArgs(args);
      Config c = ConfigProvider.getConfig();
      MemoryConfigSource mconfig = (MemoryConfigSource)StreamSupport.stream(c.getConfigSources().spliterator(),false)
            .filter(s->s.getName().equals("CliConfig"))
            .findFirst().get();

      //FIXME -the below attempt to set properties will not work - not happening early enough - the OidcClient is already created when this can take effect
      // seems like the only way to make this work is to have custom filter and oidc client instance that will allo
      // anyway it seems that a better way to do this is probably to have the user login to web
      // interface and get a refresh token that can be saved to a file, and that read in at start

      //Desktop.getDesktop().browse(new URI("http://localhost:8080/pst/gui/"));

      mconfig.setPassword(password);
      System.out.println("have set password to string of " + password.length() + " characters");
      mconfig.setUserName(user);
      System.out.println("user: " + user);

      //System.out.println(oidcClient.getTokens());

      return new CommandLine.RunLast().execute(parseResult) ;
   }
}
