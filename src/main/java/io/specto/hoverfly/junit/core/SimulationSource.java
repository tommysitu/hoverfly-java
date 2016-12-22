package io.specto.hoverfly.junit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.specto.hoverfly.junit.core.model.GlobalActions;
import io.specto.hoverfly.junit.core.model.HoverflyData;
import io.specto.hoverfly.junit.core.model.HoverflyMetaData;
import io.specto.hoverfly.junit.core.model.RequestResponsePair;
import io.specto.hoverfly.junit.core.model.Simulation;
import io.specto.hoverfly.junit.dsl.StubServiceBuilder;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import static io.specto.hoverfly.junit.core.HoverflyUtils.findResourceOnClasspath;
import static java.util.stream.Collectors.toSet;
import static jersey.repackaged.com.google.common.collect.Lists.newArrayList;


/**
 * Interface for converting a resource into a {@link Simulation}
 */
public interface SimulationSource {

    /**
     * Creates a simulation from a URL
     *
     * @param url the url of the simulation
     * @return the resource
     */
    static SimulationSource url(final URL url) {
        return () -> {
            try {
                return Optional.of(new ObjectMapper().readValue(url, Simulation.class));
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot read simulation", e);
            }
        };
    }

    /**
     * Creates a simulation from the classpath
     *
     * @param classpath the classpath of the simulation
     * @return the resource
     */
    static SimulationSource classpath(final String classpath) {
        return () -> {
            try {
                return Optional.of(new ObjectMapper().readValue(Paths.get(findResourceOnClasspath(classpath)).toFile(), Simulation.class));
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot load classpath resource: '" + classpath + "'", e);
            }
        };
    }

    /**
     * Creates a simulation from the dsl
     *
     * @param stubServiceBuilder dsl stubs for each service
     * @return the resource
     * @see io.specto.hoverfly.junit.dsl.HoverflyDsl
     */
    static SimulationSource dsl(final StubServiceBuilder... stubServiceBuilder) {
        return () -> {
            final Set<RequestResponsePair> pairs = Arrays.stream(stubServiceBuilder)
                    .map(StubServiceBuilder::getRequestResponsePairs)
                    .flatMap(Set::stream)
                    .collect(toSet());

            return Optional.of(new Simulation(new HoverflyData(pairs, new GlobalActions(newArrayList())), new HoverflyMetaData()));
        };
    }

    /**
     * Creates a simulation by returning itself
     *
     * @param simulation the simulation
     * @return the simulation
     */
    static SimulationSource simulation(final Simulation simulation) {
        return () -> Optional.of(simulation);
    }

    /**
     * Creates no simulation
     *
     * @return an empty simulation
     */
    static SimulationSource empty() {
        return Optional::empty;
    }

    Optional<Simulation> getSimulation();
}
