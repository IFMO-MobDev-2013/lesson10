package com.mikhov.Weather.Async;

public interface IProgressTracker {
    void onProgress(String message);
    void onComplete();
}