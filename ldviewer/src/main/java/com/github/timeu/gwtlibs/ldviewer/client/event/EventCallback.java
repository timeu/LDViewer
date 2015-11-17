package com.github.timeu.gwtlibs.ldviewer.client.event;


import jsinterop.annotations.JsFunction;

/**
 * Created by uemit.seren on 8/11/15.
 */
@JsFunction
@FunctionalInterface
public interface EventCallback<T> {
    void onCall(T data);
}
