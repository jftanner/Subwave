[![Stories in Ready](https://badge.waffle.io/tanndev/Subwave.png?label=ready&title=Ready)](https://waffle.io/tanndev/Subwave)
[![Build Status](https://travis-ci.org/tanndev/Subwave.svg?branch=master)](https://travis-ci.org/tanndev/Subwave)
# Subwave
Subwave is a simple conversation-based chat system similar to IRC.

Many clients can connect to a single server to participate in the conversations on that server.
All messages are passed through the server; clients never connect directly to each other.
This allows the server to verify and approve all messages and means that client machines don't need to worry about
configuring any kind of port forwarding on their networks. Only the server needs to be reachable.

# Server Instructions:
As of version 0.0.2, there is now a GUI for the server that allows it to be run without a command line.
- Download SubwaveServer.jar.
- Go to the download location and double click the file. (Or, using a command line, type "java -jar SubwaveServer.jar")
- The server will start automatically and provide the IP and local port being used.
- Note: users behind a NAT or firewall will need to configure port forwarding and/or firewall exemptions manually.

# Client instructions:
As of version 0.0.2, the client must be executed in a command line.
- Download SubwaveClient.jar.
- Using a terminal or command prompt, navigate to the download directory.
- Type "java -jar SubwaveClient.jar"
- At the prompt, type a nickname that you'd like to use.
