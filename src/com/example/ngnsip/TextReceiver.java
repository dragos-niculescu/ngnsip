package com.example.ngnsip;

import java.io.UnsupportedEncodingException;

import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnMessagingEventArgs;
import org.doubango.ngn.utils.NgnContentType;
import org.doubango.ngn.utils.NgnStringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class TextReceiver extends BroadcastReceiver {

	ChatActivity mActivity; 
	public TextReceiver (ChatActivity a){ 
		mActivity = a; 
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		final String action = intent.getAction();


		if(NgnMessagingEventArgs.ACTION_MESSAGING_EVENT.equals(action)){
			NgnMessagingEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if(args == null){
				Log.d("DEBUG", "Invalid messaging event args");
				return;
			}

			switch(args.getEventType()){
			case INCOMING:
			{
				if(NgnStringUtils.equals(args.getContentType(), NgnContentType.T140COMMAND, true)){

				}
				else{ // display any other content type (e.g plain/text, xml, pdf...) "AS IS"
					byte[] contentBytes = args.getPayload();
					if(contentBytes != null && contentBytes.length > 0){
						try {
							String contentStr = new String(contentBytes, "UTF-8");
							TextView chat = (TextView)mActivity.findViewById(R.id.textView_chat);
							String chat_str = chat.getText().toString();
							chat.setText(chat_str + "\nThem: " + contentStr);	
							// Toast.makeText(context, contentStr, Toast.LENGTH_LONG).show();

						} catch (UnsupportedEncodingException e) {
							Log.i("DEBUG", e.toString());
						}
					}
				}
				break;
			}
			default:
			{
				break;
			}
			}
		}



	}

}
