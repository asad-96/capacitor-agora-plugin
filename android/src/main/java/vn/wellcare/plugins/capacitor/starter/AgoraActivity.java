package vn.wellcare.plugins.capacitor.starter;

import static android.text.TextUtils.isEmpty;
import static io.agora.rtc2.Constants.LOG_FILTER_OFF;

import android.Manifest;
import android.app.PictureInPictureParams;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;

import java.util.Random;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import vn.wellcare.plugins.capacitor.agora.R;
import vn.wellcare.plugins.capacitor.starter.floatwindow.AVCallFloatView;
import vn.wellcare.plugins.capacitor.starter.floatwindow.FloatWindowHelper;
import vn.wellcare.plugins.capacitor.starter.util.Constant;

public class AgoraActivity extends AppCompatActivity {

    //A flag that will update the status of channel joining
    private boolean isJoined = false;
    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;
    public static OnAgoraEvent onAgoraEvent;

    //COMMONS
    private final String EVENT = "event";
    private final String UID = "uid";
    private final String ELAPSED = "elapsed";
    private final String CHANNEL = "channel";
    private final String REASON = "reason";

    /*
        This event listener will update the events stated in @IRtcEngineEventHandler and send it back to
        JS code
     */
    public interface OnAgoraEvent {
        void onEvent(JSObject jsonObject);
    }

    public static void setOnAgoraEvent(OnAgoraEvent onAgoraEvent1) {
        onAgoraEvent = onAgoraEvent1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            checkFloatingWindowPermissions();
        }
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
            return;
        }
        init();
    }

    /*
        This will set the video sdk engine in order to join channel
     */
    private void init() {
        String channelName = getIntent().getStringExtra(Constant.CHANNELNAME);
        String uid = getIntent().getStringExtra(Constant.UID);
        int uidN;
        Random rand = new Random();
        if (!isEmpty(uid)) {
            try {
                uidN = Integer.parseInt(uid);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                uidN = rand.nextInt(20);
            }
        } else {
            uidN = rand.nextInt(20);
        }
        String token = getIntent().getStringExtra(Constant.TOKEN);
        String appId = getIntent().getStringExtra(Constant.APPID);
        setupVideoSDKEngine(appId);
        joinChannel(token, channelName, uidN);
    }

    //set up video RTC engine
    private void setupVideoSDKEngine(String appId) {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            agoraEngine.setLogFilter(LOG_FILTER_OFF);
            agoraEngine.setLogLevel(LOG_FILTER_OFF);
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("setupVideoSDKEngine err", e.getMessage());
        }
    }

    /**
     * Occurs when a remote user (Communication)/host (Live Broadcast) joins the channel.
     * <p>
     * Communication profile: This callback notifies the app when another user joins the channel. If other users are already in the channel, the SDK also reports to the app on the existing users.
     * Live Broadcast profile: This callback notifies the app when the host joins the channel. If other hosts are already in the channel, the SDK also reports to the app on the existing hosts. We recommend having at most 17 hosts in a channel
     * <p>
     * The SDK triggers this callback under one of the following circumstances:
     * <p>
     * A remote user/host joins the channel by calling the joinChannel method.
     * A remote user switches the user role to the host by calling the setClientRole method after joining the channel.
     * A remote user/host rejoins the channel after a network interruption.
     * The host injects an online media stream into the channel by calling the addInjectStreamUrl method.
     *
     * @param uid ID of the user or host who joins the channel.
     * @param elapsed Time delay (ms) from the local user calling joinChannel/setClientRole until this callback is triggered.
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            if (onAgoraEvent != null) {
                JSObject jsObject = new JSObject();
                jsObject.put(EVENT, "onUserJoined");
                jsObject.put(UID, uid);
                jsObject.put(ELAPSED, elapsed);
                onAgoraEvent.onEvent(jsObject);
            }
            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            if (onAgoraEvent != null) {
                JSObject jsObject = new JSObject();
                jsObject.put(EVENT, "onJoinChannelSuccess");
                jsObject.put(CHANNEL, channel);
                jsObject.put(UID, uid);
                jsObject.put(ELAPSED, elapsed);
                onAgoraEvent.onEvent(jsObject);
            }
        }

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who leaves the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(int uid, int reason) {
            if (onAgoraEvent != null) {
                JSObject jsObject = new JSObject();
                jsObject.put(EVENT, "onUserOffline");
                jsObject.put(UID, uid);
                jsObject.put(REASON, reason);
                onAgoraEvent.onEvent(jsObject);
            }
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }

        /**
         * Reports an error during SDK runtime.
         *
         * In most cases, the SDK cannot fix the issue and resume running. The SDK requires the app to take action or informs the user about the issue.
         *
         * For example, the SDK reports an ERR_START_CALL error when failing to initialize a call. The app informs the user that the call initialization failed and invokes the leaveChannel method to leave the channel. For detailed error codes, see Error Codes.
         *
         * @param error Error Code
         */
        @Override
        public void onError(int err) {
            super.onError(err);
        }

        @Override
        public void onEncryptionError(int errorType) {
            super.onEncryptionError(errorType);
        }

        @Override
        public void onPermissionError(int permission) {
            super.onPermissionError(permission);
        }

        /**
         * Occurs when the local user fails to receive a remote data stream.
         *
         * The SDK triggers this callback when the local user fails to receive the stream message that the remote user sends by calling the sendStreamMessage method.
         *
         * @param uid User ID of the remote user sending the data stream.
         * @param streamId Stream ID.
         * @param error Error Code.
         * @param missed The number of lost messages.
         * @param cached The number of incoming cached messages when the data stream is interrupted.
         */
        @Override
        public void onStreamMessageError(int uid, int streamId, int error, int missed, int cached) {
            super.onStreamMessageError(uid, streamId, error, missed, cached);
        }

        /**
         * Occurs when a user leaves the channel.
         *
         * When the app calls the leaveChannel method, the SDK uses this callback to notify the app when the user leaves the channel.
         *
         * With this callback, the application retrieves the channel information, such as the call duration and statistics.
         *
         * @param stats Statistics of the call: RtcStats
         */
        @Override
        public void onLeaveChannel(RtcStats stats) {
            super.onLeaveChannel(stats);
            if (onAgoraEvent != null) {
                JSObject jsObject = new JSObject();
                jsObject.put(EVENT, "onLeaveChannel");
                jsObject.put(UID, stats.users);
                onAgoraEvent.onEvent(jsObject);
            }
        }
    };

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    //This will set the other user video at screen, after other user joined to the channel
    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

    //This will set the current local user camera preview to the surface
    private void setupLocalVideo() {
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = new SurfaceView(this);
        FrameLayout container = findViewById(R.id.local_video_view_container);
        if (container != null) {
            container.addView(localSurfaceView);
            // Pass the SurfaceView object to Agora so that it renders the local video.
            agoraEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));
            container.setVisibility(View.VISIBLE);
            localSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    public void joinChannel(String token, String channelName, Integer uid) {
        if (checkSelfPermission()) {
            ChannelMediaOptions options = new ChannelMediaOptions();

            // For a Video call, set the channel profile as COMMUNICATION.
            options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION;
            // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
            options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER;
            // Display LocalSurfaceView.
            setupLocalVideo();
            localSurfaceView.setVisibility(View.VISIBLE);
            // Start local preview.
            agoraEngine.startPreview();
            // Join the channel with a temp token.
            // You need to specify the user ID yourself, and ensure that it is unique in the channel.
            agoraEngine.joinChannel(token, channelName, uid, options);
        } else {
            Toast.makeText(getApplicationContext(), "Permissions was not granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        onUserLeaveHint();
    }

    public void leaveChannel(View view) {
        if (!isJoined) {
            showMessage("Join a channel first");
        } else {
            agoraEngine.leaveChannel();
            showMessage("You left the channel");
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.setVisibility(View.GONE);
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.setVisibility(View.GONE);
            isJoined = false;
            finish();
        }
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        showFloatWindow();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        if(isInPictureInPictureMode){
            findViewById(R.id.LeaveButton).setVisibility(View.GONE);
        }else{
            findViewById(R.id.LeaveButton).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) {
            if (isJoined && !isFloatWindowShowing()) {
                showFloatWindow();
            }
        }
    }

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission() {
        return ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQ_ID) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage("Audio permission not granted");
            }
            if (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                showMessage("Camera permission not granted");
            }
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                init();
            }
        }
    }

    private AVCallFloatView floatWindowView;

    //shows the floating window
    private void showFloatWindow() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Display d = getWindowManager()
                    .getDefaultDisplay();
            Point p = new Point();
            d.getSize(p);
            int width = p.x;
            int height = p.y;

            Rational ratio
                    = new Rational(width, height);
            PictureInPictureParams.Builder pip_Builder = null;
            pip_Builder = new PictureInPictureParams.Builder();
            pip_Builder.setAspectRatio(ratio).build();
            enterPictureInPictureMode(pip_Builder.build());
        }else {
            if (FloatWindowHelper.checkPermission(this)) {
                if (isFloatWindowShowing()) {
                    return;
                }
                floatWindowView = FloatWindowHelper.createFloatView(this, 50, 50);

                LayoutInflater inflater = LayoutInflater.from(this);
                View floatView = inflater.inflate(R.layout.float_window_layout, null);
                floatWindowView.addView(floatView);
                floatView.findViewById(R.id.btn_close).setOnClickListener(v -> {
                    dismissFloatWindow();
                });
                FrameLayout fl_local_container = floatWindowView.findViewById(R.id.fl_local_container);
                FrameLayout container = floatWindowView.findViewById(R.id.fl_container);
                FrameLayout local_video_view_container = findViewById(R.id.local_video_view_container);
                FrameLayout remote_video_view_container = findViewById(R.id.remote_video_view_container);
                local_video_view_container.removeView(localSurfaceView);
                remote_video_view_container.removeView(remoteSurfaceView);
                fl_local_container.addView(localSurfaceView);
                if (remoteSurfaceView == null) {
                    remoteSurfaceView = new SurfaceView(getBaseContext());
                    remoteSurfaceView.setZOrderMediaOverlay(true);
                    container.addView(remoteSurfaceView);
                    remoteSurfaceView.setVisibility(View.VISIBLE);
                } else {
                    container.addView(remoteSurfaceView);
                }
                finish();
            } else {
                FloatWindowHelper.applyPermission(this);
            }
        }
    }

    //This will dismiss the floating window
    private void dismissFloatWindow() {
        if (!isFloatWindowShowing()) {
            return;
        }
        FrameLayout container = floatWindowView.findViewById(R.id.fl_container);
        FrameLayout fl_local_container = floatWindowView.findViewById(R.id.fl_local_container);
        if (container.getChildCount() > 0) {
            container.removeView(remoteSurfaceView);
            FrameLayout remote_video_view_container = findViewById(R.id.remote_video_view_container);
            remote_video_view_container.addView(remoteSurfaceView);
        }
        if (fl_local_container.getChildCount() > 0) {
            fl_local_container.removeView(localSurfaceView);
            FrameLayout local_video_view_container = findViewById(R.id.local_video_view_container);
            local_video_view_container.addView(localSurfaceView);
        }

        FloatWindowHelper.destroyFloatView(floatWindowView);
        floatWindowView = null;
        if (isDestroyed()) {
            agoraEngine.stopPreview();
            agoraEngine.leaveChannel();

            // Destroy the engine in a sub-thread to avoid congestion
            new Thread(() -> {
                RtcEngine.destroy();
                agoraEngine = null;
            }).start();
        }
    }

    //UTILS
    private void checkFloatingWindowPermissions() {
        if (!FloatWindowHelper.checkPermission(this)) {
            FloatWindowHelper.applyPermission(this);
        }
    }

    private boolean isFloatWindowShowing() {
        return floatWindowView != null;
    }
}