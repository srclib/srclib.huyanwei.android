package srclib.huyanwei.fragmentview;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

//import android.support.v4.app.Fragment;
import android.view.Menu;

public class MainActivity extends Activity {

	FragmentManager mFragmentManager;
	FragmentTransaction mFragmentTransaction;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mFragmentManager = getFragmentManager();
		
		mFragmentTransaction = mFragmentManager.beginTransaction();
		
		FragmentOne mFragmentOne = new FragmentOne();
		mFragmentTransaction.add(R.id.linearLayout1, (Fragment)mFragmentOne);		
		mFragmentTransaction.addToBackStack(null);
		
		FragmentTwo mFragmentTwo = new FragmentTwo();
		mFragmentTransaction.add(R.id.linearLayout2, (Fragment)mFragmentTwo);
		mFragmentTransaction.addToBackStack(null);
		
		mFragmentTransaction.commit();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
