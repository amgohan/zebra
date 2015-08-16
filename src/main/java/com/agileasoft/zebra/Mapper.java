package com.agileasoft.zebra;

/**
 * The base class for all Mappers. <br>
 * All subclass mappers of this abstract class must implements a one way mapping mapAToB, the mapBToA is optional, only if we want create a
 * bidirectional mapper.
 *
 * @author amgohan
 * @param <A>
 *            source class
 * @param <B>
 *            destination class
 */
public abstract class Mapper<A, B> {

	protected MapperProcessor mapper;

	private boolean isSetMapperCalled = false;

	/**
	 * Method to map an instance of type A to an instance of type B.
	 *
	 * @param source
	 *            class A
	 * @return destination class B
	 */
	public abstract B mapAToB(final A source);

	/**
	 * Method to map back an instance of type B to an instance of type A.
	 *
	 * @param source
	 *            class A
	 * @return destination class B
	 */
	public A mapBToA(final B source) {

		throw new UnsupportedOperationException("method not implemented.");
	}

	/**
	 * this setter can be called one time for a specific mapper.
	 *
	 * @param mapper
	 *            MapperProcessor
	 */
	public void setMapper(final MapperProcessor mapper) {
		if (this.isSetMapperCalled) {
			throw new IllegalStateException("mapper can't be setted more than once.");
		}
		this.mapper = mapper;
		this.isSetMapperCalled = true;
	}
}
