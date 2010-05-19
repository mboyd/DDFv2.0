
import com.dropoutdesign.ddf.client.FloorWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;

public class SeqTest {
    
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
	byte[] pixels = new byte[width*height*3];
	while(true) {
	    for(int t=0;t<24;t++) {
		int b = 0;
		for(int y=0;y<height;y++) {
		    for(int x=0;x<width;x++) {
			for(int c=0;c<3;c++) {
			    pixels[b] = ((b%24)==t ? (byte)0xFF : (byte)0x00);
			    b++;
			}
		    }
		}
		writer.waitAndSend(pixels);
	    }
	}
    }
    
}
