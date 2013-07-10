package srclib.huyanwei.selfdestruct;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import android.provider.Telephony;
import static android.provider.Telephony.Intents.SECRET_CODE_ACTION;

public class SpecialBroadcastReceiver extends BroadcastReceiver
{
    // process *#*#05028888#*#*
    Uri self_destruct_uri = Uri.parse("android_secret_code://05028888");

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Uri uri = intent.getData();
		if (intent.getAction().equals(SECRET_CODE_ACTION))
		{
			if (uri.equals(self_destruct_uri))
			{	
				Log.i("SECRET_CODE","self_destruct_uri");
				Intent i = new Intent(Intent.ACTION_MAIN);
				i.setComponent(new ComponentName("srclib.huyanwei.selfdestruct","srclib.huyanwei.selfdestruct.MainActivity"));
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
			}
		}
	}
}
