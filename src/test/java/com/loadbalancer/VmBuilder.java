package serverloadbalancer;

public class VmBuilder implements Builder<Vm> {
  private int size;

  @Override
  public Vm build() {
    return new Vm(size);
  }

  public static VmBuilder vm() {
    return new VmBuilder();
  }

  public VmBuidler ofSize(final int size) {
    this.size = size;
    return this;
  }
}
