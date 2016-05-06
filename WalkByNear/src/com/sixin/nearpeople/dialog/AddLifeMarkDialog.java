package com.sixin.nearpeople.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.hogan.walkbynear.R;

public class AddLifeMarkDialog extends AlertDialog {
	private String message, message1, message2;
	private EditText lifeMark_name;
	private TextView btn_cancel;
	private TextView btn_ok;
	private OnPositiveClickListenner positiveClickListenner;
	private OnNagtiveClickListenner nagtiveClickListenner;

	public AddLifeMarkDialog(Context context) {
		super(context);
		this.setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_add_life_mark);

		lifeMark_name = (EditText) findViewById(R.id.add_LifeMark_name);
		lifeMark_name.requestFocus(View.FOCUS_DOWN);
		btn_cancel = (TextView) findViewById(R.id.alter_cancel);
		btn_ok = (TextView) findViewById(R.id.alter_ok);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (nagtiveClickListenner != null) {
					nagtiveClickListenner
							.onNagtiveClick(AddLifeMarkDialog.this);

				} else {
					AddLifeMarkDialog.this.dismiss();
				}
			}
		});
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (positiveClickListenner != null) {
					positiveClickListenner
							.onPositiveClick(AddLifeMarkDialog.this);
				} else {
					AddLifeMarkDialog.this.dismiss();
				}
			}
		});
		lifeMark_name.setFocusable(true);
		lifeMark_name.setFocusableInTouchMode(true);
		lifeMark_name.requestFocus();
		InputMethodManager inputManager = (InputMethodManager) lifeMark_name
				.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(lifeMark_name, 0);
	}

	public interface OnPositiveClickListenner {
		public void onPositiveClick(AddLifeMarkDialog dialog);
	}

	public interface OnNagtiveClickListenner {
		public void onNagtiveClick(AddLifeMarkDialog dialog);
	}

	public void setOnPositiveClickListenner(
			OnPositiveClickListenner positiveClickListenner) {
		this.positiveClickListenner = positiveClickListenner;
	}

	public void setOnNagtiveClickListenner(
			OnNagtiveClickListenner nagtiveClickListenner) {
		this.nagtiveClickListenner = nagtiveClickListenner;
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

	public String getLifeMarkName() {
		return this.lifeMark_name.getText().toString().trim();
	}
	public void showKeyboard() {
		if(lifeMark_name!=null){
			//设置可获得焦点
			lifeMark_name.setFocusable(true);
			lifeMark_name.setFocusableInTouchMode(true);
			//请求获得焦点
			lifeMark_name.requestFocus();
			//调用系统输入法
			InputMethodManager inputManager = (InputMethodManager) lifeMark_name
					.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(lifeMark_name, 0);
		}
	}
}
