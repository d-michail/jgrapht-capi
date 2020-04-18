package org.jgrapht.nlib.api;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.jgrapht.nlib.Constants;
import org.jgrapht.nlib.Errors;
import org.jgrapht.nlib.Status;

public class GenericAPI {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	/**
	 * Destroy a handle
	 * 
	 * @param thread the thread
	 * @param handle the handle
	 */
	@CEntryPoint(name = Constants.LIB_PREFIX + "destroy")
	public static void destroy(IsolateThread thread, ObjectHandle handle) {
		try {
			globalHandles.destroy(handle);
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_REFERENCE);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_next")
	public static long iteratorNext(IsolateThread thread, ObjectHandle itHandle) {
		try {
			Iterator<Long> it = globalHandles.get(itHandle);
			return it.next();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_REFERENCE);
		} catch (NoSuchElementException e) {
			Errors.setError(Status.ITERATOR_NO_SUCH_ELEMENT);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return Constants.LONG_NO_RESULT;
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + "it_hasnext")
	public static boolean iteratorHasNext(IsolateThread thread, ObjectHandle itHandle) {
		try {
			Iterator<Long> it = globalHandles.get(itHandle);
			return it.hasNext();
		} catch (IllegalArgumentException e) {
			Errors.setError(Status.INVALID_REFERENCE);
		} catch (Exception e) {
			Errors.setError(Status.GENERIC_ERROR);
		}
		return false;
	}

}