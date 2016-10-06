package com.github.rasmussaks.akenajalukku.fragment;

import android.support.v4.app.Fragment;


public class DrawerFragment extends Fragment {
    private DrawerFragmentListener listener;

    public void setDrawerFragmentListener(DrawerFragmentListener listener) {
        this.listener = listener;
    }

    public void closeDrawer() {
        if (listener != null) listener.onCloseDrawer();
    }

    public interface DrawerFragmentListener {
        void onCloseDrawer();
    }
}
