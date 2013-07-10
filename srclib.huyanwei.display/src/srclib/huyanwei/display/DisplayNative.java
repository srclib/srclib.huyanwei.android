package srclib.huyanwei.display;

public class DisplayNative {
	static 
	{
		System.loadLibrary("display_jni");
	};	
	public static native int get_framebuffer_info_init();
	public static native int get_framebuffer_info_width();
	public static native int get_framebuffer_info_height();
	public static native int get_framebuffer_info(int[] width,int[] height);
	public static native int get_framebuffer_info_deinit();
}
