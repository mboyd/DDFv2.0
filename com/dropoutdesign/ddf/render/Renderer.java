package com.dropoutdesign.ddf.render;

import com.dropoutdesign.ddf.*;

/**
 * Interface for classes that generate frames dynamically.
 */
public interface Renderer {
	
	/**
	 * Render and return a new frame.
	 * Renderes should expect calls to this method roughly in line with the
	 * framerate of the floor to which they are being drawn; however,
	 * this may not always be the case, for instance if the renderer is paused
	 * by the user.
	 */
	public byte[] drawFrame();
	
}