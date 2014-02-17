package com.fromflash.samegame;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	
	public boolean soundOn;
	private  SoundPool mSoundPool; 
	private  HashMap<Integer, Integer> mSoundPoolMap; 
	private  AudioManager  mAudioManager;
	private  Context mContext;
	
	public SoundManager() {
	}
		
	public void initSounds(Context theContext) {
		soundOn = true;
		mContext = theContext;
	    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); 
	    mSoundPoolMap = new HashMap<Integer, Integer>(); 
	    mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	} 
	
	public void addSound(int index,int SoundID) {
		mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index) {
		if (!soundOn) return;
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f); 
	}
	
	public void playLoopedSound(int index) {
		if (!soundOn) return;
	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f); 
	}
	
}