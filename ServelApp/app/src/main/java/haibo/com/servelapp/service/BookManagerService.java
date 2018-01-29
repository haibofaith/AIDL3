package haibo.com.servelapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import haibo.com.servelapp.Book;
import haibo.com.servelapp.IBookManager;
import haibo.com.servelapp.IOnNewBookArrivedListener;

public class BookManagerService extends Service {

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    //之所以用列表，因为每个客户端都需要注册一个监听在服务端
//    private CopyOnWriteArrayList<IOnNewBookArrivedListener> mlistener = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener>  mListenerList = new RemoteCallbackList<>();

    private AtomicBoolean flag = new AtomicBoolean();


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
//            if (!mlistener.contains(listener))
//                mlistener.add(listener);

            mListenerList.register(listener);
            Log.e("BookManagerService", "registerListener success"+mListenerList.getRegisteredCallbackCount());
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
//            if (mlistener.contains(listener))
//                mlistener.remove(listener);
//            flag = false;
            mListenerList.unregister(listener);
            Log.e("BookManagerService", "unregisterListener success"+mListenerList.getRegisteredCallbackCount());
        }
    };

    public BookManagerService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "Android"));
        mBookList.add(new Book(2, "Ios"));
        //这种方式的缺点就是每次有个一个客户端进来，就会生成一个新的服务端线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!flag.get()) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int bookId = mBookList.size() + 1;
                    Book newBook = new Book(bookId, "newBook" + bookId);
                    Log.e("BookManagerService", "newBook" + bookId);
                    mBookList.add(newBook);
                    final int N = mListenerList.beginBroadcast();
                    for (int i=0;i<N;i++){
                        //对已经注册的所有监听者都发消息
                        IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
                        if (listener != null) {
                            try {
                                listener.OnNewBookArrived(newBook);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    mListenerList.finishBroadcast();
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
