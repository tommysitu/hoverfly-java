Overview
########
What is Hoverfly?
=================

`Hoverfly <http://hoverfly.io>`_ is a lightweight service virtualisation tool which allows you to stub / simulate http services. Written in Go, it runs as a proxy and responds to clients on behalf of a real service as if it is that service.

It enables you to get around common testing problems caused by you external dependencies, such as non-deterministic data, flakiness, not yet implemented API's, licensing fees, slow tests and more.

Hoverfly Java
=============

Hoverfly Java is a native language binding which gives you an expressive API for managing Hoverfly in Java.  It gives you a Hoverfly class which abstracts away the binary and API calls, a DSL for creating simulations, and a JUnit integration for using it within JUnit tests.


