package com.example.ngnsip;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class RegistrationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final String action = intent.getAction();
	    // Registration Event

	    if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
	      NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
	      if(args == null){
	        Log.d("DEBUG", "Invalid event args");
	        return;
	      }
	      switch(args.getEventType()){
	        case REGISTRATION_NOK:
	        	Toast.makeText(context, "Failed to register", Toast.LENGTH_SHORT).show();
	          Log.d("DEBUG", "Failed to register :(");
	          break;
	        case UNREGISTRATION_OK:
	          Log.d("DEBUG", "You are now unregistered :(");
	          break;
	        case REGISTRATION_OK:
	        	Toast.makeText(context, "Registered :-)", Toast.LENGTH_SHORT).show();
	          Log.d("DEBUG", "You are now registered :)");
	          break;
	        case REGISTRATION_INPROGRESS:
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
