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
As of version 0.0.2, there is now a GUI for the server that allows it to be run without a command line.
- Download SubwaveServer.jar.
- Go to the download location and double click the file. (Or, using a command line, type "java -jar SubwaveServer.jar")
- The server will start automatically and provide the IP and local port being used.
- Note: users behind a NAT or firewall will need to configure port forwarding and/or firewall exemptions manually.

## Client instructions
For the moment, the client must be executed using a command line.
- Download SubwaveClient.jar.
- Using a terminal or command prompt, navigate to the download directory.
- Launch the client using "java -jar SubwaveClient.jar *server_address* *port*
 - Server address and port are optional. If omitted, default settings will be used. (localhost 4444)
 - A server address may be entered without a port. Default settings will be used (<server_address> 4444)
 - A port may not be entered without a server address.
- At the prompt, type a nickname that you'd like to use.
- Once the connection is established, use any of the following commands: (Elements in **bold** are required, **italics** are optional)
 - \new *conversation_name* - Creates a new conversation and connects to it
 - \join *conversationID* - Joins a conversation. No ID is necessary if the last message was an invitation.
 - \invite **clientID** *conversationID* - Invites another user to the conversation.
 - \m or \msg *conversationID* **Message** - Sends a chat message to the conversation.
 - \me or \emote *conversationID* **Message** - Sends an emote to the conversation.
 - \r or \reply **Message** - Replies to the last conversation sent to or recieved from.
 - \quit - Exits the program

## Current version limitations
As of version 0.0.2 there remain a number of limitations and issues:
- Clients are not announced to connect
 - When new clients connect to the server, other clients are not notified.
 - This has a high priority and will be corrected very soon.
- No GUI for clients.
 - A prototype is currently in development and will be enabled in an upcoming release.
- openJDK 6 is not compatible with the prototype client GUI.
 - openJDK 6 support may be restored during development.
 - Oracle JDK 7 and Oracle JDK 8 are both supported.
 - Delete the directory ChatClient\src\ui\gui to restore openJDK 6 support.

See the issue tracker at [Waffle IO](https://waffle.io/tanndev/Subwave) for current progress information.

