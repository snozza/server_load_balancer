package serverloadbalancer;

import static serverloadbalancer.CurrentLoadPercentageMatcher.hasCurrentLoadPercentageOf;
import static serverloadbalancer.VmBuilder.vm;
import static serverloadbalancer.ServerBuilder.server;
import static serverloadbalancer.VmCountOfServerMatcher.hasAVmCountOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class serverLoadBalancerTest {

  @Test
  public void itCompiles() {
    assertThat(true, equalTo(true));
  }

  private void balance(Server[] servers, Vm[] vms) {
    new ServerLoadBalancer().balance(servers, vms);
  }
  
  private <T> T a(Builder<T> builder) {
    return builder.build();
  }

  private Server[] theListOfServerWith(final Server... servers) {
    return servers;
  }

  private Vm[] theListOfVm(final Vm... vms) {
    return vms;
  }

  private Vm[] anEmptyListOfVm() {
    return new Vm[0];
  } 
  
  @Test
  public void balancingOneServer_noVm_serverStaysEmpty() {
    Server theServer = a(server().withCapacity(1));
    balance(theListOfServerWith(theServer), anEmptyListOfVm());
    assertThat(theServer, hasCurrentLoadPercentageOf(0.0d));
  }

  @Test
  public void balanceAServerWithOnceSlotCapacity_andAOneSlotVm_fillsTheServerWithTheVM() {
    Server theServer = a(server().withCapacity(1));
    Vm theVm = a(vm().ofSize(1));
    balance(theListOfServerWith(theServer), theListOfVm(theVm));
    assertThat(theServer, hasCurrentLoadPercentageOf(100.0d));
  }

  @Test
  public void balanceAServerWithTenSlotsCapacity_andAOneSlotVm_fillsTheServerAtTenPercent() {
    Server theServer = a(server().withCapacity(10));
    Vm theVm = a(vm().ofSize(1));
    balance(theListOfServerWith(theServer), theListOfVm(theVm));
    assertThat(theServer, hasCurrentLoadPercentageOf(10.0d));
    assertThat("The server should contain the VM", theServer.contains(theVm));
  }
    
  @Test
  public void balanceAServerWithEnoughCapacity_getsFilledWithAllVms() {
    Server theServer = a(server().withCapacity(100));
    Vm theFirstVm = a(vm().ofSize(1));
    Vm theSecondVm = a(vm().ofSize(1));
    balance(theListOfServerWith(theServer), theListOfVm(theFirstVm, theSecondVm));
    assertThat("The server should contain the VM", theServer.contains(theFirstVm));
    assertThat("The server should contain the VM", theServer.contains(theSecondVm));
    assertThat(theServer, hasAVmCountOf(2));
  }

  @Test
  public void aVm_isBalance_onLessLoadedServerFirst() {
    Server mostLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(50.0d));
    Server lessLoadedServer = a(server().withCapacity(100).withCurrentLoadOf(45.0d));
    Vm theVm = a(vm().ofSize(10));
    balance(theListOfServerWith(mostLoadedServer, lessLoadedServer), theListOfVm(theVm));
    assertThat("The less loaded server contains the Vm", lessLoadedServer.contains(theVm));
  }
}
