package haibo.com.clientapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import haibo.com.servelapp.Book;
import haibo.com.servelapp.IBookManager;


public class MainActivity extends Activity {
    private TextView textView;
    private EditText edit_query;
    private IBookManager bookManager;
    private static int count = 0;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> list = bookManager.getBookList();
                count = list.size();
                textView.setText("getBookList结果是:"+list.toString());
                Log.e("MainActivity",list.toString());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.booklist);
        edit_query = (EditText) findViewById(R.id.edit_query);
        //初始化启动service
        Intent intent = new Intent("haibo.com.servelapp.service.BookManagerService");
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    public void addBook(View view) throws RemoteException {
        //添加一本书并展示
        String str = edit_query.getText().toString();
        if (!TextUtils.isEmpty(str)){
            count++;
            bookManager.addBook(new Book(count,str));
        }
        List<Book> newlist = bookManager.getBookList();
        textView.setText("新getBookList结果是:"+newlist.toString());
        Log.e("MainActivity",newlist.toString());
    }
}
