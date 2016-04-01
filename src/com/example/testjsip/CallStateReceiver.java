package com.example.testjsip;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnInviteEventArgs;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnInviteSession.InviteState;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CallStateReceiver extends BroadcastReceiver {

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

	      NgnAVSession avSession
	                = NgnAVSession.getSession(args.getSessionId());
	      if (avSession == null) {
	        return;
	      }

	      final InviteState callState = avSession.getState();
	      NgnEngine mEngine = NgnEngine.getInstance();
	      
	      switch(callState){
	        case NONE:
	        default:
	        break;
	        case INCOMING:
	          Log.i("DEBUG", "Incoming call");
	          // Ring

	          mEngine.getSoundService().startRingTone();
	          break;
	        case INCALL:
	          Log.i("DEBUG", "Call connected");
	          mEngine.getSoundService().stopRingTone();
	          break;
	        case TERMINATED:
	          Log.i("DEBUG", "Call terminated");
	          mEngine.getSoundService().stopRingTone();
	          mEngine.getSoundService().stopRingBackTone();
	          break;
	      }
	    }
	}

}
