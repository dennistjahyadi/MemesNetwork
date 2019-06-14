package com.dovoo.memesnetwork.testing;

import java.util.ArrayList;

public class MediaList extends ArrayList<Content.Media> {

    @Override public int size() {
        return Integer.MAX_VALUE;
    }

    @Override public Content.Media get(int index) {
        return Content.Media.getItem(index);
    }

    @Override public int indexOf(Object o) {
        return o instanceof Content.Media ? ((Content.Media) o).index : -1;
    }

    @Override public boolean add(Content.Media media) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override public Content.Media remove(int index) {
        throw new UnsupportedOperationException("Unsupported");
    }
}