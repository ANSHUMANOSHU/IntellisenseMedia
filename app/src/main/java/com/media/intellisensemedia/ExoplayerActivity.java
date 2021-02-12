package com.media.intellisensemedia;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ExoplayerActivity extends AppCompatActivity
        implements Player.EventListener , CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String VIDEO_URI = "VIDEOURI";
    PlayerView playerView;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;
    JavaCameraView javaCameraView;
    File cascadeFile;
    CascadeClassifier cascadeClassifier;
    Mat mat;
    int secDelay = 1;
    ActionBar actionBar;

    BaseLoaderCallback baseLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

            if (status == LoaderCallbackInterface.SUCCESS) {

                InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
                File file = getDir("cascade", MODE_PRIVATE);

                cascadeFile = new File(file, "haarcascade_frontalface_alt2.xml");

                try {
                    FileOutputStream fos = new FileOutputStream(cascadeFile.getAbsolutePath());
                    byte[] data = new byte[4096];  //4096(4 MBPS) TRANSFER SPEED PER CLOCK
                    //x64 -> 1024 -- 65536
                    //x86/x32 -> 512  --  32768
                    int bytes;
                    while ((bytes = is.read(data)) != -1) {
                        fos.write(data, 0, bytes);
                    }
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
                if (cascadeClassifier.empty()) {
                    cascadeClassifier = null;
                } else {
                    cascadeFile.delete();
                }
                javaCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_exoplayer);
        actionBar = getSupportActionBar();

        if(getSupportActionBar().isShowing()){
            getSupportActionBar().hide();
        }

        //I N I T I A L I Z E   P L A Y E R    V I E W
        playerView = findViewById(R.id.videoFullScreenPlayer);

        //I N I T I A L I Z E   C A M E R A    V I E W
        javaCameraView = findViewById(R.id.camera);

        //C H E C K    O P E N C V    S T A T U S
        if (OpenCVLoader.initDebug()) {
            baseLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            Toast.makeText(this, "Error Loading OpenCV...", Toast.LENGTH_SHORT).show();
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, baseLoaderCallback);
        }

        // L O A D   F R O N T   C A M E R A   A N D   C A M E R A   E V E N T   L I S T E N E R
        javaCameraView.setCameraIndex(1);
        javaCameraView.setCvCameraViewListener(this);

        //  S E T U P    P L A Y E R
        setUp(getIntent().getStringExtra(VIDEO_URI));



    }

    private void setUp(String videoUri) {
        initializePlayer();
        if (videoUri == null) {
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }

    private void initializePlayer() {
        if (player == null) {
            // 1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(new DefaultAllocator(true, 16),
                    VideoPlayerConfig.MIN_BUFFER_DURATION,
                    VideoPlayerConfig.MAX_BUFFER_DURATION,
                    VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                    VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER, -1, true);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);
            // 2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(this,trackSelector,loadControl,null);
            playerView.setPlayer(player);
        }
    }

    private void buildMediaSource(Uri mUri) {
        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)), bandwidthMeter);
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);
        // Prepare the player with the source.
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }

    public void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    public void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }

    public void resumePlayer() {
        if (player != null) {
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        resumePlayer();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }
    @Override
    public void onLoadingChanged(boolean isLoading) {
    }
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    }
    @Override
    public void onRepeatModeChanged(int repeatMode) {
    }
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
    }
    @Override
    public void onPlayerError(ExoPlaybackException error) {
    }
    @Override
    public void onPositionDiscontinuity(int reason) {
    }
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
    }
    @Override
    public void onSeekProcessed() {
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mat.release();
    }

    //5ms
    //1000/5 - 200
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Log.d("Hardik", "onCreate: "+Thread.activeCount());

            try {
                Thread.sleep(secDelay * 1000);
            } catch (InterruptedException ignore) {

            }

        mat = inputFrame.rgba();
        MatOfRect matOfRect = new MatOfRect();
        cascadeClassifier.detectMultiScale(mat, matOfRect);
        if(player!=null && player.getPlaybackState() == Player.STATE_READY && matOfRect.toArray().length == 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setPlayWhenReady(false);
                }
            });
        }else if(player!=null && matOfRect.toArray().length > 0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setPlayWhenReady(true);
                }
            });
        }
        return null;
    }


    class VideoPlayerConfig {
        //Minimum Video you want to buffer while Playing
        public static final int MIN_BUFFER_DURATION = 3000;
        //Max Video you want to buffer during PlayBack
        public static final int MAX_BUFFER_DURATION = 5000;
        //Min Video you want to buffer before start Playing it
        public static final int MIN_PLAYBACK_START_BUFFER = 1500;
        //Min video You want to buffer when user resumes video
        public static final int MIN_PLAYBACK_RESUME_BUFFER = 5000;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        Rational rational = new Rational(width, height);
        PictureInPictureParams.Builder builder = new PictureInPictureParams.Builder();
        builder.setAspectRatio(rational).build();
        enterPictureInPictureMode(builder.build());

    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode)
            actionBar.hide();
        else
            actionBar.show();
    }

}

