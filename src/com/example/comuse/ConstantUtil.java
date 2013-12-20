package com.example.comuse;

public class ConstantUtil {
	
	private ConstantUtil(){
		throw new AssertionError();
	}
	//spのキー
	public final static String SHARED_PREF_POSITION_KEY = "position";
	//brのaction
	public final static String BROADCAST_ACTION_DECIDE_RECEIVER = "com.example.comuse.decide_receiver";
	//SMSの受信filter
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	//音楽が誰かから届いたときにNotificationをクリックしたときのflag
	public static final int NOTIFICATION_FLAG_RECEIVED_MUSIC = 123;
	//SMS関連定数
	public static final String SMS_URI = "content://sms/";
    public static final String SMS_SENT_URI = "content://sms/sent";
    public static final String PDUS = "pdus";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_BODY = "body";
    //sms識別タグ
    public static final String SMS_TAG = "#CoMuse:";

}
