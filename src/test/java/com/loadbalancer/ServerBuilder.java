package serverloadbalancer;

public class ServerBuilder implements Builder<Server> {
  private int capacity;

  @Override
  public Server build() {
    return new Server(capacity);
  }
 
  public static ServerBuilder server() {
    return new ServerBuilder();
  }

  public ServerBuilder withCapacity(final int capacity) {
    this.capacity = capacity;
    return this;
  }
}
