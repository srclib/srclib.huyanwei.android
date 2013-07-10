package com.android.downloader;

import java.io.File;

import com.android.network.DownloadProgressListener;
import com.android.network.FileDownloader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private EditText downloadpathText;
    private TextView resultView;
    private ProgressBar progressBar;
    
    /**
     * ��Handler��������������������ĵ�ǰ�̵߳���Ϣ���У�������������Ϣ���з�����Ϣ
     * ��Ϣ�����е���Ϣ�ɵ�ǰ�߳��ڲ����д���
     * ʹ��Handler����UI������Ϣ��
     */
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {			
			switch (msg.what) {
			case 1:				
				progressBar.setProgress(msg.getData().getInt("size"));
				float num = (float)progressBar.getProgress()/(float)progressBar.getMax();
				int result = (int)(num*100);
				resultView.setText(result+ "%");
				
				//��ʾ���سɹ���Ϣ
				if(progressBar.getProgress()==progressBar.getMax()){
					Toast.makeText(MainActivity.this, R.string.success, 1).show();
				}
				break;
			case -1:
				//��ʾ���ش�����Ϣ
				Toast.makeText(MainActivity.this, R.string.error, 1).show();
				break;
			}
		}
    };
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        downloadpathText = (EditText) this.findViewById(R.id.path);
        progressBar = (ProgressBar) this.findViewById(R.id.downloadbar);
        resultView = (TextView) this.findViewById(R.id.resultView);
        Button button = (Button) this.findViewById(R.id.button);
        
        button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String path = downloadpathText.getText().toString();
				System.out.println(Environment.getExternalStorageState()+"------"+Environment.MEDIA_MOUNTED);
				
				if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
					//��ʼ�����ļ�
					download(path, Environment.getExternalStorageDirectory());
				}else{
					//��ʾSDCard������Ϣ
					Toast.makeText(MainActivity.this, R.string.sdcarderror, 1).show();
				}
			}
		});
    }
    
  	/**
  	 * ���߳�(UI�߳�)
  	 * ������ʾ�ؼ��Ľ������ֻ����UI�̸߳���������ڷ�UI�̸߳��¿ؼ�������ֵ�����º����ʾ���治�ᷴӳ����Ļ��
  	 * ������ø��º����ʾ���淴ӳ����Ļ�ϣ���Ҫ��Handler���á�
  	 * @param path
  	 * @param savedir
  	 */
    private void download(final String path, final File savedir) {
    	new Thread(new Runnable() {			
			@Override
			public void run() {
				//����3���߳̽�������
				FileDownloader loader = new FileDownloader(MainActivity.this, path, savedir, 3);
		    	progressBar.setMax(loader.getFileSize());//���ý����������̶�Ϊ�ļ��ĳ���
		    	
				try {
					loader.download(new DownloadProgressListener() {
						@Override
						public void onDownloadSize(int size) {//ʵʱ��֪�ļ��Ѿ����ص����ݳ���
							Message msg = new Message();
							msg.what = 1;
							msg.getData().putInt("size", size);
							handler.sendMessage(msg);//������Ϣ
						}
					});
				} catch (Exception e) {
					handler.obtainMessage(-1).sendToTarget();
				}
			}
		}).start();
	}
}