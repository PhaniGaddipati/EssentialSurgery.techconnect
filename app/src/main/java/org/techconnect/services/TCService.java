package org.techconnect.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.centum.techconnect.R;
import org.techconnect.analytics.FirebaseEvents;
import org.techconnect.misc.ResourceHandler;
import org.techconnect.model.FlowChart;
import org.techconnect.model.User;
import org.techconnect.network.TCNetworkHelper;
import org.techconnect.sql.TCDatabaseHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class TCService extends IntentService {

    public static final int LOAD_CHARTS_RESULT_SUCCESS = 0;
    public static final int LOAD_CHARTS_RESULT_ERROR = -1;
    private static final String PARAM_RESULT_RECIEVER = "org.techconnect.services.extra.resultreciever";
    private static final String LOAD_CHARTS = "org.techconnect.services.action.loadcharts";
    private static final String PARAM_IDS = "org.techconnect.services.extra.chartid";
    private static final String LOAD_CHART_RESULT_MESSAGE = "org.techconnect.services.result.message";
    private static final int LOAD_CHARTS_NOTIFICATION = 1;

    private NotificationManager notificationManager;
    private TCNetworkHelper TCNetworkHelper;

    public TCService() {
        super("TechConnectService");
        TCNetworkHelper = new TCNetworkHelper();
    }

    /**
     * Starts this service to to download a chart.
     */
    public static void startLoadCharts(Context context, String chartIds[], ResultReceiver resultReceiver) {
        Intent intent = new Intent(context, TCService.class);
        intent.setAction(LOAD_CHARTS);
        intent.putExtra(PARAM_IDS, chartIds);
        intent.putExtra(PARAM_RESULT_RECIEVER, resultReceiver);
        context.startService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notificationManager != null) {
            notificationManager.cancel(LOAD_CHARTS_NOTIFICATION);
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (LOAD_CHARTS.equals(action)) {
                final String chartIds[] = intent.getStringArrayExtra(PARAM_IDS);
                final ResultReceiver resultReceiver = intent.getParcelableExtra(PARAM_RESULT_RECIEVER);
                handleLoadCharts(chartIds, resultReceiver);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleLoadCharts(String chartIds[], ResultReceiver resultReceiver) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("TechConnect")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.downloading_resources)))
                .setContentText(getString(R.string.downloading_resources))
                .setSmallIcon(R.drawable.tech_connect_app_icon)
                .setOngoing(true);
        notificationManager.notify(LOAD_CHARTS_NOTIFICATION, notificationBuilder.build());
        Bundle bundle = new Bundle();
        int resultCode;
        try {
            FlowChart[] flowCharts = TCNetworkHelper.getCharts(chartIds);
            TCDatabaseHelper.get(getApplicationContext()).upsertCharts(flowCharts);
            Set<String> res = new HashSet<>();
            Set<String> userIds = new HashSet<>();
            for (FlowChart chart : flowCharts) {
                res.addAll(chart.getAllRes());
                userIds.addAll(chart.getAllUserIds());
            }
            loadResources(res.toArray(new String[res.size()]));
            loadUsers(userIds.toArray(new String[userIds.size()]));

            for (FlowChart chart : flowCharts) {
                FirebaseEvents.logDownloadGuide(this, chart);
            }

            resultCode = LOAD_CHARTS_RESULT_SUCCESS;
        } catch (IOException e) {
            resultCode = LOAD_CHARTS_RESULT_ERROR;
            bundle.putString(LOAD_CHART_RESULT_MESSAGE, e.getMessage());
            e.printStackTrace();
        }
        notificationManager.cancel(LOAD_CHARTS_NOTIFICATION);
        resultReceiver.send(resultCode, bundle);
    }

    /**
     * Downloads the users by ID and adds them to the database
     *
     * @param userIds
     */
    private void loadUsers(String[] userIds) {
        TCDatabaseHelper db = TCDatabaseHelper.get(getApplicationContext());
        TCNetworkHelper network = new TCNetworkHelper();
        User user;
        for (String id : userIds) {
            user = null;
            try {
                user = network.getUser(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (user == null) {
                Log.e(this.getClass().getName(), "Failed to load user " + id);
            } else {
                db.upsertUser(user);
                Log.i(this.getClass().getName(), "Downloaded user " + id);
            }
        }
    }

    /**
     * Downloads the resources and adds them to the resource handler.
     *
     * @param resources
     */
    private void loadResources(String resources[]) {
        for (String resUrl : resources) {
            if (ResourceHandler.get(getApplicationContext()).hasStringResource(resUrl)) {
                Log.d(this.getClass().getName(), "ResourceHandler has \"" + resUrl + "\"");
            } else {
                String fileName;
                try {
                    fileName = downloadFile(resUrl);
                    ResourceHandler.get(getApplicationContext()).addStringResource(resUrl, fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(this.getClass().getName(), "Failed to load: " + resUrl);
                }
            }
        }
    }

    /**
     * Downloads a file into the private file directory, and returns the name of the file.
     *
     * @param fileUrl
     * @return
     * @throws IOException
     */
    private String downloadFile(String fileUrl) throws IOException {
        Log.d(this.getClass().getName(), "Attempting to download " + fileUrl);
        String fileName = "tc" + (int) Math.round(Integer.MAX_VALUE * Math.random());
        HttpURLConnection connection = (HttpURLConnection) new URL(fileUrl.replace(" ", "%20")).openConnection();

        FileOutputStream fileOutputStream = getApplicationContext().openFileOutput(fileName, Context.MODE_PRIVATE);
        InputStream inputStream = connection.getInputStream();

        int readBytes;
        byte buffer[] = new byte[1024];
        if (inputStream != null) {
            while ((readBytes = inputStream.read(buffer)) > -1) {
                fileOutputStream.write(buffer, 0, readBytes);
            }
            inputStream.close();
        }

        connection.disconnect();
        fileOutputStream.flush();
        fileOutputStream.close();

        Log.i(getClass().getName(), "Downloaded file: " + fileUrl + " --> " + fileName);
        return fileName;
    }

}
