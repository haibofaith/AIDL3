// IBookManager.aidl
package haibo.com.servelapp;

// Declare any non-default types here with import statements
import haibo.com.servelapp.Book;
import haibo.com.servelapp.IOnNewBookArrivedListener;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

    void registerListener(IOnNewBookArrivedListener listener);

    void unregisterListener(IOnNewBookArrivedListener listener);
}
