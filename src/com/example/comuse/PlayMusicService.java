package com.example.comuse;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import br.com.kots.mob.complex.preferences.ComplexPreferences;

public class PlayMusicService extends Service {
	// ハンドラ
	Handler handler = new Handler();
	// 音番号フラグ
	private int seNo = 0;
	private int[] loadIds = new int[4];
	private long[] musicDuration = new long[4];
	private SoundPool[] soundPools = new SoundPool[4];
	private long[] musicMiliSeconds;
	private Timer timer;

	private MediaPlayer mp, mp1, mp2, mp3;
	private int[] currentMusicIds;
	private MediaPlayer receivedMp, receivedMp1, receivedMp2, receivedMp3;
	private MediaPlayer[] mps = { mp, mp1, mp2, mp3 };
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
				int total = bundle.getInt("total");
				CreateMusicCode createMusic = new CreateMusicCode(total,getApplicationContext());
				if (intent.getAction().equals(
						ConstantUtil.INTENT_START_SERVICE_DECIDE_RECEIVE)) {
					receivedMusicIds = bundle.getIntArray("musicIndex");
					createMusic.setReceivedIndex(receivedMusicIds);
					
				}
				Log.i("pms total", total + "");
				SharedPreferences sp = getSharedPreferences("totalPoint",
						MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putInt("totalPoint", total);
				editor.commit();
				// music を作成
				currentMusicIds = createMusic.createMusicCode(total);
				for (int i = 0; i < currentMusicIds.length; i++) {
					musicDuration[i] = createMusic
							.getMusicMillis(currentMusicIds[i]);
				}
				for (int i = 0; i < soundPools.length; i++) {
					soundPools[i] = new SoundPool(2, AudioManager.STREAM_MUSIC,
							0);
					loadIds[i] = soundPools[i].load(getApplicationContext(),
							currentMusicIds[i], 1);
				}
				musicMiliSeconds = new long[4];
				for (int i = 0; i < musicMiliSeconds.length; ++i) {
					musicMiliSeconds[i] = createMusic
							.getMusicMillis(currentMusicIds[i]);
				}
				// soundpoolが動かなかった場合の代理コード
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
						// APIレベル11以降の機種の場合の処理
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
				// soundpoolが動かなかった場合の代理コード
				mps[0].start();

				timer = new Timer();
				timer.schedule(new myTimerTask(), System.currentTimeMillis());
			} else if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_COMUSE)) {
				Log.i("play music service comuse ", "play comuse");
				// 受け取った音楽のローディング処理
				ComplexPreferences cp = ComplexPreferences
						.getComplexPreferences(this,
								ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC,
								MODE_PRIVATE);
				String[] receivedData = cp.getObject(
						ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC,
						String[].class);

				// soundpoolが動かなかった場合の代理コード
				for (int i = 0; i < receivedMps.length; i++) {
					final int j = i;
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
						receivedMps[i].setNextMediaPlayer(receivedMps[i + 1]);
						if (j == receivedMps.length - 1) {
							receivedMps[receivedMps.length - 1]
									.setOnCompletionListener(new OnCompletionListener() {

										@Override
										public void onCompletion(MediaPlayer mp) {
											stopSelf();
										}
									});
						}
					}
				}
				mps[0].start();

				timer = new Timer();
				timer.schedule(new myTimerTask(), System.currentTimeMillis());
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mps != null) {
			for (int i = 0; i < mps.length; i++) {
				mps[i].stop();
				mps[i].reset();
				mps[i].release();
				mps[i] = null;
			}
		}
		if (receivedMps != null) {
			for (int i = 0; i < receivedMps.length; i++) {
				receivedMps[i].stop();
				receivedMps[i].reset();
				receivedMps[i].release();
				receivedMps[i] = null;
			}
		}
	}

	// Timer
	private class myTimerTask extends TimerTask {

		@Override
		public void run() {
			handler.post(new Runnable() {
				@Override
				public void run() {
					switch (seNo) {
					case 0:
						Log.i("play music service ", "1st phrase");
						soundPools[seNo].play(loadIds[0], 1, 1, 0, 0, 1);
						seNo = 1;
						playMusic(seNo);
						break;
					case 1:
						Log.i("play music service ", "2nd phrase");
						soundPools[seNo].play(loadIds[1], 1, 1, 0, 0, 1);
						seNo = 2;
						playMusic(seNo);
						break;
					case 2:
						Log.i("play music service ", "3st phrase");
						soundPools[seNo].play(loadIds[2], 1, 1, 0, 0, 1);
						seNo = 3;
						playMusic(seNo);
						break;
					case 3:
						Log.i("play music service ", "4st phrase");
						soundPools[seNo].play(loadIds[3], 1, 1, 0, 0, 1);
						Intent broadIntent = new Intent(
								ConstantUtil.INTENT_END_MUSIC);
						sendBroadcast(broadIntent);
						seNo = 0;
						stopSelf();
					}
				}
			});
		}

		// 次の曲を投げる
		private void playMusic(int musicMillSe) {
			timer = new Timer();
			timer.schedule(new myTimerTask(), musicMillSe);
		}
	}

}
