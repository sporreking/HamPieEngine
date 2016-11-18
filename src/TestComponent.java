import sk.entity.Component;

public class TestComponent extends Component {
	
	public int i = 0;
	
	@Override
	public void update(double delta) {
		print();
	}
	
	public void print() {
		System.out.println("My value is: " + i);
	}
	
}