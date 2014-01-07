package com.example.comuse;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class PlayMusicService extends Service {
	private MediaPlayer mp, mp1, mp2, mp3;
	private MediaPlayer[] mps = { mp, mp1, mp2, mp3 };
	private int[] currentMusicIds;
	private MediaPlayer receivedMp, receivedMp1, receivedMp2, receivedMp3;
	private MediaPlayer[] receivedMps = { receivedMp, receivedMp1, receivedMp2,
			receivedMp3 };
	private int[] receivedMusicIds;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_DECIDE)
					|| intent.getAction().equals(
							ConstantUtil.INTENT_START_SERVICE_DECIDE_RECEIVE)) {
				Bundle bundle = intent.getExtras();
				int total = -1;
				if (intent.getAction().equals(
						ConstantUtil.INTENT_START_SERVICE_DECIDE_RECEIVE)) {
					total = bundle.getInt("receivedtotal");
				} else {
					total = bundle.getInt("total");
				}
				Log.i("pms total", "totalは" + total);
				CreateMusicCode createMusic = new CreateMusicCode(total,
						getApplicationContext());
				// 受け取った音楽のときだけ
				if (intent.getAction().equals(
						ConstantUtil.INTENT_START_SERVICE_DECIDE_RECEIVE)) {
					receivedMusicIds = bundle.getIntArray("musicIndex");
					createMusic.setReceivedIndex(receivedMusicIds);
					Log.i("pms decide receiver", "totalは " + total
							+ " recceivedは " + receivedMusicIds.length);
					receivedMusicIds = createMusic.createMusicCode(total);
				}
				Log.i("pms total", total + "");
				SharedPreferences sp = getSharedPreferences("totalPoint",
						MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putInt("totalPoint", total);
				editor.commit();
				// music を作成
				currentMusicIds = createMusic.createMusicCode(total);

				for (int i = 0; i < mps.length; i++) {
					final int j = i;
					mps[i] = MediaPlayer.create(getApplicationContext(),
							currentMusicIds[i]);
					if (Build.VERSION.SDK_INT <= 15 && i != mps.length - 1) {
						// APIレベル15以前の機種の場合の処理
						mps[i].setOnCompletionListener(new OnCompletionListener() {

							@Override
							public void onCompletion(MediaPlayer mp) {
								mps[j + 1].start();
								if (j == mps.length - 1) {
									stopSelf();
								}
							}
						});

					} else if (Build.VERSION.SDK_INT >= 16
							&& i != mps.length - 1) {
						// APIレベル16以降の機種の場合の処理
						mps[i].setNextMediaPlayer(mps[i + 1]);
						if (j == mps.length - 1) {
							mps[mps.length - 1]
									.setOnCompletionListener(new OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											stopSelf();
										}
									});
						}
					}
				}
			} else if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_PLAY)) {
				Log.i("play music service simple play ", "play");
				// 音楽再生
				if (receivedMps[0] == null ||!receivedMps[0].isPlaying() && !receivedMps[1].isPlaying()
						&& !receivedMps[2].isPlaying()
						&& !receivedMps[3].isPlaying()) {
					mps[0].start();
				}
			} else if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_COMUSE)) {
				// 受け取った音楽の再生処理
				Log.i("play music service comuse ", "play comuse");
				if (mps[0] == null || !mps[0].isPlaying() && !mps[1].isPlaying()
						&& !mps[2].isPlaying()
						&& !mps[3].isPlaying()) {

					for (int i = 0; i < receivedMps.length; i++) {
						final int j = i;
						Log.i("pms receivedMusicIds", receivedMusicIds.length
								+ "");
						// mediaplayerを作成
						receivedMps[i] = MediaPlayer.create(
								getApplicationContext(), receivedMusicIds[i]);
						if (Build.VERSION.SDK_INT <= 15
								&& i != receivedMps.length - 1) {
							// APIレベル15以前の機種の場合の処理
							receivedMps[i]
									.setOnCompletionListener(new OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											receivedMps[j + 1].start();
											if (j == receivedMps.length - 1) {
												stopSelf();
											}
										}
									});

						} else if (Build.VERSION.SDK_INT >= 16
								&& i != receivedMps.length - 1) {
							// APIレベル16以降の機種の場合の処理
							receivedMps[i]
									.setNextMediaPlayer(receivedMps[i + 1]);
							if (j == receivedMps.length - 1) {
								receivedMps[receivedMps.length - 1]
										.setOnCompletionListener(new OnCompletionListener() {

											@Override
											public void onCompletion(
													MediaPlayer mp) {
												stopSelf();
											}
										});
							}
						}
					}
					mps[0].start();
				}
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	// メディアプレイヤーのリソース解放
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mps[0] != null) {
			for (int i = 0; i < mps.length; i++) {
				mps[i].stop();
				mps[i].reset();
				mps[i].release();
				mps[i] = null;
			}
		}
		if (receivedMps[0] != null) {
			for (int i = 0; i < receivedMps.length; i++) {
				receivedMps[i].stop();
				receivedMps[i].reset();
				receivedMps[i].release();
				receivedMps[i] = null;
			}
		}
	}

}
