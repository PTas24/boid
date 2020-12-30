package io.ogi.examples;

import io.ogi.examples.model.BoidModel;

public interface BoidSimulationBase extends Runnable{
  void initializeBoids();
  BoidModel getBoidModel();
}
