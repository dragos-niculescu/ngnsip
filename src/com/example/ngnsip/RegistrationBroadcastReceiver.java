package com.example.ngnsip;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

	private MainActivity mActivity; 

	public RegistrationBroadcastReceiver(MainActivity m)
	{
		mActivity = m;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();
		// Registration Event
		TextView status; 

		if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
			NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if(args == null){
				Log.d("DEBUG", "Invalid event args");
				return;
			}
			status = (TextView)mActivity.findViewById(R.id.textview_reg );
			switch(args.getEventType()){
			case REGISTRATION_NOK:
				Toast.makeText(context, "Failed to register", Toast.LENGTH_SHORT).show();
				status.setText("Failed");
				Log.d("DEBUG", "Failed to register :(");
				break;
			case UNREGISTRATION_OK:

				status.setText("Unregistered");
				Log.d("DEBUG", "You are now unregistered :(");
				break;
			case REGISTRATION_OK:

				status.setText(Constants.USERNAME + "@" + Constants.DOMAIN);

				Log.d("DEBUG", "You are now registered :)");
				break;
			case REGISTRATION_INPROGRESS:
				status.setText("In progress");
				Log.d("DEBUG", "Trying to register...");
				break;
			case UNREGISTRATION_INPROGRESS:
				Log.d("DEBUG", "Trying to unregister...");
				break;
			case UNREGISTRATION_NOK:
				Log.d("DEBUG", "Failed to unregister :(");
				break;
			}

		}
	}

}
