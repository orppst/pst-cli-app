package org.orph2020.pst.cli;


import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;
import picocli.CommandLine;

@QuarkusMain
@CommandLine.Command( mixinStandardHelpOptions = true,
        subcommands = {FetchPeople.class, FetchProposal.class})
public class PolarisCLI implements QuarkusApplication {

    @CommandLine.Option(names = {"-u","--user"})
    String user;

    @CommandLine.Option(names = {"-p","--password"}, arity = "0..1", interactive = true)
    String password;

    @Inject
    CommandLine.IFactory factory;

    @Inject
    FetchPeople fetchPeople;

    @Inject
    FetchProposal fetchProposal;


    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this, factory)
                .execute(args);
    }
}
