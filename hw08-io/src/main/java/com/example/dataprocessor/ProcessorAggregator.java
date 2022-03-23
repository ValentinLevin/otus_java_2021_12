package com.example.dataprocessor;

import com.example.model.Measurement;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        if (data != null) {
            return data.stream().collect(
                    Collectors.groupingBy(
                            Measurement::getName,
                            TreeMap::new,
                            Collectors.summingDouble(Measurement::getValue)
                    )
            );
        }
        return null;
    }
}
