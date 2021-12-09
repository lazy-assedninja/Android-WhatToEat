package me.lazy_assedninja.what_to_eat.vo;

import java.util.Objects;

import javax.annotation.Nullable;

public class Event<T> {

    private boolean hasBeenHandled = false;
    private final T content;

    public Event(T content) {
        this.content = content;
    }

    /**
     * Returns the content and prevents its use again.
     */
    @Nullable
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return content;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        else if (object == null || getClass() != object.getClass()) return false;

        Event<?> resource = (Event<?>) object;
        return Objects.equals(content, resource.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }
}