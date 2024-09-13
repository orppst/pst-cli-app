package org.orph2020.pst.cli;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.OidcClientException;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.orph2020.pst.apiimpl.client.ProposalRestAPI;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.Thread.sleep;

@QuarkusMain
@Command(
        name = "polaris-cli",
        mixinStandardHelpOptions = true,
        subcommands = {
                HelpCommand.class,
                FetchPeople.class,
                FetchProposal.class,
                FetchObservatory.class,
                CreateObservatory.class
        })
public class PolarisCLI implements QuarkusApplication, Runnable {

    @Inject
    IFactory factory;

    @Inject
    ObjectMapper mapper;

    @Inject
    OidcClient oidcClient;

    @RestClient
    ProposalRestAPI api;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory)
                .execute(args);
    }


    @Override
    public void run() {
        System.out.println("Running Polaris CLI");

        String device_code = "";
        String verification_uri_complete = "";
        Long poll_delay_seconds = 10L;

        // Generate a device code
        HttpClient httpClient = HttpClient.newHttpClient();
        var client = HttpClient.newHttpClient();
        Config config = ConfigProvider.getConfig();

        URI authURL = URI.create(config.getConfigValue("quarkus.oidc-client.auth-server-url").getValue()
                + "/protocol/openid-connect/auth/device");

        HttpRequest request = HttpRequest.newBuilder(authURL)
                .POST(HttpRequest.BodyPublishers.ofString("client_id=device-flow-client&client_secret="
                    +config.getConfigValue("quarkus.oidc-client.credentials.client-secret.value")
                        .getValue()))
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.body());

            device_code = (String) jsonResponse.get("device_code");
            verification_uri_complete = (String) jsonResponse.get("verification_uri_complete");
            poll_delay_seconds = (Long) jsonResponse.get("interval");

        } catch (IOException | InterruptedException e) {
            System.out.println("HTTP POST error: " + e.getMessage());
        } catch (ParseException e) {
            System.out.println("Unable to parse response: " + e.getMessage());
        }

        // Open browser for credentials
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(verification_uri_complete));
            } catch (IOException | URISyntaxException e) {
                System.out.println("Unable to open browser: " + e.getMessage());
            }
        }else{
            System.out.println("Desktop is not supported");
        }

        // Use device code to get an auth token
        Map<String, String> grantParams = new HashMap<>();
        grantParams.put("device_code", device_code);
        String encodedIdToken = "";

        boolean failed = false;
        boolean waiting = true;

        while(waiting && !failed) {
            try {
                sleep(poll_delay_seconds * 1000L);

                encodedIdToken = oidcClient.getTokens(grantParams).await().indefinitely().get("access_token");
                System.out.println("Got a token: " + encodedIdToken);

                waiting = false;

            } catch (InterruptedException | OidcClientException e) {
                System.out.println("InterruptedException: " + e.getMessage());
                JSONParser parser = new JSONParser();
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = (JSONObject) parser.parse(e.getMessage());
                    String error = (String) jsonResponse.get("error");

                    if(!Objects.equals(error, "authorization_pending")) {
                        //Something other than pending is an error
                        failed = true;
                    }

                } catch (ParseException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }

    }
}
