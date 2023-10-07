package com.example.spring_cloud_kafka_stream.services;


import com.example.spring_cloud_kafka_stream.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

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

    // producer + consumer

    @Bean
    public Function<PageEvent,PageEvent> pageEventFunction(){
        return (input)->{
            input.setName(input.getName()+"updtated");
            input.setUser(input.getUser()+"Used ");
        return input ;
        };
    }







}
