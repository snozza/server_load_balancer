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

  @Test
  public void balanceAServerWithNotEnoughRoom_isNotFilledWithAVm() {
    Server theServer = a(server().withCapacity(10).withCurrentLoadOf(90.0d));
    Vm theVm = a(vm().ofSize(2));
    balance(theListOfServerWith(theServer), theListOfVm(theVm));
    assertThat("The server does not contain the Vm", !theServer.contains(theVm));
  }

  @Test
  public void balanceMultipleServersAndVms_withCorrectLoadsOnServers() {
    Server server1 = a(server().withCapacity(4));
    Server server2 = a(server().withCapacity(6));

    Vm vm1 = a(vm().ofSize(1));
    Vm vm2 = a(vm().ofSize(4));
    Vm vm3 = a(vm().ofSize(2));
    
    balance(theListOfServerWith(server1, server2), theListOfVm(vm1, vm2, vm3));

    assertThat("The server1 should contain the Vm1", server1.contains(vm1));
    assertThat("The server2 should contain the Vm2", server2.contains(vm2));
    assertThat("The server1 should contain the Vm3", server1.contains(vm3));

    assertThat(server1, hasACurrentLoadPercentageOf(75.0d));
    assertThat(server2, hasACurrentLoadPercenatgeOf(66.66d));
  }
}
