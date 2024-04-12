package hr.application.entities;

public class SharedObject<T> {
    private T object;

    public SharedObject(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }

}
