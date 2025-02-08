package com.document.chat.rag.ingestion;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

//@Component
public class IngestionService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);
    private final VectorStore vectorStore;
    
    private final TokenTextSplitter tokenSplitter = new TokenTextSplitter();

    @Value("classpath:/docs/iphone-14-info.pdf")
    private Resource marketPDF;

    public IngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) throws Exception {
    
    try {  
    	 // Step 1: Read the PDF content
        var pdfReader = new PagePdfDocumentReader(marketPDF, PdfDocumentReaderConfig.defaultConfig());
        
        // Step 2: Split the content into tokens
        var tokens = tokenSplitter.split(pdfReader.read());
        
        if (tokens.isEmpty()) {
            throw new IllegalStateException("No tokens were extracted from the PDF.");
        }
        
        // Step 3: Write tokens to the vector store
        vectorStore.write(tokens);
        
        // Log success for debugging
        log.info("Document successfully uploaded to vector store!");
        log.info("Tokens count: {}", tokens.size());
        
    	} catch (Exception e) {
        throw new RuntimeException("Failed to load document: " + e.getMessage(), e);
     }
    	
    
    }
}
