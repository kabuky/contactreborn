package com.trunkle.contactreborn;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class ContactListView extends SurfaceView implements SurfaceHolder.Callback {

	/** Handle to the application context, used to e.g. fetch Drawables. */
    private Context mContext;
    
    /** Pointer to the text view to display "Paused.." etc. */
    private TextView mStatusText;
        
    private ContactListThread thread;
    
    public ContactListView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    	
    	// register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        
     // create thread only; it's started in surfaceCreated()
        thread = new ContactListThread(holder, context, new Handler() {
            @Override
            public void handleMessage(Message m) {
                mStatusText.setVisibility(m.getData().getInt("viz"));
                mStatusText.setText(m.getData().getString("text"));
            }
        });

        setFocusable(true); // make sure we get key events
    }
    
    /**
     * Fetches the animation thread corresponding to this LunarView.
     *
     * @return the animation thread
     */
    public ContactListThread getThread() {
        return thread;
    }
    
    /**
     * Installs a pointer to the text view used for messages.
     */
    public void setTextView(TextView textView) {
        mStatusText = textView;
    }
    
    /* Callback invoked when the surface dimensions change. */
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
        thread.setSurfaceSize(width, height);
    }
    
    /*
     * Callback invoked when the Surface has been created and is ready to be
     * used.
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // start the thread here so that we don't busy-wait in run()
        // waiting for the surface to be created
        thread.setRunning(true);
        thread.start();
    }
    
    /*
     * Callback invoked when the Surface has been destroyed and must no longer
     * be touched. WARNING: after this method returns, the Surface/Canvas must
     * never be touched again!
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // we have to tell thread to shut down & wait for it to finish, or else
        // it might touch the Surface after we return and explode
        boolean retry = true;
        //thread.setRunning(false);
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    
	class ContactListThread extends Thread {
		
		/*
         * State-tracking constants
         */
        public static final int STATE_LOSE = 1;
        public static final int STATE_PAUSE = 2;
        public static final int STATE_READY = 3;
        public static final int STATE_RUNNING = 4;
        public static final int STATE_WIN = 5;
        
		/*
         * Member (state) fields
         */
        /** The drawable to use as the background of the animation canvas */
        private Bitmap mBackgroundImage;
        
        /** The state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN */
        private int mMode;
        
        /**
         * Current height of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasHeight = 1;
        
        /** Indicate whether the surface has been created & is ready to draw */
        private boolean mRun = false;
        
        /**
         * Current width of the surface/canvas.
         *
         * @see #setSurfaceSize
         */
        private int mCanvasWidth = 1;
        
        /** Message handler used by thread to interact with TextView */
        private Handler mHandler;
        
        /** Used to figure out elapsed time between frames */
        private long mLastTime;
        
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        
        public ContactListThread(SurfaceHolder surfaceHolder, Context context, Handler handler) {
        	this.mSurfaceHolder = surfaceHolder;
        	mHandler = handler;
        	mContext = context;
        	
        	Resources res = context.getResources();
        	
        	mBackgroundImage = BitmapFactory.decodeResource(res,
                    R.drawable.dialpad);
        }
        
        /**
         * Starts the game, setting parameters for the current difficulty.
         */
        public void doStart() {
        	synchronized (mSurfaceHolder) {
        		setState(STATE_RUNNING);
        	}
        }
        
        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
            	if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
            }
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @see #setState(int, CharSequence)
         * @param mode one of the STATE_* constants
         */
        public void setState(int mode) {
            synchronized (mSurfaceHolder) {
                setState(mode, null);
            }
        }
        
        /**
         * Used to signal the thread whether it should be running or not.
         * Passing true allows the thread to run; passing false will shut it
         * down if it's already running. Calling start() after this was most
         * recently called with false will result in an immediate shutdown.
         *
         * @param b true to run, false to shut down
         */
        public void setRunning(boolean b) {
            mRun = b;
        }
        
        /**
         * Sets the game mode. That is, whether we are running, paused, in the
         * failure state, in the victory state, etc.
         *
         * @param mode one of the STATE_* constants
         * @param message string to add to screen or null
         */
        public void setState(int mode, CharSequence message) {
            /*
             * This method optionally can cause a text message to be displayed
             * to the user when the mode changes. Since the View that actually
             * renders that text is part of the main View hierarchy and not
             * owned by this thread, we can't touch the state of that View.
             * Instead we use a Message + Handler to relay commands to the main
             * thread, which updates the user-text View.
             */
            synchronized (mSurfaceHolder) {
                mMode = mode;

                if (mMode == STATE_RUNNING) {
//                    Message msg = mHandler.obtainMessage();
//                    Bundle b = new Bundle();
//                    b.putString("text", "");
//                    b.putInt("viz", View.INVISIBLE);
//                    msg.setData(b);
//                    mHandler.sendMessage(msg);
                } else {
                    
//                    Resources res = mContext.getResources();
//                    CharSequence str = "";
//                    if (mMode == STATE_READY)
//                        str = res.getText(R.string.mode_ready);
//                    else if (mMode == STATE_PAUSE)
//                        str = res.getText(R.string.mode_pause);
//                    else if (mMode == STATE_LOSE)
//                        str = res.getText(R.string.mode_lose);
//                    else if (mMode == STATE_WIN)
//                        str = res.getString(R.string.mode_win_prefix)
//                                + mWinsInARow + " "
//                                + res.getString(R.string.mode_win_suffix);
//
//                    if (message != null) {
//                        str = message + "\n" + str;
//                    }
//
//                    Message msg = mHandler.obtainMessage();
//                    Bundle b = new Bundle();
//                    b.putString("text", str.toString());
//                    b.putInt("viz", View.VISIBLE);
//                    msg.setData(b);
//                    mHandler.sendMessage(msg);
                }
            }
        }
        
        /**
         * Restores game state from the indicated Bundle. Typically called when
         * the Activity is being restored after having been previously
         * destroyed.
         *
         * @param savedState Bundle containing the game state
         */
        public synchronized void restoreState(Bundle savedState) {
        	synchronized (mSurfaceHolder) {
        		
        	}
        }
        
        @Override
        public void run() {
        	while (mRun) {
        		Canvas c = null;
                try {
                    c = mSurfaceHolder.lockCanvas(null);
                    synchronized (mSurfaceHolder) {
                        //if (mMode == STATE_RUNNING) updatePhysics();
                        doDraw(c);
                    }
                } finally {
                    // do this in a finally so that if an exception is thrown
                    // during the above, we don't leave the Surface in an
                    // inconsistent state
                    if (c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
        	}
        }
        
        /**
         * Dump game state to the provided Bundle. Typically called when the
         * Activity is being suspended.
         *
         * @return Bundle with this view's state
         */
        public Bundle saveState(Bundle map) {
        	synchronized (mSurfaceHolder) {
        		return map;
        	}
        }
        
        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {
            // synchronized to make sure these all change atomically
            synchronized (mSurfaceHolder) {
                mCanvasWidth = width;
                mCanvasHeight = height;

                // don't forget to resize the background image
                mBackgroundImage = Bitmap.createScaledBitmap(
                        mBackgroundImage, width, height, true);
            }
        }
        
        /**
         * Draws the ship, fuel/speed bars, and background to the provided
         * Canvas.
         */
        private void doDraw(Canvas canvas) {
        	
        	canvas.drawBitmap(makeRadGrad(), 0, 0, null);
        	
        	// Draw the ship with its current rotation
            canvas.save();
            
            canvas.restore();
        }
        
        private Bitmap makeRadGrad() {
            RadialGradient gradient = new RadialGradient(200, 200, 200, 0xFFFFFFFF,
                    0xFF000000, android.graphics.Shader.TileMode.CLAMP);
            Paint p = new Paint();
            p.setDither(true);
            p.setShader(gradient);

            Bitmap bitmap = Bitmap.createBitmap(400, 400, Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.drawCircle(200, 200, 200, p);

            return bitmap;
        }
        
        /**
         * Figures the lander state (x, y, fuel, ...) based on the passage of
         * realtime. Does not invalidate(). Called at the start of draw().
         * Detects the end-of-game and sets the UI to the next state.
         */
        private void updatePhysics() {
        	long now = System.currentTimeMillis();
        	
        }
	}
}
