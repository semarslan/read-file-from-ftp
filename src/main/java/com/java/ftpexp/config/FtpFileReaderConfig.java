package com.java.ftpexp.config;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

public class FtpFileReaderConfig {

    public FlatFileItemReader<FieldSet> ftpFileReader() {
        FlatFileItemReader<FieldSet> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(1); // Skip header line if necessary
        reader.setLineMapper(lineMapper());
        reader.setResource(ftpResource());
        return reader;
    }

    private Resource ftpResource() {
        String hostname = "ftp.hostname.com";
        int port = 21;
        String username = "ftp-username";
        String password = "ftp-password";
        String remoteFilePath = "/path/to/file.txt";

        // Create FTP resource
        return new FtpResource(hostname, port, username, password, remoteFilePath);
    }

    private LineMapper<FieldSet> lineMapper() {
        PatternMatchingCompositeLineMapper<FieldSet> lineMapper = new PatternMatchingCompositeLineMapper<>();
        lineMapper.setTokenizers(tokenizers());
        lineMapper.setFieldSetMappers(fieldSetMappers());
        return lineMapper;
    }

    private Map<String, LineTokenizer> tokenizers() {
        Map<String, LineTokenizer> tokenizers = new HashMap<>();

        // Define line tokenizers for different content formats
        tokenizers.put("FORMAT1", format1LineTokenizer());
        tokenizers.put("FORMAT2", format2LineTokenizer());
        // Add more tokenizers for other formats as needed

        return tokenizers;
    }

    private Map<String, FieldSetMapper<FieldSet>> fieldSetMappers() {
        Map<String, FieldSetMapper<FieldSet>> fieldSetMappers = new HashMap<>();

        // Define field set mappers for different content formats
        fieldSetMappers.put("FORMAT1", format1FieldSetMapper());
        fieldSetMappers.put("FORMAT2", format2FieldSetMapper());
        // Add more field set mappers for other formats as needed

        return fieldSetMappers;
    }

    private LineTokenizer format1LineTokenizer() {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(","); // Set the delimiter for the format
        tokenizer.setNames("field1", "field2", "field3"); // Set the field names
        return tokenizer;
    }

    private LineTokenizer format2LineTokenizer() {
        // Implement the tokenizer for format 2
        // Customize the delimiter and field names as per the format
        // Example:
        // DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        // tokenizer.setDelimiter(";"); // Set the delimiter for the format
        // tokenizer.setNames("fieldA", "

    }

    private FieldSetMapper<FieldSet> format1FieldSetMapper() {
        return fieldSet -> {
            // Map the fields from the FieldSet to your domain object for format 1
            // Example:
            // MyObject obj = new MyObject();
            // obj.setField1(fieldSet.readString("field1"));
            // obj.setField2(fieldSet.readString("field2"));
            // obj.setField3(fieldSet.readString("field3"));
            // return obj;
        };
    }

    private FieldSetMapper<FieldSet> format2FieldSetMapper() {
        // Implement the field set mapper for format 2
        // Map the fields from the FieldSet to your domain object for format 2
    }

}

@Configuration
@EnableBatchProcessing
public class BatchConfig {

   // ... other beans and configurations

   @Bean
   public PatternMatchingCompositeLineMapper<Transaction> lineMapper() {
      PatternMatchingCompositeLineMapper<Transaction> lineMapper = new PatternMatchingCompositeLineMapper<>();

      Map<String, LineTokenizer> tokenizers = new HashMap<>();
      tokenizers.put("HEADER", createHeaderLineTokenizer());
      tokenizers.put("TRANSACTION", createTransactionLineTokenizer());

      Map<String, FieldSetMapper<Transaction>> mappers = new HashMap<>();
      mappers.put("HEADER", createHeaderFieldSetMapper());
      mappers.put("TRANSACTION", createTransactionFieldSetMapper());

      lineMapper.setTokenizers(tokenizers);
      lineMapper.setFieldSetMappers(mappers);

      return lineMapper;
   }

   private LineTokenizer createHeaderLineTokenizer() {
      DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(";");
      tokenizer.setNames("transactionDate", "valör", "transactionType");
      return tokenizer;
   }

   private LineTokenizer createTransactionLineTokenizer() {
      DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer("\t");
      tokenizer.setNames("transactionType", "commissionAmount", "rewardTotal", "serviceCommission");
      return tokenizer;
   }

   private FieldSetMapper<Transaction> createHeaderFieldSetMapper() {
      return fieldSet -> null; // Skip the header line
   }

   private FieldSetMapper<Transaction> createTransactionFieldSetMapper() {
      BeanWrapperFieldSetMapper<Transaction> mapper = new BeanWrapperFieldSetMapper<>();
      mapper.setTargetType(Transaction.class);
      return mapper;
   }

   @Bean
   public FlatFileItemReader<Transaction> reader() {
      FlatFileItemReader<Transaction> reader = new FlatFileItemReader<>();
      reader.setResource(new ClassPathResource("transactions.txt"));
      reader.setLineMapper(lineMapper());
      reader.setLinesToSkip(1); // Skip the header line
      return reader;
   }

   @Bean
   public ItemProcessor<Transaction, Transaction> processor() {
      return new TransactionItemProcessor();
   }

   @Bean
   public ItemWriter<Transaction> writer() {
      return new TransactionItemWriter();
   }

   @Bean
   public Step step(ItemReader<Transaction> reader, ItemProcessor<Transaction, Transaction> processor, ItemWriter<Transaction> writer) {
      return stepBuilderFactory.get("step")
            .<Transaction, Transaction>chunk(10)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
   }

   // ... job configuration
}
