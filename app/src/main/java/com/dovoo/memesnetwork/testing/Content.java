package com.dovoo.memesnetwork.testing;

import android.net.Uri;

import java.net.URI;

public class Content {
    private static final String MP4_BUNNY = "https://img-9gag-fun.9cache.com/photo/a9KXZ9L_460svvp9.webm";
    private static final String MP4_TOS = "https://img-9gag-fun.9cache.com/photo/aGZ2MWn_460svvp9.webm";
    private static final String MP4_COSMOS = "https://img-9gag-fun.9cache.com/photo/aGZ2MWn_460svvp9.webm";

    static final String[] ITEMS = { MP4_TOS, MP4_BUNNY, MP4_COSMOS };

    public static class Media {
        public final int index;
        public final Uri mediaUri;

        public Media(int index, Uri mediaUri) {
            this.index = index;
            this.mediaUri = mediaUri;
        }

        static Media getItem(int index) {
            return new Media(index, Uri.parse(ITEMS[index % ITEMS.length]));
            //return new Media(index, Uri.parse("https://img-9gag-fun.9cache.com/photo/a9KXZ9L_460svvp9.webm"));
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Media)) return false;

            Media media = (Media) o;

            if (index != media.index) return false;
            return mediaUri.equals(media.mediaUri);
        }

        @Override public int hashCode() {
            int result = index;
            result = 31 * result + mediaUri.hashCode();
            return result;
        }
    }
}
