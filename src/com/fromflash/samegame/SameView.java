package com.fromflash.samegame;

import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * SameView: implementation of a simple game of Samegame
 * 
 * 
 */
public class SameView extends TileView {
	
	/**
     * Labels for the drawables that will be loaded into the TileView class
     */
	private int KUMA_TYPE_NUM = 6;
	private static final int KUMA_STATE_NUM = 4;
    private static final int BLUE_KUMA = 1;
    private static final int GREEN_KUMA = 2;
    private static final int PINK_KUMA = 3;
    private static final int RED_KUMA = 4;
    private static final int VIOLET_KUMA = 5;
    
    //constants for mMode
    public static final int READY = 0;
	public static final int PAUSED = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    public static final int WIN = 4;
    public static final int NEWGAME = 5;
    
    //constants for period[]
	private static final int FALLING = 4;
	private static final int SLIDING = 5;
	private static final int CHOOSE_RANDOM = 6;
	
	//constants for animState[][]
	private static final int NORMAL = 0;
	private static final int RANDOM_ANIM = 1;
	private static final int PRESSED = 2;
	private static final int GONNA_DIE = 3;
	
	//constants for mFallSlideState[][]
	private static final int NO_FALLSLIDE = 0;
	private static final int FALLING_KUMA = 1;
	private static final int SLIDING_KUMA = 2;
	private static final int WAIT_FOR_EXCHANGE = 3;
	
	private int fallslide_vel = 7; //pixel per pulse
	private int[] period = {15, 15, 25, 15, 2, 1, 5};
	private int[][] animState;
	private int[][] pulseCount;
	private int[][] mFallSlideState;
	private int[][] fallslideTarget;
	
	private int mMode;
	private Queue queue = new Queue(mTileCount);
	private Random mRandom = new Random();
	private SoundManager mSoundManager;
	private int fallPulseCount;
	private int randomPulseCount = 0;
	private boolean drop_flag = false;
	private boolean slide_flag = false;
	private boolean del_flag = false;
	private boolean timeThreadIsRunning = false;
	public int mScore = 0;
    
    private TextView mScoreText;
    private TextView mNormaText;
    private Activity mActivity;
    
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		update();
    		invalidate();
    	}
    };
    
    Thread timeThread = new Thread(new Runnable() {
    	public void run() {
    		while (true) 
	    		try {
	    			Thread.sleep(10);
	    			mHandler.sendMessage(mHandler.obtainMessage());
	    		} catch (Throwable t) {}
    	}
    });
    
	public SameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initSameView();
   }

    public SameView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	initSameView();
    }

    //reset mTileArray chua cac loai kuma
    private void initSameView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();
        
        resetTiles(KUMA_TYPE_NUM, KUMA_STATE_NUM);
        loadTile(BLUE_KUMA, 0, r.getDrawable(R.drawable.blue_kuma0));
        loadTile(BLUE_KUMA, 1, r.getDrawable(R.drawable.blue_kuma1));
        loadTile(BLUE_KUMA, 2, r.getDrawable(R.drawable.blue_kuma2));
        loadTile(BLUE_KUMA, 3, r.getDrawable(R.drawable.kuma_finalstate));
        
        loadTile(GREEN_KUMA, 0, r.getDrawable(R.drawable.green_kuma0));
        loadTile(GREEN_KUMA, 1, r.getDrawable(R.drawable.green_kuma1));
        loadTile(GREEN_KUMA, 2, r.getDrawable(R.drawable.green_kuma2));
        loadTile(GREEN_KUMA, 3, r.getDrawable(R.drawable.kuma_finalstate));
        
        loadTile(PINK_KUMA, 0, r.getDrawable(R.drawable.pink_kuma0));
        loadTile(PINK_KUMA, 1, r.getDrawable(R.drawable.pink_kuma1));
        loadTile(PINK_KUMA, 2, r.getDrawable(R.drawable.pink_kuma2));
        loadTile(PINK_KUMA, 3, r.getDrawable(R.drawable.kuma_finalstate));
        
        loadTile(RED_KUMA, 0, r.getDrawable(R.drawable.red_kuma0));
        loadTile(RED_KUMA, 1, r.getDrawable(R.drawable.red_kuma1));
        loadTile(RED_KUMA, 2, r.getDrawable(R.drawable.red_kuma2));
        loadTile(RED_KUMA, 3, r.getDrawable(R.drawable.kuma_finalstate));
        
        loadTile(VIOLET_KUMA, 0, r.getDrawable(R.drawable.violet_kuma0));
        loadTile(VIOLET_KUMA, 1, r.getDrawable(R.drawable.violet_kuma1));
        loadTile(VIOLET_KUMA, 2, r.getDrawable(R.drawable.violet_kuma2));
        loadTile(VIOLET_KUMA, 3, r.getDrawable(R.drawable.kuma_finalstate));
        
        for (int i=0; i<KUMA_STATE_NUM; i++) 
        	loadTile(NO_KUMA, i, null);
        
        setKumaTypeNum(3);
    }
    
    public void setTextViews(TextView score, TextView norma) {
    	mScoreText = score;
    	mNormaText = norma;
    }
    
    public void setActivity(Activity act) {
    	mActivity = act;
    }
    
    public void setSoundManager(SoundManager sm) {
    	mSoundManager = sm;
    }
    
    public void setMode(int newMode) {
		int oldMode = mMode;
		mMode = newMode;
		if (newMode==READY) {
			initReady();
			mActivity.showDialog(Samegame.READY_DIALOG_ID);
		} else if (newMode==WIN) {
			//mSoundManager.playSound(Samegame.SOUND_GAMEOVER_ID);
			mActivity.showDialog(Samegame.WIN_DIALOG_ID);
		} else if (newMode==LOSE) {
			//mSoundManager.playSound(Samegame.SOUND_GAMEOVER_ID);
			mActivity.showDialog(Samegame.LOSE_DIALOG_ID);
		} else if (oldMode==READY && newMode==RUNNING) {
			initNewGame();
			if (!timeThreadIsRunning) {
	    		timeThreadIsRunning = true;
	    		timeThread.start();
	    	}
		} else if ((oldMode==WIN || oldMode==LOSE) && newMode==RUNNING) {
			resetNewGame();
		} else if (oldMode==RUNNING && newMode==NEWGAME) {
			resetNewGame();
			mMode = RUNNING;
		}
	}

    private void initReady() {
    	updateScore();
    	//so luong kuma moi loai
    	int everage = mTileCount/(KUMA_TYPE_NUM-1);
    	
    	//set random everage kuma cho moi loai tu 1 toi KUMA_TYPE_NUM-1
    	for (int i=1; i<KUMA_TYPE_NUM-1; i++)
    		for (int j=0; j<everage; j++) {
    			boolean chosen = false;
    			int count = 0;
    			while (!chosen || count>mTileCount) {
    				count++;
	    			int t = mRandom.nextInt(mTileCount);
	    			int y = t/mXTileCount;
	    			int x = t - y*mXTileCount;
	    			if (mTileType[x][y]==NO_KUMA) {
	    				chosen = true;
	    				mTileType[x][y]=i;
	    			}
    			}
    		}
    	
    	//set loai cuoi cug cho nhug cho emty con lai
    	for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++)
    			if (mTileType[x][y]==NO_KUMA)
    				mTileType[x][y]=KUMA_TYPE_NUM-1;
    }
    
    private void initNewGame() {
    	animState  = new int[mXTileCount][mYTileCount];
    	pulseCount = new int[mXTileCount][mYTileCount];
    	mFallSlideState = new int[mXTileCount][mYTileCount];
    	fallslideTarget = new int[mXTileCount][mYTileCount];
    	for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) {
    			animState[x][y] = NORMAL;
    			pulseCount[x][y] = 0;
    			mFallSlideState[x][y] = NO_FALLSLIDE;
    			fallslideTarget[x][y] = 0;
    		}
    	//mSoundManager.playSound(Samegame.SOUND_NEWGAME_ID);
    }
    
    private void resetNewGame() {
    	mScore = 0;
    	resetTileView();
    	initReady();
    	for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) {
    			animState[x][y] = NORMAL;
    			pulseCount[x][y] = 0;
    			mFallSlideState[x][y] = NO_FALLSLIDE;
    			fallslideTarget[x][y] = 0;
    		}
    	//mSoundManager.playSound(Samegame.SOUND_NEWGAME_ID);
    }
    
    public void update() {
    	updateTileState();
    	if (mTileCount>0) animateRandomKuma();
    	if (drop_flag) {
    		dropKuma();
    		if (!drop_flag) 
    			initSlideKuma();
    	}
    	if (slide_flag) 
    		slideKuma();
    	if (!drop_flag && !slide_flag && mMode==RUNNING) checkStalemate();
    }
    
    private void updateTileState() {
    	//chuyen state ve 0 cho cac kuma da qua transitional period, 
    	//va update state cho pressed kuma va gonnadie kuma
    	for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) 
    			if (mTileType[x][y]!=NO_KUMA && animState[x][y]!=NORMAL) {
	    				pulseCount[x][y] += 1;
	    				if (pulseCount[x][y]==period[mTileState[x][y]]) {
	    					pulseCount[x][y] = 0;
	    					if (animState[x][y]==RANDOM_ANIM) {
	        					mTileState[x][y] = 0;
	    						animState[x][y] = NORMAL;
	        				}
	        				else if (animState[x][y]==PRESSED){
	        					mTileState[x][y] += 1;
	        					if (mTileState[x][y]>2) mTileState[x][y] = 0;
	        				}
	        				else if (animState[x][y]==GONNA_DIE) {
	        					mTileState[x][y] += 1;
	    						if (mTileState[x][y]>3) delKuma(x,y);
	    					}
	    				}
    			}
    }

    private void animateRandomKuma() { 
    	randomPulseCount++;
    	if (randomPulseCount<period[CHOOSE_RANDOM]) return;
    	randomPulseCount = 0;
    	
    	//Chon ra 1/10 trog so can phan tu state 0 con lai de change len state1
    	int n = mTileCount*period[CHOOSE_RANDOM]/period[1]/6;
    	if (n==0) n = 1;
    	
    	boolean chosen;
    	for (int i=0; i<n; i++) {
    		chosen = false;
    		int count = 0;
    		while (!chosen) {
    			count++;
    			if (count>mTileCount) chosen = true;
    			int t = mRandom.nextInt(mTileCount) + 1;
    			int x = -1;
    			int y = 0;
    			while (t>0) {
    				x++;
    				y += x / mXTileCount;
    				x = x % mXTileCount;
    				if (mTileType[x][y]!=NO_KUMA) t--;
    			}
    			if (animState[x][y]==NORMAL) {
    				chosen = true;
    				animState[x][y] = RANDOM_ANIM;
    				mTileState[x][y] = 1;
    				pulseCount[x][y] = 0;
    			}
    		}
    	}
    }
    
    private void delKuma(int x, int y) {
		mTileCount--;
		mTileType[x][y] = NO_KUMA;
		mTileState[x][y] = 0; 
		animState[x][y] = NORMAL; 
		mFallSlideState[x][y] = NO_FALLSLIDE;
		updateScore();
		if (mTileCount>0) {
			drop_flag = true;
			del_flag = false;
		}
		else setMode(WIN);
	}
    
    private void updateScore() {
    	if (mTileCount==0) mScore += 500;
		mScoreText.setText(" " + mScore);
		mNormaText.setText(" " + mTileCount + "     ");
    }
    
    private void initDropKuma() {
		//reset fallPulseCount
		fallPulseCount = 0;
		
		//reset ve 0 cho mFallSlideState[][] va fallslideTarget[][]
		for (int x=0; x<mXTileCount; x++)
			for (int y=0; y<mYTileCount; y++) {
				mFallSlideState[x][y] = NO_FALLSLIDE;
				fallslideTarget[x][y] = 0;
			}
		
		//tinh gia tri cho fallslideTarget[][] cua cac falling kuma
		queue.resetHead();
		while (!queue.isEmpty()) {
			Coordinate c = queue.poll();
			for (int y=0; y<c.y; y++) {
				mFallSlideState[c.x][y] = FALLING_KUMA;
				fallslideTarget[c.x][y]++;
			}
		}
		
		mScore += (queue.size()-1)*(queue.size()-1);
	}

	//calculate falling kuma and sliding kuma coord
	private void dropKuma() {
		if (++fallPulseCount < period[FALLING]) return;
		fallPulseCount = 0;
		boolean changed = false;
		for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) 
    			if (mTileType[x][y]!=NO_KUMA && mFallSlideState[x][y]==FALLING_KUMA) {
    				mPixelCoord[x][y].y += fallslide_vel;
    				int t = getYPixelCoord(y + fallslideTarget[x][y]);
    				if (mPixelCoord[x][y].y >= t) {
    					mPixelCoord[x][y].y = t;
    					mFallSlideState[x][y] = WAIT_FOR_EXCHANGE;
    				}
    				changed = true;
    			}
		if (changed) return;
		//dich chuyen cac kuma
		drop_flag = false;
		for (int x=0; x<mXTileCount; x++)
    		for (int y=mYTileCount-1; y>=0; y--) 
    			if (mTileType[x][y]!=NO_KUMA && mFallSlideState[x][y]==WAIT_FOR_EXCHANGE) {
    				mTileType[x][y+fallslideTarget[x][y]] = mTileType[x][y];
    				mTileState[x][y+fallslideTarget[x][y]] = mTileState[x][y];
    				mPixelCoord[x][y+fallslideTarget[x][y]] = mPixelCoord[x][y];
    				animState[x][y+fallslideTarget[x][y]] = animState[x][y];
    				pulseCount[x][y+fallslideTarget[x][y]] = pulseCount[x][y];
    				mFallSlideState[x][y] = NO_FALLSLIDE;
    				mTileType[x][y] = NO_KUMA;
    			}
	}
	
	private void initSlideKuma() {
		//reset fallPulseCount
		fallPulseCount = 0;
		
		//reset ve 0 cho mFallSlideState[][] va fallslideTarget[][]
		for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) {
    			mFallSlideState[x][y] = NO_FALLSLIDE;
    			fallslideTarget[x][y] = 0;
    		}
		
		//tinh gia tri cho fallslideTarget[][] cua cac falling kuma
		for (int x=0; x<mXTileCount-1; x++) {
			boolean emptyColumn = true;
			for (int y=0; y<mYTileCount; y++)
				if (mTileType[x][y]!=NO_KUMA) emptyColumn = false;
			if (emptyColumn) {
				slide_flag = true;
				for (int x1=x+1; x1<mXTileCount; x1++)
					for (int y=0; y<mYTileCount; y++)
						if (mTileType[x1][y]!=NO_KUMA) {
							mFallSlideState[x1][y] = SLIDING_KUMA;
							fallslideTarget[x1][y]++;
						}
			}
		}
	}
	
	private void slideKuma() {
		if (++fallPulseCount < period[SLIDING]) return;
		fallPulseCount = 0;
		boolean changed = false;
		for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++) 
    			if (mTileType[x][y]!=NO_KUMA && mFallSlideState[x][y]==SLIDING_KUMA) {
    				mPixelCoord[x][y].x -= fallslide_vel;
    				int t = getXPixelCoord(x - fallslideTarget[x][y]);
    				if (mPixelCoord[x][y].x <= t) {
    					mPixelCoord[x][y].x = t;
    					mFallSlideState[x][y] = WAIT_FOR_EXCHANGE;
    				}
    				changed = true;
    			}
		if (changed) return;
		//dich chuyen cac kuma
		slide_flag = false;
		for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount ; y++) 
    			if (mTileType[x][y]!=NO_KUMA && mFallSlideState[x][y]==WAIT_FOR_EXCHANGE) {
    				mTileType[x-fallslideTarget[x][y]][y] = mTileType[x][y];
    				mTileState[x-fallslideTarget[x][y]][y] = mTileState[x][y];
    				mPixelCoord[x-fallslideTarget[x][y]][y] = mPixelCoord[x][y];
    				animState[x-fallslideTarget[x][y]][y] = animState[x][y];
    				pulseCount[x-fallslideTarget[x][y]][y] = pulseCount[x][y];
    				mFallSlideState[x][y] = NO_FALLSLIDE;
    				mTileType[x][y] = NO_KUMA;
    			}
	}
	
	private void checkStalemate() {
		Queue q = new Queue(mTileCount);
		int kuma_count = 0;
		for (int x=0; x<mXTileCount; x++)
			for (int y=0; y<mYTileCount; y++) 
				if (mTileType[x][y]!=NO_KUMA) {
					kuma_count++;
					q.reset();
					searchSame(new Coordinate(x,y), q);
					if (q.size()>=2) return;
				}
		if (kuma_count>0) setMode(LOSE);
		else setMode(WIN);
	}

	private void searchSame(Coordinate firstc, Queue queue) {
    	int kumatype = mTileType[firstc.x][firstc.y];
    	queue.add(firstc);    	
    	int[] xcons = {0, 1, 0, -1};
    	int[] ycons = {-1, 0, 1, 0};
    	while (!queue.isEmpty()) {
    		Coordinate c = queue.poll();
    		for (int i=0; i<4; i++) {
    			Coordinate c2 = new Coordinate(c.x+xcons[i], c.y+ycons[i]);
    			if (c2.x<0 || c2.x>=mXTileCount || c2.y<0 || c2.y>=mYTileCount) continue;
    			if (!queue.passedby(c2) && mTileType[c2.x][c2.y]==kumatype) queue.add(c2);
    		}
    	}
    }
    
    private void resetPressed2Normal() {
    	for (int x=0; x<mXTileCount; x++)
    		for (int y=0; y<mYTileCount; y++)
    			if (animState[x][y]==PRESSED) {
    				animState[x][y] = NORMAL;
    				mTileState[x][y] = 0;
    			}
    }
    
    @Override
    public boolean onTouchEvent (MotionEvent event) {
    	if (event.getAction()!=MotionEvent.ACTION_DOWN) return true;
    	if (del_flag) return true;
    	if (drop_flag) return true;
    	if (slide_flag) return true;
    	
    	//Read the coords of touch event and return the xk, yk index of touched kuma
    	float x = event.getX();
    	float y = event.getY();
    	int xk = convertCoordToX(x);
    	int yk = convertCoordToY(y);
    	//Toast.makeText(mActivity, x+" "+y+"\n"+xk+" "+yk, Toast.LENGTH_SHORT).show();
    	if (xk!=-1 && yk!=-1 && mTileType[xk][yk]!=NO_KUMA) {
    		if (animState[xk][yk]==NORMAL || animState[xk][yk]==RANDOM_ANIM) {
    			resetPressed2Normal();
	    		Coordinate c = new Coordinate(xk, yk);
	    		queue.reset();
	    		searchSame(c,queue);
	    		//Toast.makeText(mActivity, "touched", Toast.LENGTH_SHORT).show();
	    		if (queue.size()>=2) {
	    			mSoundManager.playSound(Samegame.SOUND_CLICK_ID);
	    			queue.resetHead();
	    			while (!queue.isEmpty()) {
	    				c = queue.poll();
	    				animState[c.x][c.y] = PRESSED;
	    				mTileState[c.x][c.y] = 0;
	    				pulseCount[c.x][c.y] = 0; 
	    			}
	    		} else mSoundManager.playSound(Samegame.SOUND_MISTAKE_ID);
    		}
    		else if (animState[xk][yk]==PRESSED) {
    			del_flag = true;
    			mSoundManager.playSound(Samegame.SOUND_2ND_CLICK_ID);
    			queue.resetHead();
    			while (!queue.isEmpty()) {
    				Coordinate c = queue.poll();
    				animState[c.x][c.y] = GONNA_DIE;
    			}
    			initDropKuma();
    		}
    	} else mSoundManager.playSound(Samegame.SOUND_MISTAKE_ID);
    	return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode==KeyEvent.KEYCODE_DPAD_UP && mMode!=RUNNING) {
        	//setMode(RUNNING);
        	return (true);
        }
        return super.onKeyDown(keyCode, msg);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	setMode(SameView.READY);
    }
    
    public void setKumaTypeNum(int n) {
    	KUMA_TYPE_NUM = n+1;
    }
    
    public int getKumaTypeNum() {
    	return KUMA_TYPE_NUM - 1;
    }
    
    private class Queue {
    	private Coordinate[] queue;
    	private int max;
    	private int head;
    	private int tail;
    	
    	public Queue(int max_element) {
    		max = max_element;
    		queue = new Coordinate[max];
    		head = 0;
    		tail = -1;
    	}
    	public void add(Coordinate c) {
    		tail++;
    		queue[tail] = c;
    	}
    	public Coordinate poll() {
    		if (head>tail) return null;
    		head++;
    		return queue[head-1];
    	}
    	public boolean isEmpty() {
    		if (head>tail) return true;
    		else return false;
    	}
    	public boolean passedby(Coordinate c) {
    		for (int i=0; i<=tail; i++)
    			if (queue[i].equals(c)) return true;
    		return false;
    	}
    	public int size() {
    		return tail+1;
    	}
    	public void resetHead() {
    		head = 0;
    	}
    	public void reset() {
    		head = 0;
    		tail = -1;
    	}
    }
}