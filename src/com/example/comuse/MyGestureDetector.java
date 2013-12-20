package com.example.comuse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyGestureDetector implements GestureDetector.OnGestureListener {
	private List<String> nameList;
	private Context myContext;
	Map<String, String> sendList;
	ListView listView;
	private SharedPreferences sp;

	public MyGestureDetector(Context context) {
		myContext = context;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		nameList = new ArrayList<String>();
		sp = myContext.getSharedPreferences(
				ConstantUtil.SHARED_PREF_POSITION_KEY, Context.MODE_PRIVATE);
		sendList = makePhoneList();
		final Builder builder = new AlertDialog.Builder(myContext);
		builder.setView(LayoutInflater.from(myContext).inflate(
				R.layout.dialog_layout, null));
		final AlertDialog aDia = builder.create();
		aDia.show();
		listView = (ListView) aDia.findViewById(R.id.list_alert);
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setScrollBarStyle(ListView.SCROLLBARS_OUTSIDE_OVERLAY);

		final Button ok = (Button) aDia.findViewById(R.id.ok_btn_alert);
		final Button cancel = (Button) aDia.findViewById(R.id.cancel_btn_alert);
		MyCustomAdapter myAda = new MyCustomAdapter(myContext,
				R.layout.alert_dialog_layout, nameList);
		listView.setAdapter(myAda);

		// sendBroadcast
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (sp.getInt(ConstantUtil.SHARED_PREF_POSITION_KEY, -1) != -1) {
					Log.i("send broad gd", "送り主を決めたよ");
					String name = nameList.get(sp.getInt(
							ConstantUtil.SHARED_PREF_POSITION_KEY, -1));
					String phone = sendList.get(name);
					Log.i("name gd", name);
					Log.i("phone gd", phone);
					aDia.dismiss();
					Editor editor = sp.edit();
					editor.clear();
					editor.commit();
					Intent intent = new Intent(
							ConstantUtil.BROADCAST_ACTION_DECIDE_RECEIVER);
					intent.putExtra("name", name);
					intent.putExtra("phone", phone);
					myContext.sendBroadcast(intent);
					sp = null;
					Toast.makeText(myContext, name+"さんを送り先に設定したよ", Toast.LENGTH_SHORT)
					.show();
				} else {
					Toast.makeText(myContext, "音楽を贈る人を選んでね", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		// cancel
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				aDia.dismiss();
				sp = null;
			}
		});
		// 画面の横幅と縦幅を取得
		WindowManager wm = (WindowManager) myContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		int width = (int) (disp.getWidth() * 0.9);
		int height = (int) (disp.getHeight() * 0.8);
		// ダイアログに設定
		LayoutParams lp = aDia.getWindow().getAttributes();
		lp.width = width;
		lp.height = height;
		aDia.getWindow().setAttributes(lp);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	// phone listの作成
	private Map<String, String> makePhoneList() {
		Map<String, String> phoneList = new HashMap<String, String>();
		ContentResolver cr = myContext.getApplicationContext()
				.getContentResolver();
		Cursor cr1 = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		cr1.moveToFirst();
		while (cr1.moveToNext()) {
			int in = cr1.getColumnIndex("DISPLAY_NAME");
			int id = cr1.getColumnIndex("DATA1");
			String name = cr1.getString(in);
			String phone = cr1.getString(id);
			Log.i("name", name);
			Log.i("phone", phone);
			phone.trim();
			if (phone.contains("-")) {
				phone = phone.replace("-", "");
			}
			if (phone.matches("^080.*") || phone.matches("^090.*") || phone.matches("^070.*")) {
				nameList.add(name);
				phoneList.put(name, phone);
			} 
		}
		cr1.close();
		return phoneList;
	}

	// alert dialog用リストビューのカスタムアダプタ,.
	class MyCustomAdapter extends ArrayAdapter<String> {
		private int resourceId;

		public MyCustomAdapter(Context context, int resource,
				List<String> objects) {
			super(context, resource, objects);
			this.resourceId = resource;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final int POSITION = position;
			convertView = View.inflate(getContext(), resourceId, null);

			TextView nameText = (TextView) convertView
					.findViewById(R.id.name_alert);
			TextView phoneText = (TextView) convertView
					.findViewById(R.id.phone_alert);
			CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_alert);

			if (position == 0
					&& -1 == sp.getInt(ConstantUtil.SHARED_PREF_POSITION_KEY,
							-1)) {
				cb.setChecked(true);
			}

			String name = nameList.get(position);
			String phone = sendList.get(name);

			// checkのポジションを確認
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				Editor editor = sp.edit();

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (isChecked) {
						editor.putInt(ConstantUtil.SHARED_PREF_POSITION_KEY,
								POSITION);
						changeChoice();
					} else {
						editor.putInt(ConstantUtil.SHARED_PREF_POSITION_KEY, -1);
						changeChoice();
					}
					editor.commit();
				}
			});
			if (position == sp
					.getInt(ConstantUtil.SHARED_PREF_POSITION_KEY, -1)) {
				cb.setChecked(true);
			} else {
				cb.setChecked(false);
			}

			nameText.setText(name);
			phoneText.setText(phone);

			return convertView;
		}

		// 表示更新
		protected void changeChoice() {
			((BaseAdapter) listView.getAdapter()).notifyDataSetChanged();
		}

	}

}
