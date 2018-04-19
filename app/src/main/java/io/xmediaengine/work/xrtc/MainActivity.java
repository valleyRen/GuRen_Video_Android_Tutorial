package io.xmediaengine.work.xrtc;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.media.sdk.xRTCEngine;
import io.media.sdk.xRTCLogging;
import io.media.sdk.xRTCVideo.xRTCProfile;
import io.media.sdk.xRTCVideo.xRTCVideoCanvas;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private final String TAG ="xRTCTestDemo";
    public EditText mRoomIDEdit ;
    public long mRoomID ;
    public long mEnterUserID ;
    public SurfaceView  mMyView;
    protected boolean mMuteAudio = false ;
    protected boolean mMuteVideo = false ;
    public List<SurfaceView> mViewList = new LinkedList<SurfaceView>();
    public List<Long> mFailList = new ArrayList<Long>() ;
    public HashMap<Long, xRTCVideoCanvas> mViewMap = new HashMap<Long, xRTCVideoCanvas>() ;
    public xRTCEngine mRTCEngine ;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private int         mVideoType ;
    private int         mVideoProfile  = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_480;
    private boolean     mSwap = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );

        if (getIntent().hasExtra("room_id") && getIntent().hasExtra("video_type") ) {
            mVideoType = getIntent().getIntExtra("video_type", 0);

            switch (mVideoType) {
                case 0: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_160;
                }
                break;

                case 1: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_320;
                }
                break;

                case 2: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_480;

                }
                break;

                case 3: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_640;

                }
                break;

                case 4: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_800;

                }
                break;

                case 5: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_960;

                }
                break;

                case 6: {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_1280;

                }
                break;
            }
        }
        else
        {
            return;
        }

        /*
        mTopLayout = (LinearLayout)findViewById(R.id.top_view_layout);
        mBottomLayout = (LinearLayout)findViewById(R.id.bottom_view_layout);

        mBottomLayout.post(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams topParam = (LinearLayout.LayoutParams) mTopLayout.getLayoutParams();
                topParam.height = ScreenUtils.getScreenWidth(MainActivity.this)/2*4/3;
                mTopLayout.setLayoutParams(topParam);
                LinearLayout.LayoutParams bottomParam = (LinearLayout.LayoutParams) mBottomLayout.getLayoutParams();
                bottomParam.height = ScreenUtils.getScreenWidth(MainActivity.this)/2*4/3;
                mBottomLayout.setLayoutParams(topParam);
            }
        });
        */
        SurfaceView view1 = (SurfaceView)findViewById(R.id.surfaceView1 ) ;
        SurfaceView view2 = (SurfaceView)findViewById(R.id.surfaceView2 ) ;
        SurfaceView view3 = (SurfaceView)findViewById(R.id.surfaceView3 ) ;
        mMyView = (SurfaceView)findViewById(R.id.surfaceView_my ) ;

        view1.setTag(1) ;
        view2.setTag(2) ;
        view3.setTag(3) ;
        mMyView.setTag(0);

        mViewList.add( view1 ) ;
        mViewList.add( view2 ) ;
        mViewList.add( view3 ) ;

        if(getIntent().hasExtra("room_id"))
        onClickStart(null);
    }

    public DemoEventImp mEventHandler ;

    Button btns = null;

    public void onClickMuteLocalAudio(View view)
    {
        mMuteAudio = !mMuteAudio ;
        btns = (Button)findViewById(R.id.btMuteAudio );
        String text = mMuteAudio == true ? "打开声音":"关闭声音";
        btns.setText(text);
        mRTCEngine.muteLocalAudioStream( mMuteAudio ) ;
    }

    public void onClickMuteLocalVideo(View view)
    {
        mMuteVideo = !mMuteVideo ;
        btns = (Button)findViewById(R.id.btMuteVideo );
        String text = mMuteVideo == true ? "打开视频":"关闭视频";
        btns.setText(text);
        mRTCEngine.muteLocalVideoStream( mMuteVideo ) ;
    }

    public void onClickChange(View view)
    {
        if ( mViewList.isEmpty() ||mRTCEngine == null)
        {
            return;
        }


        SurfaceView userview = mViewList.remove( 0 ) ;

        userview.setVisibility( VISIBLE );

        xRTCVideoCanvas myCanvas = new xRTCVideoCanvas( userview, 0, 0 ) ;
        mRTCEngine.setupLocalVideo( myCanvas ) ;

        xRTCLogging.i(TAG, " my surface oldtag:"+mMyView.getTag()+" tag:"+userview.getTag());

        xRTCVideoCanvas  canvas = new xRTCVideoCanvas( mMyView, xRTCVideoCanvas.RENDER_TYPE_CROP, mEnterUserID ) ;
        xRTCVideoCanvas oldCanvas =  mRTCEngine.setupRemoteVideo( canvas ) ;

        if ( oldCanvas != null )
        {
            oldCanvas.getView().setVisibility( View.INVISIBLE );
            mViewList.add( oldCanvas.getView() ) ;
            xRTCLogging.i(TAG, " user surface oldtag:"+oldCanvas.getView().getTag()+ " tag:"+mMyView.getTag() );
        }

        mMyView = userview ;
        oldCanvas = null ;
    }

    public void onClickStart(View view)
    {
        EngineCreate() ;
        String strRoomID = getIntent().getStringExtra("room_id") ;

        mRoomID = Long.parseLong( strRoomID ) ;


        xRTCVideoCanvas myCanvas = new xRTCVideoCanvas( mMyView, xRTCVideoCanvas.RENDER_TYPE_FULL, 0 ) ;
        mRTCEngine.setupLocalVideo( myCanvas ) ;

        Random random = new Random( System.nanoTime() );
        long userid = random.nextLong() ;

        if ( userid < 0 )
        {
            userid = -1 * userid ;
        }

        mRTCEngine.joinChannel( null, mRoomID, "token123", userid ) ;
    }

    public void onClickStop(View view)
    {
        if ( mRTCEngine == null )
        {
            return;
        }

        mEventHandler.mActivity = null ;
        xRTCEngine.destroy() ;

        mFailList.clear();
        mViewList.clear();
        mViewMap.clear() ;
        mRTCEngine = null ;
        finish() ;
    }

    //
    // @nProfile 是视频设置
    // @IsVertical 是否是竖屏
    //
    public void EngineCreate( )
    {
        mEventHandler = new DemoEventImp() ;
        mEventHandler.mActivity = this ;

        mRTCEngine = xRTCEngine.create( this , "game_card_room", mEventHandler ) ;
        int rc = mRTCEngine.setVideoProfile( mVideoProfile , mSwap ) ;
        if ( rc < 0 )
        {
            return ;
        }

        mRTCEngine.enableAudio() ;
        mRTCEngine.enableVideo() ;
    }

    public void EngineDestroy()
    {
        xRTCEngine.destroy() ;
        mRTCEngine = null ;
    }

    public void UserEnter(long uid)
    {
        if ( mViewList.isEmpty() )
        {
            mFailList.add( uid ) ;
            xRTCLogging.e(TAG, "user enter fail...uid:"+uid ) ;
            return ;
        }

        mEnterUserID = uid ;
        SurfaceView user_view = mViewList.remove(0) ;
        if ( user_view != null )
        {
            xRTCVideoCanvas canvas = new xRTCVideoCanvas( user_view, xRTCVideoCanvas.RENDER_TYPE_CROP, uid ) ;
            xRTCLogging.e(TAG, "add surfaceview uid:"+uid +" count:"+ mFailList.size() ) ;
            mViewMap.put( uid, canvas ) ;
            mRTCEngine.setupRemoteVideo(canvas);
        }

    }

    public void UserLeave( long uid )
    {
        if ( mRTCEngine == null )
        {
            return ;
        }
        boolean bc = mFailList.remove( uid ) ;
        if ( bc )
        {
            xRTCLogging.e( TAG, "mFailList remove succ...uid:"+uid );
        }

        xRTCVideoCanvas canvas = mRTCEngine.removeRemoteCanvas( uid ) ;

        if ( canvas != null )
        {
            SurfaceView view = canvas.getView() ;

            if (mFailList.size() > 0)
            {
                long uoldid = mFailList.remove(0) ;

                xRTCVideoCanvas canvas2 = new xRTCVideoCanvas( view, 0, uoldid);

                xRTCLogging.e(TAG, "add surfaceview 2 uid:" + uid);

                mViewMap.put( uoldid, canvas2 ) ;
                mRTCEngine.setupRemoteVideo( canvas2 ) ;

            } else {
                mViewList.add( view );
            }

            xRTCLogging.e(TAG, "remove uid:"+uid+" succ listcount:"+mViewList.size() ) ;
        }
        else
        {
            xRTCLogging.e(TAG, "remove uid:"+uid+" fail...listcount:"+mViewList.size() ) ;
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EngineDestroy();
    }
}
