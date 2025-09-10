# Multithreaded TCP Hashtable Server-Client – Java Project

## Project Description
This project extends a basic TCP socket server-client system into a multithreaded server that can serve multiple clients concurrently. The server maintains a large hashtable (size 2^20) storing key-value pairs and listens for client commands to insert, delete, or search entries. Each client connection is handled in a separate thread, allowing simultaneous interactions.

## Features
- Thread-safe hashtable management using `ConcurrentHashMap`.
- Server accepts multiple client connections and spawns a dedicated thread (`ClientHandler`) for each.
- Client commands remain unchanged (insert, delete, search, disconnect) and communicate using simple comma-delimited ASCII commands.
- Server processes commands independently per client thread.
- Clean client disconnection with proper thread termination.

## Usage
1. Run the `Server` program first; it listens on TCP port 8888.
2. Run the `Client` program(s) to connect to the server at `localhost:8888`.
3. Enter commands in the format `A,B,C` to interact with the server:
   - `0,0` - Close connection.
   - `1,key,value` - Insert key-value.
   - `2,key` - Delete key.
   - `3,key` - Search key.
4. The server replies with success codes or requested values.
5. Multiple clients can connect and operate concurrently.

## Requirements
- Java 8 or later.
- No external libraries needed.

## File Structure
- `Server.java` – Multithreaded server implementation with client handler threads.
- `Client.java` – Client implementation (unchanged from previous version).
- `README.md` – This documentation.
