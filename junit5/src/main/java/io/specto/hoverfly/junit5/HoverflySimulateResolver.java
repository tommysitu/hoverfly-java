package io.specto.hoverfly.junit5;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.core.config.LocalHoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import io.specto.hoverfly.junit5.spi.HoverflySimulation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

/**
 * Hoverfly Simulate Resolver. This resolver starts Hoverfly proxy server before all test methods are executed and stops it after all.
 *
 * By default Hoverfly is configured with default configuration parameters and simulation is loaded from a file located at
 * Hoverfly default path (src/test/resources/hoverfly) and file called with fully qualified name of test class, replacing dots (.) and dollar signs ($) to underlines (_).
 *
 * To configure instance just annotate test class with {@link HoverflySimulate} annotation.
 */
public class HoverflySimulateResolver implements AfterAllCallback, BeforeAllCallback, ParameterResolver {

    private Hoverfly hoverfly;

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {

        final Optional<AnnotatedElement> testClassElement = context.getElement();

        if (testClassElement.isPresent() && AnnotationSupport.isAnnotated(testClassElement.get(), HoverflySimulate.class)) {
            final Optional<HoverflySimulate> hoverflySimulate =
                AnnotationSupport.findAnnotation(testClassElement.get(), HoverflySimulate.class);

            startHoverflyIfNotStarted(hoverflySimulate.get());
        } else {
            final Optional<Class<?>> testClass = context.getTestClass();
            if (testClass.isPresent()) {
                startHoverflyWithDefaultsIfNotStarted(testClass.get());
            }
        }

    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (isRunning()) {
            this.hoverfly.close();
            this.hoverfly = null;
        }
    }

    boolean isRunning() {
        return this.hoverfly != null;
    }

    private void startHoverflyWithDefaultsIfNotStarted(Class<?> currentTest) {
        if (!isRunning()) {
            this.hoverfly = new Hoverfly(HoverflyConfig.configs(), HoverflyMode.SIMULATE);
            this.hoverfly.start();

            final HoverflySimulation.DefaultHoverflySimulation defaultHoverflySimulation =
                new HoverflySimulation.DefaultHoverflySimulation(currentTest);
            this.hoverfly.importSimulation(defaultHoverflySimulation.simulation());
        }
    }

    private void startHoverflyIfNotStarted(HoverflySimulate hoverflySimulate) {
        if (!isRunning()) {
            LocalHoverflyConfig configs = HoverflyConfig.configs();
            configs.adminPort(hoverflySimulate.adminPort()).proxyPort(hoverflySimulate.proxyPort());
            this.hoverfly = new Hoverfly(configs,HoverflyMode.SIMULATE);
            this.hoverfly.start();

            hoverfly.importSimulation(SimulationSource.classpath(hoverflySimulate.simulationSource().classpath()));
        }
    }

    private void importSimulation(Class<? extends HoverflySimulation> hoverflySimulationClass) {
        final HoverflySimulation hoverflySimulation = createHoverflySimulation(hoverflySimulationClass);
        this.hoverfly.importSimulation(hoverflySimulation.simulation());
    }

    private HoverflySimulation createHoverflySimulation(Class<? extends HoverflySimulation> hoverflySimulationClass) {
        try {
            return hoverflySimulationClass.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        return Hoverfly.class.isAssignableFrom(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
        throws ParameterResolutionException {
        return this.hoverfly;
    }
}
