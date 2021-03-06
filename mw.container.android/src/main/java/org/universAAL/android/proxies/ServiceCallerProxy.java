/*
	Copyright 2008-2014 ITACA-TSB, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion 
	Avanzadas - Grupo Tecnologias para la Salud y el 
	Bienestar (TSB)
	
	See the NOTICE file distributed with this work for additional 
	information regarding copyright ownership
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	  http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.android.proxies;

import java.lang.ref.WeakReference;
import java.util.Hashtable;

import org.universAAL.android.container.AndroidContainer;
import org.universAAL.android.container.AndroidContext;
import org.universAAL.android.services.MiddlewareService;
import org.universAAL.android.utils.Config;
import org.universAAL.android.utils.GroundingParcel;
import org.universAAL.android.utils.AppConstants;
import org.universAAL.android.utils.RAPIManager;
import org.universAAL.android.utils.VariableSubstitution;
import org.universAAL.middleware.rdf.Resource;
import org.universAAL.middleware.serialization.MessageContentSerializerEx;
import org.universAAL.middleware.service.CallStatus;
import org.universAAL.middleware.service.ServiceCaller;
import org.universAAL.middleware.service.ServiceRequest;
import org.universAAL.middleware.service.ServiceResponse;
import org.universAAL.middleware.service.aapi.AapiServiceRequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Class that acts as a connection between an Android component and a universAAL
 * wrapper. In this case, between a receiver, a Service Caller, and an intent
 * for the response. The translation is made thanks to metadata grounding.
 * 
 * @author alfiva
 * 
 */
public class ServiceCallerProxy extends ServiceCaller {
	private WeakReference<Context> contextRef;
	private String action=null;
	private String category=null;
	private String replyAction=null;
	private String replyCategory=null;
	private String remote=null;
	private String grounding=null;
	private Hashtable<String,String> extraKEYtoInputURI;
	private Hashtable<String,String> outputURItoExtraKEY;
	private ServiceCallerProxyReceiver receiver=null;
	
	/**
	 * Constructor for the proxy.
	 * 
	 * @param parcel
	 *            The parcelable from of the grounding of the metadata.
	 * @param context
	 *            The Android context.
	 */
	public ServiceCallerProxy(GroundingParcel parcel, Context context) {
		super(AndroidContext.THE_CONTEXT);
		contextRef=new WeakReference<Context>(context);
		action=parcel.getAction();
		category=parcel.getCategory();
		replyAction=parcel.getReplyAction();
		replyCategory=parcel.getReplyCategory();
		grounding=parcel.getGrounding();
		fillTable1(parcel.getLengthIN(),parcel.getKeysIN(), parcel.getValuesIN());
		fillTable2(parcel.getLengthOUT(),parcel.getKeysOUT(), parcel.getValuesOUT());
		receiver=new ServiceCallerProxyReceiver();
		IntentFilter filter=new IntentFilter(action);
		filter.addCategory(category);
		context.registerReceiver(receiver, filter);//TODO use the other longer register method
		// This is for GW or RAPI
		remote = parcel.getRemote();
		sync();
	}
	
	public void sync() {
		//Nothing. Publishing does not need syncing. For now. It used to for GW, but not with new GW.
	}

	/**
	 * Extract the table of mappings between extras and inputs from the
	 * grounding.
	 * 
	 * @param length
	 *            Amount of entries.
	 * @param keys
	 *            Extras keys.
	 * @param values
	 *            Input values.
	 */
	private void fillTable1(int length, String[] keys, String[] values) {
		if (length == 0) {
			extraKEYtoInputURI = null;
		} else {
			extraKEYtoInputURI = new Hashtable<String, String>(length);
			for (int i = 0; i < length; i++) {
				extraKEYtoInputURI.put(keys[i], values[i]);
			}
		}
	}

	/**
	 * Extract the table of mappings between outputs and extras from the
	 * grounding.
	 * 
	 * @param length
	 *            Amount of entries.
	 * @param keys
	 *            Output keys.
	 * @param values
	 *            Extras values.
	 */
	private void fillTable2(int length, String[] keys, String[] values) {
		if (length == 0) {
			outputURItoExtraKEY = null;
		} else {
			outputURItoExtraKEY = new Hashtable<String, String>(length);
			for (int i = 0; i < length; i++) {
				outputURItoExtraKEY.put(keys[i], values[i]);
			}
		}
	}
	
	/** Get the dynamically registered broadcast receiver.
	 * @return the dynamically registered broadcast receiver.
	 */
	public ServiceCallerProxyReceiver getReceiver() {
		return receiver;
	}

	@Override
	public void communicationChannelBroken() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void close() {
		super.close();
		Context ctxt=this.contextRef.get();
		if (ctxt!=null && receiver!=null){
			ctxt.unregisterReceiver(receiver);
		}
	}

	@Override
	public void handleResponse(String reqID, ServiceResponse response) {
		Context ctxt=contextRef.get();
		if(ctxt!=null && (replyAction!=null && !replyAction.isEmpty()) && (replyCategory!=null && !replyCategory.isEmpty())){
			//If the app is really interested in the result
			Intent start = new Intent(replyAction);
			start.addCategory(replyCategory);
			if(outputURItoExtraKEY!=null && !outputURItoExtraKEY.isEmpty()){
				VariableSubstitution.putResponseOutputsAsIntentExtras(response, start, outputURItoExtraKEY);
			}
			CallStatus status = response.getCallStatus();
			if(status!=null){
				start.putExtra(CallStatus.MY_URI, status.name());
			}
			ctxt.sendBroadcast(start);
			//TODO Send to replyact/cat that the app said at first (embed into sreq, then in sresp in callee and then read here)
		}
	}
	
	/**
	 * Auxiliary class representing the Broadcast Receiver registered by the
	 * middleware where apps will send intents when they want to issue a service
	 * request to uAAL.
	 * 
	 * @author alfiva
	 * 
	 */
	public class ServiceCallerProxyReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getBooleanExtra(AppConstants.ACTION_META_FROMPROXY, false)) {
				// The intent comes from a CalleeProxy. It wouldnt have sent it
				// if its receiver and this one where the same, but that doesnt
				// count when it comes from the bus alone. This fixes that.
				return;
			}
			final MessageContentSerializerEx parser = (MessageContentSerializerEx) AndroidContainer.THE_CONTAINER
					.fetchSharedObject(AndroidContext.THE_CONTEXT,
							new Object[] { MessageContentSerializerEx.class
									.getName() });
			ServiceRequest sr;
			if(extraKEYtoInputURI!=null && !extraKEYtoInputURI.isEmpty()){
				String turtleReplaced=VariableSubstitution.putIntentExtrasAsRequestInputs(intent, grounding, extraKEYtoInputURI);
				if(turtleReplaced==null)return;//Null if error substituting. Do not send default request.
				sr=(ServiceRequest)parser.deserialize(turtleReplaced);
			}else{
				sr=(ServiceRequest)parser.deserialize(grounding);
			}
			// This is for identifying the origin of the request, to avoid duplications in callee later
			// I have to make this hack to convert the SR into an AAPI SR in order to inject metadata.
			// It would be soooo much easier if ServiceRequest allowed setProperty of AAPI metadata directly...
			final AapiServiceRequest srmeta=new AapiServiceRequest(sr.getURI());
			srmeta.setProperty(ServiceRequest.PROP_AGGREGATING_FILTER,
					sr.getProperty(ServiceRequest.PROP_AGGREGATING_FILTER));
			srmeta.setProperty(ServiceRequest.PROP_REQUESTED_SERVICE,
					sr.getProperty(ServiceRequest.PROP_REQUESTED_SERVICE));
			srmeta.setProperty(ServiceRequest.PROP_REQUIRED_PROCESS_RESULT,
					sr.getProperty(ServiceRequest.PROP_REQUIRED_PROCESS_RESULT));
			srmeta.setProperty(ServiceRequest.PROP_uAAL_SERVICE_CALLER,
					sr.getProperty(ServiceRequest.PROP_uAAL_SERVICE_CALLER));
			srmeta.setProperty(ServiceRequest.PROP_uAAL_INVOLVED_HUMAN_USER, sr
					.getProperty(ServiceRequest.PROP_uAAL_INVOLVED_HUMAN_USER));
			srmeta.addInput(AppConstants.UAAL_META_PROP_FROMACTION, action);
			srmeta.addInput(AppConstants.UAAL_META_PROP_FROMCATEGORY, category);
			Resource[] outputs=sr.getRequiredOutputs();
			if(outputs.length>0){
				srmeta.addInput(AppConstants.UAAL_META_PROP_NEEDSOUTPUTS, Boolean.TRUE);
				// I have to add this flag metadata because otherwise callee doesnt know if an output is really needed
			}
			sendRequest(srmeta);
			// If RAPI, send it to server. If GW it is automatic by the running GW TODO Sent twice (no matching above)
			if (MiddlewareService.isGWrequired() && Config.getRemoteType() == AppConstants.REMOTE_TYPE_RAPI
					&& remote != null && !remote.isEmpty()) {
				new Thread() {
					@Override
					public void run() {
						String result = RAPIManager.invoke(RAPIManager.CALLS, parser.serialize(srmeta));
                        			if (result == null) {//If problems with remote server
                        			    result = NO_MATCHING;
                        			}
						handleResponse(null, (ServiceResponse) parser.deserialize(result));
					}
				}.start();
			}
		}
	}
	
	private static final String NO_MATCHING="@prefix : <http://ontology.universAAL.org/uAAL.owl#> . "
		+ "_:BN000000 a :ServiceResponse ; "
		+ "  :callStatus :no_matching_service_found . "
		+ ":no_matching_service_found a :CallStatus . ";
}
