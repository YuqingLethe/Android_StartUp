package com.example.isamu.fitmap;

import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ActivityRecognizedService extends IntentService {
    private static String nameBrod = "com.example.isamu.fitMap.ACTINFO";

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            if(ActivityRecognitionResult.hasResult(intent)) {
                ResultReceiver receiver = intent.getParcelableExtra("receiver");




                ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
                handleDetectedActivities( result.getProbableActivities(),receiver );
            }
        }

    }
    private void handleDetectedActivities(List<DetectedActivity> probableActivities,ResultReceiver receiver) {

        int confidenceLevel = 40 ;
        for( DetectedActivity activity : probableActivities ) {
            switch( activity.getType() ) {
                case DetectedActivity.IN_VEHICLE: {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.getConfidence());
                    if( activity.getConfidence() >= confidenceLevel ) {
                        sendRespIntent(activity.getType(),activity.getConfidence());

                    }
                    break;
                }
                case DetectedActivity.ON_BICYCLE: {
                    Log.e( "ActivityRecogition", "On Bicycle: " + activity.getConfidence() );

                    break;
                }
                case DetectedActivity.ON_FOOT: {
                    Log.e( "ActivityRecogition", "On Foot: " + activity.getConfidence() );
                    if( activity.getConfidence() >= confidenceLevel ) {
                        sendRespIntent(DetectedActivity.WALKING, activity.getConfidence());
                    }
                    break;
                }
                case DetectedActivity.RUNNING: {
                    Log.e( "ActivityRecogition", "Running: " + activity.getConfidence() );
                    if( activity.getConfidence() >= confidenceLevel ) {
                        sendRespIntent(activity.getType(), activity.getConfidence());
                    }
                    break;
                }
                case DetectedActivity.STILL: {
                    Log.e( "ActivityRecogition", "Still: " + activity.getConfidence() );
                    if( activity.getConfidence() >= confidenceLevel ) {
                        sendRespIntent(activity.getType(), activity.getConfidence());
                    }
                    break;
                }
                case DetectedActivity.TILTING: {
                    Log.e( "ActivityRecogition", "Tilting: " + activity.getConfidence() );

                    break;
                }
                case DetectedActivity.WALKING: {
                    Log.e( "ActivityRecogition", "Walking: " + activity.getConfidence() );
                    if( activity.getConfidence() >= confidenceLevel ) {
                        sendRespIntent(activity.getType(), activity.getConfidence());
                    }
                    break;
                }
                case DetectedActivity.UNKNOWN: {
                    Log.e( "ActivityRecogition", "Unknown: " + activity.getConfidence() );
                    break;
                }
            }
        }
    }
    private void sendRespIntent(int type,int confidence)
    {
        Intent resp = new Intent(getString(R.string.nameBrod));
        resp.putExtra("type", type);
        resp.putExtra("confidence", confidence);
        sendBroadcast(resp);
    }


}
