package com.example.backend.helper;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizerHelper {

    @SuppressWarnings("null")
    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS);

    public static String sanitize(String html) {
        if (html == null) {
            return null;
        }

        return POLICY.sanitize(html);
    }
}
