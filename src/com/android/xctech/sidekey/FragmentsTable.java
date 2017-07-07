package com.android.xctech.sidekey;

import java.util.List;

import com.android.xctech.sidekey.operate.PickAppsFragment;
import com.android.xctech.sidekey.operate.QuickoperateFragment;

public class FragmentsTable {
    public static final int FRAGMENT_QUICKOPERATE_POSITION = 0;
    public static final int FRAGMENT_PICKAPP_POSITION = 1;

    private static final Class<?>[] fragments = {
            QuickoperateFragment.class,
            PickAppsFragment.class,
    };

    static final void initialise(List<SideKeyFragment> list) {
        for (int i = 0, listSize = 0; i < fragments.length; i++) {
            try {
                SideKeyFragment fragment = (SideKeyFragment) fragments[i].newInstance();
                list.add(listSize++, fragment);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }
    }

}
