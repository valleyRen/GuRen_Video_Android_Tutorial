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

public class MainSingleActivity extends AppCompatActivity {

    private final String TAG ="xRTCTestDemo";
    public EditText mRoomIDEdit ;
    public long mRoomID ;
    public long mEnterUserID = 0 ;
    public SurfaceView  mMyView;
    protected boolean mMuteAudio = false ;
    protected boolean mMuteVideo = false ;
    public List<Long> mFailList = new ArrayList<Long>() ;
    public HashMap<Long, xRTCVideoCanvas> mViewMap = new HashMap<Long, xRTCVideoCanvas>() ;
    public xRTCEngine mRTCEngine ;
    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;
    private int         mVideoType ;
    private int         mVideoProfile  = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_480;
    private boolean     mSwap = false ;
    private SurfaceView mUserView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_single);
        //setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
// relativeLayout
        // surface_view_big
        mUserView = (SurfaceView) findViewById(R.id.surface_view_big);
        mMyView = (SurfaceView) findViewById(R.id.surface_view_small);

        mUserView.setTag(1);
        mMyView.setTag(0) ;


        if (getIntent().hasExtra("room_id") && getIntent().hasExtra("video_type") )
        {
            mVideoType = getIntent().getIntExtra("video_type", 0);

            switch (mVideoType)
            {
                case 0:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_160 ;
                }
                break ;

                case 1:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_320 ;
                }
                break ;

                case 2:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_480 ;

                }
                break ;

                case 3:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_640 ;

                }
                break ;

                case 4:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_800 ;

                }
                break ;

                case 5:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_960 ;

                }
                break ;

                case 6:
                {
                    mVideoProfile = xRTCProfile.VIDEO_CAPTURE_TYPE_16X9_1280 ;

                }
                break ;
            }

            onClickStart(null);
        }
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
    }

    public void onClickStart(View view)
    {
        EngineCreate() ;

        String strRoomID = getIntent().getStringExtra("room_id") ;

        mRoomID = Long.parseLong( strRoomID ) ;

        xRTCVideoCanvas myCanvas = new xRTCVideoCanvas( mMyView, 0, 0 ) ;
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

        mEventHandler.mSActivity = null ;
        xRTCEngine.destroy() ;
        mRTCEngine = null ;

        mFailList.clear();
        mViewMap.clear();

        finish();
    }

    public void EngineCreate()
    {
        mEventHandler = new DemoEventImp() ;
        mEventHandler.mSActivity = this ;

        mRTCEngine = xRTCEngine.create( this , "game_card_room", mEventHandler ) ;
        int rc = mRTCEngine.setVideoProfile( mVideoProfile , mSwap ) ;
        if ( rc < 0 )
        {
            return;
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

        if ( mEnterUserID == 0 ) {

            mEnterUserID = uid;
            xRTCVideoCanvas canvas = new xRTCVideoCanvas( mUserView, xRTCVideoCanvas.RENDER_TYPE_CROP, uid);
            xRTCLogging.e( TAG, "add surfaceview uid:" + uid + " count:" + mFailList.size());
            mViewMap.put( uid, canvas );
            mRTCEngine.setupRemoteVideo( canvas ) ;
        }
        else
        {
            if ( mEnterUserID != uid )
            {
                //
                // 日志
                //
                xRTCLogging.e(TAG, "new user enter uid:"+uid+
                        " but uid:"+mEnterUserID+" in room...");

            }
            else
            {
                xRTCLogging.i(TAG, " uid :"+uid + " retry enter room...");
            }
        }

    }

    public void UserLeave( long uid )
    {
        if ( mRTCEngine == null )
        {
            return ;
        }


        xRTCVideoCanvas canvas = mRTCEngine.removeRemoteCanvas(uid) ;

        if ( canvas != null )
        {
            SurfaceView view = canvas.getView() ;
            mViewMap.remove( uid ) ;
            mEnterUserID = 0 ;
            xRTCLogging.e(TAG, "remove uid:"+uid+" succ ") ;
        }
        else
        {
            xRTCLogging.e(TAG, "remove uid:"+uid+" fail...listcount:"+ mViewMap.size() ) ;
        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EngineDestroy();
    }
}
