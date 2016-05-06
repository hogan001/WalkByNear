package com.sixin.nearpeople.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.hogan.walkbynear.R;

public class ChooseDialog extends AlertDialog {
	private String message,message1,message2;
	private TextView tips_loading_msg,alert_gift_coin,alert_message;
	private TextView btn_left;
	private TextView btn_right;
	private OnPositiveClickListenner positiveClickListenner;
	private OnNagtiveClickListenner nagtiveClickListenner;

	protected ChooseDialog(Context context) {
		super(context);
	}

	public ChooseDialog(Context context, String message,String message1,String message2) {
		super(context);
		this.message = message;
		this.message1 =message1;
		this.message2 =message2;
		this.setCancelable(false);
	}

	public ChooseDialog(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);  
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alertdialog_updatemoblic);
		
		tips_loading_msg = (TextView) findViewById(R.id.textView1);
		tips_loading_msg.setText(message);
		
		alert_gift_coin = (TextView) findViewById(R.id.alert_gift_coin);
		alert_gift_coin.setText(message1);
		
		alert_message = (TextView) findViewById(R.id.alert_message);
		alert_message.setText(message2);
		
		btn_left = (TextView) findViewById(R.id.alter_on);
		btn_right = (TextView) findViewById(R.id.alter_ok);
		btn_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (nagtiveClickListenner != null) {
					nagtiveClickListenner.onNagtiveClick(ChooseDialog.this);
				} else {
					ChooseDialog.this.dismiss();
				}
			}
		});
		btn_right.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (positiveClickListenner != null) {
					positiveClickListenner.onPositiveClick(ChooseDialog.this);
				}else {
					ChooseDialog.this.dismiss();
				}
			}
		});
	}

	public interface OnPositiveClickListenner {
		public void onPositiveClick(ChooseDialog dialog);
	}

	public interface OnNagtiveClickListenner {
		public void onNagtiveClick(ChooseDialog dialog);
	}

	public void setOnPositiveClickListenner(
			OnPositiveClickListenner positiveClickListenner) {
		this.positiveClickListenner = positiveClickListenner;
	}

	public void setOnNagtiveClickListenner(
			OnNagtiveClickListenner nagtiveClickListenner) {
		this.nagtiveClickListenner = nagtiveClickListenner;
	}

	public void setLeftButtonText(CharSequence text) {
		btn_left.setText(text);
	}

	public void setRightButtonText(CharSequence text) {
		btn_right.setText(text);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			this.dismiss();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
