/**
 * Created by Simon on 2017/3/30.
 */

public class Entry<K, V>{
    K key;
    V value;
    Entry<K,V> next;
    int hashValue;

    public Entry(K key, V value, int hashValue, Entry<K, V> next){
        this.key = key;
        this.value = value;
        this.hashValue = hashValue;
        this.next = next;
    }
}
