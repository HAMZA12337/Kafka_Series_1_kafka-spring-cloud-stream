package com.example.spring_cloud_kafka_stream.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class PageEvent {

    private String name;
    private String user ;
    private Date date;
    private long duration ;




}
