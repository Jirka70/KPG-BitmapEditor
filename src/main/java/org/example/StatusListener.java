package org.example;

public interface StatusListener {
    void message(String message);
    void error(String message);
    void success(String message);
}
