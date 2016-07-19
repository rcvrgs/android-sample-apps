package com.ooyala.sample.players;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.TextView;

import com.ooyala.android.player.exoplayer.OfflineDashDownloader;
import com.ooyala.android.player.exoplayer.OfflineDashOptions;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.sample.R;

import java.io.File;

public class OfflineDownloadActivity extends Activity implements OfflineDashDownloader.Listener {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE  = "c0cTkxOqALQviQIGAHWY5hP0q9gU";
  final String DOMAIN = "http://ooyala.com";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog= new SDCardLogcatOoyalaEventsLogger();

  protected TextView progressView;
  protected Handler handler;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(getIntent().getExtras().getString("selection_name"));
    setContentView(R.layout.offline_downloader);
    EMBED = getIntent().getExtras().getString("embed_code");
    progressView = (TextView)findViewById(R.id.progress_text);
    progressView.setText("progress: 0");
    handler = new Handler(getMainLooper());

    String widevineUrl = "http://player.ooyala.com/sas/drm2/FoeG863GnBL4IhhlFC1Q2jqbkH9m/BuY3RsMzE61s6nTC5ct6R-DOapuPt5f7/widevine_modular/ooyala";
    String mpdFile = "http://secure-cf-c.ooyala.com/BuY3RsMzE61s6nTC5ct6R-DOapuPt5f7/1/dash/1.mpd";
    File folder = new File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), EMBED);

    OfflineDashOptions options = new OfflineDashOptions.Builder(mpdFile, folder).setLicenseServerurl(widevineUrl).build();
    OfflineDashDownloader downloader = new OfflineDashDownloader(this, options, this);
    downloader.startDownload();
  }

  public void onCompletion() {
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText("Completed!");
      }
    });
  }

  public void onAbort(final Exception ex) {
    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText("Aborted:" + ex.getLocalizedMessage());
      }
    });
  }

  public void onPercentage(int percentCompleted) {
    final String progress = "progress:" + percentCompleted;

    handler.post(new Runnable() {
      @Override
      public void run() {
        progressView.setText(progress);
      }
    });
  }

}
