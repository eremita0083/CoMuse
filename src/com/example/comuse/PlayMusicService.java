package com.example.comuse;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PlayMusicService extends Service implements Runnable {
	// ハンドラ
	Handler handler = new Handler();
	// 音番号フラグ
	private int seNo = 0;
	private int[] loadIds = new int[4];
	private long[] musicDuration = new long[4];
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();
	private SoundPool[] soundPools = new SoundPool[4];
	private long[] musicMiliSeconds;
	private int[] currentMusicIds;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_DECIDE)) {
				Bundle bundle = intent.getExtras();
				int total = bundle.getInt("total");
				CreateMusicCode createMusic = new CreateMusicCode(total,
						getApplicationContext());
				Log.i("total",total+"");
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
			} else if (intent.getAction().equals(
					ConstantUtil.INTENT_START_SERVICE_PLAY)) {
				executorService.execute(this);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		executorService.shutdown();
	}

	// TODO soundId　更新
	@Override
	public void run() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				switch (seNo) {
				case 0:
					// soundIdは受け渡す
					soundPools[0].play(loadIds[0], 1, 1, 0, 0, 1);
					seNo = 1;
					playMusic();
					break;
				case 1:
					soundPools[1].play(loadIds[1], 1, 1, 0, 0, 1);
					seNo = 2;
					playMusic();
					break;
				case 2:
					soundPools[2].play(loadIds[2], 1, 1, 0, 0, 1);
					seNo = 3;
					playMusic();
					break;
				case 3:
					soundPools[3].play(loadIds[3], 1, 1, 0, 0, 1);
					Intent broadIntent = new Intent(
							ConstantUtil.INTENT_END_MUSIC);
					sendBroadcast(broadIntent);
					seNo=0;
					stopSelf();
				}
			}
		}, musicDuration[seNo]);
	}
	
	private void playMusic(){
		Intent intent = new Intent(getApplicationContext(), PlayMusicService.class);
		intent.setAction(ConstantUtil.INTENT_START_SERVICE_PLAY);
		startService(intent);
	}

}
