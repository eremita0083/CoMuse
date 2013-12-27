package com.example.comuse;

import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;

public class CreateMusicCode {
	private int total;
	private Random rand = new java.util.Random();
	private Context myContext;

	CreateMusicCode(int total,Context context) {
		this.total = total;
		this.myContext = context;
	}

	public int[] createMusicCode(int total) {
		int[] currentMusic = new int[4];
		if (total == 0 || total == 1) {
			// M メジャー　tonic subdomi tonicの王道進行でいきたい
			if (total == 0) {
				currentMusic[0] = MusicCodeUtil.cIds[rand
						.nextInt(MusicCodeUtil.cIds.length)]; // c f
				currentMusic[1] = MusicCodeUtil.gIds[rand
						.nextInt(MusicCodeUtil.gIds.length)]; // bb g e
				currentMusic[2] = MusicCodeUtil.amIds[rand
						.nextInt(MusicCodeUtil.amIds.length)]; // c am f
				currentMusic[3] = MusicCodeUtil.fIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // c f
			} else if (total == 1) {
				currentMusic[0] = MusicCodeUtil.fIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // c f
				currentMusic[1] = MusicCodeUtil.gIds[rand
						.nextInt(MusicCodeUtil.gIds.length)];// bb g e 例外
				currentMusic[2] = MusicCodeUtil.amIds[rand
						.nextInt(MusicCodeUtil.amIds.length)]; // c f 例外
				currentMusic[3] = MusicCodeUtil.dIds[rand
						.nextInt(MusicCodeUtil.dIds.length)]; // d
			}
		} else if (total == 2 || total == 3) {
			// m & M　２は元気づける感じ。3はやさしく包み込む感じ
			if (total == 2) {
				currentMusic[0] = MusicCodeUtil.cIds[rand
						.nextInt(MusicCodeUtil.cIds.length)]; // c f
				currentMusic[1] = MusicCodeUtil.gIds[rand
						.nextInt(MusicCodeUtil.gIds.length)];// bb g e 例外
				currentMusic[2] = MusicCodeUtil.amIds[rand
						.nextInt(MusicCodeUtil.amIds.length)]; // c f 例外
				currentMusic[3] = MusicCodeUtil.dIds[rand
						.nextInt(MusicCodeUtil.dIds.length)]; // d
			} else if (total == 3) {
				currentMusic[0] = MusicCodeUtil.cIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // c f
				currentMusic[1] = MusicCodeUtil.bbIds[rand
						.nextInt(MusicCodeUtil.bbIds.length)]; // bb g e
				currentMusic[2] = MusicCodeUtil.fIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // c am f
				currentMusic[3] = MusicCodeUtil.cIds[rand
						.nextInt(MusicCodeUtil.cIds.length)]; // c f
			}
		} else if (total == 4 || total == 5) {
			// m マイナー主体。４は物憂げな感じ、５はエモーショナルに。
			currentMusic[0] = MusicCodeUtil.amIds[rand
					.nextInt(MusicCodeUtil.amIds.length)]; // am
			if (total == 4) {
				currentMusic[1] = MusicCodeUtil.eIds[rand
						.nextInt(MusicCodeUtil.eIds.length)]; // bb g e
				currentMusic[2] = MusicCodeUtil.fIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // am c f
				currentMusic[3] = MusicCodeUtil.gmIds[rand
						.nextInt(MusicCodeUtil.gmIds.length)]; // gm fm
			} else if (total == 5) {
				currentMusic[1] = MusicCodeUtil.gIds[rand
						.nextInt(MusicCodeUtil.gIds.length)];// bb g e
				currentMusic[2] = MusicCodeUtil.fIds[rand
						.nextInt(MusicCodeUtil.fIds.length)]; // am c f
				currentMusic[3] = MusicCodeUtil.eIds[rand
						.nextInt(MusicCodeUtil.eIds.length)];// gm fm
			}
		}
		return currentMusic;
	}

	// 曲の長さを取得
	public int getMusicMillis(int resId) {
		MediaPlayer mp = MediaPlayer.create(myContext, resId);
		int musicLength = mp.getDuration();
		mp.stop();
		mp.reset();
		mp.release();
		mp = null;
		return musicLength;
	}

}
