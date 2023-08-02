package org.orph2020.pst.cli;
/*
 * Created on 01/08/2023 by Paul Harrison (paul.harrison@manchester.ac.uk).
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import io.smallrye.config.SmallRyeConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
          System.out.println(mapper.writeValueAsString(apiService.getObservingProposal(60)));
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

      //FIXME -the below attempt to set properties will not work - not happening early enough - the OICConfig
      mconfig.setPassword(password);
      System.out.println("have set password");
      mconfig.setUserName(user);
      System.out.println("parsed");
      return new CommandLine.RunLast().execute(parseResult) ;
   }
}
