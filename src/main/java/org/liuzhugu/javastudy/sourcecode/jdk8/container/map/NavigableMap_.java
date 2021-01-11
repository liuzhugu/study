package org.liuzhugu.javastudy.sourcecode.jdk8.container.map;

import java.util.NavigableSet;
import java.util.SortedMap;

public interface NavigableMap_<K, V> extends SortedMap_<K, V> {
    Entry<K, V> lowerEntry(K var1);

    K lowerKey(K var1);

    Entry<K, V> floorEntry(K var1);

    K floorKey(K var1);

    Entry<K, V> ceilingEntry(K var1);

    K ceilingKey(K var1);

    Entry<K, V> higherEntry(K var1);

    K higherKey(K var1);

    Entry<K, V> firstEntry();

    Entry<K, V> lastEntry();

    Entry<K, V> pollFirstEntry();

    Entry<K, V> pollLastEntry();

    java.util.NavigableMap<K, V> descendingMap();

    NavigableSet<K> navigableKeySet();

    NavigableSet<K> descendingKeySet();

    java.util.NavigableMap<K, V> subMap(K var1, boolean var2, K var3, boolean var4);

    java.util.NavigableMap<K, V> headMap(K var1, boolean var2);

    java.util.NavigableMap<K, V> tailMap(K var1, boolean var2);

    SortedMap_<K, V> subMap(K var1, K var2);

    SortedMap_<K, V> headMap(K var1);

    SortedMap_<K, V> tailMap(K var1);
}
