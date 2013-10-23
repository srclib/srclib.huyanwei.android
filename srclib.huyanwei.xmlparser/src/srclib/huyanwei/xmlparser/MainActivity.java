package srclib.huyanwei.xmlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

//import android.R;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;

/****
һ������£�xml�Ľ��������÷���һ��ʵ��������У������Ļ���ʹ�������ǳ����㣨��ȻҲ��OO�ˣ�
��Ҳ����ѡ�������ķ����ѽ�����������������������˾������ַ�ʽ�ǱȽϺõġ�
�ڽ�������������Ҫ������ʲô�أ����ǽ����Ĺؼ���
��ʵ���ǰ�Ҫ�����Ľ�����ø���������ԣ���Ա��������
���ǵ���㣬��ô�϶�����Ҫ֪����������Щ���԰���
�Ǿ͸�ʵ�����һ����������ʵ��������һ���Ĺ淶�����ڻ�����ԡ�
֪�����������Ժ���һ����Ȼ����������Щ���Ե�ֵ��
��Ϊ��ͬ��ʵ��������Բ�ͬ����������ֵ���÷�����ơ�
�����ϵ�˼·����������ӡ����������潲��
*****/

public class MainActivity extends Activity {

	private Resources mResources ;	
	private XmlResourceParser mXmlResourceParser;
	private String TAG = "srclib.huyanwei.xmlparser";
	
	private List<ItemObject> mItemObjectList;
	private ItemObject       mItemObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		mResources = this.getResources();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	// read xml string
	public List<ItemObject> parser(InputStream is) throws XmlPullParserException, IOException
	{
		ArrayList<ItemObject> mObjectArray = null ;
		ItemObject mItemObject = null;
		
		XmlPullParser parser = Xml.newPullParser(); //��android.util.Xml����һ��XmlPullParserʵ��  
        try {
			parser.setInput(is, "UTF-8");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}               //���������� ��ָ�����뷽ʽ 
	
        int eventType = 0;
		eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
            	mObjectArray = new ArrayList<ItemObject>();  
                break;  
            case XmlPullParser.START_TAG:  
                if (parser.getName().equals("book")) {  
                	mItemObject = new ItemObject();  
                } else if (parser.getName().equals("id")) { 
					eventType = parser.next();
                    mItemObject.setId(Integer.parseInt(parser.getText()));  
                } else if (parser.getName().equals("name")) {  
                    eventType = parser.next();  
                    mItemObject.setName(parser.getText());  
                } else if (parser.getName().equals("price")) { 
                    eventType = parser.next();  
                    mItemObject.setPrice(Float.parseFloat(parser.getText()));  
                }
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("book")) {  
                	mObjectArray.add(mItemObject);
                    mItemObject = null;  
                }
                break;  
            }
			eventType = parser.next();
        }
        return mObjectArray;
	}
	
	// write xml string
    public String serialize(List<ItemObject> mObjectArray) throws Exception {  
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlSerializer serializer = factory.newSerializer();  
          
        XmlSerializer serializer = Xml.newSerializer(); //��android.util.Xml����һ��XmlSerializerʵ��  
        StringWriter writer = new StringWriter();  
        serializer.setOutput(writer);   //�����������Ϊwriter  
        serializer.startDocument("UTF-8", true);  
        serializer.startTag("", "books");  
        for (ItemObject book : mObjectArray) {  
            serializer.startTag("", "book");  
            serializer.attribute("", "id", book.getId() + "");  
              
            serializer.startTag("", "name");  
            serializer.text(book.getName());  
            serializer.endTag("", "name");  
              
            serializer.startTag("", "price");  
            serializer.text(book.getPrice() + "");  
            serializer.endTag("", "price");  
              
            serializer.endTag("", "book");  
        }  
        serializer.endTag("", "books");  
        serializer.endDocument();  
          
        return writer.toString();  
    }
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		//mXmlResourceParser = mResources.getXml(R.xml.data);
		
		try {
			InputStream is = mResources.getAssets().open("data.xml");		
			mItemObjectList = this.parser(is);			
			for (ItemObject mItemObject : mItemObjectList)
			{  
                Log.i(TAG, mItemObject.toString());  
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
