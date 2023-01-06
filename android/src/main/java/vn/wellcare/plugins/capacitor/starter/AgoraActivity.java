package vn.wellcare.plugins.capacitor.starter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.getcapacitor.BridgeActivity;

import io.agora.rtc2.ChannelMediaOptions;
import io.agora.rtc2.Constants;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import vn.wellcare.plugins.capacitor.agora.R;
import vn.wellcare.plugins.capacitor.starter.floatwindow.AVCallFloatView;
import vn.wellcare.plugins.capacitor.starter.floatwindow.FloatWindowHelper;

public class AgoraActivity extends AppCompatActivity {

    // Fill the App ID of your project generated on Agora Console.
    private final String appId = "6efdd253f1c74743bd343fc97c136d6c";
    // Fill the channel name.
    private String channelName = "test1";
    // Fill the temp token generated on Agora Console.
    private String token = "007eJxTYDgmpi6ULnZpSqdTtFBA/rkT/huS2hqW2T55E1NVJXvFgU2BwSw1LSXFyNQ4zTDZ3MTcxDgpxdjEOC3Z0jzZ0NgsxSx55bPtyQ2BjAxmj3awMDJAIIjPylCSWlxiyMAAABOhH3s=";
    // An integer that identifies the local user.
    private int uid = -1;
    private boolean isJoined = false;

    private RtcEngine agoraEngine;
    //SurfaceView to render local video in a Container.
    private SurfaceView localSurfaceView;
    //SurfaceView to render Remote video in a Container.
    private SurfaceView remoteSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agora);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (!FloatWindowHelper.checkPermission(this)) {
            FloatWindowHelper.applyPermission(this);
        }
        // If all the permissions are granted, initialize the RtcEngine object and join a channel.
        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
            return;
        }
        init();

        findViewById(R.id.btn_float_window).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFloatWindow();
            }
        });
    }

    private void init() {
        channelName = getIntent().getStringExtra("room");
        uid = getIntent().getIntExtra("uid", -1);
        setupVideoSDKEngine();
        joinChannel();
    }

    private void setupVideoSDKEngine() {
        try {
            RtcEngineConfig config = new RtcEngineConfig();
            config.mContext = getBaseContext();
            config.mAppId = appId;
            config.mEventHandler = mRtcEventHandler;
            agoraEngine = RtcEngine.create(config);
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo();
        } catch (Exception e) {
            showMessage(e.toString());
        }
    }

    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {
        @Override
        // Listen for the remote host joining the channel to get the uid of the host.
        public void onUserJoined(int uid, int elapsed) {
            showMessage("Remote user joined " + uid);

            // Set the remote video view
            runOnUiThread(() -> setupRemoteVideo(uid));
        }

        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            isJoined = true;
            showMessage("Joined Channel " + channel);
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            showMessage("Remote user offline " + uid + " " + reason);
            runOnUiThread(() -> remoteSurfaceView.setVisibility(View.GONE));
        }

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

        @Override
        public void onStreamMessageError(int uid, int streamId, int error, int missed, int cached) {
            super.onStreamMessageError(uid, streamId, error, missed, cached);
        }
    };

    void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show());
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = findViewById(R.id.remote_video_view_container);
        remoteSurfaceView = new SurfaceView(getBaseContext());
        remoteSurfaceView.setZOrderMediaOverlay(true);
        container.addView(remoteSurfaceView);
        agoraEngine.setupRemoteVideo(new VideoCanvas(remoteSurfaceView, VideoCanvas.RENDER_MODE_FIT, uid));
        // Display RemoteSurfaceView.
        remoteSurfaceView.setVisibility(View.VISIBLE);
    }

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

    public void joinChannel() {
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
    public void onDestroy() {
        super.onDestroy();
        if (isJoined && !isFloatWindowShowing()) {
            showFloatWindow();
        }
    }

    private static final int PERMISSION_REQ_ID = 22;
    private static final String[] REQUESTED_PERMISSIONS =
            {
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA
            };

    private boolean checkSelfPermission() {
        if (ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, REQUESTED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
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

    private void showFloatWindow() {
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
            container.addView(remoteSurfaceView);
            finish();
        } else {
            FloatWindowHelper.applyPermission(this);
        }
    }

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

    private boolean isFloatWindowShowing() {
        return floatWindowView != null;
    }
}