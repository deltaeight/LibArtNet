# LibArtNet [![Maven Central](https://img.shields.io/maven-central/v/de.deltaeight/LibArtNet.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22de.deltaeight%22%20AND%20a:%22LibArtNet%22) [![Javadocs](http://www.javadoc.io/badge/de.deltaeight/LibArtNet.svg?color=blue)](http://www.javadoc.io/doc/de.deltaeight/LibArtNet) [![Build Status](https://travis-ci.org/deltaeight/LibArtNet.svg?branch=master)](https://travis-ci.org/deltaeight/LibArtNet)

LibArtNet is a Java implementation of the [Art-Net 4](https://art-net.org.uk) protocol as maintained by 
[Artistic License Ltd.](https://artisticlicence.com)

**Please have in mind that LibArtNet is still in beta.**

However, see the [feature list](#features), the [roadmap](#roadmap) and how to [contribute](#contribute) for further 
information. If you feel there is something missing that is not listed there, feel free to open an issue.

## <a name="features">Features</a>

* Basic Art-Net receiver
* Basic Art-Net sender

### Supported Art-Net packets

* ArtDmx
  * Sequence number
  * 15bit universe addressing 
* ArtPoll
  * unicast
  * sending `ArtPollReply` on state changes
  * priorities for diagnostic data
* ArtPollReply
  * macros
  * remotes
  * OEM codes (enum with all codes available)
  * short/long names
  * equipment styles
  * much more
* ArtTimeCode
  * 24/25/29,97/30 fps

## <a name="roadmap">Roadmap</a>

Planned features are

* Art-Net controller, an abstraction layer using the existing receiver and sender to act as a console
* Art-Net node, an abstraction layer using the existing receiver and sender to act as a node
* RDM support 

## Usage

All classes are documented using [Javadoc](https://javadoc.io/doc/de.deltaeight/LibArtNet). However, if there is 
something missing or unclear, feel free to open an issue.

### Requirements

* Java 1.8 or higher
* Gradle if you want to compile from source

### Installation

LibArtNet is available on Maven Central and on the [release page](../../releases).

#### Maven

Add this dependency to `pom.xml`:

```xml
<dependency>
  <groupId>de.deltaeight</groupId>
  <artifactId>LibArtNet</artifactId>
  <version>1.0-beta</version>
</dependency>
```

#### Gradle

Add this to `build.gradle`;

```groovy
dependencies {
    implementation 'de.deltaeight:LibArtNet:1.0-beta'
}
```

### Compiling from source

To build a `.jar` file to use in your IDE, run

```bash
./gradlew :lib:jar 
```

### Usage examples

#### Using the receiver

The receiver needs receive handlers which are called when the appropriate packet is received:

```java
ArtNetReceiver receiver = new ArtNetReceiver()
    .withArtDmxReceiveHandler(packet -> System.out.println("Channel 63 value: " + packet.getData()[62]));

receiver.start();

// Do other stuff

receiver.stop();
```

#### Using the sender

The sender needs Art-Net packets to send, therefore we need a builder instance for the desired packets first:

```java
ArtPollReplyBuilder builder = new ArtPollReplyBuilder()
        .withOemCode(OemCode.OemRobertJulDalis1)
        .withBindIp(new byte[]{127, 0, 0, 1});

ArtNetSender sender = new ArtNetSender();
artNetSender.start();

sender.send(InetAddress.getByName("127.0.0.1"), builder.build());

// Do other stuff

sender.stop();
```

## <a name="contribute">Contribute</a>

Your contribution is more than welcome!

If you'd like to contribute, every help is much appreciated. Feel free to fork, create pull requests and open issues for
bugs or feature requests.

For bug reports, feature requests and pull requests there are templates that you can just fill out to provide us with 
the required information.

Please have a look at the [contribution guide](CONTRIBUTING.md) and the  [code of conduct](CODE_OF_CONDUCT.md) before 
you contribute.

## License

LibArtNet is licensed under the MIT License. See [LICENSE.md](LICENSE.md) for details.
