package com.aws.healthcheck.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.aws.healthcheck.dto.Resource;
import com.aws.healthcheck.dto.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileHandlingService {
    String path;

    public FileHandlingService(@Value("${comm.fileHandling.path}") final String path) {
        this.path = path;
    }

    public void writeToFile(final Response<Resource> d) throws IOException {

        try {
            final CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
            final List<Resource> receivedData = d.getEmbedded().getItem().stream()
                    .collect(Collectors.toList());

            final List<String> fields = Resource.fieldsToUseInTheExport();
            for (final String field : fields) {
                csvSchemaBuilder.addColumn(field);
            }
            final CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
            final CsvMapper csvMapper = new CsvMapper();
            csvMapper.writerFor(ArrayList.class).with(csvSchema).writeValue(new File(path), receivedData);
        } catch (final InvalidDefinitionException e) {
            log.error("Csvmapper did not expect to write that class. msg={}", e.getMessage(), e);
        } catch (final IOException e) {
            log.error(
                    "IO error writing to file, this might be caused by a permission problem or file is open and therefore read-only. msg={}",
                    e.getMessage(), e);
        }
    }

    public List<Integer> fetchFirstThreeLines(final List<Integer> csvExportOutput, final int numberOfLines,
            final String fileName) throws IOException {
        final File myData = new ClassPathResource(fileName).getFile();
        try (final FileReader textReader = new FileReader(myData)) {
            int i;
            for (i = 0; i < numberOfLines; i++) {
                csvExportOutput.add(textReader.read());
            }
        } catch (final IOException e) {
            log.error("No file to open in path {}", e);
        }
        return csvExportOutput;

    }

    public Long fileLastModified(Long lastModified, final String fileName) throws IOException {

        try {
            final File myData = new ClassPathResource(fileName).getFile();
            lastModified = myData.lastModified();

        } catch (final IOException e) {
            log.error("No file to open in path {}", e);
        }
        return lastModified;

    }

    public static final Response<Resource> readJsonWithObjectMapper() throws IOException {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            final File myData = new ClassPathResource("testdata.json").getFile();
            return objectMapper.readValue(myData, Response.class);
        } catch (final IOException e) {
            log.error(
                    "IO error while reading the test data, this might be caused by a permission problem or file is open and therefore read-only. msg={}, e={}",
                    e.getMessage(), e);
        }
        return new Response<>();

    }

    public static void deleteFile(String pathToFile) throws FileNotFoundException {
        try {
            final File myFile = ResourceUtils.getFile("classpath:data.csv");

            if (myFile.exists()) {
                log.info("{} exists and will be deleted", pathToFile);
                if (myFile.delete()) {
                    log.info("{} successfully deleted", pathToFile);
                }
            }

        } catch (FileNotFoundException e) {
            log.info("nothing to delete as {} does not exist", pathToFile);

        } catch (Exception e) {
            log.error("{}", e.getMessage());
        }
    }
}
