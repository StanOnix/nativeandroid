/**
 * 
 *  OCO Source Materials 
 *      � Copyright IBM Corp. 2012 
 *
 *      See the NOTICE file distributed with this work for additional 
 *      information regarding copyright ownership 
 *       
 *      Licensed under the Apache License, Version 2.0 (the "License"); 
 *      you may not use this file except in compliance with the License. 
 *      You may obtain a copy of the License at 
 *       	http://www.apache.org/licenses/LICENSE-2.0 
 *       
 *      Unless required by applicable law or agreed to in writing, software 
 *      distributed under the License is distributed on an "AS IS" BASIS, 
 *      WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *      See the License for the specific language governing permissions and 
 *      limitations under the License. 
 *
 */
package org.universaal.nativeandroid.lightclient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 
 *  @author <a href="mailto:noamsh@il.ibm.com">noamsh </a>
 *
 */
public class LightClientActivity extends Activity 
{
	private final static String TAG 	= LightClientActivity.class.getCanonicalName();
	private final static String prefix 	= LightClientActivity.class.getPackage().getName();
	
	private BroadcastReceiver registeredReceiver = null;
			
	
	private String selectedLamp = "";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	public void onButtonOffClicked(View v){
		Intent intent = new Intent(prefix + ".TURN_OFF");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		if (!addSelectedLampNumber(intent)) {
			return;
		}
		invokeIntent(intent);
	}

	public void onButtonOnClicked(View v){
		Intent intent = new Intent(prefix + ".TURN_ON");
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		if (!addSelectedLampNumber(intent)) {
			return;
		}
		
		invokeIntent(intent);
	}
	
	public void onButtonGetLampsClicked(View v){
		// Clear the view
		RadioGroup lampsGroup = (RadioGroup)findViewById(R.id.groupLamps);
		clearLampsState(lampsGroup);
		
		// Unregister the previous registration (if exists)
		if (null != registeredReceiver) {
			unregisterReceiver(registeredReceiver);
			registeredReceiver = null;
		}
		
		// Register for receiver that will wait for the response
		BroadcastReceiver receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, final Intent intent) {
				Log.d(TAG, "Got response on GetControlledLamps request");
				
				//unregisterReceiver(this);
				
				runOnUiThread(
						new Thread() {
							@Override
							public void run() {
								analyzeGetLampsResponse(intent);
							}
						}
				);
			}
		};
		
		// Action name for the reply
		String actionNameForReply = receiver.getClass().getName();
		
		// Category for the reply
		String category = Intent.CATEGORY_DEFAULT;
		
		// Add a filter to the receiver
		IntentFilter filter = new IntentFilter(actionNameForReply);
		filter.addCategory(category);
		registerReceiver(receiver, filter);
		
		// Create the intent that will be sent as a broadcast message
		Intent intent = new Intent(prefix + ".GET_CONTROLLED_LAMPS");
		intent.putExtra(IConstants.replyToActionArg, actionNameForReply);
		intent.putExtra(IConstants.replyToCategoryArg, category);
		
		invokeIntent(intent);
	}
	
	@Override
	public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
		Intent registeredIntent = super.registerReceiver(receiver, filter);
		
		// Keep the register
		registeredReceiver = receiver;
		
		return registeredIntent;
	}

	private void analyzeGetLampsResponse(Intent intent) {
		// Extract the lamp array from the extras
		String[] lampsArr = intent.getStringArrayExtra(IConstants.lampNumberArrayArg);
		
		// Sort by lamps ids
		List<String> lampsList = Arrays.asList(lampsArr);
		Collections.sort(lampsList);
		
		// Update the UI
		RadioGroup lampsGroup = (RadioGroup)findViewById(R.id.groupLamps);
		for (String lamp : lampsList) {
			RadioButton lampButton = new RadioButton(this);
			lampButton.setText(lamp);
			lampButton.setTextColor(R.color.Blue);
			lampButton.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					selectedLamp = ((RadioButton)v).getText().toString();
				}
			});
			lampsGroup.addView(lampButton);
		}
	}
	
	private void invokeIntent(Intent intent) {
		sendBroadcast(intent);
	}
	
	private boolean addSelectedLampNumber(Intent lightServerIntent) {
		boolean isValid = false;
		if (null == selectedLamp || selectedLamp.length() == 0) {		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("No lamp has been selected")
			       .setCancelable(false).
			       setNegativeButton("Ok", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			lightServerIntent.putExtra(IConstants.lampNumberArg, selectedLamp);
			isValid = true;
		}
		
		return isValid;
	}
	
	private void clearLampsState(RadioGroup lampsGroup) {
		selectedLamp = "";
		lampsGroup.removeAllViews();
	}
}