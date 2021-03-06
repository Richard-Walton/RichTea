package richTea.core.attribute.bool;

import richTea.core.attribute.Attribute;
import richTea.core.attribute.math.AbstractMathExpressionAttribute;
import richTea.core.execution.ExecutionContext;

public abstract class AbstractMathBooleanExpressionAttribute extends AbstractMathExpressionAttribute {

	public AbstractMathBooleanExpressionAttribute(String name, Attribute leftOperand, Attribute rightOperand) {
		super(name, leftOperand, rightOperand);
	}

	@Override
	public Object getValue(ExecutionContext context) {
		Object value = super.getValue(context);
		
		if(value == null) {
			// We couldn't convert the input to numbers so we return false.
			value = false;
		}
		
		return value;
	}
}