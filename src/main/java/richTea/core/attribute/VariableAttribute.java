package richTea.core.attribute;

import richTea.core.attribute.modifier.AttributeModifier;
import richTea.core.attribute.variable.LookupChainElement;
import richTea.core.attribute.variable.LookupChainRoot;
import richTea.core.attribute.variable.PropertyLookup;
import richTea.core.execution.ExecutionContext;

public class VariableAttribute extends LookupChainElement {
	
	public VariableAttribute(String property) {
		this(property, new PropertyLookup(new PrimativeAttribute("property", property), new LookupChainRoot("root")));
	}
	
	public VariableAttribute(String name, Attribute lookupChain) {
		super(lookupChain);
		
		setName(name);
	}
	
	@Override
	protected Object performLookup(ExecutionContext content, Object lookupChainValue) {
		return lookupChainValue;
	}
	
	@Override
	public Object modify(ExecutionContext context, AttributeModifier modifier) {
		return getLookupChain().modify(context, modifier);
	}
}