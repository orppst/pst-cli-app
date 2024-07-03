package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClients;
import io.quarkus.oidc.client.OidcClientConfig;
import io.quarkus.oidc.client.OidcClientConfig.Grant.Type;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;

import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;

import java.net.URI;
import java.util.Map;
import java.util.stream.StreamSupport;
import jakarta.inject.Inject;

@CommandLine.Command(name = "fetch", mixinStandardHelpOptions = true)
@QuarkusMain
public class FetchCommand implements Runnable, QuarkusApplication {


   @CommandLine.Option(names = {"-u", "--user"})
   String user;

   @CommandLine.Option(names = {"-p", "--password"}, arity = "0..1", interactive = true)
   String password;

   @Inject
   CommandLine.IFactory factory;


   @Inject
   protected ObjectMapper mapper;
   @RestClient
   ProposalRestAPI apiService;

   @Inject
   OidcClients oidcClients;

   private volatile OidcClient oidcClient;

   //Try to programmatically create a client
   private Uni<OidcClient> createOidcClient() {
      OidcClientConfig cfg = new OidcClientConfig();
      cfg.setId("Who am I");
      cfg.setAuthServerUrl("http://localhost:53536/realms/orppst");
      cfg.setClientId("pst-gui"); //FIXME with new Id for CLI
      cfg.getCredentials().setSecret("eLt4izrWhxRftFTWTIcMbQsYlbyhfZtU");
      cfg.getGrant().setType(Type.PASSWORD);
      cfg.setGrantOptions(Map.of("password",
              Map.of("username", "pi", "password", "pi")));
      return oidcClients.newClient(cfg);
   }

   @Override
   public void run() {
      try {
         createOidcClient().subscribe().with(client -> {oidcClient = client;});
/*
         apiService = QuarkusRestClientBuilder.newBuilder()
                 .baseUri(URI.create("http://localhost:8084/pst/api/"))
                 .build(ProposalRestAPI.class);

*/
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
      // seems like the only way to make this work is to have custom filter and oidc client instance that will allow
      // anyway it seems that a better way to do this is probably to have the user login to web
      // interface and get a refresh token that can be saved to a file, and that read in at start

      //Desktop.getDesktop().browse(new URI("http://localhost:8080/pst/gui/"));

      mconfig.setPassword(password);
      System.out.println("have set password to string of " + password.length() + " characters");
      mconfig.setUserName(user);
      System.out.println("user: " + user);

      return new CommandLine.RunLast().execute(parseResult) ;
   }
}
