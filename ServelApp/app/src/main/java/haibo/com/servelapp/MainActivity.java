package haibo.com.servelapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import haibo.com.servelapp.service.BookManagerService;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void click(View view){
        startService(new Intent(this, BookManagerService.class));
    }
}
