package com.example.videocall;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Button;
import android.util.Log;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import androidx.annotation.NonNull;
import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import android.opengl.GLSurfaceView;




public class MainActivity extends AppCompatActivity implements Session.SessionListener, Publisher.PublisherListener {
    private static String API_KEY = "46748902";
    private static String SESSION_ID = "1_MX40Njc0ODkwMn5-MTU4OTk3NzE5NzE5Nn5nUmpsUHhlVDdXY1Q4dkNhWXlEU3hsNXZ-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00Njc0ODkwMiZzaWc9MTA0MjZkMTNmODc0MjY2YzE3N2JmNWM4NDI5ZDIxODYyZmVkNTRkNzpzZXNzaW9uX2lkPTFfTVg0ME5qYzBPRGt3TW41LU1UVTRPVGszTnpFNU56RTVObjVuVW1wc1VIaGxWRGRYWTFRNGRrTmhXWGxFVTNoc05YWi1mZyZjcmVhdGVfdGltZT0xNTg5OTc3MjQ1Jm5vbmNlPTAuNzM0MDAyNTQxODI1Njc3MyZyb2xlPXB1Ymxpc2hlciZleHBpcmVfdGltZT0xNTkwNTgyMDQ1JmluaXRpYWxfbGF5b3V0X2NsYXNzX2xpc3Q9";
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SETTINGS_SCREEN_perm = 123;
    private static final int RC_VIDEO_APP_perm = 124;
    private Session mSession;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private Publisher mPublisher;
    private Subscriber mSubscriber;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        requestPermissions();
        mPublisherViewContainer = (FrameLayout)findViewById(R.id.publisher_container);
        mSubscriberViewContainer = (FrameLayout)findViewById(R.id.subscriber_container);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        EasyPermissions.onRequestPermissionsResult( requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(RC_SETTINGS_SCREEN_perm)
    private void requestPermissions(){
        String [] perm = {Manifest.permission.INTERNET , Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};

        if(EasyPermissions.hasPermissions(this, perm)) {


            mSession = new Session.Builder( this,API_KEY,SESSION_ID ).build();
            mSession.setSessionListener( this );
            mSession.connect( TOKEN );


        }
        else {
            EasyPermissions.requestPermissions( this,"This app needs access to camera and mic", RC_SETTINGS_SCREEN_perm);
        }



        }


    @Override
    public void onConnected(Session session) {
        Log.i(LOG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView){
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onDisconnected(Session session) {
        Log.i(LOG_TAG, "Session Disconnected");

    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");
        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }

    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");
        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }

    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "Session error: " + opentokError.getMessage());

    }

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamCreated");

    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.i(LOG_TAG, "Publisher onStreamDestroyed");
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "Publisher error: " + opentokError.getMessage());

    }
}


