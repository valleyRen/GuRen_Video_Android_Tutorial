package io.xmediaengine.work.xrtc;

import io.media.sdk.xRTCEventHandler;
import io.media.sdk.xRTCLogging;
import io.media.sdk.xRTCMessage;

/**
 * Created by sunhui on 2017/9/7.
 */

public class DemoEventImp extends xRTCEventHandler
{
    protected final String TAG = "DemoEventImp" ;
    public MainActivity mActivity = null ;
    public MainSingleActivity mSActivity = null ;

    public void onRemoteReady()
    {

    }

    public void onRemoteLogout()
    {

    }

    public  void onFirstRemoteVideoDecoded(long uid, int width, int height, int elapsed)
    {
    }

    public void onFirstRemoteVideoFrame(long uid, int width, int height, int elapsed)
    {

    }

    public void onFirstRemoteAudioFrame( long uid, int elapsed )
    {

    }

    public  void onFirstLocalVideoFrame(int width, int height, int elapsed)
    {

    }

    public  void onUserJoined(long uid, int elapsed)
    {
        if ( mActivity != null ) {
            mActivity.UserEnter(uid);
        }
        else if ( mSActivity != null ) {
            mSActivity.UserEnter(uid);
        }
    }

    public void onUserOffline(long uid, int elapsed)
    {
        if ( mActivity != null ) {
            mActivity.UserLeave(uid);
        }
        else if(mSActivity != null){
            mSActivity.UserLeave(uid);
        }
    }

    public void onUserMuteAudio(long uid, boolean muted )
    {

    }

    public void onUserMuteVideo(long uid, boolean muted)
    {

    }

    public void onRtcStats(int stats)
    {

    }

    public void onCreateChannel( long channelid, int token )
    {

    }

    public void onLeaveChannel(int stats)
    {

    }

    public void onLastmileQuality(int quality)
    {

    }

    public static final int RTC_ERR_SESSION_CRTL     = 1 ;    // 回话管理错误
    public static final int RTC_ERR_SESSION_CHANNEL  = 2 ;   // 频道管理错误
    public static final int RTC_ERR_MCU              = 3 ;   // 媒体服务器
    public static final int RTC_ERR_MEDIA_SYS        = 4 ;   // 音视频引擎
    public static final int SAFE_ERROR_MSG_SIZE      = 500 ; // 错误代码字符串长度

    public void onError(int err, String strError )
    {

    }

    public void onJoinChannelSuccess(String channel, long uid, int elapsed)
    {
        xRTCLogging.d( TAG,"onJoinChannelSuccess name:"+channel+" uid:"+uid+" time:"+elapsed );
    }

    public void onRejoinChannelSuccess(String channel, long uid, int elapsed)
    {

    }

    public void onReadOffLineMessage(xRTCMessage[] userMessageArray, int nEnd )
    {

    }

    public void onReadMessage( xRTCMessage userMessage )
    {

    }

    public void onReadChannelMessage( xRTCMessage userMessage )
    {

    }

    public void onSendMessage( int nCode, xRTCMessage userMessage )
    {

    }
}
