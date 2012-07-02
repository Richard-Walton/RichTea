package richTea.core.factory.bindings;

import richTea.core.attribute.Attribute;
import richTea.core.attribute.PrimativeAttribute;
import richTea.core.attribute.modifier.AttributeModifier;
import richTea.core.execution.ExecutionContext;
import richTea.core.node.DataNode;
import richTea.core.node.TreeNode;

public class AttributeNode extends DataNode implements Attribute {
	
	private TreeNode owner;
	
	public String getName() {
		return resolver.getString("name");
	}
	
	public void setName(String name) {
		setAttribute(new PrimativeAttribute("name", name));
	}
	
	public Object getValue() {
		return getValue(new ExecutionContext());
	}
	
	public Object getValue(ExecutionContext context) {
		return resolver.getValue("defaultValue");
	}
	
	@Override
	public Object modify(AttributeModifier modifier) {
		Attribute attribute = getAttribute("defaultValue");
		
		return attribute != null ? attribute.modify(modifier) : null;
	}
	
	public TreeNode getOwner() {
		return owner;
	}
	
	public void setOwner(TreeNode owner) {
		this.owner = owner;
	}
}