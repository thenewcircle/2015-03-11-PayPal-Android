package com.paypal.yamba2;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class HelloActivity extends ActionBarActivity {

    private TextView helloTextView;
    private EditText personNameEditText;
    private Button helloButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        helloTextView = (TextView) findViewById(R.id.helloTextView);
        personNameEditText = (EditText) findViewById(R.id.personNameEditText);
        helloButton = (Button) findViewById(R.id.helloButton);
        helloButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = personNameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    name = "World";
                }
                String msg = String.format("Hello %s!", name);
                helloTextView.setText(msg);
            }
        });
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
        }

        return super.onOptionsItemSelected(item);
    }
}
