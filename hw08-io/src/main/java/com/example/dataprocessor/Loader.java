package com.example.dataprocessor;

import com.example.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();
}
