package org.camunda.bpm.engine.impl.cfg;

import static org.assertj.core.api.Assertions.*;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class ProcessEngineConfigurationTest {

  @Test
  public void shouldEnableStandaloneTasksByDefault() {
    // when
    ProcessEngineConfigurationImpl engineConfiguration = (ProcessEngineConfigurationImpl) ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();

    // then
    assertThat(engineConfiguration.isStandaloneTasksEnabled()).isTrue();
  }
}
