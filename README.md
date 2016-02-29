# hoverfly-junit
Junit rules for testing with hoverfly

## Quick-start

Simply add the following rule to your class, giving it the location of your recorded json on your classpath.

```java
    @ClassRule
    public static HoverflyRule hoverflyRule = new HoverflyRule("test-service.json");
```

The rule will manage spinning up and tearing down of the hoverfly process, loading the `json` database into it, and setting the appropriate environment variables so all the requests made by your test use hoverfly as a proxy.