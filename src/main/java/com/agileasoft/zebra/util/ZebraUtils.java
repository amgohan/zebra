package com.agileasoft.zebra.util;

/**
 * All utils methods of Zebra framework.
 *
 * @author amgohan
 */
public final class ZebraUtils {

	private ZebraUtils() {
	}

	/**
	 * Generate the unique name of a custom Mapper based on the full class names of source and destination objects.
	 *
	 * @param classA
	 *            source object
	 * @param classB
	 *            destination object
	 * @return the unique name of a mapper.
	 */
	public static String getMapperKey(final Class<?> classA, final Class<?> classB) {
		final StringBuilder mapKey = new StringBuilder();
		mapKey.append(classA.getName());
		mapKey.append("_");
		mapKey.append(classB.getName());
		return mapKey.toString();
	}

}
