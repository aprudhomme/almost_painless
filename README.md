# almost_painless
Self-contained extraction of painless scripting language from Elasticsearch, sourced from v7.10.2 (Apache 2.0 License).
Where required, server classes external to the painless plugin have been minimally implemented.

## Functional Changes
- Removal of ES plugin interface and REST actions
- Removal of ES specific script type handling
- Removal of JodaCompatibleZonedDateTime usage
- Removal of Json debug functions (xcontent dependency)
- Removal of sha hashing augmentations (MessageDigests dependency)

## Examples
Some simple (but far from complete) example usages.

### Define Script Classes
#### Execute Method

    public abstract class ExampleScript {
        public static final String[] PARAMETERS = new String[] {};
        ...
        public abstract Object execute();
        ...
    }

with parameters:

    public abstract class ExampleScript {
        public static final String[] PARAMETERS = new String[] {"param1", "param2"};
        ...
        public abstract double execute(int param1, Double param2);
        ...
    }

#### Factory Interfaces
Stateless Factory:

    public abstract class ExampleScript {
        public static final String[] PARAMETERS = new String[] {};
        ...
        public ExampleScript(long factoryParam1) {...}
        ...
        public abstract Object execute();
        ...
        public interface Factory {
            ExampleScript newInstance(long factoryParam1);
        }
        ...
    }

Stateful Factory:

    public abstract class ExampleScript {
        public static final String[] PARAMETERS = new String[] {};
        ...
        public ExampleScript(int fParam1, String sfParam1) {...}
        ...
        public abstract Object execute();
        ...
        public interface StatefulFactory {
            ExampleScript newInstance(String sfParam1);
        }

        public interface Factory {
            StatefulFactory newFactory(int fParam1);
        }
        ...
    }

#### Context
    public abstract class ExampleScript {
        ...
        public static final ScriptContext<Factory> CONTEXT = new ScriptContext<>("example", ExampleScript.Factory.class);
    }

#### Complete
    public abstract class ExampleScript {
        public static final String[] PARAMETERS = new String[] {"m"};
        private final String prefix;

        public ExampleScript(String prefix) {
            this.prefix = prefix;
        }

        public String getPrefix() {
            return prefix;
        }

        public abstract Object execute(Map<String, Object> m);

        public interface Factory {
            ExampleScript newInstance(String prefix);
        }

        public static final ScriptContext<Factory> CONTEXT = new ScriptContext<>("example", ExampleScript.Factory.class);
    }

### Build and Run
    Map<ScriptContext<?>, List<Whitelist>> whitelistMap = new HashMap<>();
    whitelistMap.put(ExampleScript.CONTEXT, Whitelist.BASE_WHITELISTS);
    PainlessScriptEngine scriptEngine = new PainlessScriptEngine(whitelistMap);

    Map<String, Object> m = new HashMap<>();
    m.put("key1", "value1");
    m.put("key2", 100);
    m.put("key3", 3.21);

    String source = "return prefix + m['key1']";
    ExampleScript.Factory factory = scriptEngine.compile("test_script", source, ExampleScript.CONTEXT, Collections.emptyMap());
    ExampleScript testScript = factory.newInstance("pre - ");

    testScript.execute(m) // result: "pre - value1"

Note: This is only using the basic whitelists. You can define additional lists to load for extended functionality, see WhitelistLoader.

#### Script Stack
In addition to regular Exception information, the ScriptException also includes the script stack:

    try {
        // typo
        String source = "return prefx + m['key1']";
        ExampleScript.Factory factory = scriptEngine.compile("test_script", source, ExampleScript.CONTEXT, Collections.emptyMap());
    } catch (ScriptException e) {
        e.getScriptStack().forEach(System.out::println);
        // output: "return prefx + m['key1']"
        //         "       ^---- HERE"
        ...
    }