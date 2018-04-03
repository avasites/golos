package ru.chemnote.zheev.golos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ReadActivity extends AppCompatActivity {

    BDHelper dbHelper;
    EditText getAcc;
    Button   butAdd;
    ListView listAcc;
    ContentValues cv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        butAdd = (Button) findViewById(R.id.butAdd);

        getAcc = (EditText) findViewById(R.id.accountName);

        listAcc = (ListView) findViewById(R.id.ListAcc);

        dbHelper = new BDHelper(this);

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        this.renderList(db);

        butAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String acc = getAcc.getText().toString();

                Log.d("WS", acc);

                cv = new ContentValues();

                cv.put(BDHelper.TABLE_ACCOUNT, acc);

                db.insert(BDHelper.TABLE_NAME, null, cv);

                renderList(db);
            }
        });

    }

    public void renderList(final SQLiteDatabase db)
    {
        runOnUiThread(new Runnable() {

            ArrayList<String> al = new ArrayList<>();

            @Override
            public void run() {

                Cursor c = db.query(BDHelper.TABLE_NAME, null, null, null, null, null, null, null);

                if(c.getCount() > 0) {

                    c.moveToFirst();

                    while(c.isAfterLast()==false){

                        al.add(c.getString(c.getColumnIndex(BDHelper.TABLE_ACCOUNT)));

                        c.moveToNext();
                    }

                    c.close();

                    Log.d("WS", al.toString());

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReadActivity.this,
                            android.R.layout.simple_list_item_1, al);

                    listAcc.setAdapter(adapter);

                }
            }
        });
    }

}
