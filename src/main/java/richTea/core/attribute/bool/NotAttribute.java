package richTea.core.attribute.bool;

import richTea.core.attribute.AbstractAttribute;
import richTea.core.attribute.Attribute;
import richTea.core.execution.ExecutionContext;

public class NotAttribute extends AbstractAttribute {

	private Attribute initalValue;
	
	public NotAttribute(String name, Attribute initalValue) {
		super(name);
		
		this.initalValue = initalValue;
	}
	
	protected Attribute getInitialValue() {
		return initalValue;
	}
	
	@Override
	public Boolean getValue(ExecutionContext context) {
		Object value = getInitialValue().getValue(context);
		
		return !Boolean.parseBoolean(value.toString());
	}
}