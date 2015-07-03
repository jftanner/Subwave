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
- Launch the client using "java -jar SubwaveClient.jar [server address] [port]
-- Server address and port are optional. If omitted, default settings will be used. (localhost 4444)
-- A server address may be entered without a port. Default settings will be used (<server_address> 4444)
-- A port may not be entered without a server address.
- At the prompt, type a nickname that you'd like to use.
- Once the connection is established, use any of the following commands: (Elements in <> are required, [] are optional)
-- \new [conversation name] - Creates a new conversation and connects to it
-- \join [conversationID] - Joins a conversation. No ID is necessary if the last message was an invitation.
-- \invite <clientID> [conversationID] - Invites another user to the conversation.
-- \m or \msg [conversationID] <Message> - Sends a chat message to the conversation.
-- \me or \emote [conversationID] <Message> - Sends an emote to the conversation.
-- \r or \reply <Message> - Replies to the last conversation sent to or recieved from.
-- \quit - Exits the program

