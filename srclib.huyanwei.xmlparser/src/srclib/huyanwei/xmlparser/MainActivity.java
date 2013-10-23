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
一般情况下，xml的解析结果最好放在一个实体类对象中，那样的话你使用起来非常方便（当然也更OO了）
你也可以选择其他的方法把解析结果保存下来，不过个人觉得这种方式是比较好的。
在解析过程中你需要做的是什么呢？这是解析的关键。
其实就是把要解析的结果设置给对象的属性（成员变量），
考虑到这点，那么肯定是需要知道对象有哪些属性啊，
那就给实体类加一个方法（其实这里是做一定的规范）用于获得属性。
知道了属性名以后下一步当然就是设置这些属性的值。
因为不同的实体类的属性不同，所以设置值采用反射机制。
大体上的思路就是这个样子。具体代码后面讲。
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
		
		XmlPullParser parser = Xml.newPullParser(); //由android.util.Xml创建一个XmlPullParser实例  
        try {
			parser.setInput(is, "UTF-8");
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}               //设置输入流 并指明编码方式 
	
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
          
        XmlSerializer serializer = Xml.newSerializer(); //由android.util.Xml创建一个XmlSerializer实例  
        StringWriter writer = new StringWriter();  
        serializer.setOutput(writer);   //设置输出方向为writer  
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
