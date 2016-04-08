package com.example.ngnsip;

import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.sip.NgnMessagingSession;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends Activity {
	private IntentFilter textIntentFilter;
	private TextReceiver textReceiver;

	private INgnSipService mSipService = null;
	private String sipaddr = null; // destination of the chat user@domain.com
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		Intent intent = getIntent();
		if (intent != null && intent.getExtras().containsKey("SIPADDR")) {
			sipaddr = intent.getStringExtra("SIPADDR");
		}
		TextView chat = (TextView)findViewById(R.id.textView_chat);
		chat.setMovementMethod(new ScrollingMovementMethod());


		textIntentFilter = new IntentFilter();
		textReceiver = new TextReceiver(this);
		textIntentFilter.addAction(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT);
		registerReceiver(textReceiver, textIntentFilter);


		Button BtSendTxt = (Button)findViewById(R.id.button_sendtxt);
		BtSendTxt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(MainActivity.instance.mSipService != null){
					EditText textToSend = (EditText)findViewById(R.id.editText_chatline );
					//EditText dest = (EditText)findViewById(R.id.editText_destination);
					final String remotePartyUri = "sip:" + sipaddr;
					// remote party
					final NgnMessagingSession imSession =
							NgnMessagingSession.createOutgoingSession(MainActivity.instance.mSipService.getSipStack(),
									remotePartyUri);
					if(!imSession.sendTextMessage(textToSend.getText().toString() )){
						Log.e("DEBUG","Failed to send");
					} 
					else{


						TextView chat = (TextView)findViewById(R.id.textView_chat);

						String chat_str = chat.getText().toString();
						chat.setText(chat_str + "\nMe: " + textToSend.getText().toString());
						textToSend.setText("");
						Log.d("DEBUG","Message sent"); 
					}
					// release session
					NgnMessagingSession.releaseSession(imSession);
				} else { 
					Toast.makeText(ChatActivity.this, "mSipService == null", Toast.LENGTH_SHORT).show();
				}
			}
		});


	}

	@Override
	protected void onDestroy() {
		Log.i("DEBUG", "OnDestroy");
		if(textReceiver != null){
			unregisterReceiver(textReceiver);
			textReceiver = null; 
		}
		super.onDestroy();
	}

}
