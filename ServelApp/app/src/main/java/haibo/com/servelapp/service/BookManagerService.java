package haibo.com.servelapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import haibo.com.servelapp.Book;
import haibo.com.servelapp.IBookManager;
import haibo.com.servelapp.IOnNewBookArrivedListener;

public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private static IOnNewBookArrivedListener listener;

    private boolean flag = false;

    private Binder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            BookManagerService.listener = listener;
            Log.e("BookManagerService", "registerListener success");
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            BookManagerService.listener = null;
            flag = false;
            Log.e("BookManagerService", "unregisterListener success");
        }
    };

    public BookManagerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));

        flag = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int bookId = mBookList.size() + 1;
                    Book newBook = new Book(bookId, "newBook" + bookId);
                    Log.e("BookManagerService","newBook" + bookId);
                    mBookList.add(newBook);
                    if (listener != null) {
                        try {
                            listener.OnNewBookArrived(newBook);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }
}
