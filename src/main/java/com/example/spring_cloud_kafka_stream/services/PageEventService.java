package com.example.spring_cloud_kafka_stream.services;


import com.example.spring_cloud_kafka_stream.entities.PageEvent;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
public class PageEventService {


    // get data : input mode
    @Bean
    public Consumer<PageEvent> pageEventConsumer(){
        return (pageEvent) -> {
            System.out.println("******------************");
            System.out.println(pageEvent.toString());
            System.out.println("*******-----**********");
        };
    }


    // send data :oupout mode  for every second

    @Bean
    public Supplier<PageEvent> pageEventSupplier(){
        return()->
            new PageEvent(
                    Math.random()>0.5 ?"Page 1":"Page 2",
                    Math.random()>0.5 ?"U1":"U2",
                    new Date(),new Random().nextInt(9000)
            );

    }

    // producer(out) + consumer(in)

    @Bean
    public Function<PageEvent,PageEvent> pageEventFunction(){
        return (input)->{
            input.setName(input.getName()+"updtated");
            input.setUser(input.getUser()+"Used ");
        return input ;
        };
    }


    @Bean

public Function<KStream<String,PageEvent>,KStream<String,Long>>  kStreamFunction(){

        return (input)->{

        return input
                .filter((k,v)->v.getDuration()>100)
                .map((k,v)->new KeyValue<>(v.getName(),0l))
                .groupBy((k,v)->k,Grouped.with(Serdes.String(),Serdes.Long()))
                .windowedBy(TimeWindows.of(Duration.ofDays(5000)))
                .count(Materialized.as("Page_counter"))
                .toStream()
                .map((k,v)->new KeyValue<>("=>"+k.window().startTime()+k.window().endTime()+" "+k.key(),v));


 };



}






}
