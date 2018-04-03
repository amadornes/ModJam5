package mod.crystals.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class UniqueQueue<E> implements Queue<E> {

    public static <E> UniqueQueue<E> create() {
        return new UniqueQueue<>(new ArrayDeque<>(), new HashSet<>());
    }

    public static <E> UniqueQueue<E> concurrent() {
        return new UniqueQueue<>(new ArrayDeque<>(), Collections.newSetFromMap(new ConcurrentHashMap<>()));
    }

    private final Queue<E> queue;
    private final Set<E> set;

    public UniqueQueue(Queue<E> queue, Set<E> set) {
        this.queue = queue;
        this.set = set;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return set.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return queue.toArray(a);
    }

    @Override
    public boolean add(E e) {
        return set.add(e) && queue.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return set.remove(o) && queue.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return set.contains(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean altered = false;
        for (E e : c) {
            altered |= add(e);
        }
        return altered;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return set.removeAll(c) && queue.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return set.retainAll(c) && queue.retainAll(c);
    }

    @Override
    public void clear() {
        set.clear();
        queue.clear();
    }

    @Override
    public boolean offer(E e) {
        return !set.contains(e) && queue.offer(e) && set.add(e);
    }

    @Override
    public E remove() {
        E e = queue.remove();
        set.remove(e);
        return e;
    }

    @Override
    public E poll() {
        E e = queue.poll();
        set.remove(e);
        return e;
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public E peek() {
        return queue.peek();
    }

}
