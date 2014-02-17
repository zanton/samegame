package com.fromflash.samegame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class OpenScreen extends Activity {
	public static int GameState_Request = 0;
	static final int ABOUT_DIALOG_ID = 4;
	static final int HELP_DIALOG_ID = 5;
	static final int SCORE_DIALOG_ID = 6;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        
        setContentView(R.layout.main);
        
        Button btnNewGame = (Button) findViewById(R.id.btnNewGame);
        Button btnScore = (Button) findViewById(R.id.btnScore);
        Button btnHelp = (Button) findViewById(R.id.btnHelp);
        Button btnAbout = (Button) findViewById(R.id.btnAbout);
        
        btnNewGame.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		Intent intent = new Intent(OpenScreen.this, Samegame.class);
        		startActivityForResult(intent, GameState_Request);
        	}
        });
        btnHelp.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		showDialog(HELP_DIALOG_ID);
        	}
        });
        btnAbout.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		showDialog(ABOUT_DIALOG_ID);
        	}
        });
        btnScore.setOnClickListener(new View.OnClickListener(){
        	public void onClick(View v) {
        		showDialog(SCORE_DIALOG_ID);
        	}
        });
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode==GameState_Request) {
			
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case ABOUT_DIALOG_ID:
        	AboutDialog mAboutDialog = new AboutDialog(this);
        	return mAboutDialog;
        case HELP_DIALOG_ID:
        	HelpDialog mHelpDialog = new HelpDialog(this);
        	return mHelpDialog;
        case SCORE_DIALOG_ID:
        	ScoreDialog mScoreDialog = new ScoreDialog(this);
        	return mScoreDialog;
		}
		return null;
	}
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch (id) {
    	case SCORE_DIALOG_ID:
    		((ScoreDialog) dialog).updateScore();
    		break;
    	}
    }
}
