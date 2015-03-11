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

}
