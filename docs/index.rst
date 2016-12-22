Overview
########
What is Hoverfly?
=================

`Hoverfly <http://hoverfly.io>`_ is a lightweight service virtualisation tool which allows you to stub / simulate http services. It is a proxy written in Go which responds to http requests with stored responses, pretending to be it's real counterpart.

It enables you to get around common testing problems caused by external dependencies, such as non-deterministic data, flakiness, not yet implemented API's, licensing fees, slow tests and more.

Hoverfly Java
=============

Hoverfly Java is a native language binding which gives you an expressive API for managing Hoverfly in Java.  It gives you a Hoverfly class which abstracts away the binary and API calls, a DSL for creating simulations, and a JUnit integration for using it within JUnit tests.


Quick start
###########

Maven
=====

If using maven, add the following dependency to your pom:

.. code-block:: xml

    <depencency>
        <groupId>io.specto</groupId>
        <artifactId>hoverfly-junit</artifactId>
        <version>0.3.0</version>
    </dependency>

Gradle
======

.. code-block:: groovy

   testCompile "io.specto:hoverfly-junit:0.3.0"

Code example
============

The simplest way is to get started is with the JUnit rule. Just give it some valid Hoverfly Json. Behind the scenes the JVM proxy settings will be configured to use the managed Hoverfly process, so you can just make requests as normal, only this time Hoverfly will respond instead of the real service. (assuming your http client respects JVM proxy settings).

.. code-block:: java

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode(classpath("test-service.json"));

    @Test
    public void shouldBeAbleToGetABookingUsingHoverfly() {
        // When
        final ResponseEntity<String> getBookingResponse = restTemplate.getForEntity("http://www.my-test.com/api/bookings/1", String.class);

        // Then
        assertThat(getBookingResponse.getStatusCode()).isEqualTo(OK);
        assertThatJson(getBookingResponse.getBody()).isEqualTo("{" +
                "\"bookingId\":\"1\"," +
                "\"origin\":\"London\"," +
                "\"destination\":\"Singapore\"," +
                "\"time\":\"2011-09-01T12:30\"," +
                "\"_links\":{\"self\":{\"href\":\"http://localhost/api/bookings/1\"}}" +
                "}");
    }

Core
####

Simulating
----------

The core of this library is the Hoverfly class, which abstracts away and orchestrates a Hoverfly instance.  A flow might be as follows:

.. code-block:: java

    final Hoverfly hoverfly = new Hoverfly(config(), SIMULATE);
    hoverfly.start();
    hoverfly.importSimulation(classpath(simulation))
    // do some requests here
    hoverfly.stop();

Capturing
---------

The previous examples have only used Hoverfly in simulate mode. You can also run it in capture mode, meaning that requests will be made to the real service as normal,
only they will be intercepted and recorded by Hoverfly.  This can be a simple way of breaking a tests dependency on an external service; wait until you have a green
test, then switch back into simulate mode using the data produced during capture mode.

.. code-block:: java

    final Hoverfly hoverfly = new Hoverfly(config(), CAPTURE);
    hoverfly.start();
    // do some requests here
    hoverfly.exportSimulation(classpath(simulation))
    hoverfly.stop();


Config
------

Hoverfly takes a config, which contains sensible defaults if not configured.  Ports will be randomised to unused ones, which is useful on something like a CI server if you want
to avoid port clashes.

.. code-block:: java

    config().proxyPort(8080)

SSL
---

When requests pass through Hoverfly, it needs to decrypt them in order for it to persist them to a database, or to perform matching.  So you end up with SSL between Hoverfly and
the external service, and then SSL again between your client and Hoverfly.  To get this to work, Hoverfly comes with it's own self-signed certificate which has to be trusted by
your client.  To avoid the pain of configuring your keystore, Hoverfly's certificate is trusted automatically when you instantiate it.