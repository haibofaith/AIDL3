// IBookManager.aidl
package haibo.com.servelapp;

// Declare any non-default types here with import statements
import haibo.com.servelapp.Book;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);
}
