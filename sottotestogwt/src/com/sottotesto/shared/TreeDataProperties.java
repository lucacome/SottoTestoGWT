package com.sottotesto.shared;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TreeDataProperties extends PropertyAccess<TreeData>{

	// Note that this implies that the string value available in the id property will be unique
	ModelKeyProvider<TreeData> id();

	// Create a simple LabelProvider based on the firstName property - note that although firstName is already 
	// used we can still have another Provider hooked to the same property, using the Editor annotation @Path
	// Note also that this could just as easily be much more complex, either as an XTemplate or as a custom label
	/*
	  @Path("firstName")
	  LabelProvider<Person> simpleLabel(); 
	 */

	// None of these are used in this example, and could be removed, but could be helpful in other cases
	ValueProvider<TreeData, String> name();
	ValueProvider<TreeData, String> clickAction(); 
	ValueProvider<TreeData, String> jsonFD(); 

	ValueProvider<TreeData, String> jsonHT(); 

}
