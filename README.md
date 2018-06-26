# Instant Netty startup using GraalVM's Native Image Generation

The purpose of this repo is to walk you through generating a native executable image from a simple [Netty](http://netty.io/) application using the [GraalVM](http://graalvm.org/) [`native-image`](http://www.graalvm.org/docs/reference-manual/aot-compilation/) tool.

### Setting up the development environment

To set up your development environment you first need to [download GraalVM](http://www.graalvm.org/downloads/). Either the Comunity Edition or the Enterprise Edition will work for the purpose of this example. The GraalVM download contains a full JVM plus few other utilities like the `native-tool`. Then you need to set your `JAVA_HOME` to point to GraalVM:
```
$ export JAVA_HOME=<graalvm-download-location>/graalvm-1.0.0-rc1
```

Then you need to add GraalVM to your path:
```
$ export PATH=$JAVA_HOME/bin:$PATH
```
 
Now you can run the `native-image` tool:
```
$ native-image --help

GraalVM native-image building tool

This tool can be used to generate an image that contains ahead-of-time compiled Java code.
...
```

Alternativelly, you could build `native-image` from source following the [quick start guide](https://github.com/oracle/graal/tree/master/substratevm#quick-start).

For compilation native-image depends on the local toolchain, so please make sure: `glibc-devel`, `zlib-devel` (header files for the C library and `zlib`) and `gcc` are available on your system. On the OS that this demo was tested, Ubuntu 16.04,  the following command was required to install `zlib-devel`, the rest of the dependencies being installed out-of-the-box:
```
$ sudo apt-get install zlib1g-dev
```

### Setting up the project

For the purpose of this demo we copied the `HttpHelloWorldServer` example from the [netty examples repo](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/helloworld).

The example is built with Maven. But before we build we need to install `svm.jar` in the local Maven repository since the projects `pom.xml` file depends on it. The `svm.jar` library contains all the code needed to compile the substitutions required for Netty.
```
$ mvn install:install-file -Dfile=${JAVA_HOME}/jre/lib/svm/builder/svm.jar -DgroupId=com.oracle.substratevm -DartifactId=svm -Dversion=GraalVM-1.0.0-rc1 -Dpackaging=jar
```
The other required library, `graal-sdk.jar`, is automatically added to the classpath when you run the `javac` command shipped with GraalVM.

Now you can build with:
```
$ mvn clean package
```
This will create a jar file with all dependencies embedded.

#### Run Netty on JVM

On the regular JVM you can run as usual using the `java` command:
```
$ java -jar target/netty-svm-httpserver-full.jar
Open your web browser and navigate to http://127.0.0.1:8080/
```

#### Run Netty with GraalVM Native

To build the native image we use the native-image tool:
```
$ native-image -jar target/netty-svm-httpserver-full.jar -H:ReflectionConfigurationResources=netty_reflection_config.json -H:Name=netty-svm-http-server
Build on Server(pid: 29456, port: 26681)
   classlist:     194.15 ms
       (cap):     468.11 ms
       setup:     626.51 ms
  (typeflow):   3,709.95 ms
   (objects):   2,402.43 ms
  (features):      42.58 ms
    analysis:   6,274.84 ms
    universe:     141.76 ms
     (parse):     310.85 ms
    (inline):     658.15 ms
   (compile):   1,782.15 ms
     compile:   3,055.74 ms
       image:     484.51 ms
       write:     132.26 ms
     [total]:  10,936.45 ms
```
This creates an executable file that is less than 9 MB in size:
```
$ ls -Gg --block-size=k netty-svm-http-server
-rwxrwxr-x 1 8330K May  8 23:33 netty-svm-http-server
```
We can now run the executable:
```
$ ./netty-svm-http-server
Open your web browser and navigate to http://127.0.0.1:8080/
```
