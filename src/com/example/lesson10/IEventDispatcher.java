package com.example.lesson10;

public interface IEventDispatcher {
    void addEventListener(IEventHadler listener);

    void removeEventListener(IEventHadler listener);

    void dispatchEvent(Event e);
}
