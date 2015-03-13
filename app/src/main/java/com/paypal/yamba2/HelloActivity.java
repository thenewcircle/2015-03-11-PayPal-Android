package com.paypal.yamba2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.thenewcircle.yamba.client.YambaClient;
import com.thenewcircle.yamba.client.YambaClientException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;


public class HelloActivity extends ActionBarActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CONTACT = 2;
    private TextView helloTextView;
    private EditText personNameEditText;
    private Button helloButton;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        helloTextView = (TextView) findViewById(R.id.helloTextView);
        personNameEditText = (EditText) findViewById(R.id.personNameEditText);
        helloButton = (Button) findViewById(R.id.helloButton);
        imageView = (ImageView) findViewById(R.id.imageView);
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHelloButtonPressed();
            }
        });
    }

    public void fileIO() {
        File homeDir = getFilesDir();
        File dataFile = new File(homeDir, "myData.txt");
        Toast.makeText(this, dataFile.toString(), Toast.LENGTH_LONG).show();
        try {
            PrintWriter out = new PrintWriter(dataFile);
            out.println("Hello Lalitha");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileReader in = new FileReader(dataFile);
            BufferedReader in2 = new BufferedReader(in);
            String line = in2.readLine();
            in2.close();
            Toast.makeText(this, line, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void onHelloButtonPressed() {
        String name = personNameEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            name = "World";
        }
        final String msg = String.format("Hello %s!", name);
        Log.d("HelloActivity", msg);
        helloTextView.setText(msg);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                String msg = params[0];
                try {
                    YambaClient cloud = new YambaClient("student", "password",
                            "http://yamba.thenewcircle.com/api");
                    cloud.postStatus(msg);
                } catch (YambaClientException e) {
                    Log.d("HelloActivity", "Exception posting status", e);
                }
                return "Posted: " + msg;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Toast.makeText(HelloActivity.this, result, Toast.LENGTH_LONG).show();
            }
        };
        task.execute(msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_implicit_main) {
            startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER));
            return true;
        } else if (id == R.id.action_implicit_calculator) {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.android.calculator2");
            intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_new_task) {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage("com.android.calculator2");
            startActivity(intent);
            return true;
        } else if (id == R.id.action_browser) {
            Uri uri = Uri.parse("http://www.ebay.com");
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
            return true;
        } else if (id == R.id.action_implicit_custom) {
            return true;
        } else if (id == R.id.action_explicit) {
            startActivity(new Intent(this, ChirpActivity.class));
            startActivity(new Intent().setComponent(new ComponentName("com.paypal.yamba2", "com.paypal.yamba2.ChirpActivity")));
            return true;
        } else if (id == R.id.action_alarm) {
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                    .putExtra(AlarmClock.EXTRA_MESSAGE, "Hello Alarm")
                    .putExtra(AlarmClock.EXTRA_HOUR, 14)
                    .putExtra(AlarmClock.EXTRA_MINUTES, 00);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_photo) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//          intent.putExtra(MediaStore.EXTRA_OUTPUT, photoLocation);
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            return true;
        } else if (id == R.id.action_contact) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent, REQUEST_CONTACT);
            return true;
        } else if (id == R.id.action_files) {
            fileIO();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        } else if (requestCode == REQUEST_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(0);
                personNameEditText.setText(name);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
