package com.agileasoft.zebra;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author amgohan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapperFactory implements IMapper {

	private final Map<String, Mapper> map = new HashMap<String, Mapper>();

	public <A, B> MapperFactory register(final Mapper<A, B> customMapper) {

		Objects.requireNonNull(customMapper, "Null Mapper can't be registered.");
		final Type[] mapperGenericTypes = ((ParameterizedType) customMapper.getClass().getGenericSuperclass()).getActualTypeArguments();
		final Class<A> classA = (Class<A>) mapperGenericTypes[0];
		final Class<B> classB = (Class<B>) mapperGenericTypes[1];
		final String mapperKey = this.getMapperKey(classA, classB);
		if (this.map.containsKey(mapperKey)) {
			throw new UnsupportedOperationException("A Mapper<" + classA.getSimpleName() + ", " + classB.getSimpleName() + "> is already registered.");
		}
		customMapper.setMapper(this);
		this.map.put(mapperKey, customMapper);
		return this;
	}

	public <A, B> void registerAll(final List<? extends Mapper> customMappers) {

		for (final Mapper<A, B> customMapper : customMappers) {
			this.register(customMapper);
		}
	}

	@Override
	public <A, B> B map(final A sourceObject, final Class<B> destinationClass) {

		if (sourceObject == null) {
			return null;
		}

		Objects.requireNonNull(destinationClass, "Destination Class Type must be not null.");

		final Class<A> sourceClass = (Class<A>) sourceObject.getClass();

		final String aTb = this.getMapperKey(sourceClass, destinationClass);
		final String bTa = this.getMapperKey(destinationClass, sourceClass);
		final Mapper<A, B> customMapperAToB = this.map.get(aTb);
		final Mapper<B, A> customMapperBToA = this.map.get(bTa);
		if (customMapperAToB != null) {
			return customMapperAToB.mapAToB(sourceObject);
		}
		if (customMapperBToA != null) {
			return customMapperBToA.mapBToA(sourceObject);
		}
		throw new UnsupportedOperationException("No mapper defined for [" + aTb + "] or [" + bTa + "]");
	}

	public <A, B> List<B> map(final List<A> sourceList, final Class<B> destinationClass) {

		if (sourceList == null) {
			return null;
		}
		final List<B> returnList = new ArrayList<B>(sourceList.size());
		for (final A a : sourceList) {
			returnList.add(this.map(a, destinationClass));
		}
		return returnList;
	}

	private String getMapperKey(final Class<?> classA, final Class<?> classB) {
		final StringBuilder mapKey = new StringBuilder();
		mapKey.append(classA.getName());
		mapKey.append("_");
		mapKey.append(classB.getName());
		return mapKey.toString();
	}
}
