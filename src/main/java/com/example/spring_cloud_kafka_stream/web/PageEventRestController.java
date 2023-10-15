package com.example.spring_cloud_kafka_stream.web;


import com.example.spring_cloud_kafka_stream.entities.PageEvent;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class PageEventRestController {

@Autowired
private StreamBridge streamBridge;

@Autowired
private InteractiveQueryService interactiveQueryService;


// Is this publish function we create producer console to send messages to consumers
@GetMapping("/publish/{topic}/{name}")
public PageEvent publish(@PathVariable String topic, @PathVariable String name){

    PageEvent pageEvent=new PageEvent(name,Math.random()>0.5 ?"U1":"U2",new Date(),new Random().nextInt(9000));
    streamBridge.send(topic,pageEvent);

    return pageEvent;}


// Analytics endpoint
// Define a GET mapping for the "/analytics" endpoint, producing text event streams

    @GetMapping(path = "/analytics", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<String, Long>> analytics() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> {
                    Map<String, Long> stringLongMap = new HashMap<>();
                    ReadOnlyWindowStore<String, Long> stats =
                            interactiveQueryService.getQueryableStore("st-store", QueryableStoreTypes.windowStore());
                    Instant now = Instant.now();
                    Instant from = now.minusMillis(5000);
                    KeyValueIterator<Windowed<String>, Long> fetchAll = stats.fetchAll(from, now);
                    //WindowStoreIterator<Long> fetch = stats.fetch(name, from, now);
                    while (fetchAll.hasNext()) {
                        KeyValue<Windowed<String>, Long> next = fetchAll.next();
                        stringLongMap.put(next.key.key(), next.value);
                    }
                    return stringLongMap;
                }).share();
    }









}
