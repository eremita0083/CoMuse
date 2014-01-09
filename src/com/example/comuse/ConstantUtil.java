package com.example.comuse;

//定数ユーティリティークラス
public class ConstantUtil {

	private ConstantUtil() {
		throw new AssertionError();
	}

	// spのキー
	public final static String SHARED_PREF_POSITION_KEY = "position";
	// brのaction
	public final static String BROADCAST_ACTION_DECIDE_RECEIVER = "com.example.comuse.decide_receiver";
	// SMSの受信filter
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	// 音楽が誰かから届いたときにNotificationをクリックしたときのflag
	public static final int NOTIFICATION_FLAG_RECEIVED_MUSIC = 123;
	// SMS関連定数
	public static final String SMS_URI = "content://sms/";
	public static final String SMS_SENT_URI = "content://sms/sent";
	public static final String PDUS = "pdus";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_BODY = "body";
	// sms識別タグ
	public static final String SMS_TAG = "#CoMuse";
	// startserviceのアクション
	public static final String INTENT_START_SERVICE_PLAY = "com.example.comuse.play";
	public static final String INTENT_START_SERVICE_DECIDE = "com.example.comuse.decide";
	public static final String INTENT_START_SERVICE_DECIDE_RECEIVE = "com.example.comuse.decide.receive";
	public static final String INTENT_START_SERVICE_COMUSE = "com.example.comuse.comuse";
	// 曲終了のintentのアクション
	public static final String INTENT_END_MUSIC = "com.example.comuse.music_end";
	// 曲を贈るときのraw id のキー
	public static final String INTENT_PRESENT_MUSIC = "presentMusic";
	// complex prefのキー
	public static final String COMPLEX_PREF_KEY_MUSIC_INDEX = "musicIndex";
	public static final String COMPLEX_PREF_KEY_RECEIVED_MUSIC = "receivedMusic";
}
