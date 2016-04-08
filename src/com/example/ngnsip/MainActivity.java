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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private NgnEngine mEngine = null;
	public INgnSipService mSipService = null;
	private IntentFilter regIntentFilter; 
	private IntentFilter callIntentFilter;
	private RegistrationBroadcastReceiver regBroadcastReceiver;
	private CallStateReceiver callStateReceiver;
	public NgnAVSession mSession = null; 
	String destinationAddr = "";

	public static MainActivity instance;
	public MainActivity getInstance(){
		return instance;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		instance = this;

		mEngine = NgnEngine.getInstance();
		if(mEngine == null)
			Log.i("DEBUG", "mEngine null" );

		mSipService = mEngine.getSipService();


		// Register broadcast receivers


		regBroadcastReceiver = new RegistrationBroadcastReceiver(this);
		regIntentFilter = new IntentFilter();
		regIntentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
		registerReceiver(regBroadcastReceiver, regIntentFilter);
		// Incoming call broadcast receiver

		callIntentFilter = new IntentFilter();
		callStateReceiver = new CallStateReceiver(this);
		callIntentFilter.addAction(NgnInviteEventArgs.ACTION_INVITE_EVENT);
		registerReceiver(callStateReceiver, callIntentFilter);


		Button buttonchat = (Button)findViewById(R.id.button_startchat );
		buttonchat.setOnClickListener(new Button.OnClickListener(){	
			@Override 
			public void onClick(View view) { 	        	  
				Intent intent = new Intent (getApplicationContext() , ChatActivity.class);
				EditText dest = (EditText)findViewById(R.id.editText_destination);
				intent.putExtra("SIPADDR", dest.getText().toString());
				startActivity(intent);
			}
		});


		Button button = (Button)findViewById(R.id.button_register );
		button.setOnClickListener(new Button.OnClickListener(){	
			@Override
			public void onClick(View view) {
				configure_stack(); 
				initializeManager();
			}
		});

		Button buttonUnreg = (Button)findViewById(R.id.button_unregister );
		buttonUnreg.setOnClickListener(new Button.OnClickListener(){	
			@Override
			public void onClick(View view) {
				mSipService.unRegister();
			}
		});

		Button buttoncall = (Button)findViewById(R.id.button_call );
		buttoncall.setOnClickListener(new Button.OnClickListener(){	
			@Override
			public void onClick(View view) {
				EditText sipaddr = (EditText)findViewById(R.id.editText_destination );
				final String validUri = NgnUriUtils.makeValidSipUri(sipaddr.getText().toString());
				//String.format("sip:%s@%s", "echo", "conference.sip2sip.info"));
				if(validUri == null){
					Log.e("DEBUG", "Invalid number");
					return;
				}
				if(!mEngine.isStarted() || !mSipService.isRegistered()){
					Log.e("DEBUG", "not registered");
					return;
				}
				mSession = NgnAVSession.createOutgoingSession(
						NgnEngine.getInstance().getSipService().getSipStack(), NgnMediaType.Audio);
				if(mSession.makeCall(validUri)){
					TextView status = (TextView)findViewById(R.id.textView_callstatus);
					status.setText("Calling...");
					Log.d("DEBUG", "Call OK");
				} else {
					Log.d("DEBUG", "Call failed");
				}
			}
		});

		Button BtHangUp = (Button)findViewById(R.id.button_hangup);

		BtHangUp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSession != null){
					mSession.hangUpCall();
					Log.d("DEBUG", "Hangup");
				}
			}
		});


		Button BtSendDTMF = (Button)findViewById(R.id.button_send_dtmf );
		BtSendDTMF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSession != null){
					final String textToSend = "#";
					int char2send = ((EditText)findViewById(R.id.editText_dtmf)).getText().toString().charAt(0);

					if(char2send >= '0' && char2send <= '9')
						char2send -= '0';
					if(char2send == '#')
						char2send = 11;
					if(char2send == '*')
						char2send = 10;
					if(!mSession.sendDTMF( char2send )){
						Log.e("DEBUG","Failed to send DTMF " + char2send);
					}
					else{
						Log.d("DEBUG", char2send + ": DTMF sent");
					}
				}
			}
		});


	}


	public void configure_stack()
	{
		NgnEngine mEngine = NgnEngine.getInstance();
		INgnConfigurationService mConfigurationService
		= mEngine.getConfigurationService();
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPI, Constants.IDENTITY_IMPI);
		// "sip_username");
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_IMPU, String.format("sip:%s@%s", Constants.USERNAME, Constants.DOMAIN)); 
		//String.format("sip:%s@%s", "sip_username", "sip_domain"));
		mConfigurationService.putString(NgnConfigurationEntry.IDENTITY_PASSWORD, Constants.IDENTITY_PASSWORD);
		// "sip_password");
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_PCSCF_HOST, Constants.NETWORK_PCSCF_HOST); 
		//"sip_server_host");
		mConfigurationService.putInt(NgnConfigurationEntry.NETWORK_PCSCF_PORT, Constants.NETWORK_PCSCF_PORT);
		//"sip_server_port");
		mConfigurationService.putString(NgnConfigurationEntry.NETWORK_REALM, Constants.NETWORK_REALM);
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
	protected void onResume() {
		super.onResume();
		Log.i("DEBUG", "OnResume");
		//  registerReceiver(regBroadcastReceiver, regIntentFilter);
		//  registerReceiver(callStateReceiver, callIntentFilter);
		//  registerReceiver(textReceiver, textIntentFilter);
	}

	@Override
	protected void onPause() {
		Log.i("DEBUG", "OnPause");
		//	unregisterReceiver(regBroadcastReceiver);
		//	unregisterReceiver(callStateReceiver);
		//	unregisterReceiver(textReceiver);
		super.onPause();
	} 

	@Override
	protected void onDestroy() {
		Log.i("DEBUG", "OnDestroy");
		if(mEngine.isStarted()){
			mEngine.stop();
		}
		if(regBroadcastReceiver != null){
			unregisterReceiver(regBroadcastReceiver);
			regBroadcastReceiver = null;
		}
		if(callStateReceiver != null){
			unregisterReceiver(callStateReceiver);
			callStateReceiver = null;
		}
		super.onDestroy();
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
