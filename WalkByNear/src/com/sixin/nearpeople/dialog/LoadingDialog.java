package com.sixin.nearpeople.dialog;

import com.hogan.walkbynear.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;




/**
 *  登陸專用Dialog
 * 
 *
 * @author ch
 * 
 * 
 * @Createtime 2014-11-29 14:14:34
 *
 */
public class LoadingDialog extends AlertDialog {

    private TextView tips_loading_msg;

    private String message = null;

    public LoadingDialog(Context context) {
        super(context);
        message = getContext().getResources().getString(R.string.msg_load_ing);
    }

    public LoadingDialog(Context context, String message) {
        super(context);
        this.message = message;
        this.setCancelable(false);
        
       
    }

    public LoadingDialog(Context context, int theme, String message) {
        super(context, theme);
        this.message = message;
        this.setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.view_tips_loading);
        tips_loading_msg = (TextView) findViewById(R.id.tips_loading_msg);
        tips_loading_msg.setText(this.message);
    }

    public void setText(String message) {
        this.message = message;
        tips_loading_msg.setText(this.message);
    }

    public void setText(int resId) {
        setText(getContext().getResources().getString(resId));
    }
    
    private Activity activity;
    public void setFinishedActivity(Activity activity){
    	this.activity = activity;
    }
    
    /* (重写父类方法)
     * @see android.app.AlertDialog#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (activity!=null) {
    		activity.finish();
    		this.activity = null;
		}
    	return super.onKeyDown(keyCode, event);
    }
}
