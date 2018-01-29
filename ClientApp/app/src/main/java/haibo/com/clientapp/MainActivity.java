package haibo.com.clientapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import haibo.com.servelapp.Book;
import haibo.com.servelapp.IBookManager;
import haibo.com.servelapp.IOnNewBookArrivedListener;


public class MainActivity extends Activity {
    private TextView textView,newbook;
    private EditText edit_query;
    private IBookManager bookManager;
    private static int count = 0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Log.e("MainActivity","receive new book:"+msg.obj.toString());
                    newbook.setText("receive new book:"+msg.obj.toString());
                    break;
            }
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> list = bookManager.getBookList();
                count = list.size();
                textView.setText("getBookList结果是:"+list.toString());
                Log.e("MainActivity",list.toString());

                bookManager.registerListener(new IOnNewBookArrivedListener.Stub() {
                    @Override
                    public void OnNewBookArrived(Book newBook) throws RemoteException {
                        Message msg = Message.obtain();
                        msg.obj = newBook;
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bookManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.booklist);
        edit_query = (EditText) findViewById(R.id.edit_query);
        newbook = (TextView) findViewById(R.id.newbook);
        //初始化启动service
        Intent intent = new Intent("haibo.com.servelapp.service.BookManagerService");
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            bookManager.unregisterListener(new IOnNewBookArrivedListener.Stub() {
                @Override
                public void OnNewBookArrived(Book newBook) throws RemoteException {

                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(connection);
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
