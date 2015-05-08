package com.agileasoft.zebra;

/**
 * @author amgohan
 * @param <A>
 * @param <B>
 */
public abstract class Mapper<A, B> {

	protected MapperFactory mapper;

	public abstract B mapAToB(final A source);

	public A mapBToA(final B source) {

		throw new UnsupportedOperationException("method not implemented.");
	}

	void setMapper(final MapperFactory mapper) {
		this.mapper = mapper;
	}
}
