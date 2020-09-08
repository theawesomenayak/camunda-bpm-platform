package org.camunda.bpm.engine.test.api.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.exception.NotAllowedException;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.util.ProcessEngineBootstrapRule;
import org.camunda.bpm.engine.test.util.ProcessEngineTestRule;
import org.camunda.bpm.engine.test.util.ProvidedProcessEngineRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.RuleChain;

public class StandaloneTasksDisabledTest {

  @ClassRule
  public static ProcessEngineBootstrapRule bootstrapRule = new ProcessEngineBootstrapRule(p ->
     p.setStandaloneTasksEnabled(false));

  public ProvidedProcessEngineRule engineRule = new ProvidedProcessEngineRule(bootstrapRule);
  public ProcessEngineTestRule engineTestRule = new ProcessEngineTestRule(engineRule);

  @Rule
  public RuleChain ruleChain = RuleChain.outerRule(engineRule).around(engineTestRule);

  @Rule
  public ExpectedException exception = ExpectedException.none();

  private RuntimeService runtimeService;
  private TaskService taskService;
  private IdentityService identityService;
  private AuthorizationService authorizationService;
  private CaseService caseService;


  // TODO: double-check if all of this is needed
  @Before
  public void setUp() throws Exception {
    runtimeService = engineRule.getRuntimeService();
    taskService = engineRule.getTaskService();
    identityService = engineRule.getIdentityService();
    authorizationService = engineRule.getAuthorizationService();
    caseService = engineRule.getCaseService();
  }

  @After
  public void tearDown() {
    identityService.clearAuthentication();
    engineRule.getProcessEngineConfiguration().setAuthorizationEnabled(false);
    engineTestRule.deleteAllAuthorizations();
    engineTestRule.deleteAllStandaloneTasks();
  }

  @Test
  public void shouldNotCreateStandaloneTask() {
    // given
    Task task = taskService.newTask();

    // then
    exception.expect(NotAllowedException.class); // TODO: most speecific class?
    exception.expectMessage("Cannot save standalone task. They are disabled in the process engine configuration.");

    // when
    taskService.saveTask(task);
  }

  @Test
  @Deployment(resources = "org/camunda/bpm/engine/test/api/oneTaskProcess.bpmn20.xml")
  public void shouldAllowToUpdateProcessInstanceTask() {

    // given
    runtimeService.startProcessInstanceByKey("oneTaskProcess");
    Task task = taskService.createTaskQuery().singleResult();

    task.setAssignee("newAssignee");

    // when
    taskService.saveTask(task);

    // then
    Task updatedTask = taskService.createTaskQuery().singleResult();
    assertThat(updatedTask.getAssignee()).isEqualTo("newAssignee");
  }

  @Test
  @Deployment(resources = "org/camunda/bpm/engine/test/api/cmmn/oneTaskCase.cmmn")
  public void shouldAllowToUpdateCaseInstanceTask() {

    // given
    caseService.createCaseInstanceByKey("oneTaskCase").getId();
    Task task = taskService.createTaskQuery().singleResult();

    task.setAssignee("newAssignee");

    // when
    taskService.saveTask(task);

    // then
    Task updatedTask = taskService.createTaskQuery().singleResult();
    assertThat(updatedTask.getAssignee()).isEqualTo("newAssignee");
  }
}
