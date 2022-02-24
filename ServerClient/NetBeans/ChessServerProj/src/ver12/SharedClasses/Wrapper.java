package ver12.SharedClasses;

public class Wrapper<V> {
    private V obj;

    public Wrapper() {
        this(null);
    }

    public Wrapper(V obj) {
        set(obj);
    }

    public synchronized void set(V obj) {
        this.obj = obj;
    }

    public synchronized boolean isExists() {
        return obj != null;
    }

    public synchronized V get() {
        return obj;
    }
}
