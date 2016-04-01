package com.example.testjsip;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.utils.NgnUriUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OutgoingCallReceiver extends BroadcastReceiver {


	  @Override
	  public void onReceive(Context context, Intent intent) {
	    String phoneNumber = getResultData();
	    if (phoneNumber == null) {
	      phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
	    }
	    
	    setResultData(null);

	    final String validUri = NgnUriUtils.makeValidSipUri(
	      String.format("sip:%s@%s", "echo", "conference.sip2sip.info"));
	    if(validUri == null){
	      Log.e("DEBUG", "Invalid number");
	      return;
	    }
	    
	    NgnAVSession avSession = NgnAVSession.createOutgoingSession(
	      NgnEngine.getInstance().getSipService().getSipStack(), NgnMediaType.Audio);
	    avSession.makeCall(validUri);
	  }
	

}
