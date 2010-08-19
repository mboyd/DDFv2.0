import com.dropoutdesign.ddf.*;
import com.dropoutdesign.ddf.module.*;

public class TestModule {
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: TestModule <module1> <module2> ...");
		}
		
		for (String module : args) {
			if (module.indexOf("/dev") == -1) {
				module = "/dev/" + module;
			}
			
			ModuleConfig mc = new ModuleConfig();
			mc.address = module;
			mc.origin = "(0,0)";
			mc.size = "(16,4)";
			
			Module 
			
		}
	}
}