package com.example.comuse;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
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

public class TopActivity extends Activity implements OnItemSelectedListener,
		OnClickListener {
	private String[] element1 = { "うれしい", "たのしい", "かなしい" };
	private String[] element2 = { "ドキドキ", "ふつ～", "ねむたい" };
	private String[] element3 = { "あげあげ", "ふつ～", "いらいら" };
	private String selectSp1;
	private String selectSp2;
	private String selectSp3;
	private List<String> totalElementList = new ArrayList<String>();
	private SoundPool soundPool1, soundPool2, soundPool3, soundPool4;
	// music code
	private int[] cIds = { R.raw.c_101, R.raw.c_103, R.raw.c_105, R.raw.c_201,
			R.raw.c_203, R.raw.c_205 };
	private int[] dIds = { R.raw.d_104, R.raw.d_204 };
	private int[] eIds = { R.raw.e_102, R.raw.e_202 };
	private int[] fIds = { R.raw.f_101, R.raw.f_103, R.raw.f_105, R.raw.f_201,
			R.raw.f_203, R.raw.f_205 };
	private int[] fmIds = { R.raw.fm_104, R.raw.fm_204 };
	private int[] gIds = { R.raw.g_102, R.raw.g_202 };
	private int[] gmIds = { R.raw.gm_104, R.raw.gm_204 };
	private int[] amIds = { R.raw.am_101, R.raw.am_103, R.raw.am_105,
			R.raw.am_201, R.raw.am_203, R.raw.am_205 };
	private int[] bbIds = { R.raw.bb_102, R.raw.bb_202 };
	// random
	private Random rand = new java.util.Random();
	private Button playBtn, comuseBtn, sendBtn, makeMusicBtn;
	private TextView sendTo;
	private GestureDetector gd;
	private BroadcastReceiver receiver;
	private String phoneNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.top);
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

		// soundpool生成
		soundPool1 = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPool2 = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPool3 = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundPool4 = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);

		// もし通知メッセージからアプリを起動したらここでGcmBroadcastReceiverからの値を受け取る
		Intent intent = getIntent();
		String mess = intent.getStringExtra("MESS");
		if (!TextUtils.isEmpty(mess)) {
			comuseBtn.setVisibility(View.VISIBLE);
			comuseBtn.setOnClickListener(this);
		}

		// gesture登録
		gd = new GestureDetector(this, new MyGestureDetector(TopActivity.this));

		// receiver選択後のbroadcastreceiver
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
					}
				}
			};
			IntentFilter filter = new IntentFilter();
			filter.addAction(ConstantUtil.BROADCAST_ACTION_DECIDE_RECEIVER);
			registerReceiver(receiver, filter);
		}
	}

	// spinnerから要素を取得
	@Override
	public void onItemSelected(AdapterView<?> av, View v, int position,
			long arg3) {
		if (v != null) {
			switch (av.getId()) {
			case R.id.element_spinner1:
				selectSp1 = ((TextView) v).getText().toString(); // TODO
																	// ここでnullpoが出てるっぽい。
				Log.i("sp", selectSp1);
				break;
			case R.id.element_spinner2:
				selectSp2 = ((TextView) v).getText().toString();
				Log.i("sp", selectSp2);
				break;
			case R.id.element_spinner3:
				selectSp3 = ((TextView) v).getText().toString();
				Log.i("sp", selectSp3);
				break;
			}
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> av) {

	}

	// phrase
	private List<Integer> firstPhrase = new ArrayList<Integer>();
	private List<Integer> secondPhrase = new ArrayList<Integer>();
	private List<Integer> thirdPhrase = new ArrayList<Integer>();
	private List<Integer> fourthPhrase = new ArrayList<Integer>();

	@Override
	public void onClick(View v) {
		// spinnerの要素から作曲
		switch (v.getId()) {
		case R.id.decide_btn:
			if (!TextUtils.isEmpty(selectSp1) && !TextUtils.isEmpty(selectSp2)
					&& !TextUtils.isEmpty(selectSp3)) {
				// 要素から作曲
				int elementC = rand.nextInt(cIds.length);
				int elementD = rand.nextInt(dIds.length);
				int elementE = rand.nextInt(eIds.length);
				int elementF = rand.nextInt(fIds.length);
				int elementFm = rand.nextInt(fmIds.length);
				int elementG = rand.nextInt(gIds.length);
				int elementGm = rand.nextInt(gmIds.length);
				int elementAm = rand.nextInt(amIds.length);
				int elementBb = rand.nextInt(bbIds.length);

				// soundpoolに投げ込む
				firstPhrase.add(soundPool1.load(TopActivity.this,
						cIds[elementC], 1));
				int firstCLength = getMusicMillis(cIds[elementC]);
				Log.i("1stC", firstCLength + "");
				firstPhrase.add(soundPool1.load(TopActivity.this,
						fIds[elementF], 1));
				int firstFLength = getMusicMillis(fIds[elementF]);
				Log.i("1stF", firstFLength + "");
				firstPhrase.add(soundPool1.load(TopActivity.this,
						amIds[elementAm], 1));
				int firstAmLength = getMusicMillis(amIds[elementAm]);
				Log.i("1stAm", firstAmLength + "");

				/*
				 * soundPool2.load(TopActivity.this, resId, 1);
				 * soundPool2.load(TopActivity.this, resId, 1);
				 * soundPool2.load(TopActivity.this, resId, 1);
				 * soundPool3.load(TopActivity.this, resId, 1);
				 * soundPool3.load(TopActivity.this, resId, 1);
				 * soundPool3.load(TopActivity.this, resId, 1);
				 * soundPool4.load(TopActivity.this, resId, 1);
				 * soundPool4.load(TopActivity.this, resId, 1);
				 * soundPool4.load(TopActivity.this, resId, 1);
				 */

				playBtn.setEnabled(true);
			} else {
				// 要素未選択の場合
				Toast.makeText(getApplicationContext(), "要素を選んでください",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.play_btn:
			// Soundpool
			soundPool1.play(firstPhrase.get(0), 1, 1, 1, 0, 1);
			soundPool1.play(firstPhrase.get(1), 1, 1, 1, 0, 1);
			soundPool1.play(firstPhrase.get(2), 1, 1, 1, 0, 1);
			break;
		case R.id.push_play_btn:
			// TODO プッシュ通知で送られてきた音楽を再生する処理

			break;
		case R.id.send_btn:
			// 曲を送る人を決める TAGを最初に乗せる。
			if (!TextUtils.isEmpty(phoneNumber)) {
				SmsManager smsMgr = SmsManager.getDefault();
				smsMgr.sendTextMessage(phoneNumber, null, ConstantUtil.SMS_TAG,
						null, null);
				phoneNumber = null;
				sendTo.setText("send to");
			} else {
				Toast.makeText(this, "送信先を決めてください", Toast.LENGTH_SHORT).show();
			}

			break;
		}

	}

	// long pressを読み取るためにタッチイベントにgdを登録
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return gd.onTouchEvent(ev) || super.dispatchTouchEvent(ev);
	}

	// soundpoolのreleaseとfinish();
	@Override
	protected void onPause() {
		super.onPause();
		if (soundPool1 != null) {
			soundPool1.release();
			soundPool1 = null;
		}
		if (soundPool2 != null) {
			soundPool2.release();
			soundPool2 = null;
		}
		if (soundPool3 != null) {
			soundPool3.release();
			soundPool3 = null;
		}
		if (soundPool4 != null) {
			soundPool4.release();
			soundPool4 = null;
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

	// 曲の長さを取得
	private int getMusicMillis(int resId) {
		MediaPlayer mp = MediaPlayer.create(TopActivity.this, resId);
		int musicLength = mp.getDuration();
		mp.stop();
		mp.reset();
		mp.release();
		mp = null;
		return musicLength;
	}
}
