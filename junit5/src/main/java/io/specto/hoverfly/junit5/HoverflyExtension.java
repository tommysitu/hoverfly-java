package io.specto.hoverfly.junit5;

import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyConfig;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit5.api.HoverflyCore;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;

public class HoverflyExtension implements ParameterResolver, BeforeAllCallback, AfterAllCallback {

    private Hoverfly hoverfly;
    @Override
    public void afterAll(ExtensionContext context) throws Exception {

        if (hoverfly != null) {
            hoverfly.close();
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        hoverfly = new Hoverfly(HoverflyConfig.configs(), HoverflyMode.SIMULATE);
        hoverfly.start();
    }
//
//    @Override
//    public void beforeAll(ExtensionContext context) throws Exception {
//
//        final Optional<AnnotatedElement> testClassElement = context.getElement();
//
//        if (testClassElement.isPresent() && AnnotationSupport.isAnnotated(testClassElement.get(), HoverflySimulate.class)) {
//            final Optional<HoverflySimulate> hoverflySimulate =
//                    AnnotationSupport.findAnnotation(testClassElement.get(), HoverflySimulate.class);
//
//            startHoverflyIfNotStarted(hoverflySimulate.get());
//        } else {
//            final Optional<Class<?>> testClass = context.getTestClass();
//            if (testClass.isPresent()) {
//                startHoverflyWithDefaultsIfNotStarted(testClass.get());
//            }
//        }
//
//    }
//
//    private void startHoverflyIfNotStarted(HoverflySimulate hoverflySimulate) {
//        if (!isRunning()) {
//            try {
//                this.hoverfly = new Hoverfly(hoverflySimulate.config().newInstance().create(),
//                        HoverflyMode.SIMULATE);
//                this.hoverfly.start();
//
//                importSimulation(hoverflySimulate.source());
//            } catch (InstantiationException e) {
//                throw new IllegalStateException(e);
//            } catch (IllegalAccessException e) {
//                throw new IllegalStateException(e);
//            }
//        }
//    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context)
            throws ParameterResolutionException {

        return parameterContext.getParameter().isAnnotationPresent(HoverflyCore.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext context)
            throws ParameterResolutionException {

//        startHoverflyIfNotStarted(parameterContext.getParameter().getAnnotation(HoverflyCore.class));
        return this.hoverfly;
    }
}
