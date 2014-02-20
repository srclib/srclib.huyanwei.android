package srclib.huyanwei.bubble;

public class GSensorNative {
	static {
		System.loadLibrary("gsensorjni");
	}
	static native boolean opendev();
	static native boolean closedev();
	static native boolean calibrator(int delay,int num,int tolerance);	
}