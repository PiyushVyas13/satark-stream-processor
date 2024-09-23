package org.cyducks.reportmanager.topology;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.cyducks.reportmanager.model.Report;
import org.cyducks.reportmanager.serde.JsonArraySerde;
import org.cyducks.reportmanager.serde.JsonSerde;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ReportTopology {

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        Serde<Report> reportSerde = new JsonSerde<>(Report.class);
        Serde<List<Report>> listSerde = new JsonArraySerde<>(Report.class);

        ArrayList<Report> reports = new ArrayList<>();

//        KStream<String, Report> reportStream = builder.stream("reports", Consumed.with(Serdes.String(), reportSerde));


       builder
                .stream("reports", Consumed.with(Serdes.String(), reportSerde))
                .groupByKey()
                .windowedBy(TimeWindows.of(Duration.ofSeconds(20L)).grace(Duration.ofSeconds(2L)))
                .aggregate(() -> 0L, (key, value, aggregate) -> {
                    reports.add(value);
                    return aggregate+1;
                })
                .suppress(Suppressed.untilWindowCloses(Suppressed.BufferConfig.unbounded()))
                .toStream()
                .peek((key, value) -> log.info("{} : {}", key.key(), value))
                .map((key, value) -> KeyValue.pair(key.key(), value))
                .filter((key, count) -> count >= 10L)
                .mapValues((readOnlyKey, value) -> getReportById(reports, readOnlyKey))
                .peek((key, value) -> reports.clear())
                .to("mass-reports", Produced.with(Serdes.String(), listSerde));




        return builder.build();
    }

    public static List<Report> getReportById(ArrayList<Report> reports, String id) {
        List<Report> matchingReports = new ArrayList<>();
        for (Report  report:
             reports) {
            if(report.getId().equals(id)) {
                matchingReports.add(report);
            }
        }

        return matchingReports;
    }
}
