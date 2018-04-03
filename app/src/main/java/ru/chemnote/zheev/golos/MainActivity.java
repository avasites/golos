package ru.chemnote.zheev.golos;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


import com.commonsware.cwac.anddown.AndDown;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

public class MainActivity extends AppCompatActivity {

    private NotificationManager nm;
    private final int NOTID = 2806;
    private OkHttpClient client;
    WebSocket ws;
    AndDown andDown;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                client = new OkHttpClient.Builder()
                        .build();

                Request request = new Request.Builder()
                        .url("wss://ws.golos.io")
                        .build();

                WebSocketGolos wsg = new WebSocketGolos(MainActivity.this);

                ws = client.newWebSocket(request, wsg);

                client.dispatcher().executorService().shutdown();

        nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    protected void onStop() {

        super.onStop();

       ws.cancel();

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url("wss://ws.golos.io")
                .build();

        WebSocketGolos wsg = new WebSocketGolos(MainActivity.this);

        ws = client.newWebSocket(request, wsg);

        client.dispatcher().executorService().shutdown();

    }

    public void setTextFromWS(String text){

        tv = (TextView)findViewById(R.id.textView);

        andDown = new AndDown();

        final String result=andDown.markdownToHtml(text);

        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              tv.setMovementMethod(new ScrollingMovementMethod());

                              Html.ImageGetter loader = new Html.ImageGetter() {
                                  @Override
                                  public Drawable getDrawable(String s) {
                                      LevelListDrawable drawableList = new LevelListDrawable();
                                      new ImageLoaderTask(s, result, tv).execute(drawableList);

                                      return drawableList;
                                  }
                              };


                              Spanned resHtml = Html.fromHtml(result, loader, null);
                              tv.setText(resHtml);

                          }
                      });
    }

    public void showNotification(String author){

        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        Intent intent = new Intent(getApplicationContext(), ReadActivity.class);

        PendingIntent pending = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder
                .setContentIntent(pending)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.ic_launcher))
                .setTicker("Новый пост")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(author+ " Написал текст")
                .setContentText("Клацни по мне");

        Notification notification = builder.build();

        nm.notify(NOTID, notification);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch(id){
            case R.id.action_settings:
                Intent intent = new Intent(this, ReadActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_account:
                Intent accountIntent = new Intent(this, AccountActivity.class);
                startActivity(accountIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
