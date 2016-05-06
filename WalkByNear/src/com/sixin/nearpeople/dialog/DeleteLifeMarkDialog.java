package com.sixin.nearpeople.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hogan.walkbynear.R;

public class DeleteLifeMarkDialog extends AlertDialog {
	private String message,message1,message2;
	private TextView lifeMark_name;
	private TextView btn_cancel;
	private TextView btn_ok;
	private OnPositiveClickListenner positiveClickListenner;
	private OnNagtiveClickListenner nagtiveClickListenner;

	public DeleteLifeMarkDialog(Context context) {
		super(context);
		this.setCancelable(false);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_del_life_mark);
		
		lifeMark_name = (TextView) findViewById(R.id.del_LifeMark);
		btn_cancel = (TextView) findViewById(R.id.alter_cancel);
		btn_ok = (TextView) findViewById(R.id.alter_ok);
		btn_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (nagtiveClickListenner != null) {
					nagtiveClickListenner.onNagtiveClick(DeleteLifeMarkDialog.this);
					
				} else {
					DeleteLifeMarkDialog.this.dismiss();
				}
			}
		});
		btn_ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (positiveClickListenner != null) {
					positiveClickListenner.onPositiveClick(DeleteLifeMarkDialog.this);
				}else {
					DeleteLifeMarkDialog.this.dismiss();
				}
			}
		});
	}

	public interface OnPositiveClickListenner {
		public void onPositiveClick(DeleteLifeMarkDialog dialog);
	}

	public interface OnNagtiveClickListenner {
		public void onNagtiveClick(DeleteLifeMarkDialog dialog);
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
}
