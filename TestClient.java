
import com.dropoutdesign.ddf.client.FloorWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

public class TestClient {
    
    public static void main(String args[]) throws IOException, UnknownHostException, AWTException {
	String serverHost;
	if (args.length < 1) {
	    serverHost = "localhost";
	}
	else {
	    serverHost = args[0];
	}

	FloorWriter writer = new FloorWriter(serverHost);
	int width = writer.getFloorWidth();
	int height = writer.getFloorHeight();
	/*for(int t=0;t<pixels.length;t++) {
	    int b = 0;
	    for(int y=0;y<height;y++) {
		for(int x=0;x<width;x++) {
		    for(int c=0;c<3;c++) {
			pixels[b] = (b==t ? (byte)0xFF : (byte)0x00);
			b++;
		    }
		}
	    }
	    writer.waitAndSend(pixels);
	    }*/
	Robot robot = new Robot(); // throws AWTException if can't use robot
	byte[] pixelBytes = new byte[width*height*3];
	//System.exit(0);
	//List frameTimes = new ArrayList();
	while (true) {
	    for (int i=0;i<3;i++) {
		//long startTime = System.currentTimeMillis();
		
		//BufferedImage img = robot.createScreenCapture(new Rectangle(4, 28, 16, 32));
		//int imgWidth = img.getWidth();
		//int imgHeight = img.getHeight();
		//int pixelArray[] = new int[imgWidth*imgHeight];
		//pixelArray = img.getRGB(0, 0, imgWidth, imgHeight, pixelArray, 0, imgWidth);
		// p = pixel in, b = byte out
		int b = 0;
		for(int p=0; p<pixelBytes.length/3; p++) {
		    //int pix = pixelArray[p];
		    for(int k=0;k<3;k++) {
			pixelBytes[b++] = (byte)((k==i?1:0)*0xff);
		    }
		    //pixelBytes[b++] = 0;//(byte)(pix>>16);
		    //pixelBytes[b++] = 0;//(byte)(pix>>8);
		    //pixelBytes[b++] = 0;//(byte)(pix);
		}
		
		/*frameTimes.add(new Integer((int)(System.currentTimeMillis() - startTime)));
		  if (frameTimes.size() >= 50) {
		  System.out.println(frameTimes);
		  frameTimes.clear();
		  }*/
		
		writer.waitAndSend(pixelBytes);	    
	    }
	}
    }
    
}
