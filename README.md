# hoverfly-junit
Junit rule for testing against virtualized services (otherwise known as http API simulations) using hoverfly.  It essentially spins up a web server which will return recorded responses for their matching requests.

## Quick-start

Simply add the following rule to your class, giving it the location of your hoverfly json on your classpath.

```java
@Rule
public HoverflyRule hoverflyRule = HoverflyRule.builder("test-service.json").build();
```

The rule will attempt to detect the operating system and architecture type of the host, and then extract and execute the correct hoverfly binary.  It will import the json into it's database and then and destroy the process at the end of the tests.


## Configuration

Currently the are only a few minor configurable options.

### Visualized Service Json

This is looked for by the rule at the given location on the classpath.  It's simply json representing http requests and their corresponding responses which can be replayed by hoverfly.

```java
HoverflyRule.builder("test-service.json").build()
```

### Ports

The admin and proxy port will default to zero, which means they will be randomized as unused ports. This can be helpful when running your tests on a CI server.
If you want to set them statically you can do so through the fluent builder:

```
HoverflyRule.builder("test-service.json")
    .withAdminPort(EXPECTED_ADMIN_PORT)
    .withProxyPort(EXPECTED_PROXY_PORT)
    .build();
```