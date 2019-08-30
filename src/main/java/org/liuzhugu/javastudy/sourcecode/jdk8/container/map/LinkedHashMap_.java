package org.liuzhugu.javastudy.sourcecode.jdk8.container.map;


import org.liuzhugu.javastudy.sourcecode.jdk8.container.inteface.AbstractCollection_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.set.AbstractSet_;
import org.liuzhugu.javastudy.sourcecode.jdk8.container.set.Set_;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.io.IOException;

/**
 * 同时具有了hashmap的o(1)的查找效率,又比hashmap多了可以获取插入顺序的功能
 * */
public class LinkedHashMap_<K,V>
        extends HashMap_<K,V>
        implements Map_<K,V>
{

    /**
     * 比hashmap的节点多了前后,让linkedHashMap的节点除了水平之外还多了上下的环形的双向链表
     * 这样便同时具有了hashmap的o(1)的查找效率,又比hashmap多了可以获取插入顺序的功能
     */
    static class Entry<K,V> extends HashMap_.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

    //重写的API

    public boolean containsValue(Object value) {
        //hashmap的时候遍历整个哈希数组然后再分别遍历链表
        //LinkedHashMap因为会把所有节点连成双向链表,遍历该双向链表就行
        for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after) {
            V v = e.value;
            if (v == value || (value != null && value.equals(v)))
                return true;
        }
        return false;
    }

    public V get(Object key) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) == null)
            return null;
        //按访问顺序的话,访问之后要更新顺序
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }


    //重要代码

    //按访问顺序的话,把刚访问的节点放到尾部
    void afterNodeAccess(Node<K,V> e) { // move node to last
        LinkedHashMap_.Entry<K,V> last;
        if (accessOrder && (last = tail) != e) {
            LinkedHashMap_.Entry<K,V> p =
                    (LinkedHashMap_.Entry<K,V>)e, b = p.before, a = p.after;
            p.after = null;
            if (b == null)
                head = a;
            else
                b.after = a;
            if (a != null)
                a.before = b;
            else
                last = b;
            if (last == null)
                head = p;
            else {
                p.before = last;
                last.after = p;
            }
            tail = p;
            ++modCount;
        }
    }

    //子类复写removeEldestEntry,返回true的话,这样就会删除最年长的,在空间不够完成淘汰
    void afterNodeInsertion(boolean evict) {
        LinkedHashMap_.Entry<K,V> first;
        if (evict && (first = head) != null && removeEldestEntry(first)) {
            K key = first.key;
            removeNode(hash(key), key, null, false, true);
        }
    }
    //节点被删除后更新双向链表
    void afterNodeRemoval(Node<K,V> e) { // unlink
        LinkedHashMap_.Entry<K,V> p =
                (LinkedHashMap_.Entry<K,V>)e, b = p.before, a = p.after;
        p.before = p.after = null;
        if (b == null)
            head = a;
        else
            b.after = a;
        if (a == null)
            tail = b;
        else
            a.before = b;
    }



    private static final long serialVersionUID = 3801124242820219131L;

    /**
     * 头
     */
    transient LinkedHashMap_.Entry<K,V> head;

    /**
     * 尾
     */
    transient LinkedHashMap_.Entry<K,V> tail;

    /**
     * 按插入还是访问顺序排序
     * false       true
     */
    final boolean accessOrder;

    // internal utilities

    // link at the end of list
    private void linkNodeLast(LinkedHashMap_.Entry<K,V> p) {
        LinkedHashMap_.Entry<K,V> last = tail;
        tail = p;
        if (last == null)
            head = p;
        else {
            p.before = last;
            last.after = p;
        }
    }

    // apply src's links to dst
    private void transferLinks(LinkedHashMap_.Entry<K,V> src,
                               LinkedHashMap_.Entry<K,V> dst) {
        LinkedHashMap_.Entry<K,V> b = dst.before = src.before;
        LinkedHashMap_.Entry<K,V> a = dst.after = src.after;
        if (b == null)
            head = dst;
        else
            b.after = dst;
        if (a == null)
            tail = dst;
        else
            a.before = dst;
    }

    // overrides of HashMap_ hook methods

    void reinitialize() {
        super.reinitialize();
        head = tail = null;
    }

    Node<K,V> newNode(int hash, K key, V value, Node<K,V> e) {
        LinkedHashMap_.Entry<K,V> p =
                new LinkedHashMap_.Entry<K,V>(hash, key, value, e);
        linkNodeLast(p);
        return p;
    }

    Node<K,V> replacementNode(Node<K,V> p, Node<K,V> next) {
        LinkedHashMap_.Entry<K,V> q = (LinkedHashMap_.Entry<K,V>)p;
        LinkedHashMap_.Entry<K,V> t =
                new LinkedHashMap_.Entry<K,V>(q.hash, q.key, q.value, next);
        transferLinks(q, t);
        return t;
    }

    TreeNode<K,V> newTreeNode(int hash, K key, V value, Node<K,V> next) {
        TreeNode<K,V> p = new TreeNode<K,V>(hash, key, value, next);
        linkNodeLast(p);
        return p;
    }

    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {
        LinkedHashMap_.Entry<K,V> q = (LinkedHashMap_.Entry<K,V>)p;
        TreeNode<K,V> t = new TreeNode<K,V>(q.hash, q.key, q.value, next);
        transferLinks(q, t);
        return t;
    }



    void internalWriteEntries(java.io.ObjectOutputStream s) throws IOException {
        for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after) {
            s.writeObject(e.key);
            s.writeObject(e.value);
        }
    }


    public LinkedHashMap_(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        accessOrder = false;
    }
    public LinkedHashMap_(int initialCapacity) {
        super(initialCapacity);
        accessOrder = false;
    }
    public LinkedHashMap_() {
        super();
        accessOrder = false;
    }
    public LinkedHashMap_(Map_<? extends K, ? extends V> m) {
        super();
        accessOrder = false;
        putMapEntries(m, false);
    }
    public LinkedHashMap_(int initialCapacity, float loadFactor, boolean accessOrder) {
        super(initialCapacity, loadFactor);
        this.accessOrder = accessOrder;
    }



    /**
     * 找不到返回默认值
     */
    public V getOrDefault(Object key, V defaultValue) {
        Node<K,V> e;
        if ((e = getNode(hash(key), key)) == null)
            return defaultValue;
        if (accessOrder)
            afterNodeAccess(e);
        return e.value;
    }

    /**
     * 清空
     */
    public void clear() {
        super.clear();
        head = tail = null;
    }


    protected boolean removeEldestEntry(Map_.Entry<K,V> eldest) {
        return false;
    }


    public Set_<K> keySet() {
        Set_<K> ks;
        return (ks = keySet) == null ? (keySet = new LinkedKeySet()) : ks;
    }

    final class LinkedKeySet extends AbstractSet_<K> {
        public final int size()                 { return size; }
        public final void clear()               { LinkedHashMap_.this.clear(); }
        public final Iterator<K> iterator() {
            return new LinkedKeyIterator();
        }
        public final boolean contains(Object o) { return containsKey(o); }
        public final boolean remove(Object key) {
            return removeNode(hash(key), key, null, false, true) != null;
        }

        public final void forEach(Consumer<? super K> action) {
            if (action == null)
                throw new NullPointerException();
            int mc = modCount;
            for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after)
                action.accept(e.key);
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }


    public Collection<V> values() {
        Collection<V> vs;
        return (vs = values) == null ? (values = new LinkedValues()) : vs;
    }

    final class LinkedValues extends AbstractCollection_<V> {
        public final int size()                 { return size; }
        public final void clear()               { LinkedHashMap_.this.clear(); }
        public final Iterator<V> iterator() {
            return new LinkedValueIterator();
        }
        public final boolean contains(Object o) { return containsValue(o); }

        public final void forEach(Consumer<? super V> action) {
            if (action == null)
                throw new NullPointerException();
            int mc = modCount;
            for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after)
                action.accept(e.value);
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }


    public Set_<Map_.Entry<K,V>> entrySet() {
        Set_<Map_.Entry<K,V>> es;
        return (es = entrySet) == null ? (entrySet = new LinkedEntrySet()) : es;
    }

    final class LinkedEntrySet extends AbstractSet_<Map_.Entry<K,V>> {
        public final int size()                 { return size; }
        public final void clear()               { LinkedHashMap_.this.clear(); }
        public final Iterator<Map_.Entry<K,V>> iterator() {
            return new LinkedEntryIterator();
        }
        public final boolean contains(Object o) {
            if (!(o instanceof Map_.Entry))
                return false;
            Map_.Entry<?,?> e = (Map_.Entry<?,?>) o;
            Object key = e.getKey();
            Node<K,V> candidate = getNode(hash(key), key);
            return candidate != null && candidate.equals(e);
        }
        public final boolean remove(Object o) {
            if (o instanceof Map_.Entry) {
                Map_.Entry<?,?> e = (Map_.Entry<?,?>) o;
                Object key = e.getKey();
                Object value = e.getValue();
                return removeNode(hash(key), key, value, true, true) != null;
            }
            return false;
        }

        public final void forEach(Consumer<? super Map_.Entry<K,V>> action) {
            if (action == null)
                throw new NullPointerException();
            int mc = modCount;
            for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after)
                action.accept(e);
            if (modCount != mc)
                throw new ConcurrentModificationException();
        }
    }

    // Map_ overrides

    public void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        int mc = modCount;
        for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after)
            action.accept(e.key, e.value);
        if (modCount != mc)
            throw new ConcurrentModificationException();
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();
        int mc = modCount;
        for (LinkedHashMap_.Entry<K,V> e = head; e != null; e = e.after)
            e.value = function.apply(e.key, e.value);
        if (modCount != mc)
            throw new ConcurrentModificationException();
    }

    // Iterators

    abstract class LinkedHashIterator {
        LinkedHashMap_.Entry<K,V> next;
        LinkedHashMap_.Entry<K,V> current;
        int expectedModCount;

        LinkedHashIterator() {
            next = head;
            expectedModCount = modCount;
            current = null;
        }

        public final boolean hasNext() {
            return next != null;
        }

        final LinkedHashMap_.Entry<K,V> nextNode() {
            LinkedHashMap_.Entry<K,V> e = next;
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            if (e == null)
                throw new NoSuchElementException();
            current = e;
            next = e.after;
            return e;
        }

        public final void remove() {
            Node<K,V> p = current;
            if (p == null)
                throw new IllegalStateException();
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
            current = null;
            K key = p.key;
            removeNode(hash(key), key, null, false, false);
            expectedModCount = modCount;
        }
    }

    final class LinkedKeyIterator extends LinkedHashIterator
            implements Iterator<K> {
        public final K next() { return nextNode().getKey(); }
    }

    final class LinkedValueIterator extends LinkedHashIterator
            implements Iterator<V> {
        public final V next() { return nextNode().value; }
    }

    final class LinkedEntryIterator extends LinkedHashIterator
            implements Iterator<Map_.Entry<K,V>> {
        public final Map_.Entry<K,V> next() { return nextNode(); }
    }


}

