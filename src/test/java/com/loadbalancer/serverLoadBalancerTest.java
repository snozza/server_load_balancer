package serverloadbalancer;

import static serverloadbalancer.ServerBuilder.server;
import static org.hamcrest.Matchers.allOf;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class serverLoadBalancerTest {

  @Test
  public void itCompiles() {
    assertThat(true).isEqualTo(true);
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

  private Vm[] anEmptyListOfVm() {
    return new Vm[0];
  }
  
  private Matcher<? super Server> hasCurrentLoadPercentageOf(final double currentLoadPercentage) {
    return new CurrentLoadPercentageMatcher(currentLoadPercentage);
  }
  
  @Test
  public void balancingOneServer_noVm_serverStaysEmpty() {
    Server theServer = a(server().withCapacity(1));
    balance(theListOfServerWith(theServer, anEmptyListOfVm()));
    assertThat(theServer, hasCurrentLoadPercentageOf(0.0d));
  }

}
