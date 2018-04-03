package ru.chemnote.zheev.golos;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by zheev on 07.03.18.
 */

public class WebSocketGolos extends WebSocketListener {

    MainActivity ma;

    public WebSocketGolos(MainActivity mactivity)
    {
        ma = mactivity;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {

        try {
            JSONObject jsonObject = new JSONObject("{id:1, method:'call', 'params': ['database_api', 'set_block_applied_callback', [0] ]}");

            webSocket.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {

        JSONObject data = null;

        try {

            data = new JSONObject(text);

            if (data.has("id")){
                int idRequest = data.getInt("id");

                Log.d("WS", Integer.toString(idRequest));

                if(idRequest == 2) {

                    this.getContent(data);

                }

            }else if(data.has("method")){

                String method = data.getString("method");

                if(Objects.equals(method, "notice")){

                    int hex = this.getNumberBlock(text);

                    try {

                        JSONObject blockSet = new JSONObject("{id:2, method: 'call', 'params':['database_api', 'get_ops_in_block', [" + hex + ",'false']]}");

                        webSocket.send(blockSet.toString());

                    } catch (JSONException ee) {
                        ee.toString();
                    }

                }



            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Log.d("WS", data.toString());
//        System.out.print(data);

    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
    }

    private int getNumberBlock(String text) {

        try {
            JSONObject data = new JSONObject(text);
            String method = data.getString("method");
            //            Log.d("WS", method.toString());
            JSONArray params = data.getJSONArray("params");

            if (Objects.equals(method, "notice") && params != null) {

                JSONArray objectParam = params.getJSONArray(1);
                JSONObject arrayObjectParam = objectParam.getJSONObject(0);

                String previousParam = arrayObjectParam.getString("previous");

                Integer hex = Integer.parseInt(previousParam.substring(0, 8), 16);

                return hex;
            } else {
                return 0;
            }
        } catch (JSONException e) {
            e.toString();
        }

        return 0;

    }

    private void getContent(JSONObject data) {

        try{
            JSONArray result = data.getJSONArray("result");

            for(int i = 0; i < result.length(); i++){

                JSONObject resJson = result.getJSONObject(i);

                JSONArray resParam = resJson.getJSONArray("op");

                String typeBlock = resParam.getString(0);

                if(Objects.equals(typeBlock, "comment")){

                    JSONObject dataBlock = resParam.getJSONObject(1);

                    String parent_author = dataBlock.getString("parent_author");

                    if (Objects.equals(parent_author, "")) {

                        Log.d("WS", dataBlock.getString("permlink"));

                        ma.showNotification(dataBlock.getString("author"));

                        ma.setTextFromWS(dataBlock.getString("body"));
                    }

                    Log.d("WS", dataBlock.toString());

                }
            }

        }catch(JSONException e){

        }
    }


}
