package com.example.comuse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

public class PlayMusicService extends Service implements Runnable {
	// ハンドラ 
	Handler handler = new Handler();
	// 音番号フラグ ☆追加☆
	private int seNo = 0;
	private int[] musicLoadIds;
	private long[] musicDuration = new long[4];
	private ScheduledExecutorService executorService = Executors
			.newSingleThreadScheduledExecutor();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			musicLoadIds = bundle.getIntArray("playlist");
			musicDuration = bundle.getLongArray("musicPlayTime");
			if (intent.getAction().equals(
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

	//TODO soundId　更新
	@Override
	public void run() {
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				switch (seNo) {
				case 0:
					// soundIdは受け渡す
					TopActivity.soundPools[0].play(musicLoadIds[0], 1, 1, 0, 0, 1);
					// TODO musicDurationを２曲目の曲の長さで更新
					seNo = 1;
					executorService.execute(this);
					break;
				case 1:
					TopActivity.soundPools[1].play(musicLoadIds[1], 1, 1, 0, 0, 1);
					// TODO musicDurationを３曲目の曲の長さで更新
					seNo = 2;
					executorService.execute(this);
					break;
				case 2:
					TopActivity.soundPools[2].play(musicLoadIds[2], 1, 1, 0, 0, 1);
					// TODO musicDurationを４曲目の曲の長さで更新
					seNo = 3;
					executorService.execute(this);
					break;
				case 3:
					TopActivity.soundPools[3].play(musicLoadIds[3], 1, 1, 0, 0, 1);
					Intent broadIntent = new Intent(ConstantUtil.INTENT_END_MUSIC);
					sendBroadcast(broadIntent);
					stopSelf();
				}
			}
		},musicDuration[seNo]);
	}

}
