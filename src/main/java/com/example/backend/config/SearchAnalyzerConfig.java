package com.example.backend.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchAnalyzerConfig implements LuceneAnalysisConfigurer {

    // Tên Analyzer cho việc tìm kiếm FTS tiếng Việt
    public static final String VIETNAMESE_SEARCH = "vietnamese_search";

    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        context.analyzer(VIETNAMESE_SEARCH).custom()
                .tokenizer(StandardTokenizerFactory.class) // Bộ phân tách từ cơ bản
                .tokenFilter(LowerCaseFilterFactory.class) // Chuyển tất cả thành chữ thường
                .tokenFilter(ASCIIFoldingFilterFactory.class); // Loại bỏ dấu
    }
}