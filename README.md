# Instant Netty startup using GraalVM's Native Image Generation

The purpose of this repository is to walk you through generating a native executable image from a simple [Netty](http://netty.io/) application using the [GraalVM](http://graalvm.org/) [`native-image`](http://www.graalvm.org/docs/reference-manual/aot-compilation/) tool.

### Setting up the development environment

To set up your development environment you first need to [download GraalVM](http://www.graalvm.org/downloads/). Either the Community Edition or the Enterprise Edition works for the purpose of this example. Note that after downloading a GraalVM release, the `native-image` tool [needs to be installed](https://www.graalvm.org/docs/getting-started/#native-images) using `gu install native-image`. Then you need to set your `JAVA_HOME` to point to GraalVM.

Now you can run the `native-image` tool:
```
> $JAVA_HOME/bin/native-image --help

GraalVM native-image building tool

This tool can be used to generate an image that contains ahead-of-time compiled Java code.
...
```

Alternatively, you could build `native-image` from source following the [quick start guide](https://github.com/oracle/graal/tree/master/substratevm#quick-start).

For compilation native-image depends on the local toolchain, so please make sure: `glibc-devel`, `zlib-devel` (header files for the C library and `zlib`) and `gcc` are available on your system. On the OS that this demo was tested, Ubuntu 16.04, the following command was required to install `zlib-devel`, the rest of the dependencies being installed out-of-the-box:
```
$ sudo apt-get install zlib1g-dev
```

### Setting up the project

For the purpose of this demo we copied the `HttpHelloWorldServer` example from the [netty examples repo](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/http/helloworld).

The example is built with Maven. The `pom.xml` file declares an `svm.jar` dependency:
```
<dependency>
  <groupId>com.oracle.substratevm</groupId>
  <artifactId>svm</artifactId>
  <version>19.2.0</version>
  <scope>provided</scope>
</dependency>
```
The `svm.jar` library contains all the code needed to compile the substitutions required for Netty.
The other required library, `graal-sdk.jar`, is automatically added to the classpath when you run the `javac` command shipped with GraalVM.

Now you can build with:
```
$ mvn clean package
```
This will create a jar file with all dependencies embedded.

#### Run Netty on JVM

On the regular JVM you can run as usual using the `java` command:
```
> $JAVA_HOME/bin/java -jar target/netty-svm-httpserver-full.jar
Open your web browser and navigate to http://127.0.0.1:8080/
```

#### Run Netty with GraalVM Native Image

To build the native image we use the `native-image` tool:
```
> $JAVA_HOME/bin/native-image -jar target/netty-svm-httpserver-full.jar
Build on Server(pid: 17238, port: 45712)
[netty-svm-httpserver-full:17238]    classlist:     826.59 ms
[netty-svm-httpserver-full:17238]        (cap):     749.50 ms
[netty-svm-httpserver-full:17238]        setup:   1,375.75 ms
[netty-svm-httpserver-full:17238]   (typeflow):   5,834.62 ms
[netty-svm-httpserver-full:17238]    (objects):   5,493.09 ms
[netty-svm-httpserver-full:17238]   (features):     222.06 ms
[netty-svm-httpserver-full:17238]     analysis:  11,892.85 ms
[netty-svm-httpserver-full:17238]     (clinit):     188.83 ms
[netty-svm-httpserver-full:17238]     universe:     433.88 ms
[netty-svm-httpserver-full:17238]      (parse):     519.48 ms
[netty-svm-httpserver-full:17238]     (inline):   1,110.96 ms
[netty-svm-httpserver-full:17238]    (compile):  10,625.70 ms
[netty-svm-httpserver-full:17238]      compile:  12,985.27 ms
[netty-svm-httpserver-full:17238]        image:     910.28 ms
[netty-svm-httpserver-full:17238]        write:     185.13 ms
[netty-svm-httpserver-full:17238]      [total]:  28,682.93 ms
```
This creates an executable file that is abut 11 MByte in size:
```
> ls -Gg --block-size=k netty-svm-httpserver-full
-rwxrwxr-x 1 11328K Aug 26 20:48 netty-svm-httpserver-full
```

We can now run the executable:

```
> ./netty-svm-httpserver-full
Open your web browser and navigate to http://127.0.0.1:8080/
```
