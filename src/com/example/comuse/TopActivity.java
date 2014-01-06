package com.example.comuse;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import br.com.kots.mob.complex.preferences.ComplexPreferences;

public class TopActivity extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private SoundPool[] soundPools = new SoundPool[4];

	// 作曲要素とその受け皿
	private String[] element1 = { "うれしい", "たのしい", "かなしい" };
	private String[] element2 = { "ドキドキ", "ふつ～", "ねむたい" };
	private String[] element3 = { "あげあげ", "いらいら" };

	// TopActivityのView　や　機能　など
	private Button playBtn, comuseBtn, sendBtn, makeMusicBtn;
	private TextView sendTo;
	private GestureDetector gd;
	private BroadcastReceiver receiver;
	private String phoneNumber;

	// スピナーの何番目が選択されているか格納 ☆追加☆
	private int spinner1Position;
	private int spinner2Position;
	private int spinner3Position;
	// 受け取った音楽関連
	private int receivedTotal;
	private int[] receivedMusicIndex = new int[4];
	
	//要素のネガポジ得点
	int total;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top);
		for (int i = 0; i < soundPools.length; i++) {
			soundPools[i] = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		}
		Spinner sp1 = (Spinner) findViewById(R.id.element_spinner1);
		Spinner sp2 = (Spinner) findViewById(R.id.element_spinner2);
		Spinner sp3 = (Spinner) findViewById(R.id.element_spinner3);
		makeMusicBtn = (Button) findViewById(R.id.decide_btn);
		comuseBtn = (Button) findViewById(R.id.push_play_btn);
		sendBtn = (Button) findViewById(R.id.send_btn);
		playBtn = (Button) findViewById(R.id.play_btn);
		sendTo = (TextView) findViewById(R.id.send_to);

		// spinner adapter
		ArrayAdapter<String> sp1Ad = new ArrayAdapter<String>(TopActivity.this,
				android.R.layout.simple_dropdown_item_1line, element1);
		ArrayAdapter<String> sp2Ad = new ArrayAdapter<String>(TopActivity.this,
				android.R.layout.simple_dropdown_item_1line, element2);
		ArrayAdapter<String> sp3Ad = new ArrayAdapter<String>(TopActivity.this,
				android.R.layout.simple_dropdown_item_1line, element3);

		// spinner
		sp1.setAdapter(sp1Ad);
		sp1.setOnItemSelectedListener(this);
		sp2.setAdapter(sp2Ad);
		sp2.setOnItemSelectedListener(this);
		sp3.setAdapter(sp3Ad);
		sp3.setOnItemSelectedListener(this);

		// btn
		makeMusicBtn.setOnClickListener(this);
		playBtn.setOnClickListener(this);
		sendBtn.setOnClickListener(this);
		comuseBtn.setOnClickListener(this);

		// gesture登録
		gd = new GestureDetector(this, new MyGestureDetector(TopActivity.this));

		// 受け取り手選択後のbroadcastreceiver
		if (receiver == null) {
			receiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					Bundle bundle = intent.getExtras();
					if (bundle == null) {
						return;
					}
					if (intent.getAction().equals(
							ConstantUtil.BROADCAST_ACTION_DECIDE_RECEIVER)) {
						sendTo.setText(bundle.getString("name") + " さん");
						phoneNumber = bundle.getString("phone");
					} else if (intent.getAction().equals(
							ConstantUtil.INTENT_END_MUSIC)) {
						playBtn.setEnabled(true);
					}
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConstantUtil.BROADCAST_ACTION_DECIDE_RECEIVER);
			registerReceiver(receiver, filter);
		}
		
		// 受け取った音楽がある場合、comuseボタンをクリック可能にし再生処理へ飛ぶ
		ComplexPreferences cp = ComplexPreferences.getComplexPreferences(this,
				ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC, MODE_PRIVATE);
		String[] receivedData = cp.getObject(
				ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC, String[].class);
		if (receivedData != null) {
			Log.i("top oncreate", "noti bundle");
			String body = receivedData[1];
			Log.i("top oncreate", body);
			if (!TextUtils.isEmpty(body)) {
				comuseBtn.setVisibility(View.VISIBLE);
				comuseBtn.setOnClickListener(this);
				String[] receivedMusic = body.split(":"); // :を区切りにして1から曲のフレーズを獲得
				Log.i("received", String.format("%s:total=%s:%s:%s:%s:%s ",
						receivedMusic[0], receivedMusic[1], receivedMusic[2],
						receivedMusic[3], receivedMusic[4], receivedMusic[5]));
				//曲の流れを取得
				receivedTotal = Integer.valueOf(receivedMusic[1]);
				for (int i = 0, n = 2; i < receivedMusicIndex.length; i++, n++) {
					receivedMusicIndex[i] = Integer.valueOf(receivedMusic[n]);
				}
				
				Intent decideIntent = new Intent(this, PlayMusicService.class);
				decideIntent
						.setAction(ConstantUtil.INTENT_START_SERVICE_DECIDE_RECEIVE);
				decideIntent.putExtra("total", receivedTotal);
				decideIntent.putExtra("musicIndex", receivedMusicIndex);
				startService(decideIntent);
			}
		}
	}

	// spinnerから要素を取得
	@Override
	public void onItemSelected(AdapterView<?> av, View v, int position,
			long arg3) {
		// nega posi得点を取る
		if (v != null) {
			switch (av.getId()) {
			case R.id.element_spinner1:
				spinner1Position = position;
				Log.i("sp1", spinner1Position + "番目");
				break;
			case R.id.element_spinner2:
				spinner2Position = position;
				Log.i("sp2", spinner2Position + "番目");
				break;
			case R.id.element_spinner3:
				spinner3Position = position;
				Log.i("sp3", spinner3Position + "番目");
				break;
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> av) {

	}

	@Override
	public void onClick(View v) {
		ComplexPreferences cp = ComplexPreferences.getComplexPreferences(this,
				ConstantUtil.COMPLEX_PREF_KEY_MUSIC_INDEX, MODE_PRIVATE);
		switch (v.getId()) {
		case R.id.decide_btn:
			// spinnerの要素から作曲
			total = spinner1Position + spinner2Position + spinner3Position;
			Log.i("top decidebtn total", total+"");
			AsyncTask<Integer, Void, String> async = new AsyncTask<Integer, Void, String>() {

				@Override
				protected String doInBackground(Integer... params) {
					Intent decideIntent = new Intent(TopActivity.this,
							PlayMusicService.class);
					decideIntent
							.setAction(ConstantUtil.INTENT_START_SERVICE_DECIDE);
					decideIntent.putExtra("total", params[0]);
					startService(decideIntent);
					return null;
				}
			};
			async.execute(total);
			playBtn.setEnabled(true);
			break;
		case R.id.play_btn:
			// service start
			Intent intent = new Intent(this, PlayMusicService.class);
			intent.setAction(ConstantUtil.INTENT_START_SERVICE_PLAY);
			// ここで再生するidを受け渡す
			startService(intent);
			playBtn.setEnabled(false);
			break;
		case R.id.push_play_btn: //送られてきた音楽の再生
			Intent comuseIntent = new Intent(this, PlayMusicService.class);
			comuseIntent.setAction(ConstantUtil.INTENT_START_SERVICE_COMUSE);
			// ここで再生するidを受け渡す
			startService(comuseIntent);
			break;
		case R.id.send_btn:
			// 曲を送る人を決める TAGを最初に乗せる。
			SharedPreferences sp = getSharedPreferences("totalPoint", MODE_PRIVATE);
			int sendTotal = sp.getInt("totalPoint", -1);
			int[] index = cp.getObject(
					ConstantUtil.COMPLEX_PREF_KEY_MUSIC_INDEX, int[].class);
			if (!TextUtils.isEmpty(phoneNumber) && index != null && sendTotal != -1) {
				SmsManager smsMgr = SmsManager.getDefault();
				smsMgr.sendTextMessage(phoneNumber, null,
						String.format("%s:%s:%s:%s:%s:%s",
								ConstantUtil.SMS_TAG, sendTotal, index[0],
								index[1], index[2], index[3]), null, null);
				phoneNumber = null;
				sendTo.setText("send to");
			} else {
				if(TextUtils.isEmpty(phoneNumber)){
					Toast.makeText(this, "送信先を決めてください", Toast.LENGTH_SHORT).show();
				}else if(index == null){
					Toast.makeText(this, "要素を選んでください", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		}

	}

	// long pressを読み取るためにタッチイベントにgdを登録
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return gd.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
	}

	// soundpoolのreleaseとfinish();
	@Override
	protected void onPause() {
		super.onPause();
		if (soundPools[0] != null) {
			for (int i = 0; i < soundPools.length; i++) {
				soundPools[i].release();
				soundPools[i] = null;
			}
		}
		phoneNumber = null;
		sendTo.setText("send to");
	}

	// destroy時にreceiver解除
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterReceiver(receiver);
			receiver = null;
		}
	}

}
