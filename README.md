# LibArtNet [![Build Status](https://travis-ci.org/deltaeight/LibArtNet.svg?branch=master)](https://travis-ci.org/deltaeight/LibArtNet)

LibArtNet is a Java implementation of the [Art-Net 4](https://art-net.org.uk) protocol as maintained by 
[Artistic License Ltd.](https://artisticlicence.com)

**LibArtNet is still work in progress and not considered stable, therefore think twice if you want to use it in
production!**

However, see the [feature list](#features), the [roadmap](#roadmap) and how to [contribute](#contribute) for further 
information. If you feel there is something missing that is not listed there, feel free to open an issue.

## <a name="features">Features</a>

* Basic Art-Net receiver
* Basic Art-Net sender

### Supported Art-Net packets

* ArtDmx
* ArtPoll
* ArtPollReply

## <a name="roadmap">Roadmap</a>

Planned features are

* Art-Net controller, an abstraction layer using the existing receiver and sender to act as a console
* Art-Net node, an abstraction layer using the existing receiver and sender to act as a node
* RDM support 

## Installation

Currently, the only way to use LibArtNet is to clone this repository and compile it yourself. Publishing artifacts is 
planned but not implemented yet.

### Requirements

* Java 1.8 or higher
* Gradle

## Usage

All classes are documented using Javadoc. However, if there is something missing or unclear, feel free to open an issue.

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

For pull requests, please keep consistency with the existing code style and file headers. Please document your work
using Javadoc.

For bug reports, please include steps to reproduce, Java version and last tested commit SHA along with a description of 
what went wrong which should be as detailed as possible.

## License

LibArtNet is licensed under the MIT License. See [LICENSE.md](LICENSE.md) for details.