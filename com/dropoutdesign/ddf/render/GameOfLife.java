package com.dropoutdesign.ddf.render;

import com.dropoutdesign.ddf.*;

import java.util.Random;

public class GameOfLife implements Renderer {
	
	public int[][] grid;
	private int[][] nextGrid;
	
	private Random random;
	
	private int frameN;
	
	public static int WIDTH = 16;
	public static int HEIGHT = 32;
	
	private static int[][] adjacent = {{0,1}, {0,-1}, {1,1}, {1,0}, {1, -1}, 
										{-1,1}, {-1, 0}, {-1,-1}};
	
	public GameOfLife() {
		grid = new int[HEIGHT][WIDTH];
		nextGrid = new int[HEIGHT][WIDTH];
		
		frameN = 0;
	}
	
	public void reset() {
		random = new Random(System.currentTimeMillis());
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				grid[i][j] = 0;
			}
		}
		
		int n = random.nextInt(100) + 50;
		for (int b = 0; b < n; b++) {
			int i = random.nextInt(grid.length);
			int j = random.nextInt(grid[i].length);
			
			grid[i][j] = 1;
		}
		
		frameN = 0;
	}
	
	public void step() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				int state = grid[i][j];
				int neighbors = countNeighbors(i, j);
				
				if (state > 0) {
					if (neighbors < 2 || neighbors > 3) {
						nextGrid[i][j] = 0;
					} else {
						nextGrid[i][j] = 1;
					}
				} else {
					if (neighbors == 3) {
						nextGrid[i][j] = 1;
					} else {
						nextGrid[i][j] = 0;
					}
				}
			}
		}
	
		int[][] tmp = nextGrid;		// Swap buffers
		nextGrid = grid;
		grid = tmp;
		
		if (frameN++ > 75) {
			reset();
		}
	}
	
	public int countNeighbors(int i, int j) {
		int count = 0;
		for (int d = 0; d < adjacent.length; d++) {
			int ii = i + adjacent[d][0];
			int jj = j + adjacent[d][1];
			
			if (ii >= 0 && ii < grid.length) {
				if (jj >= 0 && jj < grid[ii].length) {
					
					if (grid[ii][jj] > 0) {
						count++;
					}
				}
			}
		}
		return count;
	}
	
	public byte[] render() {
		byte[] frame = new byte[WIDTH*HEIGHT*3];
	
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				
				int pix = ((i * WIDTH) + j) * 3;
				
				if (grid[i][j] > 0) {
					frame[pix] = (byte)random.nextInt(256);
					frame[pix+1] = (byte)random.nextInt(256);
					frame[pix+2] = (byte)random.nextInt(256);
				} else {
					frame[pix] = (byte)0x00;
					frame[pix+1] = (byte)0x00;
					frame[pix+2] = (byte)0x00;
				}
			}
		}
		return frame;
	}
	
	public byte[] drawFrame() {
		step();
		return render();
	}
	
	public static void main(String[] args) {
		try {
			long frametime = 1000 / 4;
			VirtualFloor floor = new VirtualFloor();
			floor.connect();
			GameOfLife game = new GameOfLife();
			while (true) {
				long t1 = System.currentTimeMillis();
				
				floor.drawFrame(game.render());
				game.step();
				
				long t2 = System.currentTimeMillis();
				long delta = t2 - t1;
				
				if (delta < frametime)
					Thread.sleep(frametime - delta);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}