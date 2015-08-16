package com.agileasoft.zebra;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.agileasoft.zebra.util.ZebraUtils;

/**
 * The factory that register all mappers and create a MapperProcessor.
 *
 * @author amgohan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapperFactory {

	private final Map<String, List<Mapper>> mappers = new HashMap<String, List<Mapper>>();

	boolean mapperProcessorCreated = false;

	/**
	 * register a unique mapper.
	 *
	 * @param customMapper
	 *            a mapper thant extends from the abstract Mapper.
	 * @param <A>
	 *            source class
	 * @param <B>
	 *            destination class
	 * @return this
	 */
	public <A, B> MapperFactory register(final Mapper<A, B> customMapper) {
		if (this.mapperProcessorCreated) {
			throw new IllegalStateException("You can't register a new mapper after calling build method.");
		}
		Objects.requireNonNull(customMapper, "Null Mapper can't be registered.");
		final Type[] mapperGenericTypes = ((ParameterizedType) customMapper.getClass().getGenericSuperclass()).getActualTypeArguments();
		final Class<A> classA = (Class<A>) mapperGenericTypes[0];
		final Class<B> classB = (Class<B>) mapperGenericTypes[1];
		final String mapperKey = ZebraUtils.getMapperKey(classA, classB);

		if (!this.mappers.containsKey(mapperKey)) {
			this.mappers.put(mapperKey, new ArrayList<Mapper>());
		}
		this.mappers.get(mapperKey).add(customMapper);
		return this;
	}

	/**
	 * register a list of mappers.
	 *
	 * @param customMappers
	 *            list of mappers that extends from the abstract Mapper.
	 * @param <A>
	 *            source class
	 * @param <B>
	 *            destination class
	 * @return this
	 */
	public <A, B> MapperFactory registerAll(final List<? extends Mapper> customMappers) {

		for (final Mapper<A, B> customMapper : customMappers) {
			this.register(customMapper);
		}
		return this;
	}

	/**
	 * create a MapperProcessor which will be injected anywhere you want.
	 *
	 * @return an instance of MapperProcessor.
	 */
	public MapperProcessor build() {
		if (this.mapperProcessorCreated) {
			throw new IllegalStateException("build method can be called one time.");
		}
		final MapperProcessor mapperProcessor = new MapperProcessor(this.mappers);
		this.mappers.clear();
		this.mapperProcessorCreated = true;
		return mapperProcessor;
	}
}
