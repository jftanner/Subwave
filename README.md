[![Stories in Ready](https://badge.waffle.io/tanndev/Subwave.png?label=ready&title=Ready)](https://waffle.io/tanndev/Subwave)
[![Build Status](https://travis-ci.org/tanndev/Subwave.svg?branch=master)](https://travis-ci.org/tanndev/Subwave)
# Subwave
Subwave is a simple conversation-based chat system similar to IRC.

## Implementation:
Many clients can connect to a single server to participate in the conversations on that server.
All messages are passed through the server; clients never connect directly to each other.
This allows the server to verify and approve all messages and means that client machines don't need to worry about
configuring any kind of port forwarding on their networks. Only the server needs to be reachable.

## Deployment:
There are two ways to download and deploy the application:
- Download the compiled executable-jars for the most recent version at the [releases page](www.github.com/tanndev/Subwave/releases)
 - Tagged commits are automatically deployed as github releases.
- Clone the repository using git and run the "deploy" task using Ant. (Advanced)

## Project Status:
This is an extremely early version and development is ongoing.
- See the [Waffle IO](https://waffle.io/tanndev/Subwave) board to see current issue status.
- See the [Travis CI](https://travis-ci.org/tanndev/Subwave) page to check the success of build tasks.

## Server Instructions
- Download SubwaveServer.jar.
- Go to the download location and double click the file. (Or, using a command line, type "java -jar SubwaveServer.jar")
- The server will start automatically and provide the IP and local port being used.
- Note: users behind a NAT or firewall will need to configure port forwarding and/or firewall exemptions manually.

## Client instructions
- Download SubwaveClient.jar.
- Go to the download location and double click the file. (Or, using a command line, type "java -jar SubwaveClient.jar")
- Enter the server address and port of the remote server, or press enter to use defaults. Use the form "address:port".
- Enter the nickname you'd like to use.
- To create a new conversation:
 - Click the "New Conversation" button.
 - Enter a name for the conversation.
 - You will be automatically added to the conversation.
- To invite another user to the conversation:
 - Select the desired user(s) in the "Other users on this server" list.
 - Click the "Invite to Conversation" button.
 - The selected user(s) will be automatically added to your current conversation.
- To change conversations:
 - Click the desired conversation.
 - Conversations with new messages will be highlighted in bold.

## Current version limitations
As of version 0.0.4 there remain a number of limitations and issues:
- A second server throws an exception if launched with the same port (Issue #34)
- Various TUI features are unimplemented or were broken with the GUI upgrade.
 - Some of the features will be implemented or repaired in a future release.
- openJDK 6 is not compatible with the client GUI
 - openJDK 6 support may be restored during development.
 - Oracle JDK 7 and Oracle JDK 8 are both supported.

See the issue tracker at [Waffle IO](https://waffle.io/tanndev/Subwave) for current progress information.

