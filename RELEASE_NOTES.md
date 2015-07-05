# v0.0.4
## New Features:
- Conversation switching in the client GUI (Issue #41)
- Conversations are now highlighted when they have new messages.

## Fixes:
- Conversation list no longer can get duplicates with repeated invites. (Issue #39)

** Known Issues
- A second server throws an exception if launched with the same port (Issue #34)
- Various TUI features unimplemented or broken with GUI upgrade

# v0.0.3
## New Features:
- Client GUI added

## Fixes:
- Clients are now announced when they connect/disconnect
- The GUI no longer requires any command line arguments
- Client accepts "address:port" strings to connect

## In Progress
- Conversation switching in the client GUI (Issue #41)

** Known Issues
- Clients can receive invites to conversations they are already members of (Issue #39)
- A second server throws an exception if launched with the same port (Issue #34)
- Various TUI features unimplemented or broken with GUI upgrade

# v0.0.2
## Fixes:
- Server GUI added (Client still requires command line)
- Documentation now built by ant and deployed by Travis-CI
- Travis deployments only on

## In Progress:
- Client GUI under construction

## Known Issues:
- Clients are not announced to connect
- No longer compatible with openJDK 6

# v0.0.1
- Initial Release