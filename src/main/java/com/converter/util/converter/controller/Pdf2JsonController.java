package com.converter.util.converter.controller;

import com.fasterxml.jackson.core.JsonParser;

import com.google.gson.Gson;


import com.google.gson.JsonObject;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@RestController
public class Pdf2JsonController {
	
	 @Value("${srcPdfPath}")
	 private String srcPdfPath;
	 
	 @Value("${trgtTextPath}")
	 private String trgtTextPath;
	 
	 @Value("${trgtJsonPath}")
	 private String trgtJsonPath;

    final Logger LOGGER = LoggerFactory.getLogger(getClass());
   @GetMapping("/text") 
   public String generateTxtFromPDF() throws Exception {
        File f = new File(srcPdfPath);
        String parsedText;
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();

        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        parsedText = pdfStripper.getText(pdDoc);
        PrintWriter pw = new PrintWriter(trgtTextPath);
        pw.print(parsedText);
        pw.close();
        return readText().toString();
    }

    public JsonObject readText() throws IOException {
    	 FileWriter file= new FileWriter(trgtJsonPath);
        JsonObject jsonObj = new JsonObject();
     // Use AtomicInteger as a mutable line count.
        final AtomicInteger count = new AtomicInteger();
        //read file into stream, try-with-resources
        try (Stream<String> stream = Files.lines(Paths.get(trgtTextPath))) {
        	//here we will set the text to jsonModel object
            stream.forEach(s->{	
            	jsonObj.addProperty("Content"+count.incrementAndGet(), s);});
           
            file.write(jsonObj.toString());
            
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        	file.flush();
        	file.close();
		}
      //  System.out.println(jsonObj.toString());
        return jsonObj;
    }

}
