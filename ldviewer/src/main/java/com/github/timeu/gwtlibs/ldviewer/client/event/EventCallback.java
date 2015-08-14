package com.github.timeu.gwtlibs.ldviewer.client.event;

import com.google.gwt.core.client.js.JsFunction;

/**
 * Created by uemit.seren on 8/11/15.
 */
@JsFunction
@FunctionalInterface
public interface EventCallback<T> {
    void onCall(T data);
}
