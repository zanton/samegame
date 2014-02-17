package com.fromflash.samegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Samegame extends Activity {
    
	static final int READY_DIALOG_ID = 0;
	static final int PAUSED_DIALOG_ID = 1;
	static final int LOSE_DIALOG_ID = 2;
	static final int WIN_DIALOG_ID = 3;
	static final int ABOUT_DIALOG_ID = 4;
	static final int HELP_DIALOG_ID = 5;
	static final int SCORE_DIALOG_ID = 6;
	static final int NAME_DIALOG_ID = 7;
	
	static final int SOUND_CLICK_ID = 1;
	static final int SOUND_2ND_CLICK_ID = 2;
	static final int SOUND_MISTAKE_ID = 3;
	static final int SOUND_NEWGAME_ID = 4;
	static final int SOUND_GAMEOVER_ID = 5;
	
	private SameView mSameView;
	public SoundManager mSoundManager;
	private ScoreDialog mScoreDialog;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
        setContentView(R.layout.game);
        
        mSoundManager = new SoundManager();
        mSoundManager.initSounds(getBaseContext());
        mSoundManager.addSound(SOUND_CLICK_ID, R.raw.sound_click);
        mSoundManager.addSound(SOUND_2ND_CLICK_ID, R.raw.sound_2ndclick);
        mSoundManager.addSound(SOUND_MISTAKE_ID, R.raw.sound_mistake);
        mSoundManager.addSound(SOUND_NEWGAME_ID, R.raw.sound_newgame);
        mSoundManager.addSound(SOUND_GAMEOVER_ID, R.raw.sound_gameover);
        
        mSameView = (SameView) findViewById(R.id.sameview);
        mSameView.setTextViews((TextView) findViewById(R.id.score), 
        						(TextView) findViewById(R.id.norma));
        mSameView.setActivity(this);
        mSameView.setSoundManager(mSoundManager);
        //mSameView.setMode(SameView.READY);
        
        mScoreDialog = new ScoreDialog(this);
  	}
    
    public void updateHighScore() {
    	//check if the new score is in top 5
    	int pos = mScoreDialog.checkNewScore(mSameView.mScore);
    	if (pos>5) {
    		mSameView.setMode(SameView.RUNNING);
    		return;
    	}
    	
    	//call dialog asking name
    	showDialog(NAME_DIALOG_ID);
    }
    
    public void updateHighScore2(String name_str) {
    	//write out
    	if (name_str!=null) mScoreDialog.appendNewScore(mSameView.mScore, name_str);
    	mSameView.setMode(SameView.RUNNING);
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	
        switch (id) {
        case READY_DIALOG_ID:
        	builder.setMessage(R.string.ready_dialog_text);
        	builder.setPositiveButton(R.string.ready_dialog_button_text, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		@Override
        		public void onCancel(DialogInterface dialog) {
        			mSameView.setMode(SameView.RUNNING);
        		}
        	});
            break;
        case PAUSED_DIALOG_ID:
        	builder.setMessage(R.string.paused_dialog_text);
        	builder.setPositiveButton(R.string.paused_dialog_button_text, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		@Override
        		public void onCancel(DialogInterface dialog) {
        			mSameView.setMode(SameView.RUNNING);
        		}
        	});
            break;
        case WIN_DIALOG_ID:
        	CharSequence winText = this.getResources().getString(R.string.lose_dialog_text) + ((TextView) findViewById(R.id.score)).getText() + "     ";
        	builder.setMessage(winText);
        	builder.setPositiveButton(R.string.win_dialog_button_text, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		@Override
        		public void onCancel(DialogInterface dialog) {
        			updateHighScore();
        		}
        	});
            break;
        case LOSE_DIALOG_ID:
        	CharSequence loseText = this.getResources().getString(R.string.lose_dialog_text) + ((TextView) findViewById(R.id.score)).getText() + "     ";
        	builder.setMessage(loseText);
        	//builder.setView((TextView) findViewById(R.id.score));
        	builder.setPositiveButton(R.string.lose_dialog_button_text, 
        			new DialogInterface.OnClickListener() {
        				public void onClick(DialogInterface dialog, int id) {
        					dialog.cancel();
        				}
        			});
        	builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
        		@Override
        		public void onCancel(DialogInterface dialog) {
        			updateHighScore();
        		}
        	});
            break;
        case ABOUT_DIALOG_ID:
        	AboutDialog mAboutDialog = new AboutDialog(this);
        	return mAboutDialog;
        case HELP_DIALOG_ID:
        	HelpDialog mHelpDialog = new HelpDialog(this);
        	return mHelpDialog;
        case SCORE_DIALOG_ID:
        	mScoreDialog.updateScore();
        	return mScoreDialog;
        case NAME_DIALOG_ID:
        	NameAskingDialog mNameAskingDialog = new NameAskingDialog(this);
        	return mNameAskingDialog;
        }
        return builder.create();
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch (id) {
    	case WIN_DIALOG_ID:
    		CharSequence winText = this.getResources().getString(R.string.win_dialog_text) + ((TextView) findViewById(R.id.score)).getText() + "     ";
    		((AlertDialog) dialog).setMessage(winText);
    		break;
    	case LOSE_DIALOG_ID:
    		CharSequence loseText = this.getResources().getString(R.string.lose_dialog_text) + ((TextView) findViewById(R.id.score)).getText() + "     ";
    		((AlertDialog) dialog).setMessage(loseText);
    		break;
    	case SCORE_DIALOG_ID:
    		((ScoreDialog) dialog).updateScore();
    		break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu) {
    	menu.findItem(R.id.two).setChecked(false);
    	menu.findItem(R.id.three).setChecked(false);
    	menu.findItem(R.id.four).setChecked(false);
    	menu.findItem(R.id.five).setChecked(false);
    	
    	switch (mSameView.getKumaTypeNum()) {
    	case 2: 
    		menu.findItem(R.id.two).setChecked(true);
    		break;
    	case 3:
    		menu.findItem(R.id.three).setChecked(true);
    		break;
    	case 4:
    		menu.findItem(R.id.four).setChecked(true);
    		break;
    	case 5:
    		menu.findItem(R.id.five).setChecked(true);
    		break;
    	}
    	
    	menu.findItem(R.id.sound_on).setChecked(false);
    	menu.findItem(R.id.sound_off).setChecked(false);
    	
    	if (mSoundManager.soundOn) 
    		menu.findItem(R.id.sound_on).setChecked(true);
    	else menu.findItem(R.id.sound_off).setChecked(true);
    	
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.menu_item1:
        	mSameView.setMode(SameView.NEWGAME);
            return true;
        case R.id.menu_item2:
        	showDialog(HELP_DIALOG_ID);
            return true;
        case R.id.menu_item3:
        	showDialog(ABOUT_DIALOG_ID);
        	return true;
        case R.id.two:
        	mSameView.setKumaTypeNum(2);
        	mScoreDialog.setLevel(2);
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSameView.setMode(SameView.NEWGAME);
        	}
            return true;
        case R.id.three:
        	mSameView.setKumaTypeNum(3);
        	mScoreDialog.setLevel(3);
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSameView.setMode(SameView.NEWGAME);
        	}
            return true;
        case R.id.four:
        	mSameView.setKumaTypeNum(4);
        	mScoreDialog.setLevel(4);
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSameView.setMode(SameView.NEWGAME);
        	}
        	return true;
        case R.id.five:
        	mSameView.setKumaTypeNum(5);
        	mScoreDialog.setLevel(5);
        	if (!item.isChecked()) {
        		item.setChecked(true);
        		mSameView.setMode(SameView.NEWGAME);
        	}
            return true;
        case R.id.sound_on:
        	mSoundManager.soundOn = true;
        	return true;
        case R.id.sound_off:
        	mSoundManager.soundOn = false;
        	return true;
        case R.id.menu_item6:
        	showDialog(SCORE_DIALOG_ID);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}