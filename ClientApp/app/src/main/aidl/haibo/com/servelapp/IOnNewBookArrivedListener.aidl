// IOnNewBookArrivedListener.aidl
package haibo.com.servelapp;

// Declare any non-default types here with import statements
import haibo.com.servelapp.Book;

interface IOnNewBookArrivedListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void OnNewBookArrived(in Book newBook);
}
