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

Add the following dependency to your pom:

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

The simplest way is to use the JUnit rule and give it some valid Hoverfly json.  Your JVM proxy will be set to use Hoverfly, so any requests you make will go through it (assuming your http client respects JVM proxy settings).

.. code-block:: java

    @ClassRule
    public static HoverflyRule hoverflyRule = HoverflyRule.inCaptureMode("test-service.json");

