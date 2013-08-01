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
package org.universAAL.middleware.android.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author <a href="mailto:noamsh@il.ibm.com">noamsh </a>
 * 
 *         Apr 9, 2012
 * 
 */
public class ModulesReceiver extends BroadcastReceiver {

	private final static String TAG = "ModulesReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Got intent with action [" + intent.getAction() + "]");

		Intent startServiceIntent = new Intent(intent);
		startServiceIntent.setClass(context, ModulesService.class);

		context.startService(startServiceIntent);
	}
}
