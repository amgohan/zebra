package com.agileasoft.zebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * @author amgohan
 */
public class MapperFactoryTest {

	private MapperFactory mapperFactory;

	private MapperProcessor mapper;

	private SourceObject source;

	@Before
	public void before() {

		this.mapperFactory = new MapperFactory();
		this.source = new SourceObject();
		this.source.attribute1 = "attribute1";
		this.source.attribute2 = 2;
		this.source.attributes3 = new Date();
	}

	@Test
	public void givenMapperFactoryEmpty_whenMapping_fail() {

		try {

			this.mapper = this.mapperFactory.build();
			@SuppressWarnings("unused")
			final DestinationObject b = this.mapper.map(this.source, DestinationObject.class);
			fail("must throw UnsupportedOperationException");
		} catch (final Exception e) {
			assertTrue("expected Exception is : UnsupportedOperationException", e instanceof UnsupportedOperationException);
			assertEquals(	"No mapper defined for [com.agileasoft.zebra.MapperFactoryTest$SourceObject_com.agileasoft.zebra.MapperFactoryTest$DestinationObject] or [com.agileasoft.zebra.MapperFactoryTest$DestinationObject_com.agileasoft.zebra.MapperFactoryTest$SourceObject]",
							e.getMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void givenMapperFactory_whenBuildTwice_thenThrowException() {

		this.mapperFactory.register(new CustomMapperOneWay());
		this.mapperFactory.build();
		this.mapperFactory.build();
	}

	@Test(expected = IllegalStateException.class)
	public void givenMapperFactory_whenRegisterAfterBuild_thenThrowException() {

		this.mapperFactory.register(new CustomMapperOneWay());
		this.mapperFactory.build();
		this.mapperFactory.register(new CustomMapperOneWay());
	}

	@Test
	public void givenMappingOneWay_whenMapAToB_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = this.mapper.map(this.source, DestinationObject.class);
		assertNotNull(b);
		assertEquals(this.source.attribute1, b.getAttr1());
		assertEquals(this.source.attribute2, b.getAttr2());
		assertEquals(this.source.attributes3, b.attr3);
	}

	@Test(expected = IllegalStateException.class)
	public void givenMappingOneWayTwice_whenMapAToB_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).register(new CustomMapperOneWay()).build();
		this.mapper.map(this.source, DestinationObject.class);
	}

	@Test
	public void givenMappingOneWay_whenMapAToBWithANull_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = this.mapper.map((SourceObject) null, DestinationObject.class);
		assertNull(b);
	}

	@Test
	public void givenMappingOneWay_whenStraightMap_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = this.mapper.straightMap(this.source, CustomMapperOneWay.class);
		assertNotNull(b);
		assertEquals(this.source.attribute1, b.getAttr1());
		assertEquals(this.source.attribute2, b.getAttr2());
		assertEquals(this.source.attributes3, b.attr3);
	}

	@Test
	public void givenMappingOneWay_whenStraightMapWithANull_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = this.mapper.straightMap((SourceObject) null, CustomMapperOneWay.class);
		assertNull(b);
	}

	@Test
	public void givenMappingOneWay_whenReverseMapWithBNull_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final SourceObject a = this.mapper.reverseMap((DestinationObject) null, CustomMapperOneWay.class);
		assertNull(a);
	}

	@Test
	public void givenMappingOneWay_whenMapListAToListB_success() {

		this.mapper = this.mapperFactory.registerAll(Arrays.asList(new Mapper[] { new CustomMapperOneWay() })).build();
		final List<SourceObject> listA = Arrays.asList(new SourceObject[] { this.source, this.source, this.source });
		final List<DestinationObject> listB = this.mapper.map(listA, DestinationObject.class);
		assertNotNull(listB);
		assertEquals(3, listB.size());
		for (final DestinationObject b : listB) {
			assertNotNull(b);
			assertEquals(this.source.attribute1, b.getAttr1());
			assertEquals(this.source.attribute2, b.getAttr2());
			assertEquals(this.source.attributes3, b.getAttr3());
		}
	}

	@Test
	public void givenMappingOneWay_whenMapListAToListBAndANull_success() {

		this.mapper = this.mapperFactory.registerAll(Arrays.asList(new Mapper[] { new CustomMapperOneWay() })).build();
		final List<DestinationObject> listB = this.mapper.map((List) null, DestinationObject.class);
		assertNull(listB);
	}

	@Test
	public void givenMappingOneWay_whenStraightMapList_success() {

		this.mapper = this.mapperFactory.registerAll(Arrays.asList(new Mapper[] { new CustomMapperOneWay(), new CustomMapperOneWay() })).build();
		final List<SourceObject> listA = Arrays.asList(new SourceObject[] { this.source, this.source, this.source });
		final List<DestinationObject> listB = this.mapper.straightMap(listA, CustomMapperOneWay.class);
		assertNotNull(listB);
		assertEquals(3, listB.size());
		for (final DestinationObject b : listB) {
			assertNotNull(b);
			assertEquals(this.source.attribute1, b.getAttr1());
			assertEquals(this.source.attribute2, b.getAttr2());
			assertEquals(this.source.attributes3, b.getAttr3());
		}
	}

	@Test
	public void givenMappingOneWay_whenStraightMapListWithANull_success() {

		this.mapper = this.mapperFactory.registerAll(Arrays.asList(new Mapper[] { new CustomMapperOneWay(), new CustomMapperOneWay() })).build();
		final List<DestinationObject> listB = this.mapper.straightMap((List) null, CustomMapperOneWay.class);
		assertNull(listB);
	}

	@Test
	public void givenMappingBidirectional_whenReverseMapList_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final DestinationObject b = new DestinationObject();
		b.setAttr1("bbbb");
		b.setAttr2(0);
		b.setAttr3(new Date());
		final List<DestinationObject> listB = Arrays.asList(b, b);
		final List<SourceObject> listA = this.mapper.reverseMap(listB, CustomMapperBidirectional.class);
		assertNotNull(listA);
		assertEquals(2, listA.size());
		assertEquals(b.getAttr1(), listA.get(0).attribute1);
		assertEquals(b.getAttr2(), listA.get(0).attribute2);
		assertEquals(b.getAttr3(), listA.get(0).attributes3);
		assertEquals(b.getAttr1(), listA.get(1).attribute1);
		assertEquals(b.getAttr2(), listA.get(1).attribute2);
		assertEquals(b.getAttr3(), listA.get(1).attributes3);
	}

	@Test
	public void givenMappingBidirectional_whenReverseMapListWithBNull_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final List<SourceObject> listA = this.mapper.reverseMap((List) null, CustomMapperBidirectional.class);
		assertNull(listA);
	}

	@Test
	public void givenMappingOneWay_whenMapBToA_fail() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = new DestinationObject();
		b.setAttr1("bbbb");
		b.setAttr2(0);
		b.setAttr3(new Date());
		try {
			@SuppressWarnings("unused")
			final SourceObject a = this.mapper.map(b, SourceObject.class);
			fail("must throw UnsupportedOperationException");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
			assertEquals("method not implemented.", e.getMessage());
		}

	}

	@Test
	public void givenMappingOneWay_whenReverseMap_fail() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).build();
		final DestinationObject b = new DestinationObject();
		b.setAttr1("bbbb");
		b.setAttr2(0);
		b.setAttr3(new Date());
		try {
			@SuppressWarnings("unused")
			final SourceObject a = this.mapper.reverseMap(b, CustomMapperOneWay.class);
			fail("must throw UnsupportedOperationException");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
			assertEquals("method not implemented.", e.getMessage());
		}

	}

	@Test
	public void givenMappingBidirectional_whenMapAToB_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final DestinationObject b = this.mapper.map(this.source, DestinationObject.class);
		assertNotNull(b);
		assertEquals(this.source.attribute1, b.getAttr1());
		assertEquals(this.source.attribute2, b.getAttr2());
		assertEquals(this.source.attributes3, b.attr3);
	}

	@Test
	public void givenMappingBidirectional_whenStraightMap_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final DestinationObject b = this.mapper.straightMap(this.source, CustomMapperBidirectional.class);
		assertNotNull(b);
		assertEquals(this.source.attribute1, b.getAttr1());
		assertEquals(this.source.attribute2, b.getAttr2());
		assertEquals(this.source.attributes3, b.attr3);
	}

	@Test
	public void givenMappingBidirectional_whenMapBToA_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final DestinationObject b = new DestinationObject();
		b.setAttr1("bbbb");
		b.setAttr2(0);
		b.setAttr3(new Date());

		final SourceObject a = this.mapper.map(b, SourceObject.class);
		assertNotNull(b);
		assertEquals(b.getAttr1(), a.attribute1);
		assertEquals(b.getAttr2(), a.attribute2);
		assertEquals(b.getAttr3(), a.attributes3);
	}

	@Test
	public void givenMappingBidirectional_whenReverseMap_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperBidirectional()).build();
		final DestinationObject b = new DestinationObject();
		b.setAttr1("bbbb");
		b.setAttr2(0);
		b.setAttr3(new Date());

		final SourceObject a = this.mapper.reverseMap(b, CustomMapperBidirectional.class);
		assertNotNull(b);
		assertEquals(b.getAttr1(), a.attribute1);
		assertEquals(b.getAttr2(), a.attribute2);
		assertEquals(b.getAttr3(), a.attributes3);
	}

	@Test
	public void givenMappingWrappers_whenMapAToB_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).register(new DeepMappingMapper()).build();
		final WrapperSourceObject wrapperSource = new WrapperSourceObject();
		wrapperSource.source = this.source;
		final WrapperDestinationObject wrapperCible = this.mapper.map(wrapperSource, WrapperDestinationObject.class);
		assertNotNull(wrapperCible);
		assertNotNull(wrapperCible.destination);
		assertEquals(wrapperSource.source.attribute1, wrapperCible.destination.getAttr1());
		assertEquals(wrapperSource.source.attribute2, wrapperCible.destination.getAttr2());
		assertEquals(wrapperSource.source.attributes3, wrapperCible.destination.attr3);
	}

	@Test
	public void givenMappingWrappers_whenStraightMap_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay())
										.register(new DeepMappingMapper())
										.register(new CustomMapperOneWay())
										.build();
		final WrapperSourceObject wrapperSource = new WrapperSourceObject();
		wrapperSource.source = this.source;
		final WrapperDestinationObject wrapperCible = this.mapper.straightMap(wrapperSource, DeepMappingMapper.class);
		assertNotNull(wrapperCible);
		assertNotNull(wrapperCible.destination);
		assertEquals(wrapperSource.source.attribute1, wrapperCible.destination.getAttr1());
		assertEquals(wrapperSource.source.attribute2, wrapperCible.destination.getAttr2());
		assertEquals(wrapperSource.source.attributes3, wrapperCible.destination.attr3);
	}

	@Test(expected = IllegalStateException.class)
	public void givenNoMapper_whenStraightMap_success() {

		this.mapper = this.mapperFactory.build();
		final WrapperSourceObject wrapperSource = new WrapperSourceObject();
		wrapperSource.source = this.source;
		this.mapper.straightMap(wrapperSource, DeepMappingMapper.class);
	}

	@Test(expected = IllegalStateException.class)
	public void givenNoMapper_whenReverseMap_success() {

		this.mapper = this.mapperFactory.build();
		final WrapperDestinationObject wrapperDestination = new WrapperDestinationObject();
		this.mapper.reverseMap(wrapperDestination, DeepMappingMapper.class);
	}

	@Test
	public void givenMappingWrappers_whenMapListToSet_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).register(new DeepMappingMapper()).build();
		final WrapperSourceObject wrapperSource = new WrapperSourceObject();
		wrapperSource.source = this.source;
		final Set<WrapperDestinationObject> setWrapperCible = this.mapper.map(	Arrays.asList(wrapperSource),
																				WrapperDestinationObject.class,
																				HashSet.class);
		assertNotNull(setWrapperCible);
		assertFalse(setWrapperCible.isEmpty());
		assertEquals(1, setWrapperCible.size());
		for (final WrapperDestinationObject wrapperCible : setWrapperCible) {
			assertEquals(wrapperSource.source.attribute1, wrapperCible.destination.getAttr1());
			assertEquals(wrapperSource.source.attribute2, wrapperCible.destination.getAttr2());
			assertEquals(wrapperSource.source.attributes3, wrapperCible.destination.attr3);
		}
	}

	@Test
	public void givenMappingWrappers_whenMapListToSetWithListNull_success() {

		this.mapper = this.mapperFactory.register(new CustomMapperOneWay()).register(new DeepMappingMapper()).build();
		final WrapperSourceObject wrapperSource = new WrapperSourceObject();
		wrapperSource.source = this.source;
		final Set<WrapperDestinationObject> setWrapperCible = this.mapper.map((List) null, WrapperDestinationObject.class, HashSet.class);
		assertNull(setWrapperCible);
	}

	class WrapperSourceObject {
		public SourceObject source;
	}

	class WrapperDestinationObject {
		public DestinationObject destination;
	}

	class SourceObject {
		public String attribute1;

		public Integer attribute2;

		public Date attributes3;
	}

	class DestinationObject {
		private String attr1;

		private Integer attr2;

		private Date attr3;

		public String getAttr1() {

			return this.attr1;
		}

		public void setAttr1(final String attr1) {

			this.attr1 = attr1;
		}

		public Integer getAttr2() {

			return this.attr2;
		}

		public void setAttr2(final Integer attr2) {

			this.attr2 = attr2;
		}

		public Date getAttr3() {

			return this.attr3;
		}

		public void setAttr3(final Date attr3) {

			this.attr3 = attr3;
		}

	}

	class DeepMappingMapper extends Mapper<WrapperSourceObject, WrapperDestinationObject> {

		@Override
		public WrapperDestinationObject mapAToB(final WrapperSourceObject source) {
			final WrapperDestinationObject cible = new WrapperDestinationObject();
			// the Mapper Parent has a field mapper which can call other registered mappers hear
			cible.destination = this.mapper.straightMap(source.source, CustomMapperOneWay.class);
			return cible;
		}

	}

	class CustomMapperOneWay extends Mapper<SourceObject, DestinationObject> {

		@Override
		public DestinationObject mapAToB(final SourceObject a) {

			final DestinationObject b = new DestinationObject();
			b.setAttr1(a.attribute1);
			b.setAttr2(a.attribute2);
			b.setAttr3(a.attributes3);
			return b;
		}

	}

	class CustomMapperBidirectional extends Mapper<SourceObject, DestinationObject> {

		@Override
		public DestinationObject mapAToB(final SourceObject pSource) {

			final DestinationObject destination = new DestinationObject();
			// whatever logic you want
			destination.setAttr1(pSource.attribute1);

			destination.setAttr2(pSource.attribute2);
			destination.setAttr3(pSource.attributes3);
			return destination;
		}

		@Override
		public SourceObject mapBToA(final DestinationObject b) {

			final SourceObject a = new SourceObject();
			a.attribute1 = b.getAttr1();
			a.attribute2 = b.getAttr2();
			a.attributes3 = b.getAttr3();

			return a;
		}

	}

}
