package com.vanuston.medeil.ui;

import javafx.async.Task;
import com.vanuston.medeil.util.AsyncTaskHelper;

public class AsyncTask extends Task, AsyncTaskHelper.Task {
    public var run: function() = null;

    // the helper
    def peer = new AsyncTaskHelper(this);

    // used to start the task
    override function start() {
        started = true;
        if (onStart != null) onStart();
        peer.start();
    }

    // don't need stop - isn't implemented
    override function stop() {
        // do nothing
    }

    // called from the helper Java class from a different thread
    override function taskRun() {
        // run the code to be run asynchronously
        if (run != null) run();
        // send a notification (on the dispatch thread) the code finished running
        FX.deferAction(function() {
            done = true;
            if (onDone != null) onDone();
        });
    }
}

