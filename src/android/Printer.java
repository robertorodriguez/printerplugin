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


import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.hdx.lib.printer.*;
import com.hdx.lib.serial.SerialParam;
import com.hdx.lib.serial.SerialPortOperaion;
import com.hdx.lib.serial.SerialPortOperaion.SerialReadData;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;



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

                PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
	        lock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, TAG);
		
		lock.acquire();
                try {
			PWMControl.PrinterEnable(1);
			mSerialPrinter.printString(args.get(0));
			MainActivity.this.sleep(5000);
		} finally{
		    lock.release();
		    PWMControl.PrinterEnable(0);
		}
        }
        return true;
    }

}
