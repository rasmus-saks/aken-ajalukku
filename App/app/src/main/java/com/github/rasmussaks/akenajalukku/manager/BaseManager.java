package com.github.rasmussaks.akenajalukku.manager;

import com.github.rasmussaks.akenajalukku.activity.MapActivity;

public abstract class BaseManager {
    private final MapActivity context;

    public BaseManager(MapActivity context) {
        this.context = context;
    }

    public MapActivity getContext() {
        return context;
    }

}
