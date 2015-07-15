/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package ar.com.nigdy.printer;


import hdx.pwm.PWMControl;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.hdx.lib.printer.*;
import com.hdx.lib.serial.SerialParam;
import com.hdx.lib.serial.SerialPortOperaion;
import com.hdx.lib.serial.SerialPortOperaion.SerialReadData;

import android.content.Context;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.widget.Toast;

import android.util.Log;
import android.util.TimeUtils;

import java.io.BufferedReader;

import android.os.Handler;
import android.os.Messenger;
import java.lang.*;
import java.util.concurrent.locks.*;
import android.os.Message;




public class Printer extends CordovaPlugin {

    SerialPrinter  mSerialPrinter=SerialPrinter.GetSerialPrinter();

    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("print")) {

                try {
		    mSerialPrinter.OpenPrinter(new SerialParam(115200,"/dev/ttyS2",0),new SerialDataHandler());
		} catch (Exception e1) {
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

                PowerManager pm = (PowerManager)this.cordova.getActivity().getApplicationContext().getSystemService(Context.POWER_SERVICE);
	        lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		
		lock.acquire();
                try {
			PWMControl.PrinterEnable(1);
			mSerialPrinter.printString(args.get(0).toString());

		} finally{
		    lock.release();
		    PWMControl.PrinterEnable(0);
		}
        }
        return true;
    }

    private class SerialDataHandler extends Handler{
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SerialPortOperaion.SERIAL_RECEIVED_DATA_MSG:
                	SerialReadData data = (SerialReadData)msg.obj;
                	StringBuilder sb=new StringBuilder();
                	for(int x=0;x<data.size;x++)
						sb.append(String.format("%02x", data.data[x]));
                	if((data.data[0]&1)==1)
                		Toast.makeText(getApplicationContext(), getString(R.string.no_paper),
                			     Toast.LENGTH_SHORT).show();
                	if((data.data[0]&2)==2)
                		Toast.makeText(getApplicationContext(), getString(R.string.buff_fulled),
                			     Toast.LENGTH_SHORT).show();                	
            }
        }
	}	

}
