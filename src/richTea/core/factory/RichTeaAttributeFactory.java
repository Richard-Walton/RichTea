package richTea.core.factory;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.tree.Tree;

import richTea.antlr.RichTeaParser;
import richTea.antlr.tree.AttributeData;
import richTea.antlr.tree.NodeData;
import richTea.core.attribute.ArrayAttribute;
import richTea.core.attribute.AssignmentAttribute;
import richTea.core.attribute.Attribute;
import richTea.core.attribute.BooleanAttribute;
import richTea.core.attribute.ExpressionAttribute;
import richTea.core.attribute.FunctionAttribute;
import richTea.core.attribute.LookupAttribute;
import richTea.core.attribute.NumberAttribute;
import richTea.core.attribute.PrimativeAttribute;
import richTea.core.attribute.StringAttribute;
import richTea.core.attribute.bool.AndAttribute;
import richTea.core.attribute.bool.EqualsAttribute;
import richTea.core.attribute.bool.GreaterThanAttribute;
import richTea.core.attribute.bool.GreaterThanOrEqualToAttribute;
import richTea.core.attribute.bool.LessThanAttribute;
import richTea.core.attribute.bool.LessThanOrEqualToAttribute;
import richTea.core.attribute.bool.NotAttribute;
import richTea.core.attribute.bool.NotEqualsAttribute;
import richTea.core.attribute.bool.OrAttribute;
import richTea.core.attribute.math.DivideAttribute;
import richTea.core.attribute.math.MinusAttribute;
import richTea.core.attribute.math.MultiplyAttribute;
import richTea.core.attribute.math.PlusAttribute;
import richTea.core.node.TreeNode;

public class RichTeaAttributeFactory {
	
	private RichTeaNodeFactory nodeFactory;
	
	public RichTeaAttributeFactory(RichTeaNodeFactory nodeFactory) {
		
		this.nodeFactory = nodeFactory;
	}
	
	public Attribute create(AttributeData attributeData) {
		return create(attributeData.getName(), attributeData.getValue());
	}
	
	protected Attribute create(String name, Tree value) {
		Attribute attribute = null;
		
		switch(value.getType()) {
			case RichTeaParser.STRING :	
				attribute = createStringAttribute(name, value);
				break;
			case RichTeaParser.NUMBER : 
				attribute = createNumberAttribute(name, value);
				break;
			case RichTeaParser.BOOLEAN : 
				attribute = createBooleanAttribute(name, value);
				break;
			case RichTeaParser.ARRAY :
				attribute = createArrayAttribute(name, value);
				break;
			case RichTeaParser.LOOKUP :
				attribute = createLookupAttribute(name, value);
				break;
			case RichTeaParser.FUNCTION :
				attribute = createFunctionAttribute(name, (NodeData) value);
				break;
			case RichTeaParser.PLUS :
				attribute = createPlusAttribute(name, value);
				break;
			case RichTeaParser.PLUS_EQUALS :
				attribute = createAssignmentAttribute(name, createPlusAttribute(name, value));
				break;
			case RichTeaParser.MINUS_EQUALS :
				attribute = createAssignmentAttribute(name, createMinusAttribute(name, value));
				break;
			case RichTeaParser.MULTIPLY_EQUALS :
				attribute = createAssignmentAttribute(name, createMultiplyAttribute(name, value));
				break;
			case RichTeaParser.DIVIDE_EQUALS :
				attribute = createAssignmentAttribute(name, createDivideAttribute(name, value));
				break;
			case RichTeaParser.MINUS :
				attribute = createMinusAttribute(name, value);
				break;
			case RichTeaParser.MULTIPLY :
				attribute = createMultiplyAttribute(name, value);
				break;
			case RichTeaParser.DIVIDE :
				attribute = createDivideAttribute(name, value);
				break;
			case RichTeaParser.EQ :
				attribute = createEqualsAttribute(name, value);
				break;
			case RichTeaParser.NEQ :
				attribute = createNotEqualsAttribute(name, value);
				break;
			case RichTeaParser.AND : 
				attribute = createAndAttribute(name, value);
				break;
			case RichTeaParser.OR : 
				attribute = createOrAttribute(name, value);
				break;
			case RichTeaParser.NOT :
				attribute = createNotAttribute(name, value);
				break;
			case RichTeaParser.LT : 
				attribute = createLessThanAttribute(name, value);
				break;
			case RichTeaParser.LTEQ :
				attribute = createLessThanOrEqualToAttribute(name, value);
				break;
			case RichTeaParser.GT :
				attribute = createGreaterThanAttribute(name, value);
				break;
			case RichTeaParser.GTEQ :
				attribute = createGreaterThanOrEqualToAttribute(name, value);
				break;
			default :
				attribute = new PrimativeAttribute(name, value.getText());
		}
		
		return attribute;
	}
	
	protected Attribute createStringAttribute(String name, Tree value) {
		String stringValue = value.getText().substring(1); // Remove the leading "
		stringValue = stringValue.substring(0, stringValue.length() - 1); // Remove the trailing "

		return new StringAttribute(name, stringValue);
	}
	
	protected Attribute createNumberAttribute(String name, Tree value) {
		return new NumberAttribute(name, Double.parseDouble(value.getText()));
	}
	
	protected Attribute createBooleanAttribute(String name, Tree value) {
		return new BooleanAttribute(name, Boolean.parseBoolean(value.getText()));
	}
	
	protected Attribute createLookupAttribute(String name, Tree value) {		
		List<String> lookupPath = new ArrayList<String>();
		
		for(int i = 0; i < value.getChildCount(); i++) {
			lookupPath.add(value.getChild(i).getText());
		}
		
		return new LookupAttribute(name, lookupPath);
	}
	
	protected Attribute createArrayAttribute(String name, Tree value) {		
		return new ArrayAttribute(name, getAttributeElements(name, value));
	}
		
	protected Attribute createFunctionAttribute(String name, NodeData nodeData) {
		TreeNode function = nodeFactory.create(nodeData);
		
		return new FunctionAttribute(name, function);
	}
	
	protected ExpressionAttribute createPlusAttribute(String name, Tree value) {
		return new PlusAttribute(name, getAttributeElements("operand", value));
	}
		
	protected ExpressionAttribute createMinusAttribute(String name, Tree value) {
		return new MinusAttribute(name, getAttributeElements("operand", value));
	}
	
	protected ExpressionAttribute createMultiplyAttribute(String name, Tree value) {
		return new MultiplyAttribute(name, getAttributeElements("operand", value));
	}
	
	protected ExpressionAttribute createDivideAttribute(String name, Tree value) {
		return new DivideAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createAssignmentAttribute(String name, ExpressionAttribute value) {
		return new AssignmentAttribute(name, value);
	}
	
	protected Attribute createAndAttribute(String name, Tree value) {
		return new AndAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createOrAttribute(String name, Tree value) {
		return new OrAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createNotAttribute(String name, Tree value) {
		Attribute attribute = create(name, value.getChild(0));
		
		return new NotAttribute(name, attribute);
	}
	
	protected Attribute createEqualsAttribute(String name, Tree value) {
		return new EqualsAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createNotEqualsAttribute(String name, Tree value) {
		return new NotEqualsAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createLessThanAttribute(String name, Tree value) {
		return new LessThanAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createLessThanOrEqualToAttribute(String name, Tree value) {
		return new LessThanOrEqualToAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createGreaterThanAttribute(String name, Tree value) {	
		return new GreaterThanAttribute(name, getAttributeElements("operand", value));
	}
	
	protected Attribute createGreaterThanOrEqualToAttribute(String name, Tree value) {
		return new GreaterThanOrEqualToAttribute(name, getAttributeElements("operand", value));
	}
	
	protected List<Attribute> getAttributeElements(String elementPrefix, Tree elements) {
		List<Attribute> elementAttributes = new ArrayList<Attribute>(elements.getChildCount());
		
		for(int i = 0; i < elements.getChildCount(); i++) {
			elementAttributes.add(create(String.format("%s[%s]", elementPrefix, i), elements.getChild(i)));
		}
		
		return elementAttributes;
	}
}