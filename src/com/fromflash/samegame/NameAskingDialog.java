package com.fromflash.samegame;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NameAskingDialog extends Dialog {
	private Context mContext;
	
	public NameAskingDialog(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.title_nameAsking);
		setContentView(R.layout.name);
		
		Button btn1 = (Button) findViewById(R.id.btn_nameAskingOk);
		btn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name_str = ((EditText) findViewById(R.id.nameAsking)).getText().toString();
				((Samegame) mContext).updateHighScore2(name_str);
				dismiss();
				((Samegame) mContext).showDialog(Samegame.SCORE_DIALOG_ID);
			}
		});
		
		Button btn2 = (Button) findViewById(R.id.btn_nameAskingCancel);
		btn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((Samegame) mContext).updateHighScore2(null);
				dismiss();
			}
		});
	}
}
