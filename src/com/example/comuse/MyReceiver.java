package com.example.comuse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import br.com.kots.mob.complex.preferences.ComplexPreferences;

public class MyReceiver extends BroadcastReceiver {
	private String body, from;
	private ComplexPreferences cp;

	@Override
	public void onReceive(Context context, Intent intent) {
		// bundleがnullなら何もしない
		if (intent.getAction().equals(ConstantUtil.SMS_RECEIVED_ACTION)) {
			// SMSをよみタグがついているかを確認
			cp = ComplexPreferences.getComplexPreferences(context,
					ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC,
					Context.MODE_PRIVATE);
			Bundle extras = intent.getExtras();
			if (extras != null) {
				// pduのデコードとログ出力
				Object[] pdus = (Object[]) extras.get(ConstantUtil.PDUS);
				for (Object pdu : pdus) {
					SmsMessage smsMessage = SmsMessage
							.createFromPdu((byte[]) pdu);
					from = smsMessage.getOriginatingAddress();
					body = smsMessage.getMessageBody().replaceAll("\n", "\t");
					Log.i("from", from);
					Log.i("body", body);
				}
			}
			// ノーてぃふぃけしょんでSMSが届いたら通知。
			if (body.contains(ConstantUtil.SMS_TAG)) {
				//送られてきたデータを保存
				String[] receiveData = {from,body};
				cp.putObject(ConstantUtil.COMPLEX_PREF_KEY_RECEIVED_MUSIC, receiveData);
				cp.commit();
				//ノーティふぃけーしょんを出す
				NotificationManager notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification notification = new Notification(
						R.drawable.ic_launcher, // TODO あとでアイコン変える
						"CoMuse：Music For You", System.currentTimeMillis());
				Intent intent1 = new Intent(context, TopActivity.class);
				intent1.putExtra("from", from); // 誰から来たか
				intent1.putExtra("body", body); // メールの本文
				intent1.putExtra("fromNotificaton",
						ConstantUtil.NOTIFICATION_FLAG_RECEIVED_MUSIC);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				PendingIntent contentIntent = PendingIntent.getActivity(
						context, 0, intent1,
						ConstantUtil.NOTIFICATION_FLAG_RECEIVED_MUSIC);
				notification.setLatestEventInfo(context, "CoMuse", from
						+ "から音楽が届いたよ", contentIntent);
				notificationManager.notify(R.string.app_name, notification);
			}

		}

	}

}
