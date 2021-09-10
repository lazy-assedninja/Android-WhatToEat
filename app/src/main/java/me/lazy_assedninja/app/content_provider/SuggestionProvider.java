package me.lazy_assedninja.app.content_provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Content provider for recent query suggestions.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    // AUTHORITY is a unique name, but it is recommended to use the name of the
    // package followed by the name of the class.
    public final static String AUTHORITY =
            "me.lazy_assedninja.what_to_eat.content_provider.SuggestionProvider";

    // Uncomment line below, if you want to provide two lines in each suggestion:
    // public final static int MODE = DATABASE_MODE_QUERIES | DATABASE_MODE_2LINES;
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}