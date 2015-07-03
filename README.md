[![Stories in Ready](https://badge.waffle.io/tanndev/Subwave.png?label=ready&title=Ready)](https://waffle.io/tanndev/Subwave)
[![Build Status](https://travis-ci.org/tanndev/Subwave.svg?branch=master)](https://travis-ci.org/tanndev/Subwave)
# Subwave
Subwave is a simple conversation-based chat system similar to IRC.

Many clients can connect to a single server to participate in the conversations on that server.
All messages are passed through the server; clients never connect directly to each other.
This allows the server to verify and approve all messages and means that client machines don't need to worry about
configuring any kind of port forwarding on their networks. Only the server needs to be reachable.