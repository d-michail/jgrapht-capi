/*
 * (C) Copyright 2020, by Dimitrios Michail.
 *
 * JGraphT C-API
 *
 * See the CONTRIBUTORS.md file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the
 * GNU Lesser General Public License v2.1 or later
 * which is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1-standalone.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR LGPL-2.1-or-later
 */
package org.jgrapht.capi.impl;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.ObjectHandle;
import org.graalvm.nativeimage.ObjectHandles;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CDoublePointer;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.jgrapht.Graph;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm;
import org.jgrapht.alg.interfaces.VertexCoverAlgorithm.VertexCover;
import org.jgrapht.alg.vertexcover.BarYehudaEvenTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.ClarksonTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.EdgeBasedTwoApproxVCImpl;
import org.jgrapht.alg.vertexcover.GreedyVCImpl;
import org.jgrapht.alg.vertexcover.RecursiveExactVCImpl;
import org.jgrapht.capi.Constants;
import org.jgrapht.capi.JGraphTContext.Status;
import org.jgrapht.capi.error.StatusReturnExceptionHandler;

public class VertexCoverApi {

	private static ObjectHandles globalHandles = ObjectHandles.getGlobal();

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_greedy", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new GreedyVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_greedy_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverGreedyWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new GreedyVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_clarkson", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverClarkson(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new ClarksonTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_clarkson_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverClarksonWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new ClarksonTwoApproxVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_edgebased", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverEdgeBased(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new EdgeBasedTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_baryehudaeven", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverBarYehudaEven(IsolateThread thread, ObjectHandle graphHandle,
			CDoublePointer weightRes, WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new BarYehudaEvenTwoApproxVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_baryehudaeven_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverBarYehudaEvenWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new BarYehudaEvenTwoApproxVCImpl<>(g, weights), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_exact", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverExact(IsolateThread thread, ObjectHandle graphHandle, CDoublePointer weightRes,
			WordPointer res) {
		return executeVertexCover(thread, graphHandle, g -> new RecursiveExactVCImpl<>(g), weightRes, res);
	}

	@CEntryPoint(name = Constants.LIB_PREFIX + Constants.ANYANY
			+ "vertexcover_exec_exact_weighted", exceptionHandler = StatusReturnExceptionHandler.class)
	public static int executeVertexCoverExactWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle, CDoublePointer weightRes, WordPointer res) {
		return executeVertexCoverWeighted(thread, graphHandle, mapHandle,
				(g, weights) -> new RecursiveExactVCImpl<>(g, weights), weightRes, res);
	}

	private static <V,E> int executeVertexCover(IsolateThread thread, ObjectHandle graphHandle,
			Function<Graph<V, E>, VertexCoverAlgorithm<V>> algProvider, CDoublePointer weightRes,
			WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		VertexCoverAlgorithm<V> alg = algProvider.apply(g);
		VertexCover<V> vertexCover = alg.getVertexCover();
		if (weightRes.isNonNull()) {
			weightRes.write(vertexCover.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

	private static <V,E> int executeVertexCoverWeighted(IsolateThread thread, ObjectHandle graphHandle,
			ObjectHandle mapHandle,
			BiFunction<Graph<V, E>, Map<V, Double>, VertexCoverAlgorithm<V>> algProvider,
			CDoublePointer weightRes, WordPointer res) {
		Graph<V, E> g = globalHandles.get(graphHandle);
		Map<V, Double> vertexWeights = globalHandles.get(mapHandle);
		VertexCoverAlgorithm<V> alg = algProvider.apply(g, vertexWeights);
		VertexCover<V> vertexCover = alg.getVertexCover();
		if (weightRes.isNonNull()) {
			weightRes.write(vertexCover.getWeight());
		}
		if (res.isNonNull()) {
			res.write(globalHandles.create(vertexCover));
		}
		return Status.STATUS_SUCCESS.getCValue();
	}

}
