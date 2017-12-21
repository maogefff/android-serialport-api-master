/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android_serialport_api.sample;

import java.io.IOException;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ConsoleActivity extends SerialPortActivity {

	EditText mReception;
	Button mSend;
	Button mSendSeries;
	CharSequence t;
	ConsoleThread mConsoleThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.console);

//		setTitle("Loopback test");
		mReception = (EditText) findViewById(R.id.EditTextReception);
		mSend = (Button) findViewById(R.id.ButtonSend);
		mSendSeries = (Button) findViewById(R.id.ButtonSendSeries);
		EditText Emission = (EditText) findViewById(R.id.EditTextEmission);

		mSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int i;
				if(t!=null){
					char[] text = new char[t.length()];
					for (i=0; i<t.length(); i++) {
						text[i] = t.charAt(i);
					}
					try {
						mOutputStream.write(new String(text).getBytes());
						mOutputStream.write('\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		mSendSeries.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				if(t!=null){
					mConsoleThread = new ConsoleThread();
					mConsoleThread.start();
				}
			}
		});

		Emission.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				t = v.getText();
				return false;
			}
		});
	}

	@Override
	protected void onDataReceived(final byte[] buffer, final int size) {
		runOnUiThread(new Runnable() {
			public void run() {
				if (mReception != null) {
					if (mReception.getLineCount() >= 100) {
						mReception.setText("");
					}
					mReception.append(new String(buffer, 0, size));
				}
			}
		});
	}

	private class ConsoleThread extends Thread{

		@Override
		public void run() {
			if(t!=null){
				int i;
				char[] text = new char[t.length()];
				for (i=0; i<t.length(); i++) {
					text[i] = t.charAt(i);
				}

				while(!isInterrupted()){
					try {
						mOutputStream.write(new String(text).getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mConsoleThread!=null){
			mConsoleThread.interrupt();
		}
		t = null;
	}
}
