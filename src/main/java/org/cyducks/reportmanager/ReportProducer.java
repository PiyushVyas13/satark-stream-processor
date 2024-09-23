package org.cyducks.reportmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.cyducks.reportmanager.model.Report;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Slf4j
public class ReportProducer {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static void main(String args[]) throws InterruptedException {
        KafkaProducer<String, String> reportProducer =
                new KafkaProducer<>(
                        Map.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29093",
                                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
                        )
                );

        List<Report> reportList = List.of(
              Report.builder()
                      .id(UUID.randomUUID().toString())
                      .type("theft")
                      .time(new Date())
                      .coordinates(List.of(25.53, 43.53))
                      .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build(),
                Report.builder()
                        .id(UUID.randomUUID().toString())
                        .type("theft")
                        .time(new Date())
                        .coordinates(List.of(25.53, 43.53))
                        .build()
        );

//        reportList.stream()
//                .map(report -> new ProducerRecord<>("reports", report.getId(), toJson(report)))
//                .forEach(record -> send(reportProducer, record));


        while(true) {
            Thread.sleep(1000L);

            Stream.of(Report.builder()
                    .id("M1")
                    .type("theft")
                    .time(new Date())
                    .coordinates(List.of(Math.random(), Math.random()))
                    .build()
            )
                    .peek(t -> log.info("Sending new report: {}" ,t))
                    .map(t -> new ProducerRecord<>("reports", t.getId(), toJson(t)))
                    .forEach(producerRecord -> send(reportProducer, producerRecord));
        }


    }

    @SneakyThrows
    private static void send(KafkaProducer<String, String> reportProducer, ProducerRecord<String, String> record) {
        reportProducer.send(record).get();
    }

    @SneakyThrows
    private static String toJson(Report report) {
        return OBJECT_MAPPER.writeValueAsString(report);
    }
}
