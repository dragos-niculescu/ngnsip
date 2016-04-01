package com.example.ngnsip;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private NgnEngine mEngine;
	private INgnSipService mSipService;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	private CallStateReceiver callStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		Log.i("DEBUG", "here0");
		 
	mEngine = NgnEngine.getInstance();
	if(mEngine == null)
		Log.i("DEBUG", "mEngine null" );
	Log.i("DEBUG", "here");
	mSipService = mEngine.getSipService();
	  
	  
	  // Register broadcast receivers
	 
	  
	  regBroadcastReceiver = new RegistrationBroadcastReceiver();
	  final IntentFilter intentFilter = new IntentFilter();
	  intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
	  registerReceiver(regBroadcastReceiver, intentFilter);
	  // Incoming call broadcast receiver

	  final IntentFilter intentRFilter = new IntentFilter();
	  callStateReceiver = new CallStateReceiver();
	  intentRFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
	  registerReceiver(callStateReceiver, intentRFilter);
	

	
		
		Button button = (Button)findViewById(R.id.button_register );
		button.setOnClickListener(new Button.OnClickListener(){	
	          @Override
	          public void onClick(View view) {
	        	  configure_stack(); 
	        	  initializeManager();
	        	  Log.i("DEBUG", "here2");
	        	  Toast.makeText(MainActivity.this, "You have clicked!", Toast.LENGTH_SHORT).show();
	          }
	        });
		
		Button buttoncall = (Button)findViewById(R.id.button_call );
		buttoncall.setOnClickListener(new Button.OnClickListener(){	
	          @Override
	          public void onClick(View view) {
		
		  final String validUri = NgnUriUtils.makeValidSipUri(
			      String.format("sip:%s@%s", "echo", "conference.sip2sip.info"));
			    if(validUri == null){
			      Log.e("DEBUG", "Invalid number");
			      return;
			    }
			    
			    NgnAVSession avSession = NgnAVSession.createOutgoingSession(
			      NgnEngine.getInstance().getSipService().getSipStack(), NgnMediaType.Audio);
			    if(avSession.makeCall(validUri)){ 
			    	Log.d("DEBUG", "Call OK");
			    } else {
			    	Log.d("DEBUG", "Call failed");
			    }
	          }
		 });
				    
	}

	
	public void configure_stack()
	{
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService
		            = mEngine.getConfigurationService();
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, "getonsip_eim");
		                                   // "sip_username");
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", "eim", "getonsip.com")); 
		        //String.format("sip:%s@%s", "sip_username", "sip_domain"));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, "gEyBBh7vn6svrGWD");
		                                   // "sip_password");
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, "sip.onsip.com"); 
		                                    //"sip_server_host");
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, 5060);
		                                    //"sip_server_port");
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, "getonsip.com");
		                                    //"sip_domain");
		// By default, using 3G for calls disabled

		mConfigurationService.putBoolean(NgnConfigurationEntry.NETWORK_USE_3G,
		                                    true);
		// You may want to leave the registration timeout to the default 1700 seconds

		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_REGISTRATION_TIMEOUT,
		                                3600);
		mConfigurationService.commit();
	}

public void initializeManager() {
	if(!mEngine.isStarted()){
		    if(!mEngine.start()){
		      Log.e("NGN", "Failed to start the engine :(");
		      return;
		    }
		  }
		  
		  // Register

		  if(!mSipService.isRegistered()){
		    mSipService.register(this);
		  }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
