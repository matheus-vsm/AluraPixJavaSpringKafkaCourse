package com.alura.pixstream.stream;

import com.alura.pixstream.serdes.PixSerdes;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PixAggregator {

    //metodo que cria o pipeline de processamento de streams do Kafka. sobre assim que a aplicação sobe
    @Autowired
    public void buildPipeline(StreamsBuilder streamsBuilder) {
//        KStream<String, PixDTO> messageStream = streamsBuilder
//                .stream("pix-topic", Consumed.with(Serdes.String(), PixSerdes.serdes()))
//                .peek((key, value) -> System.out.println("Pix recebido: " + value.getChaveOrigem()))
//                .filter((key, value) -> value.getValor() > 10000);
//
//        messageStream.print(Printed.toSysOut());
//        messageStream.to("pix-verificacao-fraude", Produced.with(Serdes.String(), PixSerdes.serdes()));

        KTable<String, Double> messageStream = streamsBuilder
                .stream("pix-topic", Consumed.with(Serdes.String(), PixSerdes.serdes()))
                .peek((key, value) -> System.out.println("Pix recebido: " + value.getChaveOrigem()))
                .groupBy((key, value) -> value.getChaveOrigem())
                .aggregate(
                        () -> 0.0,
                        (key, value, aggregate) -> (aggregate + value.getValor()),
                        Materialized.with(Serdes.String(), Serdes.Double())
                );

        messageStream.toStream().print(Printed.toSysOut());
        messageStream.toStream().to("pix-topic-agregacao", Produced.with(Serdes.String(), Serdes.Double()));
    }

}
