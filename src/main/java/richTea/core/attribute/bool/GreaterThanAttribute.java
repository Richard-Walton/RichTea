package richTea.core.attribute.bool;

import richTea.core.attribute.Attribute;

public class GreaterThanAttribute extends AbstractMathBooleanExpressionAttribute {

	public GreaterThanAttribute(String name, Attribute leftOperand, Attribute rightOperand) {
		super(name, leftOperand, rightOperand);
	}

	@Override
	protected Object getValue(double value1, double value2) {
		return value1 > value2;
	}
}