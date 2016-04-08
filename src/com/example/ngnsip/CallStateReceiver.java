package com.example.ngnsip;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CallStateReceiver extends BroadcastReceiver {

	private MainActivity mActivity; 
	public CallStateReceiver(MainActivity m)
	{
		mActivity = m;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		final String action = intent.getAction();

		if(NgnInviteEventArgs.ACTION_INVITE_EVENT.equals(action)){
			NgnInviteEventArgs args = 
					intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
			if(args == null){
				Log.d("DEBUG", "Invalid event args");
				return;
			}

			final NgnAVSession avSession
			= NgnAVSession.getSession(args.getSessionId());
			if (avSession == null) {
				return;
			}

			final InviteState callState = avSession.getState();
			NgnEngine mEngine = NgnEngine.getInstance();
			TextView status = (TextView)mActivity.findViewById(R.id.textView_callstatus);
			EditText destination = (EditText)mActivity.findViewById(R.id.editText_destination);
			
			switch(callState){
			case NONE:
			default:
				break;
			case INCOMING:
				Log.i("DEBUG", "Incoming call");
				// Ring
                
				mEngine.getSoundService().startRingTone();
				destination.setText(avSession.getRemotePartyUri());
				status.setText("Incomimg call, autoanswer");
				Handler handler = new Handler(); 
				handler.postDelayed(new Runnable() { 
					public void run() { 
						avSession.acceptCall(); 
					} 
				}, 2000); // answer after 2 seconds 
				

				break;
			case INCALL:
				Log.i("DEBUG", "Call connected");
			
				status.setText("In call");
				Toast.makeText(context, "Call connected", Toast.LENGTH_SHORT).show();
				mEngine.getSoundService().stopRingTone();
				break;
			case TERMINATED:
				Log.i("DEBUG", "Call terminated");
				
				status.setText("No call");
				mEngine.getSoundService().stopRingTone();
				mEngine.getSoundService().stopRingBackTone();
				break;
			}
		}
	}

}
