import static com.sun.xml.internal.fastinfoset.util.ValueArray.MAXIMUM_CAPACITY;
import static sun.plugin.security.JDK11ClassFileTransformer.init;

public class HashMap<K, V> {
    Entry[] table;
    int size;
    int threshold;
    int initialCapacity;
    float loadFactor;
    int modCount = 0;

    public HashMap(){
        initialCapacity = 16;
        loadFactor = (float)0.75;
        table = new Entry[(int) (initialCapacity * loadFactor)];
    }

    public HashMap(int initialCapacity)
    {
        if (initialCapacity < 0)
            throw new IllegalArgumentException( "Illegal initial capacity: " + initialCapacity);
        // 如果初始容量大于最大容量，让出示容量
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        this.initialCapacity = initialCapacity;
        loadFactor = (float)0.75;
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;
        // 设置容量极限等于容量 * 负载因子
        threshold = (int)(capacity * loadFactor);
        // 初始化 table 数组
        table = new Entry[capacity];            // ①
        init();
    }

    public HashMap(int initialCapacity, float loadFactor)
    {
        // 初始容量不能为负数
        if (initialCapacity < 0)
            throw new IllegalArgumentException( "Illegal initial capacity: " + initialCapacity);
        // 如果初始容量大于最大容量，让出示容量
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        // 负载因子必须大于 0 的数值
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException ("Illegal loadFactor" + loadFactor);
        // 计算出大于 initialCapacity 的最小的 2 的 n 次方值。
        int capacity = 1;
        while (capacity < initialCapacity)
            capacity <<= 1;
        this.loadFactor = loadFactor;
        // 设置容量极限等于容量 * 负载因子
        threshold = (int)(capacity * loadFactor);
        // 初始化 table 数组
        table = new Entry[capacity];            // ①
        init();
    }

    public V put(K key, V value)
    {
        // 如果 key 为 null，调用 putForNullKey 方法进行处理
        if (key == null)
            return putForNullKey(value);
        // 根据 key 的 keyCode 计算 Hash 值
        int hash = hash(key.hashCode());
        // 搜索指定 hash 值在对应 table 中的索引
        int i = indexFor(hash, table.length);
        // 如果 i 索引处的 Entry 不为 null，通过循环不断遍历 e 元素的下一个元素
        for (Entry<K,V> e = table[i]; e != null; e = e.next)
        {
            Object k;
            // 找到指定 key 与需要放入的 key 相等（hash 值相同
            // 通过 equals 比较放回 true）
            if (e.hashValue == hash && ((k = e.key) == key
                    || key.equals(k)))
            {
                V oldValue = e.value;
                e.value = value;
                return oldValue;
            }
        }
        // 如果 i 索引处的 Entry 为 null，表明此处还没有 Entry
        modCount++;
        // 将 key、value 添加到 i 索引处
        addEntry(hash, key, value, i);
        return null;
    }

    public V get(Object key)
    {
        // 如果 key 是 null，调用 getForNullKey 取出对应的 value
        if (key == null)
            return getForNullKey();
        // 根据该 key 的 hashCode 值计算它的 hash 码
        int hash = hash(key.hashCode());
        // 直接取出 table 数组中指定索引处的值，
        for (Entry<K,V> e = table[indexFor(hash, table.length)];
             e != null;
            // 搜索该 Entry 链的下一个 Entr
             e = e.next)         // ①
        {
            Object k;
            // 如果该 Entry 的 key 与被搜索 key 相同
            if (e.hashValue == hash && ((k = e.key) == key || key.equals(k)))
                return e.value;
        }
        return null;
    }

    private V getForNullKey() {
        return null;
    }

    private V putForNullKey(V value) {
        return value;
    }

    static int hash(int h)
    {
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }

    static int indexFor(int h, int length)
    {
        return h & (length - 1);
    }

    public void addEntry(int hash, K key, V value, int bucketIndex)
    {
        // 获取指定 bucketIndex 索引处的 Entry
        Entry e = table[bucketIndex];     // ①
        // 将新创建的 Entry 放入 bucketIndex 索引处，并让新的 Entry 指向原来的 Entry
        table[bucketIndex] = new Entry(key, value, hash, e);
        // 如果 Map 中的 key-value 对的数量超过了极限
        if (size++ >= threshold)
            // 把 table 对象的长度扩充到 2 倍。
            resize(2 * table.length);    // ②
    }

    public void resize(int size){
        threshold = size;
    }
}
