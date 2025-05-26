package com.example.demo1;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "images")
public class Image {
    @Id
    private String id;

    private String fileName;
    private String fileType;
    private String analysisResult;
    private String username;

    private byte[] data;


    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }
    public String getAnalysisResult() { return analysisResult; }
    public void setAnalysisResult(String analysisResult) { this.analysisResult = analysisResult; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
