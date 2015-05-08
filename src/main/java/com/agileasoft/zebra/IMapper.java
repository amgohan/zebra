package com.agileasoft.zebra;

public interface IMapper {
	<A, B> B map(final A sourceObject, final Class<B> destinationClass);
}
