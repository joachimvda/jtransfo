package org.jtransfo.guice;

import java.util.LinkedList;
import java.util.List;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfo;
import org.jtransfo.ListTypeConverter;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ExampleModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		bind(JTransfo.class).to(JTransfoGuice.class);

	}
	
	@Provides
	List<TypeConverter> jTransfoTypeConverters() {
		List<TypeConverter> result = new LinkedList<TypeConverter>();
		//add your specific type converters here.
		return result;
	}
	
	@Provides
	List<ObjectFinder> jTransfoObjectFinders() {
		LinkedList<ObjectFinder> result = new LinkedList<ObjectFinder>();
		//add your specific objectfinders here.
		return result;
	}
	
	@Provides
	List<ConvertInterceptor> jTransfoConverterInterceptors() {
		LinkedList<ConvertInterceptor> result = new LinkedList<ConvertInterceptor>();
		//add your specific convertinterceptors here
		return result;
	}

}
