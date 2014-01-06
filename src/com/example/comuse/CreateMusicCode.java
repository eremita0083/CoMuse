package com.example.comuse;

import java.util.Random;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import br.com.kots.mob.complex.preferences.ComplexPreferences;

public class CreateMusicCode {
	private int total;
	private Random rand = new java.util.Random();
	private Context myContext;
	private int[] randomIndex = new int[4];
	private int[] receivedIndex;

	CreateMusicCode(int total, Context context) {
		this.total = total;
		this.myContext = context;
	}

	public void setReceivedIndex(int[] receivedData) {
		this.receivedIndex = receivedData;
	}

	public int[] createMusicCode(int total) {
		int[] currentMusic = new int[4];
		ComplexPreferences cp = ComplexPreferences.getComplexPreferences(
				myContext, "musicIndex", Context.MODE_PRIVATE);
		//自分で作る音楽　パート
		if (receivedIndex == null) {
			Log.i("create music code", "simple music create");
			if (total == 0 || total == 1) {
				// M メジャー　tonic subdomi tonicの王道進行でいきたい
				if (total == 0) {
					randomIndex[0] = rand.nextInt(MusicCodeUtil.cIds.length);
					randomIndex[1] = rand.nextInt(MusicCodeUtil.gIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.amIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.fIds.length);
					currentMusic[0] = MusicCodeUtil.cIds[randomIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[randomIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.amIds[randomIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.fIds[randomIndex[3]]; // c f
				} else if (total == 1) {
					randomIndex[0] = rand.nextInt(MusicCodeUtil.fIds.length);
					randomIndex[1] = rand.nextInt(MusicCodeUtil.gIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.amIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.dIds.length);
					currentMusic[0] = MusicCodeUtil.fIds[randomIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[randomIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.amIds[randomIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.dIds[randomIndex[3]];
				}
			} else if (total == 2 || total == 3) {
				// m & M　２は元気づける感じ。3はやさしく包み込む感じ
				if (total == 2) { // TODO ?
					randomIndex[0] = rand.nextInt(MusicCodeUtil.cIds.length);
					randomIndex[1] = rand.nextInt(MusicCodeUtil.gIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.amIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.dIds.length);
					currentMusic[0] = MusicCodeUtil.cIds[randomIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[randomIndex[1]];// bb g
																			// e
																			// 例外
					currentMusic[2] = MusicCodeUtil.amIds[randomIndex[2]]; // c
																			// f
																			// 例外
					currentMusic[3] = MusicCodeUtil.dIds[randomIndex[3]]; // d
				} else if (total == 3) {
					randomIndex[0] = rand.nextInt(MusicCodeUtil.cIds.length);
					randomIndex[1] = rand.nextInt(MusicCodeUtil.bbIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.fIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.cIds.length);
					currentMusic[0] = MusicCodeUtil.cIds[randomIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.bbIds[randomIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[randomIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.cIds[randomIndex[3]]; // c f
				}
			} else if (total == 4 || total == 5) {
				// m マイナー主体。４は物憂げな感じ、５はエモーショナルに。
				randomIndex[0] = rand.nextInt(MusicCodeUtil.amIds.length);
				currentMusic[0] = MusicCodeUtil.amIds[randomIndex[0]]; // am
				if (total == 4) {
					randomIndex[1] = rand.nextInt(MusicCodeUtil.eIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.fIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.gmIds.length);
					currentMusic[1] = MusicCodeUtil.eIds[randomIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[randomIndex[2]]; // am
																			// c
																			// f
					currentMusic[3] = MusicCodeUtil.gmIds[randomIndex[3]]; // gm
																			// fm
				} else if (total == 5) {
					randomIndex[1] = rand.nextInt(MusicCodeUtil.gIds.length);
					randomIndex[2] = rand.nextInt(MusicCodeUtil.fIds.length);
					randomIndex[3] = rand.nextInt(MusicCodeUtil.eIds.length);
					currentMusic[1] = MusicCodeUtil.gIds[randomIndex[1]];// bb g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[randomIndex[2]]; // am
																			// c
																			// f
					currentMusic[3] = MusicCodeUtil.eIds[randomIndex[3]];// gm
																			// fm
				}
			}
			cp.putObject(ConstantUtil.COMPLEX_PREF_KEY_MUSIC_INDEX, randomIndex);
		} else {
			Log.i("create music code", "received music create");
			// 送られてきた音楽の生成　パート
			if (total == 0 || total == 1) {
				// M メジャー　tonic subdomi tonicの王道進行でいきたい
				if (total == 0) {
					currentMusic[0] = MusicCodeUtil.cIds[receivedIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[receivedIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.amIds[receivedIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.fIds[receivedIndex[3]]; // c f
				} else if (total == 1) {
					currentMusic[0] = MusicCodeUtil.fIds[receivedIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[receivedIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.amIds[receivedIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.dIds[receivedIndex[3]];
				}
			} else if (total == 2 || total == 3) {
				// m & M　２は元気づける感じ。3はやさしく包み込む感じ
				if (total == 2) {
					currentMusic[0] = MusicCodeUtil.cIds[receivedIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.gIds[receivedIndex[1]];// bb g
																			// e
																			// 例外
					currentMusic[2] = MusicCodeUtil.amIds[receivedIndex[2]]; // c
																			// f
																			// 例外
					currentMusic[3] = MusicCodeUtil.dIds[receivedIndex[3]]; // d
				} else if (total == 3) {
					currentMusic[0] = MusicCodeUtil.cIds[receivedIndex[0]]; // c f
					currentMusic[1] = MusicCodeUtil.bbIds[receivedIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[receivedIndex[2]]; // c
																			// am
																			// f
					currentMusic[3] = MusicCodeUtil.cIds[receivedIndex[3]]; // c f
				}
			} else if (total == 4 || total == 5) {
				// m マイナー主体。４は物憂げな感じ、５はエモーショナルに。
				currentMusic[0] = MusicCodeUtil.amIds[receivedIndex[0]]; // am
				if (total == 4) {
					currentMusic[1] = MusicCodeUtil.eIds[receivedIndex[1]]; // bb
																			// g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[receivedIndex[2]]; // am
																			// c
																			// f
					currentMusic[3] = MusicCodeUtil.gmIds[receivedIndex[3]]; // gm
																			// fm
				} else if (total == 5) {
					currentMusic[1] = MusicCodeUtil.gIds[receivedIndex[1]];// bb g
																			// e
					currentMusic[2] = MusicCodeUtil.fIds[receivedIndex[2]]; // am
																			// c
																			// f
					currentMusic[3] = MusicCodeUtil.eIds[receivedIndex[3]];// gm
																			// fm
				}
			}
			cp.putObject(ConstantUtil.COMPLEX_PREF_KEY_MUSIC_INDEX, receivedIndex);
		}
		cp.commit();
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
