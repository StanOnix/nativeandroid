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
package org.universAAL.android.utils;

/**
 * Helper class containing useful constant values used across the
 * entire app.
 * 
 * @author alfiva
 * 
 */
public class AppConstants {

	private static final String ACTION_PREFIX = "org.universAAL.android.action.";
	
	// Sent from ScanService to MiddlewareService, to specify which scanned
	// package has to be un/registered
	public static final String ACTION_PCK_REG = ACTION_PREFIX + "PCK_REGISTER";
	public static final String ACTION_PCK_UNREG = ACTION_PREFIX	+ "PCK_UNREGISTER";
	// Extras
	public static final String ACTION_PCK_REG_X_PARCEL = ACTION_PREFIX + "parsedParcel";
	public static final String ACTION_PCK_REG_X_ID = ACTION_PREFIX + "wrapperID";
	public static final String ACTION_PCK_REG_X_TYPE = ACTION_PREFIX + "parsedType";
	public static final String ACTION_PCK_UNREG_X_ID = ACTION_PCK_REG_X_ID;
	public static final String ACTION_PCK_UNREG_X_TYPE = ACTION_PCK_REG_X_TYPE;
	
	// Prefixes for dynamic replyTo parameters
	public static final String ACTION_REPLY = ACTION_PREFIX + "REPLY_TO";

	// Sent from MiddlewareService to ScanService, to tell it when to perform a
	// full scan to un/register
	public static final String ACTION_PCK_REG_ALL = ACTION_PREFIX + "PCK_REGISTER_ALL";
	public static final String ACTION_PCK_UNREG_ALL = ACTION_PREFIX + "PCK_UNREGISTER_ALL";

	// Sent to OntologyService, to tell it when to update the registered
	// ontologies (not all are used yet)
	public static final String ACTION_ONT_REG_ALL = ACTION_PREFIX + "ONT_REGISTER_ALL";
//	public static final String ACTION_ONT_UNREG_ALL = ACTION_PREFIX + "ONT_UNREGISTER_ALL";
//	public static final String ACTION_ONT_REG = ACTION_PREFIX + "ONT_REGISTER";
//	public static final String ACTION_ONT_UNREG = ACTION_PREFIX + "ONT_UNREGISTER";
	
	// Used to update the percentage of the progress bar
	public static final String ACTION_UI_PROGRESS = ACTION_PREFIX + "UI_PROGRESS";
	
	// Sent by this app to notify about its status
	public static final String ACTION_NOTIF_STARTED = ACTION_PREFIX + "NOTIF_STARTED";
	public static final String ACTION_NOTIF_STOPPED = ACTION_PREFIX + "NOTIF_STOPPED";
	public static final String ACTION_NOTIF_CONFIG = ACTION_PREFIX + "NOTIF_CONFIG";
	public static final String ACTION_NOTIF_KEY_IGNORE = "ignore";
	
	// Sent from external apps to remote control the start/stop of MW service
	// TODO: Not needed once bus stop bug is solved
	public static final String ACTION_SYS_START = ACTION_PREFIX + "SYS_START";
	public static final String ACTION_SYS_STOP = ACTION_PREFIX + "SYS_STOP";
//	public static final String ACTION_SYS_RESTART = ACTION_PREFIX + "SYS_RESTART";
	
	// Hidden extras used by app to tell where it wants a reply
	public static final String ACTION_META_REPLYTOACT = ACTION_PREFIX + "META_REPLYTOACT";
	public static final String ACTION_META_REPLYTOCAT = ACTION_PREFIX + "META_REPLYTOCAT";
	
	// Hidden extras to avoid feedback when intent comes from PC bus
	public static final String ACTION_META_FROMPROXY = ACTION_PREFIX + "META_FROMPROXY";
		
	// Hidden properties in SReqs and CEvs to indicate they were generated by
	// proxies. To avoid the duplication/infinite loop issue. And other things
	public static final String UAAL_META_PROP_FROMACTION = "http://ontology.universAAL.org/uAAL.owl#fromAction";
	public static final String UAAL_META_PROP_FROMCATEGORY = "http://ontology.universAAL.org/uAAL.owl#fromCategory";
	public static final String UAAL_META_PROP_NEEDSOUTPUTS = "http://ontology.universAAL.org/uAAL.owl#needsOutputs";
	
	// Assorted enumerations accessed from across the app
	public static final int REMOTE_TYPE_GW=0;
	public static final int REMOTE_TYPE_RAPI=1;
	
	public static final int USER_TYPE_AP=0;
	public static final int USER_TYPE_CARE=1;
	
	public static final int REMOTE_MODE_ALWAYS=0;
	public static final int REMOTE_MODE_WIFIOFF=1;
	public static final int REMOTE_MODE_NEVER=2;
	
	public static final int WIFI_HOME = 0;
	public static final int WIFI_NOTSET = 1;
	public static final int WIFI_STRANGER = 2;
	public static final int WIFI_OFF = 3;
	
	public static final int TYPE_CPUBLISHER = 1;
	public static final int TYPE_CSUBSCRIBER = 2;
	public static final int TYPE_SCALLEE = 3;
	public static final int TYPE_SCALLER = 4;

	public static final int STATUS_STOPPED = 0;
	public static final int STATUS_STARTING = 1;
	public static final int STATUS_STARTED = 2;
	public static final int STATUS_STOPPING = 3;
	
	// Some defaults
	public static final String MY_WIFI = "home_wifi";
	public static final String NO_WIFI = "uAALGhostWifi";
	public static final String FIRST = "is_first";
	public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
	public static final String GCM_ENCRYPTED = "gcmEncrypted";
	public static final String GCM_ENCRYPT_KEYFILE = "gcmEncrypt.key";
	public static final String REGISTRATION_COMPLETE = "registrationComplete";
	public static final String GCM_PROJECT_ID = "gcmProjectId";

	public static class Defaults {
		public static final boolean ISCOORD = true;
	    public static final boolean UIHANDLER = false;
	    public static final boolean CONNWIFI = false;
	    public static final String CFOLDER = "/data/felix/configurations/etc/";
	    public static final String OFOLDER = "/data/felix/ontologies/";
	    public static final String IFOLDER = "/data/felix/configurations/etc/images/";
	    public static final String USER = "saied";
	    public static final int TYPE = USER_TYPE_AP;//
	    public static final int CONNMODE = REMOTE_MODE_ALWAYS;
	    public static final int CONNTYPE = REMOTE_TYPE_RAPI;
	    public static final String CONNURL = "http://158.42.167.41:8181/universaal";
	    public static final String CONNGCM = "1036878524725";
	    public static final String CONNUSR = "defaultusr";
	    public static final String CONNPWD = "defaultpwd";
	}
	
	public static class Keys {
		public static final String ISCOORD = "setting_iscoord_key";
	    public static final String UIHANDLER = "setting_uihandler_key";
	    public static final String CONNWIFI = "setting_connwifi_key";
	    public static final String CFOLDER = "setting_cfolder_key";
	    public static final String OFOLDER = "setting_ofolder_key";
	    public static final String IFOLDER = "setting_ifolder_key";
	    public static final String USER = "setting_user_key";
	    public static final String TYPE = "setting_type_key";
	    public static final String CONNMODE = "setting_connmode_key";
	    public static final String CONNTYPE = "setting_conntype_key";
	    public static final String CONNURL = "setting_connurl_key";
	    public static final String CONNGCM = "setting_conngcm_key";
	    public static final String CONNUSR = "setting_connusr_key";
	    public static final String CONNPWD = "setting_connpwd_key";
	}
}
