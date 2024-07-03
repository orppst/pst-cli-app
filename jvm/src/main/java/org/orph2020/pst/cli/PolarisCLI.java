package org.orph2020.pst.cli;


import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import picocli.CommandLine;

import java.util.stream.StreamSupport;

@QuarkusMain
@CommandLine.Command
public class PolarisCLI implements QuarkusApplication {

    @CommandLine.Option(names = {"-u","--user"})
    String user;

    @CommandLine.Option(names = {"-p","--password"}, arity = "0..1", interactive = true)
    String password;

    @Inject
    CommandLine.IFactory factory;

    @Inject
    FetchCommand fetch;


    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory)
                .addSubcommand(fetch)
                .execute(args);
    }
}
