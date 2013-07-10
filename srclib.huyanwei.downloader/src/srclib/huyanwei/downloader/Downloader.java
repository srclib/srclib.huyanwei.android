package srclib.huyanwei.downloader;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class Downloader 
{
	private final static String TAG  = "srclib.huyanwei.downloader.Downloader";	
	
	private static int paused    = 0;
	private static int stoped    = 0;
	
	public static int doDownloadTheFile(String url_string, String filePath,
		String filename, long size, Handler handler) {
	//file.size()即可得到原来下载文件的大小
	//下载路径
	String url = url_string;
	// 设置代理
	Header header = null;
	HttpResponse response = null;
	// 用来获取下载文件的大小
	HttpResponse response_detector = null;
	try {
		HttpClient client = new DefaultHttpClient();
		HttpClient client_detector = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		HttpGet request_detector = new HttpGet(url);
		if (header != null) {
			request.addHeader(header);
		}
		response_detector = client_detector.execute(request_detector);
		//获取需要下载文件的大小
		long fileSize = response_detector.getEntity().getContentLength();
		// 验证下载文件的完整性
		if (fileSize != 0 && fileSize == size) {
			return 1;
		}
		//设置下载的数据位置XX字节到XX字节
		Header header_size = new BasicHeader("Range", "bytes=" + size + "-"
				+ fileSize);
		request.addHeader(header_size);
		response = client.execute(request);
		InputStream is = response.getEntity().getContent();
		if (is == null) {
			throw new RuntimeException("stream is null");
		}
		
		//SDCardUtil.createFolder(filePath);		
		File file = new File(filePath);
		file.mkdirs();// 多级目录	
		//file.mkdir();//最后目录
		
		//获取文件对象，开始往文件里面写内容 
		File myTempFile = new File(filePath + "/" + filename);
		RandomAccessFile fos = new RandomAccessFile(myTempFile, "rw");
		//从文件的size以后的位置开始写入，其实也不用，直接往后写就可以。有时候多线程下载需要用
		fos.seek(size);
		byte buf[] = new byte[1024];
		long downloadfilesize = 0;
		do
		{
			if(paused == 1)
			{
				Thread.sleep(500);
			}
			else
			{	
				do {
					if(stoped ==1)
					{
						Message msg = new Message();
						msg.what = Action.ACTION_IN;
						msg.arg1 = Action.ACTION_ABORT;
						handler.sendMessage(msg);
						
						paused = 0 ;
						
						break;
					}
					Log.d(TAG,"downloadfilesize/fileSize = "+downloadfilesize+"/"+fileSize);
					int numread = is.read(buf);
					if (numread <= 0) {
						break;
					}
					fos.write(buf, 0, numread);
					if (handler != null) {
						Message msg = new Message();
						downloadfilesize += numread;
						double percent = (double) (downloadfilesize + size)
								/ fileSize;
						msg.what = Action.ACTION_IN;
						msg.arg1 = Action.ACTION_UPDATE;
						msg.arg2 = (int) ((int) (downloadfilesize + size)/fileSize);
						msg.obj = String.valueOf(percent);
						handler.sendMessage(msg);// 更新下载进度百分比
					}
				} while( paused == 0);
			}
		}while(paused == 1);
		is.close();
	} catch (Exception ex) {
		ex.printStackTrace();
		return -1;
	}
	return 1;
	}
	
	public static int doCancelDownloadTheFile(int pause , int stop)
	{		
		paused = pause;
		stoped = stop ;
		return 1;
	}
			
	
}