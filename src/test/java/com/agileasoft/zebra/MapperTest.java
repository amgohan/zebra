package com.agileasoft.zebra;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

/**
 * @author amgohan
 */
public class MapperTest {
	private final MapperProcessor mapperProcessor = new MapperProcessor(new HashMap<String, List<Mapper>>());
	private final Mapper<String, String> mapper = new Mapper<String, String>() {

		@Override
		public String mapAToB(final String source) {
			return source;
		}
	};

	@Test(expected = IllegalStateException.class)
	public void givenMapper_whenSetMapperProcessorTwice_thenThrowException() {
		this.mapper.setMapper(this.mapperProcessor);
		this.mapper.setMapper(this.mapperProcessor);
	}
}
