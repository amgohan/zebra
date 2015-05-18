package com.agileasoft.zebra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import com.agileasoft.zebra.util.ZebraUtils;

/**
 * You can only inject a reference of this class to use every mapper you created and registred.<br>
 * Suppose that we registred a mapper like this :
 *
 * <pre>
 * {
 * 	&#064;code
 * 	public class UserMapper extends Mapper&lt;UserDto, User&gt; {
 * 		public abstract User mapAToB(final UserDto userDto) {
 * 		User user = new User();
 * 		user.setUsername(userDto.getEmail());
 * 		user.setPassword(userDto.getPassword());
 * 		}
 * 	}
 * }
 * </pre>
 *
 * So we can inject a MapperProcessor anywhere we want and do this to map an object UserDto to a User class type. <br>
 * {@code User user = mapperProcessor.map(userDto, User.class)}
 *
 * @author amgohan
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapperProcessor {
	private final Map<String, Mapper> mappers;

	/**
	 * constructor with params.
	 *
	 * @param mappers
	 *            a Map key, value :
	 *            <ul>
	 *            <li>key : the key of mapper like package.name.a.classA_package.name.b.classB</li>
	 *            <li>value : an instance of Mapper&lt;ClassA, ClassB&gt;</li>
	 *            </ul>
	 */
	public MapperProcessor(final Map<String, Mapper> mappers) {
		Objects.requireNonNull(mappers, "Mappers store must be not null.");
		this.mappers = new HashMap<String, Mapper>();
		for (final Entry<String, Mapper> entryMapper : mappers.entrySet()) {
			entryMapper.getValue().setMapper(this);
			this.mappers.put(entryMapper.getKey(), entryMapper.getValue());
		}
	}

	/**
	 * This method map the source object to an instance of destination Type.<br>
	 * A mapper of this objects must already registred. If not an exception will be thrown.
	 *
	 * @param sourceObject
	 *            the source instance of type A.
	 * @param destinationClass
	 *            the returned type of instance.
	 * @param <A>
	 *            source class
	 * @param <B>
	 *            destination class
	 * @return instance of type B.
	 */
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

	/**
	 * This method map a List of objects A to a list of objects B.
	 *
	 * @param sourceList
	 *            list of objects A.
	 * @param destinationClass
	 *            the wanted type of destination list.
	 * @param <A>
	 *            source class
	 * @param <B>
	 *            destination class
	 * @return List of type B.
	 */
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

	/**
	 * Map a collection of type A to another collection of type B. <br>
	 * Use this only if the destination collection not the same as the source collection. <br>
	 * For example mapping an ArrayList of type A to an HashSet of type B. <br>
	 * We not recommend to use this method, unless you are okay with using reflection at runtime every time you call this method.
	 *
	 * @param sourceList
	 *            list of objects A.
	 * @param destinationClass
	 *            the wanted type of destination collection.
	 * @param collectionImpl
	 *            the wanted collection implementation
	 * @param <A>
	 *            source class type of the mapper
	 * @param <B>
	 *            destination class type of the mapper
	 * @param <C>
	 *            the wanted collection implementation type
	 * @return
	 */
	public <A, B, C extends Collection<B>> C map(final Collection<A> sourceList, final Class<B> destinationClass, final Class<C> collectionImpl) {

		if (sourceList == null) {
			return null;
		}
		final C returnCollection;
		try {
			returnCollection = collectionImpl.getConstructor(int.class).newInstance(sourceList.size());
		} catch (final Exception exception) {
			throw new IllegalStateException(exception.getMessage(), exception.getCause());
		}
		for (final A a : sourceList) {
			returnCollection.add(this.map(a, destinationClass));
		}
		return returnCollection;
	}
}
