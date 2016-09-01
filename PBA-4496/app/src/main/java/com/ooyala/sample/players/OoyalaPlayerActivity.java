package com.ooyala.sample.players;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ooyala.adtech.ContentMetadata;
import com.ooyala.adtech.RequestSettings;
import com.ooyala.android.EmbedTokenGenerator;
import com.ooyala.android.EmbedTokenGeneratorCallback;
import com.ooyala.android.OoyalaNotification;
import com.ooyala.android.OoyalaPlayer;
import com.ooyala.android.OoyalaPlayerLayout;
import com.ooyala.android.PlayerDomain;
import com.ooyala.android.configuration.Options;
import com.ooyala.android.item.Video;
import com.ooyala.android.pulseintegration.OoyalaPulseManager;
import com.ooyala.android.skin.OoyalaSkinLayout;
import com.ooyala.android.skin.OoyalaSkinLayoutController;
import com.ooyala.android.skin.configuration.SkinOptions;
import com.ooyala.android.ui.OoyalaPlayerLayoutController;
import com.ooyala.android.util.SDCardLogcatOoyalaEventsLogger;
import com.ooyala.pulse.Pulse;
import com.ooyala.pulse.PulseSession;
import com.ooyala.pulse.PulseVideoAd;
import com.ooyala.sample.R;
import com.ooyala.sample.utils.EmbeddedSecureURLGenerator;


import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class OoyalaPlayerActivity extends Activity implements Observer, EmbedTokenGenerator, DefaultHardwareBackBtnHandler {
  final String TAG = this.getClass().toString();

  String EMBED = null;
  final String PCODE = "tlM2k6i2-WrXX1DE_b8zfhui_eQN";
  private final String ACCOUNT_ID = "accountID";
  final String DOMAIN = "http://ooyala.com";

  /*
   * The API Key and Secret should not be saved inside your application (even in git!).
   * However, for debugging you can use them to locally generate Ooyala Player Tokens.
   */
  private final String APIKEY = "COMPLETE ME";
  private final String SECRET = "AND ME";

  // Write the sdk events text along with events count to log file in sdcard if the log file already exists
  SDCardLogcatOoyalaEventsLogger Playbacklog = new SDCardLogcatOoyalaEventsLogger();

  protected OoyalaSkinLayoutController playerLayoutController;
  protected OoyalaPlayer player;
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

  /**
   * Called when the activity is first created.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.player_simple_frame_layout);

    EMBED = getIntent().getExtras().getString("embed_code");

    /** DITA_START:<ph id="freewheel_preconfigured"> **/
    //Initialize the player

    // Get the SkinLayout from our layout xml
    OoyalaSkinLayout skinLayout = (OoyalaSkinLayout)findViewById(R.id.ooyalaPlayer);

    // Create the OoyalaPlayer, with some built-in UI disabled
    PlayerDomain domain = new PlayerDomain(DOMAIN);
    Options options = new Options.Builder().setShowPromoImage(false).setUseExoPlayer(true).setShowNativeLearnMoreButton(false).build();
    player = new OoyalaPlayer(PCODE, domain, options);

    //Create the SkinOptions, and setup React
    SkinOptions skinOptions = new SkinOptions.Builder().build();
    playerLayoutController = new OoyalaSkinLayoutController(getApplication(), skinLayout, player, skinOptions);

    player.addObserver(this);

    // ATTENTION: This was auto-generated to implement the App Indexing API.
    // See https://g.co/AppIndexing/AndroidStudio for more information.
    client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    //Create an instance of OoyalaPulseManager and set a listener.
    final OoyalaPulseManager pulseManager = new OoyalaPulseManager(player);
    pulseManager.setListener(new OoyalaPulseManager.Listener() {
      /*
        Called by the plugin to let us create the Pulse session; the metadata retrieved from Backlot is provided here
      */
      @Override
      public PulseSession createPulseSession(OoyalaPulseManager ooyalaPulseManager, Video video, String pulseHost, ContentMetadata contentMetadata, RequestSettings requestSettings) {
        // Replace some of the Backlot metadata with our own local data
        List<Float> midrollPositions = new ArrayList<>();
        midrollPositions.add(15f);
        midrollPositions.add(30f);
        requestSettings.setLinearPlaybackPositions(midrollPositions);
        List<String> tags = new ArrayList<String>();
        tags.add("standard-midrolls");
        contentMetadata.setTags(tags);
        contentMetadata.setIdentifier("demo-midroll");
        contentMetadata.setCategory("");
        Pulse.setPulseHost(pulseHost, null, null);
        return Pulse.createSession(contentMetadata, requestSettings);
      }

      @Override
      public void openClickThrough(OoyalaPulseManager ooyalaPulseManager, PulseVideoAd pulseVideoAd) {
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()));
        startActivity(intent);

        // adClickThroughTriggered should be reported when the user has opened the
        // clickthrough link in a browser.
        // Note: If there are multiple browsers installed on device, the user will
        // be asked choose a browser or cancel. An accurate implementation should
        // only call adClickThroughTriggered if the browser was actually opened.
        pulseVideoAd.adClickThroughTriggered();
      }
    });

    if (player.setEmbedCode(EMBED)) {
      //Uncomment for autoplay
      //player.play();
    } else {
      Log.e(TAG, "Asset Failure");
    }
  }

  /** Start DefaultHardwareBackBtnHandler **/
  @Override
  public void invokeDefaultOnBackPressed() {
    super.onBackPressed();
  }
  /** End DefaultHardwareBackBtnHandler **/

  /** Start Activity methods for Skin **/
  @Override
  protected void onPause() {
    super.onPause();
    if (playerLayoutController != null) {
      playerLayoutController.onPause();
    }

    if (player != null) {
      player.suspend();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (playerLayoutController != null) {
      playerLayoutController.onResume( this, this);
    }

    if (player != null) {
      player.resume();
    }
  }

  @Override
  public void onBackPressed() {
    if (playerLayoutController != null) {
      playerLayoutController.onBackPressed();
    } else {
      super.onBackPressed();
    }
  }
  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (playerLayoutController != null) {
      playerLayoutController.onDestroy();
    }
  }
  /** End Activity methods for Skin **/


  /**
   * Listen to all notifications from the OoyalaPlayer
   */
  @Override
  public void update(Observable arg0, Object argN) {
    if (arg0 != player) {
      return;
    }

    final String arg1 = OoyalaNotification.getNameOrUnknown(argN);
    if (arg1 == OoyalaPlayer.TIME_CHANGED_NOTIFICATION_NAME) {
      return;
    }

    if (arg1 == OoyalaPlayer.ERROR_NOTIFICATION_NAME) {
      final String msg = "Error Event Received";
      if (player != null && player.getError() != null) {
        Log.e(TAG, msg, player.getError());
      } else {
        Log.e(TAG, msg);
      }
      return;
    }

    // Automation Hook: to write Notifications to a temporary file on the device/emulator
    String text = "Notification Received: " + arg1 + " - state: " + player.getState();
    // Automation Hook: Write the event text along with event count to log file in sdcard if the log file exists
    Playbacklog.writeToSdcardLog(text);

    Log.d(TAG, "Notification Received: " + arg1 + " - state: " + player.getState());
  }

  /*
   * Get the Ooyala Player Token to play the embed code.
   * This should contact your servers to generate the OPT server-side.
   * For debugging, you can use Ooyala's EmbeddedSecureURLGenerator to create local embed tokens
   */
  @Override
  public void getTokenForEmbedCodes(List<String> embedCodes,
                                    EmbedTokenGeneratorCallback callback) {
    String embedCodesString = "";
    for (String ec : embedCodes) {
      if (ec.equals("")) embedCodesString += ",";
      embedCodesString += ec;
    }

    HashMap<String, String> params = new HashMap<String, String>();
    params.put("account_id", ACCOUNT_ID);

    // Uncommenting this will bypass all syndication rules on your asset
    // This will not work unless you have a working API Key and Secret.
    // This is one reason why you shouldn't keep the Secret in your app/source control
    params.put("override_syndication_group", "override_all_synd_groups");

    String uri = "/sas/embed_token/" + PCODE + "/" + embedCodesString;

    EmbeddedSecureURLGenerator urlGen = new EmbeddedSecureURLGenerator(APIKEY, SECRET);

    URL tokenUrl = urlGen.secureURL("http://player.ooyala.com", uri, params);

    Log.d(TAG, "Token URL: " + tokenUrl);

    callback.setEmbedToken(tokenUrl.toString());
  }

  @Override
  public void onStart() {
    super.onStart();
  }
}