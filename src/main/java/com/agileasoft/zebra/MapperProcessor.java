package com.agileasoft.zebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.agileasoft.zebra.util.ZebraUtils;

/**
 * @author amgohan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapperProcessor {
	private final Map<String, Mapper> mappers;

	public MapperProcessor(final Map<String, Mapper> mappers) {
		Objects.requireNonNull(mappers, "Mappers store must be not null.");
		this.mappers = new HashMap<String, Mapper>();
		for (final Entry<String, Mapper> entryMapper : mappers.entrySet()) {
			entryMapper.getValue().setMapper(this);
			this.mappers.put(entryMapper.getKey(), entryMapper.getValue());
		}
	}

	public <A, B> B map(final A sourceObject, final Class<B> destinationClass) {

		if (sourceObject == null) {
			return null;
		}

		Objects.requireNonNull(destinationClass, "Destination Class Type must be not null.");

		final Class<A> sourceClass = (Class<A>) sourceObject.getClass();

		final String aTb = ZebraUtils.getMapperKey(sourceClass, destinationClass);
		final String bTa = ZebraUtils.getMapperKey(destinationClass, sourceClass);
		final Mapper<A, B> customMapperAToB = this.mappers.get(aTb);
		final Mapper<B, A> customMapperBToA = this.mappers.get(bTa);
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
}
