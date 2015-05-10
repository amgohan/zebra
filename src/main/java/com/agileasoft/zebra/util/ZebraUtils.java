package com.agileasoft.zebra.util;

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
	 * @return
	 */
	public static String getMapperKey(final Class<?> classA, final Class<?> classB) {
		final StringBuilder mapKey = new StringBuilder();
		mapKey.append(classA.getName());
		mapKey.append("_");
		mapKey.append(classB.getName());
		return mapKey.toString();
	}

}
