## Server and client for the First East Disco Dance Floor.
Under new management!

### Overview:

The floor consists of eight segments, each with its own microcontroller.  The segments are connected via USB<->Serial adapters to two USB hubs, which are in turn connected to a Linux PC running the DDF server.  The server receives raw pixel data from the client over the network, and writes it to the modules.  The client generates its data from a selection of patterns, each consisting of a sequence of frames making up an animation loop.

### Use:

1.  Log into the PC via ssh at dancefloor.mit.edu.
2.	 Power cycle all modules.
3.	 Invoke the DDF server with start.sh, and check the output of the server to ensure no modules failed to connect.
4.	 Connect using the client (a wired network connection is recommended).
5.	 Select and play patterns.

### Technical Documentation:

Each of the 8 modules consists of a 16x4 array of pixels, giving the floor a resolution of 16x32.  Each pixel consists of a red, green and blue LED, each with 16 steps of intensity, giving a color depth of 12 bits/pixel.  Each module accepts frames of 97 bytes (including a 1-byte header), and can be redrawn in the neighborhood of 30 Hz.

Patterns are stored as raw binary data, corresponding to 16x32 images, but at a color depth of 24 bits/pixel.

### Recent changes:

* Modules no longer need to be plugged in in order.
* Existing DDF servers are now auto-killed on startup.
* Glitchy modules should attempt to recover, and can be power-cycled inflight.

### Limitations:

* Modules not connected at startup will never connect.  This is a limitation of the underlying javacomm library and is unlikely to be fixable.
