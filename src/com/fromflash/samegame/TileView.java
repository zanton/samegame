package com.fromflash.samegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;


/**
 * TileView: a View-variant designed for handling arrays of "icons" or other
 * drawables.
 * 
 */
public class TileView extends View {
	
	protected static final int NO_KUMA = 0;
	
	private static int mTileSize;

    protected int mTileCount;
	protected static int mXTileCount;
    protected static int mYTileCount;

    private static int mXOffset;
    private static int mYOffset;
    private static int mPadding;

    private Drawable[][] mTileArray; 

    protected int[][] mTileType;
    protected int[][] mTileState;
    protected Coordinate[][] mPixelCoord;

    public TileView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initTileView();
    }

    public TileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTileView();
    }

    private void initTileView() {
    	//mTileSize = 38;
    	mTileCount = 88;
    	mXTileCount = 8;
    	mYTileCount = 11;
    	//mPadding = 0;
    	mTileType = new int[mXTileCount][mYTileCount];
        mTileState = new int[mXTileCount][mYTileCount];
        mPixelCoord = new Coordinate[mXTileCount][mYTileCount];
    	clearTiles();
    }
    
    protected void resetTileView() {
    	//mTileSize = 38;
    	mTileCount = 88;
    	//mXTileCount = 8;
    	//mYTileCount = 11;
    	//mPadding = 0;
    	clearTiles();
    	resetPixelCoord();
    }
    
    /**
	 * Resets all tiles to 0 (empty)
	 * 
	 */
	protected void clearTiles() {
	    for (int x = 0; x < mXTileCount; x++) {
	        for (int y = 0; y < mYTileCount; y++) {
	            setTile(x, y, 0, 0);
	        }
	    }
	}
	
	//calculate appropriate size for one tile
	private void calculateSizePad(int w, int h, int oldw, int oldh) {
		mTileCount = 88;
    	mXTileCount = 8;
    	mYTileCount = 11;
    	mPadding = 0;
    	
    	mTileSize = 38;
    	while ((mXTileCount*mTileSize + (mXTileCount-1)*mPadding)>=w ||
    			(mYTileCount*mTileSize + (mYTileCount-1)*mPadding)>=h) mTileSize--;
    	
		mXOffset = ((w - (mTileSize * mXTileCount) - (mPadding * (mXTileCount-1))) / 2);
        mYOffset = ((h - (mTileSize * mYTileCount) - (mPadding * (mYTileCount-1))) / 2);
	}

	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		calculateSizePad(w, h, oldw, oldh);
        resetPixelCoord();
    }  
    
    protected void resetPixelCoord() {
		for (int x=0; x<mXTileCount; x++)
			for (int y=0; y<mYTileCount; y++)
				mPixelCoord[x][y] = new Coordinate(getXPixelCoord(x), getYPixelCoord(y));
	}

	/**
	 * Rests the internal array of Drawable used for drawing tiles, and
	 * sets the maximum index of tiles and maximum index of tile states 
	 * to be inserted
	 * 
	 * @param tilecount, statecount
	 */
	public void resetTiles(int tilecount, int statecount) {
		mTileArray = new Drawable[tilecount][statecount];
	}

	/**
     * Function to set the specified Drawable as the tile for a particular
     * integer key.
     * 
     * @param key
     * @param tile
     */
    public void loadTile(int x, int y, Drawable tile) {
        Drawable d = tile;
        mTileArray[x][y] = d;
    }

    /**
     * Used to indicate that a particular tile including its state (set with 
     * loadTile and referenced by an integer) should be drawn at the given 
     * x/y coordinates during the next invalidate/draw cycle.
     * 
     * @param x
     * @param y
     * @param tileindex
     * @param tilestate
     */
    public void setTile(int x, int y, int tileindex, int tilestate) {
        mTileType[x][y] = tileindex;
        mTileState[x][y] = tilestate;
    }
    
    public int convertCoordToX(float x) {
    	for (int xk=0; xk<mXTileCount; xk++) {
    		int t = mXOffset + xk*(mTileSize + mPadding);
    		if (x>t && x<(t+mTileSize)) return xk;
    	}
    	return -1;
    }
    
    public int convertCoordToY(float y) {
    	for (int yk=0; yk<mYTileCount; yk++) {
    		int t = mYOffset + yk*(mTileSize + mPadding);
    		if (y>t && y<(t+mTileSize)) return yk;
    	}
    	return -1;
    }
    
    public int getXPixelCoord(int x) {
		return mXOffset + x * mTileSize + x * mPadding;
	}

	public int getYPixelCoord(int y) {
    	return mYOffset + y * mTileSize + y * mPadding;
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int x=0; x < mXTileCount; x++) {
            for (int y=0; y < mYTileCount; y++) 
            	if (mTileType[x][y]!=NO_KUMA){
	            	Drawable d = mTileArray[mTileType[x][y]][mTileState[x][y]];                    
	                d.setBounds(mPixelCoord[x][y].x, mPixelCoord[x][y].y, 
	                			mPixelCoord[x][y].x + mTileSize, 
	                			mPixelCoord[x][y].y + mTileSize);
	                d.draw(canvas);                
            	}
        }        
    }
    
    protected class Coordinate {
        public int x;
        public int y;

        public Coordinate(int newX, int newY) {
            x = newX;
            y = newY;
        }

        public boolean equals(Coordinate other) {
            if (x == other.x && y == other.y) {
                return true;
            }
            return false;
        }
    }
}
